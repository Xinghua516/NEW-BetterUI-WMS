-- 仓库管理系统初始化数据（包含80条物料信息和多次出入库记录）

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
-- 保留出入库类型表的初始化数据，不截断

-- 1. 物料分类表
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

-- 3. 物料信息表（80条）
INSERT INTO materials (material_code, material_name, category_id, specification, unit, barcode, brand, supplier, status, default_warehouse_id) VALUES
-- 金属材料 - 钢材 (category_id=4)
('MET-001', 'Q235钢板', 4, '2000×1000×5mm', '张', '690010010001', '首钢', '北京钢铁贸易公司', 'ACTIVE', 1),
('MET-002', 'Q345B钢板', 4, '2500×1250×10mm', '张', '690010010002', '鞍钢', '鞍山钢铁有限公司', 'ACTIVE', 1),
('MET-003', '45#圆钢', 4, 'φ50×6000mm', '根', '690010010003', '宝钢', '上海金属材料公司', 'ACTIVE', 1),
('MET-004', '20#无缝钢管', 4, 'φ32×3×6000mm', '根', '690010010004', '天津钢管', '天津无缝钢管厂', 'ACTIVE', 1),
('MET-005', 'T8碳素工具钢', 4, 'φ20×2000mm', '根', '690010010005', '武钢', '武汉钢铁集团', 'ACTIVE', 1),
('MET-006', '65Mn弹簧钢', 4, '1.5×1000×2000mm', '张', '690010010006', '本钢', '本溪钢铁集团', 'ACTIVE', 1),
('MET-007', '304不锈钢板', 4, '2×1220×2440mm', '张', '690010010007', '太钢', '太原钢铁集团', 'ACTIVE', 1),
('MET-008', '201不锈钢管', 4, 'φ50×2×6000mm', '根', '690010010008', '青山', '青山钢铁集团', 'ACTIVE', 1),
('MET-009', '40Cr合金结构钢', 4, 'φ80×6000mm', '根', '690010010009', '兴澄', '兴澄特钢', 'ACTIVE', 1),
('MET-010', 'Q235角钢', 4, '50×50×5mm', '根', '690010010010', '马钢', '马鞍山钢铁', 'ACTIVE', 1),

-- 金属材料 - 铸铁 (category_id=5)
('MET-011', 'HT250灰铸铁', 5, '300×200×50mm', '块', '690010010011', '新兴铸管', '新兴铸管股份公司', 'ACTIVE', 1),
('MET-012', 'QT450球墨铸铁', 5, 'φ100×500mm', '根', '690010010012', '圣泉集团', '山东圣泉新材料', 'ACTIVE', 1),
('MET-013', 'HT300灰铸铁', 5, '400×300×80mm', '块', '690010010013', '冀东', '冀东水泥集团', 'ACTIVE', 1),
('MET-014', 'QT600球墨铸铁', 5, 'φ150×800mm', '根', '690010010014', '天铁', '天津铁厂', 'ACTIVE', 1),
('MET-015', 'HT150灰铸铁', 5, '200×150×30mm', '块', '690010010015', '唐钢', '唐山钢铁', 'ACTIVE', 1),

-- 金属材料 - 铝及铝合金 (category_id=6)
('MET-016', '6061铝合金板', 6, '1220×2440×2mm', '张', '690010010016', '西南铝', '重庆西南铝业', 'ACTIVE', 1),
('MET-017', '5052铝合金板', 6, '1000×2000×3mm', '张', '690010010017', '东轻', '东北轻合金', 'ACTIVE', 1),
('MET-018', 'LY12铝合金棒', 6, 'φ50×2000mm', '根', '690010010018', '忠旺', '辽宁忠旺集团', 'ACTIVE', 1),
('MET-019', '7075铝合金管', 6, 'φ30×2×2000mm', '根', '690010010019', '南山', '南山铝业', 'ACTIVE', 1),
('MET-020', '1060纯铝板', 6, '1220×2440×1mm', '张', '690010010020', '云铝', '云南铝业', 'ACTIVE', 1),

-- 金属材料 - 铜及铜合金 (category_id=7)
('MET-021', 'T2紫铜板', 7, '1000×2000×2mm', '张', '690010010021', '江铜', '江西铜业', 'ACTIVE', 1),
('MET-022', 'H62黄铜带', 7, '0.3×100mm', '卷', '690010010022', '铜陵', '铜陵有色', 'ACTIVE', 1),
('MET-023', 'QSn6.5-0.1锡青铜棒', 7, 'φ20×1000mm', '根', '690010010023', '中铝', '中国铝业', 'ACTIVE', 1),
('MET-024', 'TU1无氧铜管', 7, 'φ10×1×2000mm', '根', '690010010024', '海亮', '海亮股份', 'ACTIVE', 1),
('MET-025', 'HPb59-1铅黄铜棒', 7, 'φ30×1000mm', '根', '690010010025', '金田', '金田铜业', 'ACTIVE', 1),

-- 金属材料 - 锌及锌合金 (category_id=8)
('MET-026', 'Zn99.995锌锭', 8, '25kg/块', '块', '690010010026', '株冶', '株洲冶炼集团', 'ACTIVE', 1),
('MET-027', 'ZZnAl4铜锌合金', 8, 'φ50×200mm', '块', '690010010027', '葫芦岛', '葫芦岛锌业', 'ACTIVE', 1),
('MET-028', '压铸锌合金锭', 8, '20kg/块', '块', '690010010028', '洛铜', '洛阳铜业', 'ACTIVE', 1),
('MET-029', 'Zn-Al-Mg合金板', 8, '1000×2000×5mm', '张', '690010010029', '白银', '白银有色', 'ACTIVE', 1),
('MET-030', '锌基耐磨合金', 8, '300×200×50mm', '块', '690010010030', '金川', '金川集团', 'ACTIVE', 1),

-- 电子电气 - 集成电路 (category_id=13)
('ELEC-001', 'STM32F103单片机', 13, 'LQFP48封装', '个', '690010020001', 'ST', '深圳电子元件公司', 'ACTIVE', 2),
('ELEC-002', 'ATmega328P芯片', 13, 'DIP28封装', '个', '690010020002', 'Microchip', '上海微芯电子', 'ACTIVE', 2),
('ELEC-003', 'LM358运算放大器', 13, 'DIP8封装', '个', '690010020003', 'TI', '德州仪器中国代理', 'ACTIVE', 2),
('ELEC-004', 'NE555定时器', 13, 'DIP8封装', '个', '690010020004', 'Fairchild', '飞兆半导体', 'ACTIVE', 2),
('ELEC-005', '74HC595移位寄存器', 13, 'DIP16封装', '个', '690010020005', 'NXP', '恩智浦半导体', 'ACTIVE', 2),
('ELEC-006', 'PIC16F877A单片机', 13, 'DIP40封装', '个', '690010020006', 'Microchip', '微芯科技', 'ACTIVE', 2),
('ELEC-007', 'CD4051多路开关', 13, 'DIP16封装', '个', '690010020007', 'TI', '德州仪器', 'ACTIVE', 2),
('ELEC-008', 'AD822运算放大器', 13, 'SOIC8封装', '个', '690010020008', 'ADI', '亚德诺半导体', 'ACTIVE', 2),
('ELEC-009', 'MAX232电平转换', 13, 'DIP16封装', '个', '690010020009', 'Maxim', '美信半导体', 'ACTIVE', 2),
('ELEC-010', 'IR2104驱动芯片', 13, 'SOIC8封装', '个', '690010020010', 'IR', '国际整流器公司', 'ACTIVE', 2),

-- 电子电气 - 传感器 (category_id=14)
('ELEC-011', 'DS18B20温度传感器', 14, 'TO-92封装', '个', '690010020011', 'Maxim', '北京Maxim代理', 'ACTIVE', 2),
('ELEC-012', 'HC-SR04超声波传感器', 14, '模块', '个', '690010020012', 'HC', '深圳电子模块厂', 'ACTIVE', 2),
('ELEC-013', 'MQ-2烟雾传感器', 14, '模块', '个', '690010020013', 'MQ', '广州传感器公司', 'ACTIVE', 2),
('ELEC-014', 'MPU6050陀螺仪', 14, '模块', '个', '690010020014', 'InvenSense', '应美盛电子', 'ACTIVE', 2),
('ELEC-015', 'DHT11温湿度传感器', 14, '模块', '个', '690010020015', 'Aosong', '奥松电子', 'ACTIVE', 2),
('ELEC-016', 'TCS3200颜色传感器', 14, '模块', '个', '690010020016', 'TAOS', '德州先进光学系统', 'ACTIVE', 2),
('ELEC-017', 'FC-28土壤湿度传感器', 14, '模块', '个', '690010020017', 'FC', '深圳飞创电子', 'ACTIVE', 2),
('ELEC-018', 'GP2Y1014AU粉尘传感器', 14, '模块', '个', '690010020018', 'Sharp', '夏普电子', 'ACTIVE', 2),
('ELEC-019', '霍尔传感器A3144', 14, 'TO-92封装', '个', '690010020019', 'Allegro', '雅丽高半导体', 'ACTIVE', 2),
('ELEC-020', '光电传感器EE-SX670', 14, '插件', '个', '690010020020', 'Omron', '欧姆龙电子', 'ACTIVE', 2),

-- 电子电气 - 连接器 (category_id=15)
('ELEC-021', 'USB Type-C连接器', 15, '母座', '个', '690010020021', 'Foxconn', '富士康', 'ACTIVE', 2),
('ELEC-022', 'HDMI连接器', 15, '19针', '个', '690010020022', 'Molex', '莫仕连接器', 'ACTIVE', 2),
('ELEC-023', 'DB9串口连接器', 15, '公头', '个', '690010020023', 'TE', '泰科电子', 'ACTIVE', 2),
('ELEC-024', 'XH2.54端子', 15, '2P', '个', '690010020024', 'JST', '日本压着端子', 'ACTIVE', 2),
('ELEC-025', '杜邦线接头', 15, '公头', '个', '690010020025', 'Generic', '通用电子', 'ACTIVE', 2),

-- 机械零件 - 标准件 (category_id=17)
('MECH-001', 'M8×30螺栓', 17, '8.8级', '个', '690010030001', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-002', 'M10螺母', 17, '8级', '个', '690010030002', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-003', 'φ10垫片', 17, '不锈钢', '个', '690010030003', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-004', 'M12×1.25丝锥', 17, '高速钢', '支', '690010030004', '上工', '上海工具厂', 'ACTIVE', 3),
('MECH-005', 'M6×16内六角螺丝', 17, '12.9级', '个', '690010030005', '哈量', '哈尔滨量具刃具', 'ACTIVE', 3),
('MECH-006', 'M5×10十字螺丝', 17, '4.8级', '个', '690010030006', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-007', 'M14六角螺母', 17, '8级', '个', '690010030007', '晋亿', '晋亿实业', 'ACTIVE', 3),
('MECH-008', 'φ8弹性销', 17, '不锈钢', '个', '690010030008', '标准件厂', '河北标准件集团', 'ACTIVE', 3),
('MECH-009', 'M16膨胀螺栓', 17, '8.8级', '套', '690010030009', '宁波标准件', '宁波标准件厂', 'ACTIVE', 3),
('MECH-010', 'M4×8紧定螺丝', 17, '45#钢', '个', '690010030010', '标准件厂', '河北标准件集团', 'ACTIVE', 3),

-- 机械零件 - 轴承 (category_id=20)
('MECH-011', '6205深沟球轴承', 20, '内径25mm', '套', '690010030011', 'SKF', '斯凯孚中国', 'ACTIVE', 3),
('MECH-012', '30203圆锥滚子轴承', 20, '内径17mm', '套', '690010030012', 'NSK', '恩斯克中国', 'ACTIVE', 3),
('MECH-013', '7206角接触球轴承', 20, '内径30mm', '套', '690010030013', 'FAG', '舍弗勒集团', 'ACTIVE', 3),
('MECH-014', 'NA2205滚针轴承', 20, '内径25mm', '套', '690010030014', 'IKO', '汤姆逊中国', 'ACTIVE', 3),
('MECH-015', '51106推力球轴承', 20, '内径30mm', '套', '690010030015', 'NTN', '恩梯恩中国', 'ACTIVE', 3),

-- 化工耗材 - 胶粘剂 (category_id=24)
('CHEM-001', '环氧树脂AB胶', 24, '50ml/套', '套', '690010040001', '得力', '得力化工', 'ACTIVE', 4),
('CHEM-002', '瞬间胶', 24, '20g/支', '支', '690010040002', '乐泰', '汉高股份', 'ACTIVE', 4),
('CHEM-003', '硅酮密封胶', 24, '300ml/支', '支', '690010040003', '道康宁', '陶氏化学', 'ACTIVE', 4),
('CHEM-004', '厌氧胶', 24, '50ml/瓶', '瓶', '690010040004', '回天', '回天新材料', 'ACTIVE', 4),
('CHEM-005', '热熔胶棒', 24, '11mm×200mm', '支', '690010040005', '3M', '3M中国', 'ACTIVE', 4),

-- 化工耗材 - 润滑剂 (category_id=25)
('CHEM-006', '锂基润滑脂', 25, '1kg/罐', '罐', '690010040006', '长城', '中国石化', 'ACTIVE', 4),
('CHEM-007', '机械润滑油', 25, '4L/桶', '桶', '690010040007', '昆仑', '中国石油', 'ACTIVE', 4),
('CHEM-008', '高温润滑脂', 25, '500g/罐', '罐', '690010040008', '美孚', '埃克森美孚', 'ACTIVE', 4),
('CHEM-009', '精密仪器润滑油', 25, '100ml/瓶', '瓶', '690010040009', '壳牌', '壳牌中国', 'ACTIVE', 4),
('CHEM-010', '链条润滑剂', 25, '400ml/喷罐', '罐', '690010040010', 'WD-40', 'WD-40中国', 'ACTIVE', 4),

-- 工具设备 - 切削工具 (category_id=28)
('TOOL-001', '高速钢立铣刀', 28, 'φ10mm', '支', '690010050001', '上工', '上海工具厂', 'ACTIVE', 5),
('TOOL-002', '硬质合金车刀', 28, '外圆', '把', '690010050002', '株洲钻石', '株洲硬质合金', 'ACTIVE', 5),
('TOOL-003', '麻花钻', 28, 'φ8mm', '支', '690010050003', '哈量', '哈尔滨量具刃具', 'ACTIVE', 5),
('TOOL-004', '丝锥套装', 28, 'M3-M12', '套', '690010050004', '成量', '成都工具研究所', 'ACTIVE', 5),
('TOOL-005', '合金锯片', 28, '100mm×30T', '片', '690010050005', '金田', '金田锯业', 'ACTIVE', 5),

-- 工具设备 - 测量工具 (category_id=29)
('TOOL-006', '游标卡尺', 29, '0-150mm', '把', '690010050006', '上量', '上海量具刃具', 'ACTIVE', 5),
('TOOL-007', '千分尺', 29, '0-25mm', '把', '690010050007', '哈量', '哈尔滨量具刃具', 'ACTIVE', 5),
('TOOL-008', '百分表', 29, '0-10mm', '个', '690010050008', '成量', '成都工具研究所', 'ACTIVE', 5),
('TOOL-009', '直角尺', 29, '300mm', '把', '690010050009', '桂量', '桂林量具刃具', 'ACTIVE', 5),
('TOOL-010', '卷尺', 29, '5m×19mm', '把', '690010050010', '得力', '得力工具', 'ACTIVE', 5),

-- 工具设备 - 电动工具 (category_id=30)
('TOOL-011', '手电钻', 30, '10mm', '台', '690010050011', '博世', '博世电动工具', 'ACTIVE', 5),
('TOOL-012', '角磨机', 30, '100mm', '台', '690010050012', '东成', '江苏东成电动工具', 'ACTIVE', 5),
('TOOL-013', '砂光机', 30, '110mm', '台', '690010050013', '牧田', '牧田中国', 'ACTIVE', 5),
('TOOL-014', '电烙铁', 30, '60W', '把', '690010050014', 'Weller', '威乐电子', 'ACTIVE', 5),
('TOOL-015', '热风枪', 30, '2000W', '把', '690010050015', '得伟', '得伟工具', 'ACTIVE', 5);

-- 4. 物料批次表
INSERT INTO material_batches (batch_number, material_id, warehouse_id, quantity, available_quantity, locked_quantity, production_date, expiry_date, supplier, manufacturer, notes) VALUES
-- 金属材料批次（1-30）
('B20230501-001', 1, 1, 50, 50, 0, '2023-05-01', NULL, '北京钢铁贸易公司', '首钢', 'Q235钢板第一批'),
('B20230502-001', 2, 1, 30, 25, 5, '2023-05-02', NULL, '鞍山钢铁有限公司', '鞍钢', 'Q345B钢板'),
('B20230503-001', 3, 1, 15, 15, 0, '2023-05-03', NULL, '上海金属材料公司', '宝钢', '45#圆钢（库存偏低）'),
('B20230504-001', 4, 1, 60, 55, 5, '2023-05-04', NULL, '天津无缝钢管厂', '天津钢管', '20#无缝钢管'),
('B20230505-001', 5, 1, 40, 38, 2, '2023-05-05', NULL, '武汉钢铁集团', '武钢', 'T8碳素工具钢'),
('B20230506-001', 6, 1, 25, 25, 0, '2023-05-06', NULL, '本溪钢铁集团', '本钢', '65Mn弹簧钢'),
('B20230507-001', 7, 1, 35, 35, 0, '2023-05-07', NULL, '太原钢铁集团', '太钢', '304不锈钢板'),
('B20230508-001', 8, 1, 50, 45, 5, '2023-05-08', NULL, '青山钢铁集团', '青山', '201不锈钢管'),
('B20230509-001', 9, 1, 20, 20, 0, '2023-05-09', NULL, '兴澄特钢', '兴澄', '40Cr合金结构钢'),
('B20230510-001', 10, 1, 45, 40, 5, '2023-05-10', NULL, '马鞍山钢铁', '马钢', 'Q235角钢'),
('B20230511-001', 11, 1, 30, 30, 0, '2023-05-11', NULL, '新兴铸管股份公司', '新兴铸管', 'HT250灰铸铁'),
('B20230512-001', 12, 1, 25, 25, 0, '2023-05-12', NULL, '山东圣泉新材料', '圣泉集团', 'QT450球墨铸铁'),
('B20230513-001', 13, 1, 20, 20, 0, '2023-05-13', NULL, '冀东水泥集团', '冀东', 'HT300灰铸铁'),
('B20230514-001', 14, 1, 15, 15, 0, '2023-05-14', NULL, '天津铁厂', '天铁', 'QT600球墨铸铁（库存偏低）'),
('B20230515-001', 15, 1, 35, 35, 0, '2023-05-15', NULL, '唐山钢铁', '唐钢', 'HT150灰铸铁'),
('B20230516-001', 16, 1, 40, 38, 2, '2023-05-16', NULL, '重庆西南铝业', '西南铝', '6061铝合金板'),
('B20230517-001', 17, 1, 30, 30, 0, '2023-05-17', NULL, '东北轻合金', '东轻', '5052铝合金板'),
('B20230518-001', 18, 1, 25, 25, 0, '2023-05-18', NULL, '辽宁忠旺集团', '忠旺', 'LY12铝合金棒'),
('B20230519-001', 19, 1, 20, 20, 0, '2023-05-19', NULL, '南山铝业', '南山', '7075铝合金管'),
('B20230520-001', 20, 1, 35, 35, 0, '2023-05-20', NULL, '云南铝业', '云铝', '1060纯铝板'),
('B20230521-001', 21, 1, 30, 30, 0, '2023-05-21', NULL, '江西铜业', '江铜', 'T2紫铜板'),
('B20230522-001', 22, 1, 40, 38, 2, '2023-05-22', NULL, '铜陵有色', '铜陵', 'H62黄铜带'),
('B20230523-001', 23, 1, 25, 25, 0, '2023-05-23', NULL, '中国铝业', '中铝', 'QSn6.5-0.1锡青铜棒'),
('B20230524-001', 24, 1, 20, 20, 0, '2023-05-24', NULL, '海亮股份', '海亮', 'TU1无氧铜管'),
('B20230525-001', 25, 1, 35, 35, 0, '2023-05-25', NULL, '金田铜业', '金田', 'HPb59-1铅黄铜棒'),
('B20230526-001', 26, 1, 50, 50, 0, '2023-05-26', NULL, '株洲冶炼集团', '株冶', 'Zn99.995锌锭'),
('B20230527-001', 27, 1, 30, 30, 0, '2023-05-27', NULL, '葫芦岛锌业', '葫芦岛', 'ZZnAl4铜锌合金'),
('B20230528-001', 28, 1, 25, 25, 0, '2023-05-28', NULL, '洛阳铜业', '洛铜', '压铸锌合金锭'),
('B20230529-001', 29, 1, 20, 20, 0, '2023-05-29', NULL, '白银有色', '白银', 'Zn-Al-Mg合金板'),
('B20230530-001', 30, 1, 15, 15, 0, '2023-05-30', NULL, '金川集团', '金川', '锌基耐磨合金（库存偏低）'),

-- 电子电气批次（31-55）
('B20230401-001', 31, 2, 200, 180, 20, '2023-04-01', '2026-04-01', '深圳电子元件公司', 'ST', 'STM32F103单片机'),
('B20230402-001', 32, 2, 25, 25, 0, '2023-04-02', '2026-04-02', '上海微芯电子', 'Microchip', 'ATmega328P芯片（库存偏低）'),
('B20230403-001', 33, 2, 150, 140, 10, '2023-04-03', '2026-04-03', '德州仪器中国代理', 'TI', 'LM358运算放大器'),
('B20230404-001', 34, 2, 100, 95, 5, '2023-04-04', '2026-04-04', '飞兆半导体', 'Fairchild', 'NE555定时器'),
('B20230405-001', 35, 2, 80, 80, 0, '2023-04-05', '2026-04-05', '恩智浦半导体', 'NXP', '74HC595移位寄存器'),
('B20230406-001', 36, 2, 50, 50, 0, '2023-04-06', '2026-04-06', '微芯科技', 'Microchip', 'PIC16F877A单片机'),
('B20230407-001', 37, 2, 60, 60, 0, '2023-04-07', '2026-04-07', '德州仪器', 'TI', 'CD4051多路开关'),
('B20230408-001', 38, 2, 40, 40, 0, '2023-04-08', '2026-04-08', '亚德诺半导体', 'ADI', 'AD822运算放大器'),
('B20230409-001', 39, 2, 70, 70, 0, '2023-04-09', '2026-04-09', '美信半导体', 'Maxim', 'MAX232电平转换'),
('B20230410-001', 40, 2, 30, 30, 0, '2023-04-10', '2026-04-10', '国际整流器公司', 'IR', 'IR2104驱动芯片'),
('B20230411-001', 41, 2, 300, 280, 20, '2023-04-11', '2026-04-11', '北京Maxim代理', 'Maxim', 'DS18B20温度传感器'),
('B20230412-001', 42, 2, 50, 50, 0, '2023-04-12', '2026-04-12', '深圳电子模块厂', 'HC', 'HC-SR04超声波传感器'),
('B20230413-001', 43, 2, 40, 40, 0, '2023-04-13', '2026-04-13', '广州传感器公司', 'MQ', 'MQ-2烟雾传感器'),
('B20230414-001', 44, 2, 30, 30, 0, '2023-04-14', '2026-04-14', '应美盛电子', 'InvenSense', 'MPU6050陀螺仪'),
('B20230415-001', 45, 2, 60, 60, 0, '2023-04-15', '2026-04-15', '奥松电子', 'Aosong', 'DHT11温湿度传感器'),
('B20230416-001', 46, 2, 25, 25, 0, '2023-04-16', '2026-04-16', '德州先进光学系统', 'TAOS', 'TCS3200颜色传感器（库存偏低）'),
('B20230417-001', 47, 2, 40, 40, 0, '2023-04-17', '2026-04-17', '深圳飞创电子', 'FC', 'FC-28土壤湿度传感器'),
('B20230418-001', 48, 2, 30, 30, 0, '2023-04-18', '2026-04-18', '夏普电子', 'Sharp', 'GP2Y1014AU粉尘传感器'),
('B20230419-001', 49, 2, 100, 100, 0, '2023-04-19', '2026-04-19', '雅丽高半导体', 'Allegro', '霍尔传感器A3144'),
('B20230420-001', 50, 2, 50, 50, 0, '2023-04-20', '2026-04-20', '欧姆龙电子', 'Omron', '光电传感器EE-SX670'),
('B20230421-001', 51, 2, 80, 80, 0, '2023-04-21', '2026-04-21', '富士康', 'Foxconn', 'USB Type-C连接器'),
('B20230422-001', 52, 2, 40, 40, 0, '2023-04-22', '2026-04-22', '莫仕连接器', 'Molex', 'HDMI连接器'),
('B20230423-001', 53, 2, 30, 30, 0, '2023-04-23', '2026-04-23', '泰科电子', 'TE', 'DB9串口连接器'),
('B20230424-001', 54, 2, 200, 190, 10, '2023-04-24', '2026-04-24', '日本压着端子', 'JST', 'XH2.54端子'),
('B20230425-001', 55, 2, 150, 150, 0, '2023-04-25', '2026-04-25', '通用电子', 'Generic', '杜邦线接头');

-- 5. 初始化库存数据（确保部分物料库存低于预警阈值）
INSERT INTO inventory (material_id, warehouse_id, quantity, available_quantity, locked_quantity, last_stocktake_time)
SELECT
    m.id,
    m.default_warehouse_id,
    -- 特意让15种物料库存低于预警值，触发预警
    CASE
        WHEN m.id IN (3,14,30,32,46,56,65,70,75,80,85,90,95,100,105) THEN FLOOR(RAND() * 10) + 5  -- 5-14（低于预警值）
        ELSE FLOOR(RAND() * 100) + 60  -- 60-159（高于预警值）
        END,
    CASE
        WHEN m.id IN (3,14,30,32,46,56,65,70,75,80,85,90,95,100,105) THEN FLOOR(RAND() * 10) + 5
        ELSE FLOOR(RAND() * 100) + 50
        END,
    CASE
        WHEN m.id IN (3,14,30,32,46,56,65,70,75,80,85,90,95,100,105) THEN 0
        ELSE FLOOR(RAND() * 10) + 5
        END,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)
FROM materials m;

-- 6. 库存预警设置
INSERT INTO inventory_alert_settings (material_id, warehouse_id, min_stock, max_stock, is_enabled, created_at, updated_at)
SELECT
    m.id,
    m.default_warehouse_id,
    -- 设置明确的预警阈值
    CASE
        WHEN c.category_code IN ('METAL', 'METAL-FERROUS', 'METAL-NONFERROUS') THEN 20  -- 金属材料最低库存20
        WHEN c.category_code IN ('ELECTRIC', 'ELEC-COMP', 'ELEC-CABLE') THEN 30     -- 电子元件最低库存30
        WHEN c.category_code IN ('MECHANICAL', 'MECH-STANDARD', 'MECH-BEARING') THEN 25  -- 机械零件最低库存25
        WHEN c.category_code IN ('CHEMICAL', 'CHEM-RAW', 'CHEM-ADHESIVE') THEN 15  -- 化工耗材最低库存15
        WHEN c.category_code IN ('TOOLING', 'TOOL-CUTTING', 'TOOL-MEASURE') THEN 10  -- 工具设备最低库存10
        ELSE 25
        END,
    CASE
        WHEN c.category_code IN ('METAL', 'METAL-FERROUS', 'METAL-NONFERROUS') THEN 200
        WHEN c.category_code IN ('ELECTRIC', 'ELEC-COMP', 'ELEC-CABLE') THEN 100
        WHEN c.category_code IN ('MECHANICAL', 'MECH-STANDARD', 'MECH-BEARING') THEN 80
        WHEN c.category_code IN ('CHEMICAL', 'CHEM-RAW', 'CHEM-ADHESIVE') THEN 50
        WHEN c.category_code IN ('TOOLING', 'TOOL-CUTTING', 'TOOL-MEASURE') THEN 30
        ELSE 120
        END,
    TRUE,
    NOW(),
    NOW()
FROM materials m
         JOIN material_categories c ON m.category_id = c.id
WHERE m.default_warehouse_id IS NOT NULL
ON DUPLICATE KEY UPDATE
                     min_stock = VALUES(min_stock),
                     max_stock = VALUES(max_stock),
                     updated_at = NOW();

-- 7. 生成库存交易记录的存储过程（支持单一物料多次出入库）
DELIMITER $$
CREATE PROCEDURE GenerateInventoryTransactions()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE mat_id BIGINT;
    DECLARE wh_id BIGINT;
    DECLARE current_qty INT;
    DECLARE trans_qty INT;
    DECLARE trans_type INT;
    DECLARE trans_count INT;
    DECLARE i INT;
    DECLARE cur CURSOR FOR SELECT material_id, warehouse_id FROM inventory;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO mat_id, wh_id;
        IF done THEN LEAVE read_loop; END IF;

        -- 为每个物料随机生成1-5条交易记录，确保总记录数多于物料数
        SET trans_count = FLOOR(RAND() * 5) + 1;
        SET i = 1;

        WHILE i <= trans_count DO
                -- 获取当前库存数量，确保出库不会导致负库存
                SELECT quantity INTO current_qty FROM inventory WHERE material_id = mat_id AND warehouse_id = wh_id;

                -- 随机生成交易类型（1-11）
                SET trans_type = FLOOR(RAND() * 11) + 1;

                -- 根据交易类型确定数量，出库交易确保不超过当前库存
                IF trans_type IN (1,2,3,4) THEN  -- 入库交易（正数）
                    SET trans_qty = FLOOR(RAND() * 50) + 10;
                    -- 更新库存
                    UPDATE inventory
                    SET quantity = quantity + trans_qty,
                        available_quantity = available_quantity + trans_qty,
                        last_stocktake_time = NOW()
                    WHERE material_id = mat_id AND warehouse_id = wh_id;
                ELSE  -- 出库交易（负数），确保不会导致负库存
                    SET trans_qty = -LEAST(FLOOR(RAND() * 30) + 5, current_qty);
                    -- 更新库存
                    UPDATE inventory
                    SET quantity = quantity + trans_qty,
                        available_quantity = available_quantity + trans_qty,
                        last_stocktake_time = NOW()
                    WHERE material_id = mat_id AND warehouse_id = wh_id;
                END IF;

                -- 插入交易记录
                INSERT INTO inventory_transactions (
                    transaction_no, transaction_type_id, material_id, warehouse_id, quantity, transaction_time, created_by
                ) VALUES (
                             CONCAT('TR', UNIX_TIMESTAMP(), '_', mat_id, '_', wh_id, '_', i),
                             trans_type,
                             mat_id,
                             wh_id,
                             trans_qty,
                             DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),  -- 随机过去30天内的日期
                             'system'
                         );

                SET i = i + 1;
            END WHILE;
    END LOOP;
    CLOSE cur;
END$$
DELIMITER ;

-- 执行存储过程生成交易记录（预计生成 80 * 3 = 240 条左右记录）
CALL GenerateInventoryTransactions();
DROP PROCEDURE IF EXISTS GenerateInventoryTransactions;

-- 8. 手动触发库存预警检查
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
                     threshold_value = s.min_stock,
                     is_processed = FALSE;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示统计信息
SELECT COUNT(*) AS total_materials FROM materials;
SELECT COUNT(*) AS total_transactions FROM inventory_transactions;
SELECT COUNT(*) AS low_stock_alert_count FROM inventory_alerts WHERE alert_type = 'LOW_STOCK' AND is_processed = FALSE;

-- 查看特定物料的多次出入库记录
SELECT
    m.material_code,
    m.material_name,
    t.transaction_no,
    t.transaction_type_id,
    t.quantity,
    t.transaction_time
FROM inventory_transactions t
         JOIN materials m ON t.material_id = m.id
WHERE m.id = 1  -- 查看第一个物料的所有交易记录
ORDER BY t.transaction_time;
