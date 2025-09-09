package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserInfoMapper {

    UserInfo getUserInfoById(@Param("uid") Long uid);

    List<UserInfo> getAllUserInfo();

    int insertUserInfo(UserInfo userInfo);

    int updateUserInfo(UserInfo userInfo);

    int deleteUserInfo(@Param("uid") Long uid);
}
