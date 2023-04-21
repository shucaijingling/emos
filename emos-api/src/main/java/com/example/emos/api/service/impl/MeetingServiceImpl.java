package com.example.emos.api.service.impl;

import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.dao.TbMeetingDao;
import com.example.emos.api.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private TbMeetingDao tbMeetingDao;

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
}
