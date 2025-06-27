-- mall_product 数据库测试数据
-- 顺序：product_category -> product_brand -> product

USE mall_product;

-- 清空现有数据（可选, 注意顺序）
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE product;
-- TRUNCATE TABLE product_brand;
-- TRUNCATE TABLE product_category;
-- SET FOREIGN_KEY_CHECKS = 1;


-- 1. 插入商品分类数据
INSERT INTO product_category (id, parent_id, name, level, icon, image, sort, status) VALUES
(1, 0, '新鲜水果', 1, 'fas fa-apple-alt', '/images/category/fruit.jpg', 1, 1),
(2, 0, '有机蔬菜', 1, 'fas fa-carrot', '/images/category/vegetable.jpg', 2, 1),
(3, 0, '海鲜水产', 1, 'fas fa-fish', '/images/category/seafood.jpg', 3, 1),
(4, 0, '肉禽蛋品', 1, 'fas fa-drumstick-bite', '/images/category/meat.jpg', 4, 1),
(5, 0, '粮油调味', 1, 'fas fa-wheat-awn', '/images/category/grain.jpg', 5, 1),
(6, 0, '乳品饮料', 1, 'fas fa-milk', '/images/category/dairy.jpg', 6, 1),
(7, 0, '休闲零食', 1, 'fas fa-cookie-bite', '/images/category/snack.jpg', 7, 1),
(8, 0, '酒水茶饮', 1, 'fas fa-wine-bottle', '/images/category/drink.jpg', 8, 1);

-- 2. 插入商品品牌数据
INSERT INTO product_brand (id, name, logo, description, sort, status) VALUES
(1, '农夫山泉', '/images/brand/nongfu.png', '天然矿泉水品牌', 1, 1),
(2, '蒙牛', '/images/brand/mengniu.png', '乳制品品牌', 2, 1),
(3, '伊利', '/images/brand/yili.png', '乳制品品牌', 3, 1),
(4, '三只松鼠', '/images/brand/3squirrels.png', '休闲零食品牌', 4, 1),
(5, '良品铺子', '/images/brand/liangpin.png', '休闲零食品牌', 5, 1),
(6, '海底捞', '/images/brand/haidilao.png', '火锅品牌', 6, 1),
(7, '统一', '/images/brand/tongyi.png', '饮料品牌', 7, 1),
(8, '康师傅', '/images/brand/kangshifu.png', '方便面品牌', 8, 1);

-- 3. 插入商品数据
INSERT INTO product (name, category_id, brand_id, price, original_price, description, detail, main_image, sales, stock, status, sort) VALUES
-- 新鲜水果类 (category_id=1)
('红富士苹果 5斤装', 1, 1, 29.90, 39.90, '新鲜红富士苹果，脆甜可口，营养丰富', '产地：山东烟台<br>规格：5斤装<br>保质期：常温7天，冷藏15天<br>特点：皮薄肉厚，脆甜多汁', 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400&h=400&fit=crop', 1250, 500, 1, 1),
('海南香蕉 3斤装', 1, 1, 15.80, 19.90, '海南特产香蕉，香甜软糯', '产地：海南<br>规格：3斤装<br>保质期：常温5天<br>特点：香甜软糯，营养丰富', 'https://images.unsplash.com/photo-1570913149827-d2ac84ab3f9a?w=400&h=400&fit=crop', 890, 300, 1, 2),

-- 有机蔬菜类 (category_id=2)
('有机生菜 500g', 2, 1, 8.90, 12.90, '有机认证生菜，新鲜脆嫩', '产地：本地<br>规格：500g<br>保质期：冷藏7天<br>特点：有机认证，新鲜脆嫩', 'https://images.unsplash.com/photo-1622205313162-be1d5716a43b?w=400&h=400&fit=crop', 450, 200, 1, 5),
('有机胡萝卜 1kg', 2, 1, 12.90, 16.90, '有机胡萝卜，营养丰富，口感清甜', '产地：本地<br>规格：1kg<br>保质期：冷藏15天<br>特点：有机认证，营养丰富', 'https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=400&h=400&fit=crop', 380, 150, 1, 6),

-- 海鲜水产类 (category_id=3)
('新鲜基围虾 500g', 3, 6, 68.90, 88.90, '新鲜基围虾，肉质鲜美，营养丰富', '产地：广东湛江<br>规格：500g<br>保质期：冷冻30天<br>特点：新鲜捕捞，肉质鲜美', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 180, 80, 1, 9),
('三文鱼刺身 200g', 3, 6, 45.90, 59.90, '新鲜三文鱼刺身，口感细腻', '产地：挪威<br>规格：200g<br>保质期：冷藏3天<br>特点：新鲜进口，口感细腻', 'https://images.unsplash.com/photo-1544943328-3c6c4c0c0c0c?w=400&h=400&fit=crop', 95, 50, 1, 10),

-- 肉禽蛋品类 (category_id=4)
('新鲜鸡蛋 30枚装', 4, 2, 25.90, 32.90, '新鲜土鸡蛋，营养丰富', '产地：本地<br>规格：30枚装<br>保质期：冷藏30天<br>特点：土鸡蛋，营养丰富', 'https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=400&h=400&fit=crop', 680, 200, 1, 13),
('新鲜猪肉 1kg', 4, 2, 45.90, 55.90, '新鲜猪肉，肉质鲜嫩', '产地：本地<br>规格：1kg<br>保质期：冷藏7天<br>特点：新鲜屠宰，肉质鲜嫩', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 420, 150, 1, 14),

-- 粮油调味类 (category_id=5)
('五常大米 5kg', 5, 1, 89.90, 109.90, '五常大米，香糯可口', '产地：黑龙江五常<br>规格：5kg<br>保质期：12个月<br>特点：香糯可口，营养丰富', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 320, 100, 1, 17),
('金龙鱼食用油 5L', 5, 1, 65.90, 79.90, '金龙鱼食用油，健康营养', '产地：本地<br>规格：5L<br>保质期：18个月<br>特点：健康营养，品质保证', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 450, 150, 1, 18),

-- 乳品饮料类 (category_id=6)
('蒙牛纯牛奶 250ml*12盒', 6, 2, 35.90, 42.90, '蒙牛纯牛奶，营养丰富', '产地：内蒙古<br>规格：250ml*12盒<br>保质期：6个月<br>特点：营养丰富，品质保证', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 580, 200, 1, 21),
('伊利酸奶 100g*8杯', 6, 3, 18.90, 22.90, '伊利酸奶，口感细腻', '产地：内蒙古<br>规格：100g*8杯<br>保质期：21天<br>特点：口感细腻，营养丰富', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 420, 150, 1, 22),

-- 休闲零食类 (category_id=7)
('三只松鼠坚果大礼包', 7, 4, 68.90, 88.90, '三只松鼠坚果大礼包，营养美味', '产地：安徽芜湖<br>规格：1kg<br>保质期：12个月<br>特点：营养美味，品质保证', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 380, 120, 1, 25),
('良品铺子零食大礼包', 7, 5, 59.90, 75.90, '良品铺子零食大礼包，美味可口', '产地：湖北武汉<br>规格：800g<br>保质期：12个月<br>特点：美味可口，品质保证', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 420, 150, 1, 26),

-- 酒水茶饮类 (category_id=8)
('茅台飞天53度 500ml', 8, 1, 1499.00, 1599.00, '茅台飞天53度，酱香型白酒', '产地：贵州茅台<br>规格：500ml<br>保质期：长期<br>特点：酱香型白酒，品质保证', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 45, 20, 1, 29),
('青岛啤酒 330ml*24罐', 8, 1, 45.90, 55.90, '青岛啤酒，清爽怡人', '产地：山东青岛<br>规格：330ml*24罐<br>保质期：12个月<br>特点：清爽怡人，品质保证', 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop', 320, 100, 1, 31); 