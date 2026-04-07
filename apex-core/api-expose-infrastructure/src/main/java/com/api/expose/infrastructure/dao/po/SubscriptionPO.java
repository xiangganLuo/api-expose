package com.api.expose.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
 * @author xiangganluo
 */

/**
 * 订阅关系持久化对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("apex_subscription")
public class SubscriptionPO {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 应用ID */
    private Long appId;
    /** API资产ID */
    private Long apiAssetId;
    /** 订阅状态 (PENDING/APPROVED/REJECTED/REVOKED) */
    private String status;
    /** 申请时间 */
    private Date applyTime;
    /** 通过时间 */
    private Date approveTime;
    /** 备注 */
    private String remark;
}
