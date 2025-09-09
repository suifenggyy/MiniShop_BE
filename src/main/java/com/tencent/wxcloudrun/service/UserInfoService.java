package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.UserInfo;
import java.util.List;
import java.util.Optional;

public interface UserInfoService {

    Optional<UserInfo> getUserInfoById(Long uid);

    List<UserInfo> getAllUserInfo();

    boolean insertUserInfo(UserInfo userInfo);

    boolean updateUserInfo(UserInfo userInfo);

    boolean deleteUserInfo(Long uid);
}
