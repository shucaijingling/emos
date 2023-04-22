package com.example.emos.api.controller.form;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "查询某个会议室一周会议表单")
public class SearchOfflineMeetingInWeekForm {

    @Schema(description = "日期")
    private String date;

    @NotBlank(message = "mold不能为空")
    @Pattern(regexp = "^全部会议$|^我的会议$", message = "mold内容不正确")
    @Schema(description = "模式")
    private String mold;

    @NotBlank(message = "name不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,20}$", message = "name内容不正确")
    @Schema(description = "会议室名称")
    private String name;
}
