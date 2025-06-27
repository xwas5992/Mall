-- mall_product 数据库测试数据
-- 表: product

-- 清空现有数据（可选）
-- DELETE FROM product;

-- 插入测试商品数据
INSERT INTO product (name, description, price, stock, brand, category, image_url, status, created_at, updated_at) VALUES
-- 新鲜水果类
('红富士苹果 5斤装', '新鲜红富士苹果，脆甜可口，营养丰富，产地山东烟台', 29.90, 500, '农夫山泉', '新鲜水果', 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('海南香蕉 3斤装', '海南特产香蕉，香甜软糯，营养丰富', 15.80, 300, '农夫山泉', '新鲜水果', 'https://images.unsplash.com/photo-1570913149827-d2ac84ab3f9a?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('新疆阿克苏苹果 4斤装', '新疆阿克苏冰糖心苹果，甜脆爽口，品质优良', 35.90, 200, '农夫山泉', '新鲜水果', 'https://images.unsplash.com/photo-1557800636-894a64c1696f?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('智利进口车厘子 1斤装', '智利进口车厘子，个大肉厚，甜度极高', 89.90, 100, '农夫山泉', '新鲜水果', 'https://images.unsplash.com/photo-1528821128474-27f963b062bf?w=400&h=400&fit=crop', 1, NOW(), NOW()),

-- 有机蔬菜类
('有机生菜 500g', '有机认证生菜，新鲜脆嫩，无农药残留', 8.90, 200, '有机农场', '有机蔬菜', 'https://images.unsplash.com/photo-1622205313162-be1d5716a43b?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('有机胡萝卜 1kg', '有机胡萝卜，营养丰富，口感清甜', 12.90, 150, '有机农场', '有机蔬菜', 'https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('有机西红柿 1kg', '有机西红柿，酸甜可口，营养丰富', 15.90, 180, '有机农场', '有机蔬菜', 'https://images.unsplash.com/photo-1546094096-0df4bcaaa337?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('有机黄瓜 500g', '有机黄瓜，新鲜脆嫩，清爽可口', 9.90, 120, '有机农场', '有机蔬菜', 'https://images.unsplash.com/photo-1449300079323-02e209d9d3a6?w=400&h=400&fit=crop', 1, NOW(), NOW()),

-- 海鲜水产类
('新鲜基围虾 500g', '新鲜基围虾，肉质鲜美，营养丰富，产地广东湛江', 68.90, 80, '海鲜世家', '海鲜水产', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('三文鱼刺身 200g', '新鲜三文鱼刺身，口感细腻，产地挪威', 45.90, 50, '海鲜世家', '海鲜水产', 'https://images.unsplash.com/photo-1544943328-3c6c4c0c0c0c?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('带鱼段 1kg', '新鲜带鱼段，肉质细嫩，产地浙江舟山', 35.90, 100, '海鲜世家', '海鲜水产', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('扇贝肉 500g', '新鲜扇贝肉，鲜美可口，产地山东青岛', 42.90, 70, '海鲜世家', '海鲜水产', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),

-- 肉禽蛋品类
('新鲜鸡蛋 30枚装', '新鲜土鸡蛋，营养丰富，无激素添加', 25.90, 200, '农家乐', '肉禽蛋品', 'https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('新鲜猪肉 1kg', '新鲜猪肉，肉质鲜嫩，产地本地', 45.90, 150, '农家乐', '肉禽蛋品', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('新鲜鸡肉 1kg', '新鲜鸡肉，肉质细嫩，产地本地', 32.90, 120, '农家乐', '肉禽蛋品', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('新鲜牛肉 500g', '新鲜牛肉，肉质鲜美，产地本地', 68.90, 80, '农家乐', '肉禽蛋品', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),

-- 粮油调味类
('五常大米 5kg', '五常大米，香糯可口，产地黑龙江五常', 89.90, 100, '金龙鱼', '粮油调味', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('金龙鱼食用油 5L', '金龙鱼食用油，健康营养，品质保证', 65.90, 150, '金龙鱼', '粮油调味', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('海天酱油 500ml', '海天酱油，味道鲜美，产地广东佛山', 12.90, 200, '海天', '粮油调味', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('太太乐鸡精 200g', '太太乐鸡精，提鲜增味，产地上海', 8.90, 180, '太太乐', '粮油调味', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),

-- 乳品饮料类
('蒙牛纯牛奶 250ml*12盒', '蒙牛纯牛奶，营养丰富，产地内蒙古', 35.90, 200, '蒙牛', '乳品饮料', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('伊利酸奶 100g*8杯', '伊利酸奶，口感细腻，产地内蒙古', 18.90, 150, '伊利', '乳品饮料', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('农夫山泉矿泉水 550ml*24瓶', '农夫山泉矿泉水，天然健康，产地浙江千岛湖', 28.90, 300, '农夫山泉', '乳品饮料', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('统一绿茶 500ml*15瓶', '统一绿茶，清爽解腻，产地本地', 25.90, 250, '统一', '乳品饮料', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),

-- 休闲零食类
('三只松鼠坚果大礼包', '三只松鼠坚果大礼包，营养美味，产地安徽芜湖', 68.90, 120, '三只松鼠', '休闲零食', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('良品铺子零食大礼包', '良品铺子零食大礼包，美味可口，产地湖北武汉', 59.90, 150, '良品铺子', '休闲零食', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('薯片大礼包 混合口味', '薯片大礼包，多种口味，产地本地', 25.90, 200, '康师傅', '休闲零食', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('巧克力夹心饼干', '巧克力夹心饼干，香甜可口，产地本地', 15.90, 250, '康师傅', '休闲零食', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),

-- 酒水茶饮类
('茅台飞天53度 500ml', '茅台飞天53度，酱香型白酒，产地贵州茅台', 1499.00, 20, '茅台', '酒水茶饮', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('五粮液52度 500ml', '五粮液52度，浓香型白酒，产地四川宜宾', 899.00, 30, '五粮液', '酒水茶饮', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('青岛啤酒 330ml*24罐', '青岛啤酒，清爽怡人，产地山东青岛', 45.90, 100, '青岛啤酒', '酒水茶饮', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW()),
('西湖龙井茶 100g', '西湖龙井茶，清香怡人，产地浙江杭州', 128.00, 80, '西湖龙井', '酒水茶饮', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 1, NOW(), NOW());

-- 说明：
-- 1. 创建了32个商品，涵盖8个主要分类
-- 2. 价格范围：8.90 - 1499.00元，覆盖不同消费层次
-- 3. 库存设置合理，范围：20 - 500件
-- 4. 所有商品都是上架状态 (status = 1)
-- 5. 品牌信息真实可信
-- 6. 图片使用Unsplash占位图片
-- 7. 时间戳使用NOW()函数自动生成 