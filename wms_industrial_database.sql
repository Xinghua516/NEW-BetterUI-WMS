-- 删除现有表（如果存在）
-- 注意删除顺序，先删除从表再删除主表
DROP TABLE IF EXISTS low_stock_items;
DROP TABLE IF EXISTS inventory_records;
DROP TABLE IF EXISTS bom_items;
DROP TABLE IF EXISTS materials;
DROP TABLE IF EXISTS bom_headers;

-- 创建BOM清单头表
-- 存储零件类型信息
CREATE TABLE bom_headers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bom_group VARCHAR(50) COMMENT 'BOM单组别',
    bom_code VARCHAR(50) NOT NULL UNIQUE COMMENT 'BOM单编号',
    status VARCHAR(20) COMMENT '状态',
    material_code VARCHAR(50) COMMENT '物料代码',
    material_name VARCHAR(200) COMMENT '物料名称',
    specification VARCHAR(200) COMMENT '规格',
    unit VARCHAR(20) COMMENT '单位',
    quantity DECIMAL(10,4) COMMENT '数量',
    cost DECIMAL(10,2) COMMENT '费用',
    remark TEXT COMMENT '备注',
    material_property VARCHAR(50) COMMENT '物料属性',
    auxiliary_property VARCHAR(50) COMMENT '辅助属性',
    creator VARCHAR(50) COMMENT '建立人员',
    created_date DATE COMMENT '建立日期',
    auditor VARCHAR(50) COMMENT '审核人员',
    audit_date DATE COMMENT '审核日期',
    last_updater VARCHAR(50) COMMENT '最后更新人员',
    last_update_date DATE COMMENT '最后更新日期',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BOM清单头表(零件类型表)';

-- 创建物料主数据表
-- 存储所有唯一物料信息
CREATE TABLE materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_code VARCHAR(50) NOT NULL UNIQUE COMMENT '物料代码',
    material_name VARCHAR(200) NOT NULL COMMENT '物料名称',
    specification VARCHAR(200) COMMENT '规格型号',
    material_property VARCHAR(50) COMMENT '物料属性',
    auxiliary_property VARCHAR(50) COMMENT '辅助属性',
    unit VARCHAR(20) NOT NULL COMMENT '单位',
    status VARCHAR(20) COMMENT '使用状态',
    warehouse VARCHAR(50) COMMENT '默认仓库',
    remark TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料主数据表';

-- 创建BOM清单明细表
-- 存储零件详细信息，通过bom_header_id关联到bom_headers表，通过material_id关联到materials表
CREATE TABLE bom_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bom_header_id BIGINT NOT NULL COMMENT 'BOM头ID',
    seq_no INT NOT NULL COMMENT '顺序号',
    material_id BIGINT NOT NULL COMMENT '物料ID',
    quantity DECIMAL(10,4) NOT NULL COMMENT '用量',
    loss_rate DECIMAL(7,4) COMMENT '损耗率(%)',
    status VARCHAR(20) COMMENT '使用状态',
    warehouse VARCHAR(50) COMMENT '发料仓库',
    min_stock INT DEFAULT 0 COMMENT '最低库存',
    remark TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (bom_header_id) REFERENCES bom_headers(id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE,
    INDEX idx_bom_header_id (bom_header_id),
    INDEX idx_material_id (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BOM清单明细表(零件详细信息表)';

-- 创建库存记录表
-- 记录零件出入库历史，通过material_id关联到materials表
CREATE TABLE inventory_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(10) NOT NULL COMMENT '记录类型(IN/OUT)',
    material_id BIGINT NOT NULL COMMENT '物料ID',
    material_code VARCHAR(50) NOT NULL COMMENT '物料代码',
    material_name VARCHAR(100) NOT NULL COMMENT '物料名称',
    specification VARCHAR(200) COMMENT '规格',
    quantity INT NOT NULL COMMENT '数量',
    warehouse VARCHAR(50) COMMENT '仓库',
    operator VARCHAR(50) COMMENT '操作人',
    time DATETIME NOT NULL COMMENT '时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_material_id (material_id),
    INDEX idx_material_code (material_code),
    INDEX idx_time (time),
    FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存记录表(零件出入库记录)';

-- 创建低库存预警表
-- 存储低库存量零件信息，通过material_id关联到materials表
CREATE TABLE low_stock_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id BIGINT NOT NULL COMMENT '物料ID',
    material_code VARCHAR(50) NOT NULL UNIQUE COMMENT '零件编号',
    material_name VARCHAR(100) NOT NULL COMMENT '零件名称',
    specification VARCHAR(200) COMMENT '规格',
    current_stock INT NOT NULL COMMENT '当前库存',
    min_stock INT NOT NULL COMMENT '最低库存',
    warehouse VARCHAR(50) COMMENT '仓库',
    status VARCHAR(20) COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_material_id (material_id),
    INDEX idx_material_code (material_code),
    INDEX idx_current_stock (current_stock),
    FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='低库存项目表(低库存量零件表)';