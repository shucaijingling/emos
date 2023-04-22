package com.example.emos.api.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.dao.TbMeetingDao;
import com.example.emos.api.db.pojo.TbMeeting;
import com.example.emos.api.exception.EmosException;
import com.example.emos.api.service.MeetingService;
import com.example.emos.api.task.MeetingWorkflowTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private TbMeetingDao tbMeetingDao;

    @Autowired
    private MeetingWorkflowTask meetingWorkflowTask;

    @Override
    public PageUtils ssearchOfflineMeetingByPage(HashMap map) {
        ArrayList<HashMap> list = tbMeetingDao.searchOfflineMeetingByPage(map);
        long count = tbMeetingDao.searchOfflineMeetingCount(map);
        int start = (Integer) map.get("start");
        int length = (Integer) map.get("length");
        //把meeting字段转换成json数组对象
        for (HashMap hashMap : list) {
            String meeting = (String) hashMap.get("meeting");
            //如果meeting是有效字符串，就转换成json数组对象
            if (meeting != null && meeting.length() > 0) {
                hashMap.replace("meeting", JSONUtil.parseArray(meeting));
            }
        }
        return new PageUtils(list,count,start,length);
    }

    @Override
    public int insert(TbMeeting meeting) {
        int rows = tbMeetingDao.insert(meeting);
        if (rows != 1) {
            throw new EmosException("会议添加失败");
        }
        meetingWorkflowTask.startMeetingWorkflow(meeting.getUuid(), meeting.getCreatorId(), meeting.getTitle(),
                meeting.getDate(), meeting.getStart()+ ":00", "线下会议");
        return rows;
    }

    @Override
    public ArrayList<HashMap> searchOfflineMeetingInWeek(HashMap map) {
        return tbMeetingDao.searchOfflineMeetingInWeek(map);
    }

    @Override
    public HashMap searchMeetingInfo(short status, long id) {
        //判断正在进行中的会议
        HashMap map;
        if (status == 4) {
            map = tbMeetingDao.searchCurrentMeetingInfo(id);
        }else {
            map = tbMeetingDao.searchMeetingInfo(id);
        }
        return map;
    }

    @Override
    public int deleteMeetingApplication(HashMap map) {
        Long id = MapUtil.getLong(map, "id");
        String uuid = MapUtil.getStr(map, "uuid");
        String instanceId = MapUtil.getStr(map, "instanceId");
        //查询会议详情，要判断是否距离会议开始不足20分钟
        HashMap meeting = tbMeetingDao.searchMeetingById(map);
        String date = MapUtil.getStr(meeting, "date");
        String start = MapUtil.getStr(meeting, "start");
        int status = MapUtil.getInt(meeting, "status");
        boolean isCreator = Boolean.parseBoolean(MapUtil.getStr(meeting, "isCreator"));
        DateTime dateTime = DateUtil.parse(date + " " + start);
        DateTime now = DateUtil.date();

        //距离会议开始不足20分钟，不能删除会议
        if (now.isAfterOrEquals(dateTime.offset(DateField.MINUTE, -20))) {
            throw new EmosException("距离会开始不足20分钟，不能删除会议");
        }

        //只能申请人删除该会议
        if (!isCreator) {
            throw new EmosException("只能申请人删除该会议");
        }

        //待审批和未开始的会议可以删除
        if (status==1||status==3) {
            int rows = tbMeetingDao.deleteMeetingApplication(map);
            if (rows == 1) {
                String reason = MapUtil.getStr(map, "reason");
                meetingWorkflowTask.deleteMeetingApplication(uuid,instanceId,reason);
            }
            return rows;
        }else {
            throw new EmosException("只能删除待审批和未开始的会议");
        }
    }
}
