package com.api.expose.system.controller.admin.mail.vo.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 邮件发送 Req VO")
@Data
public class MailTemplateSendReqVO {

    @Schema(description = "接收邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "7685413@qq.com")
    @NotEmpty(message = "接收邮箱不能为空")
    private String mail;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_01")
    @NotNull(message = "模板编码不能为空")
    private String templateCode;

    @Schema(description = "模板参数")
    private Map<String, Object> templateParams;

    @Schema(description = "附件文件列表")
    private List<MultipartFile> attachments;

    @Schema(description = "附件文件URL列表（用于上传后的文件引用）")
    private List<String> attachmentUrls;

}
