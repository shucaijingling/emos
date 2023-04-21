package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.InsertMeetingForm;
import com.example.emos.api.controller.form.ReceiveNotifyForm;
import com.example.emos.api.controller.form.SearchOfflineMeetingByPageForm;
import com.example.emos.api.db.pojo.TbMeeting;
import com.example.emos.api.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/meeting")
@Tag(name = "MeetingController", description = "会议web接口")
@Slf4j
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @PostMapping("/searchOfflineMeetingByPage")
    @Operation(summary = "查询线下会议分页数据")
    @SaCheckLogin
    public R searchOfflineMeetingByPage(@Valid @RequestBody SearchOfflineMeetingByPageForm form) {
        Integer page = form.getPage();
        Integer length = form.getLength();
        Integer start = (page - 1) * length;
        HashMap map = new HashMap<>(){{
            put("date", form.getDate());
            put("mold", form.getMold());
            put("userId", StpUtil.getLoginId());
            put("start", start);
            put("length", length);
        }};
        PageUtils pageUtils = meetingService.ssearchOfflineMeetingByPage(map);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/insert")
    @Operation(summary = "添加会议")
    @SaCheckLogin
    public R insert(@Valid @RequestBody InsertMeetingForm form) {
        DateTime start = DateUtil.parse(form.getDate() + " " + form.getStart());
        DateTime end = DateUtil.parse(form.getDate() + " " + form.getEnd());
        if (start.isAfterOrEquals(end)) {
            return R.error("结束时间必须大于开始时间");
        }else if (new DateTime().isAfterOrEquals(start)) {
            return R.error("会议开始时间不能早于当前时间");
        }
        TbMeeting meeting = JSONUtil.parse(form).toBean(TbMeeting.class);
        meeting.setUuid(UUID.randomUUID().toString());
        meeting.setCreatorId(StpUtil.getLoginIdAsInt());
        meeting.setStatus((short)1);
        int rows = meetingService.insert(meeting);
        return R.ok().put("rows", rows);
    }


    @PostMapping("/receiveNotify")
    public R receiveNotify(@Valid @RequestBody ReceiveNotifyForm form) {
        if (form.getResult().equals("同意")) {
            log.debug(form.getUuid() + "的会议审批通过");
        }else {
            log.debug(form.getUuid() + "的会议审批不通过");
        }
        return R.ok();
    }
}
