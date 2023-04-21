package com.example.emos.api.task;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.db.dao.TbMeetingDao;
import com.example.emos.api.db.dao.TbUserDao;
import com.example.emos.api.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class MeetingWorkflowTask {

    @Autowired
    private TbUserDao tbUserDao;

    @Autowired
    private TbMeetingDao tbMeetingDao;

    @Value("${emos.receiveNotify}")
    private String receiveNotify;

    @Value("${emos.code}")
    private String code;

    @Value("${emos.tcode}")
    private String tcode;

    @Value("${workflow.url}")
    private String workflow;


    @Async("AsyncTaskExecutor")
    public void startMeetingWorkflow(String uuid,
                                     int creatorId,
                                     String title,
                                     String date,
                                     String start,
                                     String meetingType) {
        //查询申请人基本信息
        HashMap info = tbUserDao.searchUserInfo(creatorId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("url", receiveNotify);
        jsonObject.set("uuid", uuid);
        jsonObject.set("creatorId", creatorId);
        jsonObject.set("createorName", info.get("name").toString());
        jsonObject.set("code", code);
        jsonObject.set("tcode", tcode);
        jsonObject.set("title", title);
        jsonObject.set("date", date);
        jsonObject.set("start", start);
        jsonObject.set("meetingType", meetingType);

        String[] roles = info.get("roles").toString().split("，");
        //判断用户角色是不是总经理，总经理创建的会议不需要审批，所以不需要查询总经理userId和部门经理id
        if (!ArrayUtil.contains(roles, "总经理")) {
            //查询部门经理userId
            Integer managerId = tbUserDao.searchDeptManagerId(creatorId);
            jsonObject.set("managerId", managerId);

            //查询总经理userId
            Integer gmId = tbUserDao.searchGmId();
            jsonObject.set("gmId", gmId);

            //查询参会人是否为同一个部门
            boolean bool = tbMeetingDao.searchMeetingMembersInSameDept(uuid);
            jsonObject.set("bool", bool);
        }
        String url = workflow + "/workflow/startMeetingProcess";
        HttpResponse response = HttpRequest.post(url).header("Content-Type", "application/json").body(jsonObject.toString()).execute();
        if (response.getStatus() == 200) {
            jsonObject = JSONUtil.parseObj(response.body());
            String instanceId = jsonObject.getStr("instanceId");
            HashMap map = new HashMap<>();
            map.put("uuid", uuid);
            map.put("instanceId", instanceId);
            //更新会议记录的instance_id字段
            int row = tbMeetingDao.updateMeetingInstanceId(map);
            if (row != 1) {
                throw new EmosException("保存会议工作流实例id失败");
            }
        }else {
            log.error(response.body());
        }
    }
}
