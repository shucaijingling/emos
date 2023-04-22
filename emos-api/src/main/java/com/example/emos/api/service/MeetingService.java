package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.pojo.TbMeeting;

import java.util.ArrayList;
import java.util.HashMap;

public interface MeetingService {

    public PageUtils ssearchOfflineMeetingByPage(HashMap map);

    public int insert(TbMeeting meeting);

    public ArrayList<HashMap> searchOfflineMeetingInWeek(HashMap map);

    public HashMap searchMeetingInfo(short status, long id);


    public int deleteMeetingApplication(HashMap map);
}
