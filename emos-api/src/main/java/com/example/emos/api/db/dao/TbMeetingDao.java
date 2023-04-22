package com.example.emos.api.db.dao;

import com.example.emos.api.db.pojo.TbMeeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbMeetingDao {
    public boolean searchMeetingMembersInSameDept(String uuid);
    public HashMap searchMeetingById(HashMap param);

    public ArrayList<HashMap> searchOfflineMeetingByPage(HashMap map);

    public long searchOfflineMeetingCount(HashMap map);

    public int updateMeetingInstanceId(HashMap map);

    public int insert(TbMeeting meeting);

    public ArrayList<HashMap> searchOfflineMeetingInWeek(HashMap map);

    public HashMap searchMeetingInfo(long id);

    public HashMap searchCurrentMeetingInfo(long id);

    public int deleteMeetingApplication(HashMap map);
}