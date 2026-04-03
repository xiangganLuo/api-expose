package com.api.expose.system.mq.message.mail;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 邮箱发送消息
 *
 */
@Data
public class MailSendMessage {

    /**
     * 邮件日志编号
     */
    @NotNull(message = "邮件日志编号不能为空")
    private Long logId;
    /**
     * 接收邮件地址
     */
    @NotNull(message = "接收邮件地址不能为空")
    private String mail;
    /**
     * 邮件账号编号
     */
    @NotNull(message = "邮件账号编号不能为空")
    private Long accountId;

    /**
     * 邮件发件人
     */
    private String nickname;
    /**
     * 邮件标题
     */
    @NotEmpty(message = "邮件标题不能为空")
    private String title;
    /**
     * 邮件内容
     */
    @NotEmpty(message = "邮件内容不能为空")
    private String content;

    /**
     * 附件信息列表
     */
    private List<MailAttachment> attachments;

    /**
     * 邮件附件信息
     */
    @Data
    public static class MailAttachment {
        /**
         * 附件文件名
         */
        private String filename;
        /**
         * 附件文件路径（存储在服务器上的路径）
         */
        private String filePath;
        /**
         * 附件内容类型
         */
        private String contentType;
        /**
         * 附件大小（字节）
         */
        private Long fileSize;
    }

}
