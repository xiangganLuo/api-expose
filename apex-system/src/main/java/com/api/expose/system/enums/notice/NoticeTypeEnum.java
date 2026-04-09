package com.api.expose.system.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知类型
 *
 * @author apex
 */
@Getter
@AllArgsConstructor
public enum NoticeTypeEnum {

    NOTICE(1),
    ANNOUNCEMENT(2);

    /**
     * 类型
     */
    private final Integer type;

}
