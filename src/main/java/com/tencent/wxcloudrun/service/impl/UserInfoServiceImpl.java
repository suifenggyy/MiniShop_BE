package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.UserInfoMapper;
import com.tencent.wxcloudrun.model.UserInfo;
import com.tencent.wxcloudrun.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    final UserInfoMapper userInfoMapper;

    public UserInfoServiceImpl(@Autowired UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    @Override
    public Optional<UserInfo> getUserInfoById(Long uid) {
        return Optional.ofNullable(userInfoMapper.getUserInfoById(uid));
    }

    @Override
    public List<UserInfo> getAllUserInfo() {
        return userInfoMapper.getAllUserInfo();
    }

    @Override
    public boolean insertUserInfo(UserInfo userInfo) {
        return userInfoMapper.insertUserInfo(userInfo) > 0;
    }

    @Override
    public boolean updateUserInfo(UserInfo userInfo) {
        return userInfoMapper.updateUserInfo(userInfo) > 0;
    }

    @Override
    public boolean deleteUserInfo(Long uid) {
        return userInfoMapper.deleteUserInfo(uid) > 0;
    }
}
