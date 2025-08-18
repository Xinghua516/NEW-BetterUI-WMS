-- 修复后的仓库管理系统数据初始化脚本

-- 禁用外键检查，便于初始化
SET FOREIGN_KEY_CHECKS = 0;

-- 清空现有数据
TRUNCATE TABLE inventory_alerts;
TRUNCATE TABLE inventory_alert_settings;
TRUNCATE TABLE inventory_transactions;
TRUNCATE TABLE inventory;
TRUNCATE TABLE materials;
TRUNCATE TABLE material_batches;
TRUNCATE TABLE warehouses;
TRUNCATE TABLE material_categories;
TRUNCATE TABLE inventory_transaction_types;

-- 1. 物料分类表（多级分类）
INSERT INTO material_categories (parent_id, category_code, category_name, description, sort_order, status) VALUES
-- 金属材料类
(NULL, 'METAL', '金属材料', '工业用各类金属原材料', 1, 'ACTIVE'),
(1, 'METAL-FERROUS', '黑色金属', '铁、钢等含铁金属材料', 1, 'ACTIVE'),
(1, 'METAL-NONFERROUS', '有色金属', '铝、铜、锌等非铁金属', 2, 'ACTIVE'),
(2, 'METAL-STEEL', '钢材', '各类钢材及制品', 1, 'ACTIVE'),
(2, 'METAL-CASTIRON', '铸铁', '灰铸铁、球墨铸铁等', 2, 'ACTIVE'),
(3, 'METAL-AL', '铝及铝合金', '纯铝及铝合金材料', 1, 'ACTIVE'),
(3, 'METAL-CU', '铜及铜合金', '纯铜及铜合金材料', 2, 'ACTIVE'),
(3, 'METAL-ZN', '锌及锌合金', '纯锌及锌合金材料', 3, 'ACTIVE'),

-- 电子电气类
(NULL, 'ELECTRIC', '电子电气', '电气元件及线缆', 2, 'ACTIVE'),
(9, 'ELEC-COMP', '电子元件', '各类电子元器件', 1, 'ACTIVE'),
(9, 'ELEC-CABLE', '电线电缆', '各类导线及电缆', 2, 'ACTIVE'),
(9, 'ELEC-POWER', '电力器件', '接触器、继电器等', 3, 'ACTIVE'),
(10, 'ELEC-IC', '集成电路', '芯片、单片机等', 1, 'ACTIVE'),
(10, 'ELEC-SENSOR', '传感器', '各类检测传感器', 2, 'ACTIVE'),
(10, 'ELEC-CONN', '连接器', '插头、插座等', 3, 'ACTIVE'),

-- 机械零件类
(NULL, 'MECHANICAL', '机械零件', '各类机械零部件', 3, 'ACTIVE'),
(16, 'MECH-STANDARD', '标准件', '螺栓、螺母等标准件', 1, 'ACTIVE'),
(16, 'MECH-SHAFT', '轴类', '传动轴、主轴等', 2, 'ACTIVE'),
(16, 'MECH-GEAR', '齿轮类', '齿轮、齿条等', 3, 'ACTIVE'),
(16, 'MECH-BEARING', '轴承', '各类轴承', 4, 'ACTIVE'),
(16, 'MECH-FITTING', '连接件', '法兰、接头等', 5, 'ACTIVE'),

-- 化工及耗材类
(NULL, 'CHEMICAL', '化工耗材', '化工原料及消耗品', 4, 'ACTIVE'),
(22, 'CHEM-RAW', '化工原料', '基础化工原料', 1, 'ACTIVE'),
(22, 'CHEM-ADHESIVE', '胶粘剂', '胶水、密封胶等', 2, 'ACTIVE'),
(22, 'CHEM-LUBRICANT', '润滑剂', '润滑油、脂等', 3, 'ACTIVE'),
(22, 'CHEM-CLEANER', '清洗剂', '各类工业清洗剂', 4, 'ACTIVE'),

-- 工具设备类
(NULL, 'TOOLING', '工具设备', '生产工具及设备', 5, 'ACTIVE'),
(27, 'TOOL-CUTTING', '切削工具', '刀具、刀片等', 1, 'ACTIVE'),
(27, 'TOOL-MEASURE', '测量工具', '量具、仪表等', 2, 'ACTIVE'),
(27, 'TOOL-POWER', '电动工具', '电钻、砂轮机等', 3, 'ACTIVE'),
(27, 'TOOL-HAND', '手动工具', '扳手、螺丝刀等', 4, 'ACTIVE');

-- 2. 仓库信息表
INSERT INTO warehouses (warehouse_code, warehouse_name, location, contact_person, contact_phone, status, description) VALUES
                                                                                                                          ('WH-METAL', '金属材料仓库', '厂区A1栋', '张明', '13800138001', 'ACTIVE', '存放各类金属原材料'),
                                                                                                                          ('WH-ELEC', '电子元件仓库', '厂区A2栋', '李华', '13800138002', 'ACTIVE', '存放电子元件及电气产品'),
                                                                                                                          ('WH-MECH', '机械零件仓库', '厂区B1栋', '王强', '13800138003', 'ACTIVE', '存放机械零部件及标准件'),
                                                                                                                          ('WH-CHEM', '化工品仓库', '厂区C1栋', '赵伟', '13800138004', 'ACTIVE', '存放化工原料及耗材(通风防爆)'),
                                                                                                                          ('WH-TOOL', '工具设备仓库', '厂区D1栋', '刘杰', '13800138005', 'ACTIVE', '存放各类生产工具及设备');

-- 3. 物料信息表 (创建80条物料记录)
INSERT INTO materials (material_code, material_name, category_id, specification, unit, barcode, brand, supplier, status, default_warehouse_id) VALUES
-- 金属材料 (1-20)
('MET-001', 'Q235钢板', 4, '2000×1000×5mm', '张', '690010010001', '首钢', '北京钢铁贸易公司', 'ACTIVE', 1),
('MET-002', 'Q345B钢板', 4, '2500×1250×10mm', '张', '690010010002', '鞍钢', '鞍山钢铁有限公司', 'ACTIVE', 1),
('MET-003', '45#圆钢', 4, 'φ50×6000mm', '根', '690010010003', '宝钢', '上海金属材料公司', 'ACTIVE', 1),
('MET-004', '20#无缝钢管', 4, 'φ32×3×6000mm', '根', '690010010004', '天津钢管', '天津无缝钢管厂', 'ACTIVE', 1),
('MET-005', 'T8碳素工具钢', 4, 'φ20×2000mm', '根', '690010010005', '武钢', '武汉钢铁集团', 'ACTIVE', 1),
('MET-006', 'HT250灰铸铁', 5, '300×200×50mm', '块', '690010010006', '新兴铸管', '新兴铸管股份公司', 'ACTIVE', 1),
('MET-007', 'QT450球墨铸铁', 5, 'φ100×500mm', '根', '690010010007', '圣泉集团', '山东圣泉新材料', 'ACTIVE', 1),
('MET-008', '6061铝合金板', 6, '1220×2440×2mm', '张', '690010010008', '西南铝', '重庆西南铝业', 'ACTIVE', 1),
('MET-009', '5052铝合金板', 6, '1220×2440×3mm', '张', '690010010009', '东北轻合金', '东北轻合金有限公司', 'ACTIVE', 1),
('MET-010', 'H62黄铜板', 7, '1000×500×2mm', '张', '690010010010', '洛阳铜业', '洛阳铜加工有限公司', 'ACTIVE', 1),
('MET-011', '紫铜管', 7, 'φ15×1×2000mm', '根', '690010010011', '江西铜业', '江西铜业集团', 'ACTIVE', 1),
('MET-012', '锌合金锭', 8, '25kg/块', '块', '690010010012', '云南锌业', '云南冶金集团', 'ACTIVE', 1),
('MET-013', '不锈钢板304', 4, '2000×1000×1.5mm', '张', '690010010013', '太钢', '太原钢铁集团', 'ACTIVE', 1),
('MET-014', '不锈钢管316', 4, 'φ25×2×3000mm', '根', '690010010014', '久立特材', '浙江久立特材科技股份', 'ACTIVE', 1),
('MET-015', '镀锌钢板', 4, '2000×1000×1mm', '张', '690010010015', '马钢', '马鞍山钢铁股份', 'ACTIVE', 1),
('MET-016', '冷轧钢板', 4, '2000×1000×0.8mm', '张', '690010010016', '本钢', '本溪钢铁集团', 'ACTIVE', 1),
('MET-017', '热轧圆钢', 4, 'φ30×6000mm', '根', '690010010017', '沙钢', '江苏沙钢集团', 'ACTIVE', 1),
('MET-018', '方钢管', 4, '40×40×2×6000mm', '根', '690010010018', '邯钢', '邯郸钢铁集团', 'ACTIVE', 1),
('MET-019', '工字钢', 4, '10# 6000mm', '根', '690010010019', '莱钢', '莱芜钢铁集团', 'ACTIVE', 1),
('MET-020', '角钢', 4, '∠50×50×5×6000mm', '根', '690010010020', '包钢', '包头钢铁集团', 'ACTIVE', 1),

-- 电子电气 (21-40)
('ELEC-001', 'STM32F103单片机', 13, 'LQFP48封装', '个', '690010020001', 'ST', '深圳电子元件公司', 'ACTIVE', 2),
('ELEC-002', 'ATmega328P芯片', 13, 'DIP28封装', '个', '690010020002', 'Microchip', '上海微芯电子', 'ACTIVE', 2),
('ELEC-003', 'DS18B20温度传感器', 14, 'TO-92封装', '个', '690010020003', 'Maxim', '北京Maxim代理', 'ACTIVE', 2),
('ELEC-004', 'LM358运算放大器', 13, 'DIP8封装', '个', '690010020004', 'TI', '德州仪器中国代理', 'ACTIVE', 2),
('ELEC-005', '74HC00芯片', 13, 'DIP14封装', '个', '690010020005', '飞利浦', '飞利浦电子', 'ACTIVE', 2),
('ELEC-006', '红外接收头', 14, '1838B', '个', '690010020006', ' Vishay', '威世科技', 'ACTIVE', 2),
('ELEC-007', '光耦PC817', 13, 'DIP4封装', '个', '690010020007', 'Sharp', '夏普电子', 'ACTIVE', 2),
('ELEC-008', '晶振8MHz', 13, 'HC-49S封装', '个', '690010020008', '爱普生', '爱普生电子', 'ACTIVE', 2),
('ELEC-009', '蜂鸣器', 13, '5V 有源', '个', '690010020009', '嘉兴佳乐', '嘉兴佳乐电子', 'ACTIVE', 2),
('ELEC-010', '按键开关', 13, '6x6x5mm', '个', '690010020010', 'ALPS', '阿尔卑斯电气', 'ACTIVE', 2),
('ELEC-011', '贴片电容100nF', 13, '0805封装', '个', '690010020011', '风华高科', '广东风华高新科技股份', 'ACTIVE', 2),
('ELEC-012', '贴片电阻10K', 13, '0805封装', '个', '690010020012', '国巨', '国巨电子有限公司', 'ACTIVE', 2),
('ELEC-013', 'LED灯(红)', 13, 'φ3mm', '个', '690010020013', '欧司朗', '欧司朗光电半导体', 'ACTIVE', 2),
('ELEC-014', 'LED灯(绿)', 13, 'φ3mm', '个', '690010020014', '欧司朗', '欧司朗光电半导体', 'ACTIVE', 2),
('ELEC-015', 'LED灯(蓝)', 13, 'φ3mm', '个', '690010020015', '欧司朗', '欧司朗光电半导体', 'ACTIVE', 2),
('ELEC-016', '三极管9013', 13, 'TO-92封装', '个', '690010020016', '长电', '长电科技有限公司', 'ACTIVE', 2),
('ELEC-017', '稳压二极管', 13, '1N4733A 5.1V', '个', '690010020017', 'ONSEMI', '安森美半导体', 'ACTIVE', 2),
('ELEC-018', '肖特基二极管', 13, '1N5819', '个', '690010020018', 'RECTRON', '丽特能电子', 'ACTIVE', 2),
('ELEC-019', '整流桥', 13, 'KBP307', '个', '690010020019', '深圳市拓兆', '深圳市拓兆电子有限公司', 'ACTIVE', 2),
('ELEC-020', '电解电容1000μF', 13, '50V 10×15mm', '个', '690010020020', '尼吉康', '尼吉康电子', 'ACTIVE', 2),

-- 机械零件 (41-60)
('MECH-001', 'M8×30螺栓', 17, '8.8级', '个', '690010030001', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-002', 'M10螺母', 17, '8级', '个', '690010030002', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-003', 'φ10垫片', 17, '不锈钢', '个', '690010030003', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-004', 'M12×1.25丝锥', 17, '高速钢', '支', '690010030004', '上工', '上海工具厂', 'ACTIVE', 3),
('MECH-005', 'M6×20螺栓', 17, '8.8级', '个', '690010030005', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-006', 'M6螺母', 17, '8级', '个', '690010030006', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-007', 'M12×40螺栓', 17, '8.8级', '个', '690010030007', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-008', 'M16螺母', 17, '8级', '个', '690010030008', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-009', 'φ16垫片', 17, '不锈钢', '个', '690010030009', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-010', 'M20×80螺栓', 17, '8.8级', '个', '690010030010', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-011', '深沟球轴承', 20, '6204 20×47×14mm', '个', '690010030011', ' NSK', '日本精工株式会社', 'ACTIVE', 3),
('MECH-012', '深沟球轴承', 20, '6205 25×52×15mm', '个', '690010030012', ' NSK', '日本精工株式会社', 'ACTIVE', 3),
('MECH-013', '圆柱滚子轴承', 20, 'NU206 30×62×17mm', '个', '690010030013', ' SKF', '斯凯孚(中国)有限公司', 'ACTIVE', 3),
('MECH-014', '推力球轴承', 20, '51105 25×37×12mm', '个', '690010030014', ' SKF', '斯凯孚(中国)有限公司', 'ACTIVE', 3),
('MECH-015', '直齿轮', 19, '模数2 齿数20', '个', '690010030015', '中机', '中机集团', 'ACTIVE', 3),
('MECH-016', '斜齿轮', 19, '模数2 齿数30 螺旋角15°', '个', '690010030016', '中机', '中机集团', 'ACTIVE', 3),
('MECH-017', '同步带轮', 19, '20齿 5mm节距', '个', '690010030017', '麦高迪', '麦高迪亚太传动系统', 'ACTIVE', 3),
('MECH-018', '链轮', 19, '16齿 08B链轮', '个', '690010030018', '雷勃', '雷勃电气集团', 'ACTIVE', 3),
('MECH-019', '联轴器', 21, '弹性联轴器 19mm孔径', '个', '690010030019', '雷勃', '雷勃电气集团', 'ACTIVE', 3),
('MECH-020', '万向节', 21, '十字万向节', '个', '690010030020', '中机', '中机集团', 'ACTIVE', 3),

-- 化工及耗材 (61-70)
('CHEM-001', '切削液', 25, '20L/桶', '桶', '690010040001', '福斯', '福斯润滑油(中国)有限公司', 'ACTIVE', 4),
('CHEM-002', '导轨油', 25, '20L/桶', '桶', '690010040002', '壳牌', '壳牌(中国)有限公司', 'ACTIVE', 4),
('CHEM-003', '液压油', 25, '200L/桶', '桶', '690010040003', '美孚', '美孚石油(中国)有限公司', 'ACTIVE', 4),
('CHEM-004', '防锈油', 25, '20L/桶', '桶', '690010040004', '长城', '中国石化润滑油有限公司', 'ACTIVE', 4),
('CHEM-005', '密封胶', 24, '300ml/支', '支', '690010040005', '道康宁', '道康宁(中国)有限公司', 'ACTIVE', 4),
('CHEM-006', '结构胶', 24, '300ml/支', '支', '690010040006', '汉高', '汉高(中国)有限公司', 'ACTIVE', 4),
('CHEM-007', '清洁剂', 26, '500ml/瓶', '瓶', '690010040007', '3M', '3M中国有限公司', 'ACTIVE', 4),
('CHEM-008', '除锈剂', 26, '500ml/瓶', '瓶', '690010040008', 'WD-40', 'WD-40(中国)有限公司', 'ACTIVE', 4),
('CHEM-009', '脱脂剂', 23, '25kg/桶', '桶', '690010040009', '陶氏', '陶氏化学(中国)有限公司', 'ACTIVE', 4),
('CHEM-010', '润滑脂', 25, '1kg/罐', '罐', '690010040010', '嘉实多', '嘉实多(中国)有限公司', 'ACTIVE', 4),

-- 工具设备 (71-80)
('TOOL-001', '游标卡尺', 30, '0-150mm 数显', '把', '690010050001', '三丰', '三丰精密量具(中国)有限公司', 'ACTIVE', 5),
('TOOL-002', '千分尺', 30, '0-25mm', '把', '690010050002', 'Mitutoyo', '三丰精密量具(中国)有限公司', 'ACTIVE', 5),
('TOOL-003', '万用表', 30, '数字式', '个', '690010050003', '福禄克', '福禄克测试仪器(中国)有限公司', 'ACTIVE', 5),
('TOOL-004', '示波器', 30, '100MHz 双通道', '台', '690010050004', '泰克', '泰克(中国)有限公司', 'ACTIVE', 5),
('TOOL-005', '电钻', 29, '13mm 710W', '把', '690010050005', '博世', '博世(中国)有限公司', 'ACTIVE', 5),
('TOOL-006', '角磨机', 29, '125mm 750W', '把', '690010050006', '博世', '博世(中国)有限公司', 'ACTIVE', 5),
('TOOL-007', '扳手', 31, '10-32mm 组合', '套', '690010050007', '史丹利', '史丹利(中国)有限公司', 'ACTIVE', 5),
('TOOL-008', '螺丝刀', 31, '一字十字组合', '套', '690010050008', '史丹利', '史丹利(中国)有限公司', 'ACTIVE', 5),
('TOOL-009', '铣刀', 28, 'φ10mm 硬质合金', '支', '690010050009', '山特维克', '山特维克(中国)有限公司', 'ACTIVE', 5),
('TOOL-010', '钻头', 28, 'φ8mm 高速钢', '支', '690010050010', '钴领', '钴领(中国)有限公司', 'ACTIVE', 5);

-- 4. 初始化库存数据
INSERT INTO inventory (material_id, warehouse_id, quantity, available_quantity, locked_quantity, last_stocktake_time)
SELECT 
    m.id,
    m.default_warehouse_id,
    -- 随机生成库存数量，部分物料设置为低库存以触发预警
    CASE 
        WHEN m.material_code IN ('MET-003', 'ELEC-002', 'MECH-001') THEN FLOOR(RAND() * 10)  -- 低库存物料
        ELSE FLOOR(RAND() * 100) + 20  -- 正常库存物料
        END,
    -- 可用库存 = 总库存 - 随机锁定库存
    CASE 
        WHEN m.material_code IN ('MET-003', 'ELEC-002', 'MECH-001') THEN FLOOR(RAND() * 10)
        ELSE FLOOR(RAND() * 100) + 10
        END,
    -- 锁定库存
    CASE 
        WHEN m.material_code IN ('MET-003', 'ELEC-002', 'MECH-001') THEN 0
        ELSE FLOOR(RAND() * 10) + 5
        END,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)  -- 随机生成最近30天内的盘点时间
FROM materials m;

-- 5. 物料批次表
INSERT INTO material_batches (batch_number, material_id, warehouse_id, quantity, available_quantity, locked_quantity, production_date, expiry_date, supplier, manufacturer, notes) VALUES
-- 金属材料批次
('B20230501-001', 1, 1, 50, 50, 0, '2023-05-01', NULL, '北京钢铁贸易公司', '首钢', 'Q235钢板第一批'),
('B20230502-001', 2, 1, 30, 25, 5, '2023-05-02', NULL, '鞍山钢铁有限公司', '鞍钢', 'Q345B钢板'),
('B20230503-001', 3, 1, 100, 85, 15, '2023-05-03', NULL, '上海金属材料公司', '宝钢', '45#圆钢'),
('B20230504-001', 4, 1, 60, 55, 5, '2023-05-04', NULL, '天津无缝钢管厂', '天津钢管', '20#无缝钢管'),

-- 电子电气批次
('B20230401-001', 21, 2, 200, 180, 20, '2023-04-01', '2026-04-01', '深圳电子元件公司', 'ST', 'STM32F103单片机'),
('B20230410-001', 22, 2, 500, 500, 0, '2023-04-10', '2026-04-10', '上海微芯电子', 'Microchip', 'ATmega328P芯片'),
('B20230415-001', 23, 2, 300, 280, 20, '2023-04-15', '2026-04-15', '北京Maxim代理', 'Maxim', 'DS18B20温度传感器'),

-- 机械零件批次
('B20230315-001', 41, 3, 5000, 4800, 200, '2023-03-15', NULL, '河北标准件集团', '标准件厂', 'M8×30螺栓'),
('B20230320-001', 42, 3, 3000, 2900, 100, '2023-03-20', NULL, '河北标准件集团', '标准件厂', 'M10螺母');

-- 6. 库存交易类型初始化 (修复字段名问题)
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

-- 7. 库存预警设置
INSERT INTO inventory_alert_settings (material_id, warehouse_id, min_stock, max_stock, is_enabled, created_by, updated_by) 
SELECT 
    m.id,
    m.default_warehouse_id,
    -- 根据分类设置最低库存阈值
    CASE 
        WHEN c.category_code IN ('METAL', 'METAL-FERROUS', 'METAL-NONFERROUS') THEN 50
        WHEN c.category_code IN ('ELECTRIC', 'ELEC-COMP', 'ELEC-CABLE') THEN 30
        WHEN c.category_code IN ('MECHANICAL', 'MECH-STANDARD', 'MECH-BEARING') THEN 20
        WHEN c.category_code IN ('CHEMICAL', 'TOOLING') THEN 15
        ELSE 25
        END,
    -- 设置最高库存阈值
    CASE 
        WHEN c.category_code IN ('METAL', 'METAL-FERROUS', 'METAL-NONFERROUS') THEN 200
        WHEN c.category_code IN ('ELECTRIC', 'ELEC-COMP', 'ELEC-CABLE') THEN 100
        WHEN c.category_code IN ('MECHANICAL', 'MECH-STANDARD', 'MECH-BEARING') THEN 80
        WHEN c.category_code IN ('CHEMICAL', 'TOOLING') THEN 150
        ELSE 120
        END,
    TRUE,  -- 启用预警
    'system', -- 创建人
    'system'  -- 更新人
FROM materials m
         JOIN material_categories c ON m.category_id = c.id
WHERE m.default_warehouse_id IS NOT NULL;

-- 8. 生成库存交易记录
DELIMITER $$
CREATE PROCEDURE GenerateInventoryTransactions()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE mat_id BIGINT;
    DECLARE wh_id BIGINT;
    DECLARE current_qty INT;
    DECLARE trans_count INT;
    DECLARE i INT DEFAULT 1;
    DECLARE trans_type_id BIGINT;
    DECLARE qty INT;
    DECLARE trans_date DATETIME;
    DECLARE direction VARCHAR(10);
    DECLARE batch_id BIGINT;
    DECLARE mat_code VARCHAR(50);
    DECLARE trans_no VARCHAR(50);
    
    -- 游标遍历所有库存记录
    DECLARE cur CURSOR FOR 
        SELECT material_id, warehouse_id, quantity FROM inventory;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 检查是否存在交易类型数据，如果不存在则创建基础数据
    IF (SELECT COUNT(*) FROM inventory_transaction_types) = 0 THEN
        INSERT INTO inventory_transaction_types (type_code, type_name, direction, description) VALUES
                                                                                                   ('PURCHASE_IN', '采购入库', 'IN', '采购物料入库'),
                                                                                                   ('PRODUCTION_IN', '生产入库', 'IN', '生产成品入库'),
                                                                                                   ('SALES_OUT', '销售出库', 'OUT', '销售订单出库'),
                                                                                                   ('PRODUCTION_OUT', '生产领料', 'OUT', '生产领用出库');
    END IF;
    
    OPEN cur;
    
    read_loop: LOOP
        FETCH cur INTO mat_id, wh_id, current_qty;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 获取该物料的批次ID
        SELECT id INTO batch_id FROM material_batches 
        WHERE material_id = mat_id AND warehouse_id = wh_id LIMIT 1;
        
        -- 为每个物料生成2-5条出入库记录
        SET trans_count = FLOOR(RAND() * 4) + 2;
        SET i = 1;
        SET @initial_qty = 0;
        
        WHILE i <= trans_count DO
            -- 确保能获取到有效的交易类型ID
            SELECT id INTO trans_type_id
            FROM inventory_transaction_types
            ORDER BY RAND()
            LIMIT 1;
            
            -- 获取交易方向
            SELECT direction INTO direction
            FROM inventory_transaction_types
            WHERE id = trans_type_id;
            
            -- 根据交易方向生成数量
            IF direction = 'IN' THEN
                SET qty = FLOOR(RAND() * 50) + 10;
            ELSE
                -- 出库数量不能超过当前库存
                SET qty = -FLOOR(RAND() * LEAST(50, GREATEST(1, @initial_qty / 2))) - 1;
                -- 确保不出现负库存
                IF @initial_qty + qty < 0 THEN
                    SET qty = -@initial_qty;
                END IF;
            END IF;
            
            -- 生成随机交易日期（过去90天内）
            SET trans_date = DATE_SUB(NOW(), INTERVAL (FLOOR(RAND() * 90) * 24 + FLOOR(RAND() * 24)) HOUR);
            
            -- 生成唯一交易单号
            SET trans_no = CONCAT('TR', DATE_FORMAT(trans_date, '%Y%m%d'), LPAD(FLOOR(RAND() * 10000), 4, '0'));
            
            -- 获取物料代码
            SELECT material_code INTO mat_code FROM materials WHERE id = mat_id;
            
            -- 插入交易记录
            INSERT INTO inventory_transactions (
                transaction_no, transaction_type_id, material_id, warehouse_id,
                batch_id, quantity, unit_cost, total_cost, reference_no, transaction_time,
                notes, created_by
            ) VALUES (
                trans_no, trans_type_id, mat_id, wh_id,
                batch_id, qty,
                FLOOR(RAND() * 100) + 10, -- 随机单位成本
                (FLOOR(RAND() * 100) + 10) * ABS(qty), -- 总成本
                CONCAT('REF', FLOOR(RAND() * 100000)), -- 参考单号
                trans_date,
                CONCAT('Auto-generated transaction for ', mat_code), -- 备注
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

-- 执行存储过程生成交易记录
CALL GenerateInventoryTransactions();

-- 清理存储过程
DROP PROCEDURE IF EXISTS GenerateInventoryTransactions;

-- 9. 触发库存预警
INSERT INTO inventory_alerts (material_id, warehouse_id, alert_type, current_quantity, threshold_value)
SELECT 
    i.material_id,
    i.warehouse_id,
    'LOW_STOCK',
    i.quantity,
    s.min_stock
FROM inventory i
         JOIN inventory_alert_settings s ON i.material_id = s.material_id 
    AND i.warehouse_id = s.warehouse_id
WHERE i.quantity <= s.min_stock
ON DUPLICATE KEY UPDATE
                     current_quantity = i.quantity,
                     threshold_value = s.min_stock;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示预警数量
SELECT COUNT(*) AS low_stock_alert_count FROM inventory_alerts WHERE alert_type = 'LOW_STOCK';