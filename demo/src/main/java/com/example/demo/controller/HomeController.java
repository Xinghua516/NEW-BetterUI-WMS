
// 添加库存空间使用情况
Map<String, Integer> spaceUsage = calculateSpaceUsage();
model.addAttribute("spaceUsage", spaceUsage);


/**
 * 计算库存空间使用情况
 * @return 库存空间使用情况映射
 */
private Map<String, Integer> calculateSpaceUsage() {
    Map<String, Integer> spaceUsage = new HashMap<>();
    
    try {
        // 假设有一个方法可以从数据库获取总空间和已用空间
        int totalSpace = inventoryRecordRepository.getTotalSpace();
        int usedSpace = inventoryRecordRepository.getUsedSpace();
        
        if (totalSpace > 0) {
            int availableSpace = totalSpace - usedSpace;
            spaceUsage.put("usedSpace", usedSpace);
            spaceUsage.put("availableSpace", availableSpace);
        } else {
            // 如果没有数据，添加默认值避免前端错误
            spaceUsage.put("usedSpace", 0);
            spaceUsage.put("availableSpace", 100);
        }
    } catch (Exception e) {
        // 如果计算过程中出现异常，返回默认值
        spaceUsage.put("usedSpace", 0);
        spaceUsage.put("availableSpace", 100);
    }
    
    return spaceUsage;
}
