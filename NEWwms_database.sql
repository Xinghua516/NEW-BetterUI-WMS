-- 创建WMS系统数据库
CREATE DATABASE IF NOT EXISTS wms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE wms_db;

-- 1. 物料分类表
-- 用于对物料进行分类管理，支持多级分类
CREATE TABLE material_categories (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     parent_id BIGINT COMMENT '父分类ID，顶级分类为NULL',
                                     category_code VARCHAR(50) NOT NULL UNIQUE COMMENT '分类编码',
                                     category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
                                     description TEXT COMMENT '分类描述',
                                     sort_order INT DEFAULT 0 COMMENT '排序序号',
                                     status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用，INACTIVE-停用',
                                     created_by VARCHAR(50) COMMENT '创建人',
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_by VARCHAR(50) COMMENT '更新人',
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     FOREIGN KEY (parent_id) REFERENCES material_categories(id) ON DELETE SET NULL,
                                     INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料分类表';

-- 2. 仓库信息表
-- 存储仓库基本信息
CREATE TABLE warehouses (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            warehouse_code VARCHAR(50) NOT NULL UNIQUE COMMENT '仓库编码',
                            warehouse_name VARCHAR(100) NOT NULL COMMENT '仓库名称',
                            location VARCHAR(200) COMMENT '仓库位置',
                            contact_person VARCHAR(50) COMMENT '联系人',
                            contact_phone VARCHAR(20) COMMENT '联系电话',
                            status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用，INACTIVE-停用',
                            description TEXT COMMENT '仓库描述',
                            created_by VARCHAR(50) COMMENT '创建人',
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_by VARCHAR(50) COMMENT '更新人',
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库信息表';

-- 3. 物料信息表
-- 存储所有物料的基本信息
CREATE TABLE materials (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           material_code VARCHAR(50) NOT NULL UNIQUE COMMENT '物料编码',
                           material_name VARCHAR(200) NOT NULL COMMENT '物料名称',
                           category_id BIGINT COMMENT '物料分类ID',
                           specification VARCHAR(200) COMMENT '规格型号',
                           unit VARCHAR(20) NOT NULL COMMENT '计量单位',
                           barcode VARCHAR(100) UNIQUE COMMENT '条形码',
                           brand VARCHAR(100) COMMENT '品牌',
                           supplier VARCHAR(100) COMMENT '供应商',
                           status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用，INACTIVE-停用',
                           default_warehouse_id BIGINT COMMENT '默认仓库ID',
                           description TEXT COMMENT '物料描述',
                           created_by VARCHAR(50) COMMENT '创建人',
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_by VARCHAR(50) COMMENT '更新人',
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (category_id) REFERENCES material_categories(id) ON DELETE SET NULL,
                           FOREIGN KEY (default_warehouse_id) REFERENCES warehouses(id) ON DELETE SET NULL,
                           INDEX idx_category_id (category_id),
                           INDEX idx_supplier (supplier)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料信息表';

-- 4. 库存表
-- 存储各物料在各仓库的当前库存数量
CREATE TABLE inventory (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           material_id BIGINT NOT NULL COMMENT '物料ID',
                           warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
                           quantity INT NOT NULL DEFAULT 0 COMMENT '当前库存数量',
                           available_quantity INT NOT NULL DEFAULT 0 COMMENT '可用库存数量',
                           locked_quantity INT NOT NULL DEFAULT 0 COMMENT '锁定库存数量',
                           last_stocktake_time DATETIME COMMENT '最后盘点时间',
                           last_update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                           FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE,
                           FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
                           UNIQUE KEY uk_material_warehouse (material_id, warehouse_id), -- 确保同一物料在同一仓库只有一条记录
                           INDEX idx_material_id (material_id),
                           INDEX idx_warehouse_id (warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 5. 库存预警设置表
-- 存储各物料的库存预警阈值设置
CREATE TABLE inventory_alert_settings (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          material_id BIGINT NOT NULL COMMENT '物料ID',
                                          warehouse_id BIGINT COMMENT '仓库ID，NULL表示所有仓库',
                                          min_stock INT NOT NULL DEFAULT 0 COMMENT '最低库存预警值',
                                          max_stock INT COMMENT '最高库存预警值',
                                          is_enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用预警',
                                          created_by VARCHAR(50) COMMENT '创建人',
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          updated_by VARCHAR(50) COMMENT '更新人',
                                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE,
                                          FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
                                          UNIQUE KEY uk_material_warehouse (material_id, warehouse_id), -- 确保同一物料在同一仓库只有一条设置
                                          INDEX idx_material_id (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存预警设置表';

-- 6. 库存预警表
-- 存储当前需要预警的库存信息（低库存或高库存）
CREATE TABLE inventory_alerts (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  material_id BIGINT NOT NULL COMMENT '物料ID',
                                  warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
                                  alert_type VARCHAR(20) NOT NULL COMMENT '预警类型：LOW_STOCK-低库存，HIGH_STOCK-高库存',
                                  current_quantity INT NOT NULL COMMENT '当前库存数量',
                                  threshold_value INT NOT NULL COMMENT '阈值',
                                  is_processed BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已处理',
                                  processed_by VARCHAR(50) COMMENT '处理人',
                                  processed_time DATETIME COMMENT '处理时间',
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE,
                                  FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
                                  UNIQUE KEY uk_material_warehouse_type (material_id, warehouse_id, alert_type),
                                  INDEX idx_is_processed (is_processed),
                                  INDEX idx_alert_type (alert_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存预警表';

-- 7. 出入库类型表
-- 定义出入库的类型
CREATE TABLE inventory_transaction_types (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             type_code VARCHAR(20) NOT NULL UNIQUE COMMENT '类型编码',
                                             type_name VARCHAR(50) NOT NULL COMMENT '类型名称',
                                             direction VARCHAR(10) NOT NULL COMMENT '方向：IN-入库，OUT-出库',
                                             description TEXT COMMENT '描述',
                                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出入库类型表';

-- 8. 出入库记录表
-- 记录所有物料的出入库操作
CREATE TABLE inventory_transactions (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        transaction_no VARCHAR(50) NOT NULL UNIQUE COMMENT '交易单号',
                                        transaction_type_id BIGINT NOT NULL COMMENT '交易类型ID',
                                        material_id BIGINT NOT NULL COMMENT '物料ID',
                                        warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
                                        quantity INT NOT NULL COMMENT '数量，正数表示入库，负数表示出库',
                                        unit_cost DECIMAL(12,2) COMMENT '单位成本',
                                        total_cost DECIMAL(12,2) COMMENT '总成本',
                                        reference_no VARCHAR(50) COMMENT '参考单号（如采购单号、销售单号）',
                                        transaction_time DATETIME NOT NULL COMMENT '交易时间',
                                        notes TEXT COMMENT '备注',
                                        created_by VARCHAR(50) NOT NULL COMMENT '操作人',
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (transaction_type_id) REFERENCES inventory_transaction_types(id),
                                        FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE,
                                        FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE,
                                        INDEX idx_transaction_time (transaction_time),
                                        INDEX idx_material_id (material_id),
                                        INDEX idx_warehouse_id (warehouse_id),
                                        INDEX idx_transaction_type_id (transaction_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出入库记录表';

-- 初始化出入库类型数据
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

-- 创建触发器：当出入库记录插入时，自动更新库存表
DELIMITER $$
CREATE TRIGGER trg_after_inventory_transactions_insert
    AFTER INSERT ON inventory_transactions
    FOR EACH ROW
BEGIN
    -- 声明变量
    DECLARE inv_id BIGINT;

    -- 检查库存记录是否存在
    SELECT id INTO inv_id
    FROM inventory
    WHERE material_id = NEW.material_id AND warehouse_id = NEW.warehouse_id;

    -- 存在则更新，不存在则插入
    IF inv_id IS NOT NULL THEN
        UPDATE inventory
        SET quantity = quantity + NEW.quantity,
            available_quantity = available_quantity + NEW.quantity,
            last_update_time = CURRENT_TIMESTAMP
        WHERE id = inv_id;
    ELSE
        INSERT INTO inventory (material_id, warehouse_id, quantity, available_quantity)
        VALUES (NEW.material_id, NEW.warehouse_id, NEW.quantity, NEW.quantity);
    END IF;
END$$
DELIMITER ;

-- 创建触发器：当库存更新时，自动检查并更新库存预警表
DELIMITER $$
CREATE TRIGGER trg_after_inventory_update
    AFTER UPDATE ON inventory
    FOR EACH ROW
BEGIN
    -- 声明变量
    DECLARE min_stock_val INT;
    DECLARE max_stock_val INT;

    -- 获取该物料在特定仓库的预警阈值设置
    SELECT COALESCE(min_stock, 0), COALESCE(max_stock, 0) INTO min_stock_val, max_stock_val
    FROM inventory_alert_settings
    WHERE material_id = NEW.material_id
      AND (warehouse_id = NEW.warehouse_id OR warehouse_id IS NULL)
    ORDER BY warehouse_id DESC
    LIMIT 1;

    -- 处理低库存预警
    IF NEW.quantity <= min_stock_val THEN
        -- 插入或更新低库存预警记录
        INSERT INTO inventory_alerts
        (material_id, warehouse_id, alert_type, current_quantity, threshold_value)
        VALUES
            (NEW.material_id, NEW.warehouse_id, 'LOW_STOCK', NEW.quantity, min_stock_val)
        ON DUPLICATE KEY UPDATE
                             current_quantity = NEW.quantity,
                             threshold_value = min_stock_val,
                             is_processed = FALSE,
                             processed_by = NULL,
                             processed_time = NULL;
    ELSE
        -- 库存高于最低阈值，删除低库存预警
        DELETE FROM inventory_alerts
        WHERE material_id = NEW.material_id
          AND warehouse_id = NEW.warehouse_id
          AND alert_type = 'LOW_STOCK';
    END IF;

    -- 处理高库存预警
    IF max_stock_val > 0 AND NEW.quantity >= max_stock_val THEN
        -- 插入或更新高库存预警记录
        INSERT INTO inventory_alerts
        (material_id, warehouse_id, alert_type, current_quantity, threshold_value)
        VALUES
            (NEW.material_id, NEW.warehouse_id, 'HIGH_STOCK', NEW.quantity, max_stock_val)
        ON DUPLICATE KEY UPDATE
                             current_quantity = NEW.quantity,
                             threshold_value = max_stock_val,
                             is_processed = FALSE,
                             processed_by = NULL,
                             processed_time = NULL;
    ELSE
        -- 库存低于最高阈值，删除高库存预警
        DELETE FROM inventory_alerts
        WHERE material_id = NEW.material_id
          AND warehouse_id = NEW.warehouse_id
          AND alert_type = 'HIGH_STOCK';
    END IF;
END$$
DELIMITER ;
