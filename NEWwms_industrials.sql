-- 禁用外键检查，方便数据插入
SET FOREIGN_KEY_CHECKS = 0;

-- 清空现有数据
TRUNCATE TABLE inventory_alerts;
TRUNCATE TABLE inventory_alert_settings;
TRUNCATE TABLE inventory_transactions;
TRUNCATE TABLE inventory;
TRUNCATE TABLE materials;
TRUNCATE TABLE warehouses;
TRUNCATE TABLE material_categories;
TRUNCATE TABLE inventory_transaction_types;

-- 1. 重新插入出入库类型数据
INSERT INTO inventory_transaction_types (type_code, type_name, direction, description) VALUES
                                                                                           ('PURCHASE_IN', '采购入库', 'IN', '采购物料入库'),
                                                                                           ('PRODUCTION_IN', '生产入库', 'IN', '生产成品入库'),
                                                                                           ('RETURN_IN', '退货入库', 'IN', '客户退货入库'),
                                                                                           ('TRANSFER_IN', '调拨入库', 'IN', '从其他仓库调拨入库'),
                                                                                           ('ADJUST_IN', '调整入库', 'IN', '库存调整入库'),
                                                                                           ('SALES_OUT', '销售出库', 'OUT', '销售订单出库'),
                                                                                           ('PRODUCTION_OUT', '生产领料', 'OUT', '生产领用出库'),
                                                                                           ('RETURN_OUT', '退货出库', 'OUT', '向供应商退货出库'),
                                                                                           ('TRANSFER_OUT', '调拨出库', 'OUT', '调拨到其他仓库出库'),
                                                                                           ('ADJUST_OUT', '调整出库', 'OUT', '库存调整出库'),
                                                                                           ('SCRAP_OUT', '报废出库', 'OUT', '物料报废出库');

-- 2. 物料分类数据（与之前相同）
INSERT INTO material_categories (parent_id, category_code, category_name, description, sort_order) VALUES
                                                                                                       (NULL, 'RAW_MATERIAL', '原材料', '生产所需的原始材料', 1),
                                                                                                       (NULL, 'SEMI_PRODUCT', '半成品', '未完成最终生产的产品', 2),
                                                                                                       (NULL, 'FINISHED_PRODUCT', '成品', '可直接销售的最终产品', 3),
                                                                                                       (NULL, 'PACKAGING', '包装材料', '产品包装用材料', 4),
                                                                                                       (NULL, 'ACCESSORY', '辅助材料', '生产辅助用材料', 5),
                                                                                                       (1, 'METAL', '金属材料', '各类金属原材料', 1),
                                                                                                       (1, 'PLASTIC', '塑料原料', '各类塑料原材料', 2),
                                                                                                       (1, 'ELECTRONIC', '电子元件', '各类电子元件', 3),
                                                                                                       (1, 'CHEMICAL', '化工原料', '各类化工原材料', 4),
                                                                                                       (3, 'ELECTRONICS', '电子产品', '电子类成品', 1),
                                                                                                       (3, 'MECHANICAL', '机械产品', '机械类成品', 2),
                                                                                                       (3, 'CONSUMABLE', '消费品', '直接消费的成品', 3);

-- 3. 仓库数据（与之前相同）
INSERT INTO warehouses (warehouse_code, warehouse_name, location, contact_person, contact_phone, description) VALUES
                                                                                                                  ('WH001', '一号主仓库', '厂区A栋1楼', '张三', '13800138001', '主要存放成品和半成品'),
                                                                                                                  ('WH002', '二号原料仓', '厂区B栋2楼', '李四', '13900139001', '存放各类原材料'),
                                                                                                                  ('WH003', '三号辅助仓', '厂区C栋1楼', '王五', '13700137001', '存放包装材料和辅助材料'),
                                                                                                                  ('WH004', '四号临时仓', '厂区D栋3楼', '赵六', '13600136001', '临时存储和周转');

-- 4. 物料数据 (80条，与之前相同)
-- 4.1 金属材料 (10条)
INSERT INTO materials (material_code, material_name, category_id, specification, unit, barcode, brand, supplier, default_warehouse_id) VALUES
                                                                                                                                           ('MAT-M-001', '碳钢板', (SELECT id FROM material_categories WHERE category_code='METAL'), '1.2mm*1220mm*2440mm', '张', '6901234560001', '宝钢', '上海钢材供应商', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-M-002', '不锈钢板', (SELECT id FROM material_categories WHERE category_code='METAL'), '1.5mm*1220mm*2440mm', '张', '6901234560002', '太钢', '上海钢材供应商', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-M-003', '铝合金板', (SELECT id FROM material_categories WHERE category_code='METAL'), '2.0mm*1220mm*2440mm', '张', '6901234560003', '西南铝', '广州铝材供应商', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-M-004', '圆钢', (SELECT id FROM material_categories WHERE category_code='METAL'), 'Φ20mm*3000mm', '根', '6901234560004', '宝钢', '北京金属材料公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-M-005', '角钢', (SELECT id FROM material_categories WHERE category_code='METAL'), '50*50*5mm', '根', '6901234560005', '唐钢', '北京金属材料公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-M-006', '铜管', (SELECT id FROM material_categories WHERE category_code='METAL'), 'Φ15mm*1m', '根', '6901234560006', '海亮', '上海铜管厂', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-M-007', '镀锌板', (SELECT id FROM material_categories WHERE category_code='METAL'), '0.8mm*1220mm*2440mm', '张', '6901234560007', '鞍钢', '天津钢材贸易公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-M-008', '铁丝', (SELECT id FROM material_categories WHERE category_code='METAL'), 'Φ2.5mm', '卷', '6901234560008', '首钢', '河北金属制品厂', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-M-009', '钢板网', (SELECT id FROM material_categories WHERE category_code='METAL'), '1m*2m', '张', '6901234560009', '安平县', '河北丝网厂', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-M-010', '钢筋', (SELECT id FROM material_categories WHERE category_code='METAL'), 'Φ12mm*9m', '根', '6901234560010', '武钢', '武汉钢铁贸易公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002'));

-- 4.2 塑料原料 (10条)
INSERT INTO materials (material_code, material_name, category_id, specification, unit, barcode, brand, supplier, default_warehouse_id) VALUES
                                                                                                                                           ('MAT-P-001', '聚乙烯(PE)', (SELECT id FROM material_categories WHERE category_code='PLASTIC'), '颗粒状', '袋', '6901234560011', '燕山石化', '北京化工原料公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-P-002', '聚丙烯(PP)', (SELECT id FROM material_categories WHERE category_code='PLASTIC'), '颗粒状', '袋', '6901234560012', '扬子石化', '南京化工贸易公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-P-003', '聚氯乙烯(PVC)', (SELECT id FROM material_categories WHERE category_code='PLASTIC'), '颗粒状', '袋', '6901234560013', '齐鲁石化', '山东化工原料厂', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-P-004', 'ABS树脂', (SELECT id FROM material_categories WHERE category_code='PLASTIC'), '颗粒状', '袋', '6901234560014', '奇美', '台湾化学工业公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-P-005', '聚苯乙烯(PS)', (SELECT id FROM material_categories WHERE category_code='PLASTIC'), '颗粒状', '袋', '6901234560015', '上海石化', '上海化工原料公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-P-006', '聚碳酸酯(PC)', (SELECT id FROM material_categories WHERE category_code='PLASTIC'), '颗粒状', '袋', '6901234560016', '拜耳', '德国化工中国分公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-P-007', '尼龙6(PA6)', (SELECT id FROM material_categories WHERE category_code='PLASTIC'), '颗粒状', '袋', '6901234560017', '巴斯夫', '德国化工中国分公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-P-008', '聚甲醛(POM)', (SELECT id FROM material_categories WHERE category_code='PLASTIC'), '颗粒状', '袋', '6901234560018', '杜邦', '美国化工中国分公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-P-009', '聚四氟乙烯(PTFE)', (SELECT id FROM material_categories WHERE category_code='PLASTIC'), '粉末状', '瓶', '6901234560019', '3M', '美国3M中国公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-P-010', '聚氨酯(PU)', (SELECT id FROM material_categories WHERE category_code='PLASTIC'), '液体', '桶', '6901234560020', '亨斯迈', '美国化工中国分公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002'));

-- 4.3 电子元件 (15条)
INSERT INTO materials (material_code, material_name, category_id, specification, unit, barcode, brand, supplier, default_warehouse_id) VALUES
                                                                                                                                           ('MAT-E-001', '电阻', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), '1kΩ 0.25W', '个', '6901234560021', 'Yageo', '深圳电子元件公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-002', '电容', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), '10μF 50V', '个', '6901234560022', 'Murata', '广州电子配件厂', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-003', '电感', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), '10mH', '个', '6901234560023', 'TDK', '上海电子元件贸易公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-004', '二极管', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), '1N4007', '个', '6901234560024', 'ON Semiconductor', '北京电子科技公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-005', '三极管', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), '2N3904', '个', '6901234560025', 'Fairchild', '深圳半导体公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-006', '集成电路', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), 'LM358', '个', '6901234560026', 'TI', '德州仪器中国分公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-007', '微控制器', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), 'STM32F103', '个', '6901234560027', 'STMicroelectronics', '意法半导体中国公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-008', '传感器', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), '温度传感器', '个', '6901234560028', 'Bosch', '博世中国分公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-009', '继电器', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), '5V 10A', '个', '6901234560029', 'Omron', '欧姆龙中国分公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-010', '连接器', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), 'USB Type-C', '个', '6901234560030', 'Molex', '莫仕连接器中国公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-011', 'LED', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), '5mm 红色', '个', '6901234560031', 'Cree', '科锐中国分公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-012', '保险丝', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), '2A 250V', '个', '6901234560032', 'Littlefuse', '力特电子中国公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-013', '晶振', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), '16MHz', '个', '6901234560033', 'TXC', '台湾晶技中国公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-014', '开关', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), '按钮开关', '个', '6901234560034', 'Alps', '阿尔卑斯电子中国公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-E-015', '电池', (SELECT id FROM material_categories WHERE category_code='ELECTRONIC'), 'CR2032 3V', '个', '6901234560035', 'Panasonic', '松下电器中国公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002'));

-- 4.4 化工原料 (10条)
INSERT INTO materials (material_code, material_name, category_id, specification, unit, barcode, brand, supplier, default_warehouse_id) VALUES
                                                                                                                                           ('MAT-C-001', '丙酮', (SELECT id FROM material_categories WHERE category_code='CHEMICAL'), '分析纯 500ml', '瓶', '6901234560036', '国药集团', '上海化学试剂厂', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-C-002', '乙醇', (SELECT id FROM material_categories WHERE category_code='CHEMICAL'), '95% 500ml', '瓶', '6901234560037', '国药集团', '北京化学试剂公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-C-003', '盐酸', (SELECT id FROM material_categories WHERE category_code='CHEMICAL'), '36% 500ml', '瓶', '6901234560038', '西陇科学', '广州化学试剂厂', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-C-004', '氢氧化钠', (SELECT id FROM material_categories WHERE category_code='CHEMICAL'), '分析纯 500g', '瓶', '6901234560039', '阿拉丁', '上海阿拉丁生化科技', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-C-005', '甘油', (SELECT id FROM material_categories WHERE category_code='CHEMICAL'), '分析纯 500ml', '瓶', '6901234560040', 'Sigma-Aldrich', '西格玛奥德里奇中国公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-C-006', '油漆', (SELECT id FROM material_categories WHERE category_code='CHEMICAL'), '红色 1L', '桶', '6901234560041', '立邦', '立邦涂料中国公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-C-007', '胶水', (SELECT id FROM material_categories WHERE category_code='CHEMICAL'), '瞬间胶 50ml', '支', '6901234560042', '乐泰', '汉高中国分公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-C-008', '清洗剂', (SELECT id FROM material_categories WHERE category_code='CHEMICAL'), '工业用 5L', '桶', '6901234560043', '3M', '美国3M中国公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-C-009', '润滑油', (SELECT id FROM material_categories WHERE category_code='CHEMICAL'), '机械用 1L', '瓶', '6901234560044', '壳牌', '壳牌中国分公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002')),
                                                                                                                                           ('MAT-C-010', '树脂', (SELECT id FROM material_categories WHERE category_code='CHEMICAL'), '环氧树脂 1kg', '袋', '6901234560045', '陶氏', '陶氏化学中国分公司', (SELECT id FROM warehouses WHERE warehouse_code='WH002'));

-- 4.5 电子产品 (15条)
INSERT INTO materials (material_code, material_name, category_id, specification, unit, barcode, brand, supplier, default_warehouse_id) VALUES
                                                                                                                                           ('MAT-F-001', '智能手机', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '6.7英寸 256GB', '台', '6901234560046', '华为', '华为技术有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-002', '笔记本电脑', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '15.6英寸 i7', '台', '6901234560047', '联想', '联想集团有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-003', '平板电脑', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '10.9英寸 128GB', '台', '6901234560048', '苹果', '苹果电脑贸易(上海)有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-004', '智能手表', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '运动款', '只', '6901234560049', '小米', '小米科技有限责任公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-005', '蓝牙耳机', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '无线降噪', '副', '6901234560050', '索尼', '索尼(中国)有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-006', '移动电源', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '20000mAh', '个', '6901234560051', 'Anker', '安克创新科技股份有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-007', '智能音箱', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '语音控制', '台', '6901234560052', '天猫精灵', '阿里巴巴集团', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-008', '路由器', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '千兆双频', '台', '6901234560053', 'TP-Link', '普联技术有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-009', '监控摄像头', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '1080P 夜视', '个', '6901234560054', '海康威视', '杭州海康威视数字技术股份有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-010', '打印机', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '彩色喷墨', '台', '6901234560055', '惠普', '惠普(中国)有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-011', '键盘', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '机械轴', '个', '6901234560056', '罗技', '罗技(中国)科技有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-012', '鼠标', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '无线', '个', '6901234560057', '雷蛇', '雷蛇电脑周边(深圳)有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-013', '充电宝', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '10000mAh', '个', '6901234560058', '罗马仕', '深圳罗马仕科技有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-014', 'U盘', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '128GB USB3.0', '个', '6901234560059', '金士顿', '金士顿科技(上海)有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001')),
                                                                                                                                           ('MAT-F-015', '存储卡', (SELECT id FROM material_categories WHERE category_code='ELECTRONICS'), '64GB microSD', '个', '6901234560060', '闪迪', '闪迪科技(上海)有限公司', (SELECT id FROM warehouses WHERE warehouse_code='WH001'));

-- 4.6 包装材料 (10条)
INSERT INTO materials (material_code, material_name, category_id, specification, unit, barcode, brand, supplier, default_warehouse_id) VALUES
                                                                                                                                           ('MAT-PK-001', '纸箱', (SELECT id FROM material_categories WHERE category_code='PACKAGING'), '30*20*20cm', '个', '6901234560061', '玖龙', '东莞纸箱厂', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-PK-002', '塑料袋', (SELECT id FROM material_categories WHERE category_code='PACKAGING'), '20*30cm', '个', '6901234560062', '聚乙烯', '深圳塑料制品厂', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-PK-003', '泡沫板', (SELECT id FROM material_categories WHERE category_code='PACKAGING'), '1m*2m*5cm', '张', '6901234560063', 'EPS', '广州泡沫制品公司', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-PK-004', '胶带', (SELECT id FROM material_categories WHERE category_code='PACKAGING'), '48mm*50m', '卷', '6901234560064', '3M', '美国3M中国公司', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-PK-005', '缠绕膜', (SELECT id FROM material_categories WHERE category_code='PACKAGING'), '50cm*100m', '卷', '6901234560065', '拉伸膜', '上海包装材料公司', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-PK-006', '标签', (SELECT id FROM material_categories WHERE category_code='PACKAGING'), '5*8cm', '张', '6901234560066', '不干胶', '北京标签印刷厂', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-PK-007', '打包带', (SELECT id FROM material_categories WHERE category_code='PACKAGING'), '16mm*100m', '卷', '6901234560067', 'PET', '天津包装材料厂', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-PK-008', '气泡膜', (SELECT id FROM material_categories WHERE category_code='PACKAGING'), '50cm*10m', '卷', '6901234560068', '气垫膜', '南京包装制品公司', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-PK-009', '封箱器', (SELECT id FROM material_categories WHERE category_code='PACKAGING'), '手动', '个', '6901234560069', '胶带机', '青岛包装机械厂', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-PK-010', '打包机', (SELECT id FROM material_categories WHERE category_code='PACKAGING'), '半自动', '台', '6901234560070', '包装机', '上海包装机械厂', (SELECT id FROM warehouses WHERE warehouse_code='WH003'));

-- 4.7 辅助材料 (10条)
INSERT INTO materials (material_code, material_name, category_id, specification, unit, barcode, brand, supplier, default_warehouse_id) VALUES
                                                                                                                                           ('MAT-A-001', '螺丝刀', (SELECT id FROM material_categories WHERE category_code='ACCESSORY'), '十字 5mm', '把', '6901234560071', '世达', '上海工具公司', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-A-002', '扳手', (SELECT id FROM material_categories WHERE category_code='ACCESSORY'), '10mm', '把', '6901234560072', '得力', '宁波工具厂', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-A-003', '钳子', (SELECT id FROM material_categories WHERE category_code='ACCESSORY'), '尖嘴', '把', '6901234560073', '张小泉', '杭州剪刀厂', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-A-004', '卷尺', (SELECT id FROM material_categories WHERE category_code='ACCESSORY'), '3m', '个', '6901234560074', '钢卷尺', '北京测量工具公司', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-A-005', '手套', (SELECT id FROM material_categories WHERE category_code='ACCESSORY'), '劳保', '双', '6901234560075', '防护手套', '天津劳保用品厂', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-A-006', '口罩', (SELECT id FROM material_categories WHERE category_code='ACCESSORY'), '一次性', '个', '6901234560076', '医用口罩', '广州医疗器械厂', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-A-007', '安全帽', (SELECT id FROM material_categories WHERE category_code='ACCESSORY'), 'ABS材质', '顶', '6901234560077', '安全头盔', '上海安全用品公司', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-A-008', '清洁布', (SELECT id FROM material_categories WHERE category_code='ACCESSORY'), '无尘', '包', '6901234560078', '无尘布', '深圳洁净用品厂', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-A-009', '垃圾袋', (SELECT id FROM material_categories WHERE category_code='ACCESSORY'), '50*60cm', '包', '6901234560079', '黑色垃圾袋', '武汉塑料制品厂', (SELECT id FROM warehouses WHERE warehouse_code='WH003')),
                                                                                                                                           ('MAT-A-010', '拖把', (SELECT id FROM material_categories WHERE category_code='ACCESSORY'), '平板式', '把', '6901234560080', '清洁工具', '成都日用品公司', (SELECT id FROM warehouses WHERE warehouse_code='WH003'));

-- 5. 初始化库存数据（与之前相同）
INSERT INTO inventory (material_id, warehouse_id, quantity, available_quantity, locked_quantity, last_stocktake_time)
SELECT
    m.id,
    m.default_warehouse_id,
    -- 随机生成库存数量，大部分在安全范围内，10个左右会低于预警值
    CASE
        WHEN m.material_code IN ('MAT-M-003', 'MAT-P-005', 'MAT-E-007', 'MAT-C-002', 'MAT-F-004', 'MAT-PK-006', 'MAT-A-008', 'MAT-E-012', 'MAT-F-009', 'MAT-P-008') THEN FLOOR(RAND() * 10)  -- 低库存物料
        ELSE FLOOR(RAND() * 100) + 20  -- 正常库存物料
        END,
    -- 可用库存 = 总库存 - 随机锁定库存
    CASE
        WHEN m.material_code IN ('MAT-M-003', 'MAT-P-005', 'MAT-E-007', 'MAT-C-002', 'MAT-F-004', 'MAT-PK-006', 'MAT-A-008', 'MAT-E-012', 'MAT-F-009', 'MAT-P-008') THEN FLOOR(RAND() * 10)
        ELSE FLOOR(RAND() * 100) + 10
        END,
    -- 锁定库存
    CASE
        WHEN m.material_code IN ('MAT-M-003', 'MAT-P-005', 'MAT-E-007', 'MAT-C-002', 'MAT-F-004', 'MAT-PK-006', 'MAT-A-008', 'MAT-E-012', 'MAT-F-009', 'MAT-P-008') THEN 0
        ELSE FLOOR(RAND() * 10) + 5
        END,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)  -- 随机生成最近30天内的盘点时间
FROM materials m;

-- 6. 库存预警设置（与之前相同）
INSERT INTO inventory_alert_settings (material_id, warehouse_id, min_stock, max_stock, is_enabled)
SELECT
    m.id,
    m.default_warehouse_id,
    -- 设置最低库存阈值，金属和塑料类较高，小零件较低
    CASE
        WHEN c.category_code IN ('METAL', 'PLASTIC') THEN 50
        WHEN c.category_code IN ('ELECTRONIC', 'CHEMICAL') THEN 30
        WHEN c.category_code IN ('ELECTRONICS', 'MECHANICAL') THEN 20
        WHEN c.category_code IN ('PACKAGING', 'ACCESSORY') THEN 15
        ELSE 25
        END,
    -- 设置最高库存阈值
    CASE
        WHEN c.category_code IN ('METAL', 'PLASTIC') THEN 200
        WHEN c.category_code IN ('ELECTRONIC', 'CHEMICAL') THEN 100
        WHEN c.category_code IN ('ELECTRONICS', 'MECHANICAL') THEN 80
        WHEN c.category_code IN ('PACKAGING', 'ACCESSORY') THEN 150
        ELSE 120
        END,
    TRUE  -- 启用预警
FROM materials m
         JOIN material_categories c ON m.category_id = c.id;

-- 7. 生成出入库记录（修正语法错误）
DELIMITER $$
CREATE PROCEDURE GenerateInventoryTransactions()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE mat_id BIGINT;
    DECLARE wh_id BIGINT;
    DECLARE current_qty INT;
    DECLARE trans_count INT;
    DECLARE i INT;
    DECLARE trans_type_id BIGINT;
    DECLARE qty INT;
    DECLARE trans_date DATETIME;
    DECLARE direction VARCHAR(10);

    -- 游标遍历所有库存记录
    DECLARE cur CURSOR FOR
        SELECT material_id, warehouse_id, quantity FROM inventory;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO mat_id, wh_id, current_qty;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- 为每个物料生成2-5条出入库记录
        SET trans_count = FLOOR(RAND() * 4) + 2;
        SET i = 1;

        -- 初始库存
        SET @initial_qty = 0;

        WHILE i <= trans_count DO
                -- 随机选择交易类型
                SELECT id, direction INTO trans_type_id, direction
                FROM inventory_transaction_types
                ORDER BY RAND()
                LIMIT 1;

                -- 根据交易方向生成数量
                IF direction = 'IN' THEN
                    SET qty = FLOOR(RAND() * 50) + 10;
                ELSE
                    -- 出库数量不能超过当前库存
                    SET qty = -FLOOR(RAND() * LEAST(50, @initial_qty / 2 + 1)) - 5;
                    -- 确保不出现负库存
                    IF @initial_qty + qty < 0 THEN
                        SET qty = -@initial_qty;
                    END IF;
                END IF;

                -- 修正：将天数转换为小时后再加上随机小时数，避免语法错误
                SET trans_date = DATE_SUB(NOW(), INTERVAL (FLOOR(RAND() * 90) * 24 + FLOOR(RAND() * 24)) HOUR);

                -- 生成唯一交易单号
                SET @trans_no = CONCAT('TR', DATE_FORMAT(trans_date, '%Y%m%d'),
                                       FLOOR(RAND() * 1000000));

                -- 获取物料代码
                SELECT material_code INTO @mat_code FROM materials WHERE id = mat_id;

                -- 插入交易记录
                INSERT INTO inventory_transactions (
                    transaction_no, transaction_type_id, material_id, warehouse_id,
                    quantity, unit_cost, total_cost, reference_no, transaction_time,
                    notes, created_by
                ) VALUES (
                             @trans_no, trans_type_id, mat_id, wh_id, qty,
                             FLOOR(RAND() * 100) + 10, -- 随机单位成本
                             FLOOR(RAND() * 100) + 10 * ABS(qty), -- 总成本
                             CONCAT('REF', FLOOR(RAND() * 100000)), -- 参考单号
                             trans_date,
                             CONCAT('Auto-generated transaction for ', @mat_code), -- 备注
                             'system' -- 创建人
                         );

                -- 更新初始库存
                SET @initial_qty = @initial_qty + qty;

                SET i = i + 1;
            END WHILE;
    END LOOP;

    CLOSE cur;
END$$
DELIMITER ;

-- 调用存储过程生成出入库记录
CALL GenerateInventoryTransactions();

-- 删除存储过程
DROP PROCEDURE IF EXISTS GenerateInventoryTransactions;

-- 8. 手动触发库存预警
INSERT INTO inventory_alerts (material_id, warehouse_id, alert_type, current_quantity, threshold_value)
SELECT
    i.material_id,
    i.warehouse_id,
    'LOW_STOCK',
    i.quantity,
    s.min_stock
FROM inventory i
         JOIN inventory_alert_settings s ON i.material_id = s.material_id
    AND (i.warehouse_id = s.warehouse_id OR s.warehouse_id IS NULL)
WHERE i.quantity <= s.min_stock
ON DUPLICATE KEY UPDATE
                     current_quantity = i.quantity,
                     threshold_value = s.min_stock;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示生成的预警数量
SELECT COUNT(*) AS low_stock_alert_count FROM inventory_alerts WHERE alert_type = 'LOW_STOCK';
