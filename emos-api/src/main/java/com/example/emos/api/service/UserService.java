package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.pojo.TbUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public interface UserService {
    public HashMap createQrCode();

    public boolean checkQrCode(String code,String uuid);

    public HashMap wechatLogin(String uuid);

    public Set<String> searchUserPermissions(int userId);

    public HashMap searchUserSummary(int userId);

    public HashMap searchById(int userId);

    public ArrayList<HashMap> searchAllUser();

    public Integer login(HashMap map);

    public int updatePassword(HashMap map);

    public PageUtils searchUserByPage(HashMap map);

    public int insert(TbUser tbUser);

    public int update(HashMap map);
}
