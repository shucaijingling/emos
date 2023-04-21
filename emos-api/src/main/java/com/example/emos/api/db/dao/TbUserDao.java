package com.example.emos.api.db.dao;

import com.example.emos.api.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@Mapper
public interface TbUserDao {
    public Set<String> searchUserPermissions(int userId);

    public HashMap searchById(int userId);

    public Integer searchIdByOpenId(String openId);

    public HashMap searchUserSummary(int userId);

    public HashMap searchUserInfo(int userId);

    public Integer searchDeptManagerId(int id);

    public Integer searchGmId();

    public ArrayList<HashMap> searchAllUser();


    public Integer login(HashMap map);

    public int updatePassword(HashMap map);

    public ArrayList<HashMap> searchUserByPage(HashMap map);

    public long searchUserCount(HashMap map);

    public int insert(TbUser tbUser);

    public int update(HashMap map);

    public int deleteUserByIds(Integer[] ids);
}