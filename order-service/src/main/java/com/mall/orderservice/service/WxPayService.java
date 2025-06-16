import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.orderservice.config.WxPayConfig;
import com.mall.orderservice.dto.WxPayRequest;
import com.mall.orderservice.model.Order;
import com.mall.orderservice.model.OrderStatusHistory;
import com.mall.orderservice.util.WxPayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WxPayService {

    private final WxPayConfig wxPayConfig;
    private final OrderService orderService;
    private final WxPayUtil wxPayUtil;
    private final ObjectMapper objectMapper;

    private static final String API_BASE_URL = "https://api.mch.weixin.qq.com/v3";
    private static final String SANDBOX_API_BASE_URL = "https://api.mch.weixin.qq.com/sandboxnew/v3";

    /**
     * 创建H5支付订单
     */
    @Transactional
    public String createH5Pay(WxPayRequest request) {
        // 1. 获取订单信息
        Order order = orderService.getOrder(request.getOrderId());
        if (order == null) {
            throw PaymentException.orderNotFound(request.getOrderNo());
        }

        // 2. 验证订单状态
        if (order.getStatus() != Order.OrderStatus.PENDING_PAYMENT) {
            throw PaymentException.invalidOrderStatus(
                order.getOrderNo(),
                order.getStatus().name(),
                Order.OrderStatus.PENDING_PAYMENT.name()
            );
        }

        // 3. 验证支付金额
        if (order.getTotalAmount().compareTo(request.getAmount()) != 0) {
            throw PaymentException.invalidAmount(order.getOrderNo(), 
                String.format("订单金额不匹配，订单金额：%s，支付金额：%s", 
                    order.getTotalAmount(), request.getAmount()));
        }

        try {
            // 4. 构建支付参数
            Map<String, Object> params = new HashMap<>();
            params.put("appid", wxPayConfig.getAppId());
            params.put("mchid", wxPayConfig.getMchId());
            params.put("description", "商城订单-" + order.getOrderNo());
            params.put("out_trade_no", order.getOrderNo());
            params.put("notify_url", wxPayConfig.getNotifyUrl());
            
            Map<String, Object> amount = new HashMap<>();
            amount.put("total", order.getTotalAmount().multiply(new BigDecimal("100")).intValue());
            amount.put("currency", "CNY");
            params.put("amount", amount);
            
            Map<String, Object> sceneInfo = new HashMap<>();
            sceneInfo.put("payer_client_ip", request.getClientIp());
            Map<String, String> h5Info = new HashMap<>();
            h5Info.put("type", "Wap");
            h5Info.put("app_name", "商城");
            h5Info.put("app_url", request.getAppUrl());
            sceneInfo.put("h5_info", h5Info);
            params.put("scene_info", sceneInfo);

            // 5. 调用支付API
            String url = getApiBaseUrl() + "/v3/pay/transactions/h5";
            String response = wxPayUtil.post(url, objectMapper.writeValueAsString(params));
            JsonNode responseNode = objectMapper.readTree(response);
            
            // 6. 处理响应
            if (responseNode.has("h5_url")) {
                // 更新订单状态
                order.setPaymentStatus(Order.PaymentStatus.PROCESSING);
                orderRepository.save(order);
                
                // 记录状态变更
                orderService.addOrderStatusHistory(
                    order,
                    Order.OrderStatus.PENDING_PAYMENT,
                    "发起支付",
                    "SYSTEM",
                    OrderStatusHistory.OperationType.PAY
                );
                
                return responseNode.get("h5_url").asText();
            } else {
                throw PaymentException.paymentFailed(order.getOrderNo(), 
                    responseNode.has("message") ? responseNode.get("message").asText() : "创建支付订单失败");
            }
        } catch (PaymentException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建H5支付订单失败", e);
            throw PaymentException.paymentFailed(order.getOrderNo(), e.getMessage());
        }
    }

    /**
     * 处理支付回调通知
     */
    @Transactional
    public void handlePayNotify(String notifyData) {
        try {
            // 1. 验证签名
            if (!wxPayUtil.verifySign(notifyData)) {
                throw PaymentException.invalidSignature();
            }

            // 2. 解密回调数据
            JsonNode rootNode = objectMapper.readTree(notifyData);
            String ciphertext = rootNode.path("resource").path("ciphertext").asText();
            String associatedData = rootNode.path("resource").path("associated_data").asText();
            String nonce = rootNode.path("resource").path("nonce").asText();
            
            String decryptedData = wxPayUtil.decryptNotifyData(ciphertext, associatedData, nonce);
            JsonNode dataNode = objectMapper.readTree(decryptedData);
            
            // 3. 获取订单信息
            String orderNo = dataNode.path("out_trade_no").asText();
            Order order = orderService.getOrderByOrderNo(orderNo);
            if (order == null) {
                throw PaymentException.orderNotFound(orderNo);
            }

            // 4. 处理支付结果
            String tradeState = dataNode.path("trade_state").asText();
            switch (tradeState) {
                case "SUCCESS":
                    // 支付成功
                    if (order.getStatus() != Order.OrderStatus.PENDING_PAYMENT) {
                        throw PaymentException.invalidOrderStatus(
                            orderNo,
                            order.getStatus().name(),
                            Order.OrderStatus.PENDING_PAYMENT.name()
                        );
                    }
                    
                    // 更新订单状态
                    order.setStatus(Order.OrderStatus.PAID);
                    order.setPaymentStatus(Order.PaymentStatus.PAID);
                    order.setPaymentTime(LocalDateTime.parse(
                        dataNode.path("success_time").asText().replace("Z", "")));
                    order.setTransactionId(dataNode.path("transaction_id").asText());
                    
                    // 记录状态变更
                    orderService.addOrderStatusHistory(
                        order,
                        Order.OrderStatus.PAID,
                        "支付成功",
                        "SYSTEM",
                        OrderStatusHistory.OperationType.PAY
                    );
                    break;

                case "CLOSED":
                    // 订单已关闭
                    order.setStatus(Order.OrderStatus.CLOSED);
                    order.setPaymentStatus(Order.PaymentStatus.CLOSED);
                    
                    // 记录状态变更
                    orderService.addOrderStatusHistory(
                        order,
                        Order.OrderStatus.CLOSED,
                        "支付关闭",
                        "SYSTEM",
                        OrderStatusHistory.OperationType.PAY_CLOSE
                    );
                    break;

                case "PAYERROR":
                    // 支付失败
                    order.setPaymentStatus(Order.PaymentStatus.FAILED);
                    
                    // 记录状态变更
                    orderService.addOrderStatusHistory(
                        order,
                        Order.OrderStatus.PAYMENT_FAILED,
                        "支付失败: " + dataNode.path("trade_state_desc").asText(),
                        "SYSTEM",
                        OrderStatusHistory.OperationType.PAY
                    );
                    break;

                default:
                    log.warn("未知的支付状态: {}, 订单号: {}", tradeState, orderNo);
                    break;
            }

            // 5. 保存订单更新
            orderRepository.save(order);
            
        } catch (PaymentException e) {
            throw e;
        } catch (Exception e) {
            log.error("处理支付回调通知失败", e);
            throw PaymentException.paymentFailed("未知订单号", e.getMessage());
        }
    }

    /**
     * 查询支付订单
     */
    public Map<String, Object> queryPayOrder(String orderNo) throws Exception {
        String url = getApiBaseUrl() + "/pay/transactions/out-trade-no/" + orderNo;
        return wxPayUtil.get(url);
    }

    /**
     * 关闭支付订单
     */
    public void closePayOrder(String orderNo) throws Exception {
        String url = getApiBaseUrl() + "/pay/transactions/out-trade-no/" + orderNo + "/close";
        Map<String, Object> params = new HashMap<>();
        params.put("mchid", wxPayConfig.getMchId());
        wxPayUtil.post(url, params);
    }

    /**
     * 申请退款
     */
    public Map<String, Object> refund(String orderNo, Long refundAmount, String reason) throws Exception {
        String url = getApiBaseUrl() + "/refund/domestic/refunds";
        Map<String, Object> params = new HashMap<>();
        params.put("out_trade_no", orderNo);
        params.put("out_refund_no", "refund_" + orderNo + "_" + System.currentTimeMillis());
        params.put("reason", reason);
        params.put("amount", new HashMap<String, Object>() {{
            put("refund", refundAmount);
            put("total", refundAmount);
            put("currency", "CNY");
        }});
        
        return wxPayUtil.post(url, params);
    }

    /**
     * 查询退款
     */
    public Map<String, Object> queryRefund(String refundNo) throws Exception {
        String url = getApiBaseUrl() + "/refund/domestic/refunds/" + refundNo;
        return wxPayUtil.get(url);
    }

    /**
     * 处理退款回调通知
     */
    @Transactional
    public void handleRefundNotify(
            String orderNo,
            String refundNo,
            String refundStatus,
            String successTime,
            String refundAmount,
            String totalAmount,
            String payerTotal,
            String payerRefund) {
        log.info("处理退款回调通知 - 订单号: {}, 退款单号: {}, 退款状态: {}", orderNo, refundNo, refundStatus);

        // 1. 获取订单信息
        Order order = orderService.getOrderByOrderNo(orderNo);
        if (order == null) {
            log.error("退款回调处理失败 - 订单不存在: {}", orderNo);
            throw new RuntimeException("订单不存在");
        }

        // 2. 根据退款状态处理订单
        switch (refundStatus) {
            case "SUCCESS":
                // 退款成功
                order.setRefundStatus(Order.RefundStatus.REFUNDED);
                order.setRefundTime(LocalDateTime.parse(successTime.replace("Z", "")));
                order.setRefundAmount(new BigDecimal(refundAmount).divide(new BigDecimal("100")));
                order.setStatus(Order.OrderStatus.REFUNDED);
                
                // 记录订单状态变更
                orderService.addOrderStatusHistory(
                    order,
                    Order.OrderStatus.REFUNDED,
                    "退款成功",
                    "SYSTEM",
                    OrderStatusHistory.OperationType.REFUND
                );
                break;

            case "CLOSED":
                // 退款关闭
                order.setRefundStatus(Order.RefundStatus.CLOSED);
                order.setStatus(Order.OrderStatus.CLOSED);
                
                // 记录订单状态变更
                orderService.addOrderStatusHistory(
                    order,
                    Order.OrderStatus.CLOSED,
                    "退款关闭",
                    "SYSTEM",
                    OrderStatusHistory.OperationType.REFUND_CLOSE
                );
                break;

            case "PROCESSING":
                // 退款处理中
                order.setRefundStatus(Order.RefundStatus.PROCESSING);
                
                // 记录订单状态变更
                orderService.addOrderStatusHistory(
                    order,
                    Order.OrderStatus.REFUNDING,
                    "退款处理中",
                    "SYSTEM",
                    OrderStatusHistory.OperationType.REFUND
                );
                break;

            case "ABNORMAL":
                // 退款异常
                order.setRefundStatus(Order.RefundStatus.FAILED);
                
                // 记录订单状态变更
                orderService.addOrderStatusHistory(
                    order,
                    Order.OrderStatus.REFUND_FAILED,
                    "退款异常",
                    "SYSTEM",
                    OrderStatusHistory.OperationType.REFUND
                );
                break;

            default:
                log.warn("未知的退款状态: {}", refundStatus);
                break;
        }

        // 3. 保存订单更新
        orderRepository.save(order);
        log.info("退款回调处理完成 - 订单号: {}, 退款状态: {}", orderNo, refundStatus);
    }

    /**
     * 获取API基础URL
     */
    private String getApiBaseUrl() {
        return wxPayConfig.getSandbox() ? SANDBOX_API_BASE_URL : API_BASE_URL;
    }
} 