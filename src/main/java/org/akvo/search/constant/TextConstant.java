/*
 * Trent Inc.
 * Copyright (c) 2018- 2020.
 */

package org.akvo.search.constant;

/**
 * 输出文本常量
 *
 * @author trent
 * @date 2020年09月05日
 * @since JDK 1.8
 */
public interface TextConstant {
    String BACKUP = "已备份hosts 文件至: %s\n\n";
    String INPUT_SITE_INFO = "请在搜索栏中写入网址\n";
    String INTRANET = "内网IP: %s\n";
    String NO_RECORD_FROM_HOSTS = "无记录，hosts 文件中没有需要更新的网址\n";
    String NO_CORRESPOND_IP = "输入的网址: %s没有找到对应ip\n";
    String ERROR_INFO_FORMATTER = "Error in [%s]\n\n";
    String ERROR_FROM_SITE = "Error in [%s] \nOf the \"%s\"\n\n";
    String DONE = "完成 (%d/%d)\n";
    String UNDONE = "未完成： %s\n";
    String NEXT_SEARCH = "\n等待下一次搜索\n";
    String NAME_INFO = "正在使用[%s] 进行第 %d 次查询\n\n";

}