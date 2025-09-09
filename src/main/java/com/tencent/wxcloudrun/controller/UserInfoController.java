package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.UserInfoRequest;
import com.tencent.wxcloudrun.model.UserInfo;
import com.tencent.wxcloudrun.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 用户信息控制器
 */
@RestController
@RequestMapping("/api/userinfo")
public class UserInfoController {

    final UserInfoService userInfoService;
    final Logger logger;

    public UserInfoController(@Autowired UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
        this.logger = LoggerFactory.getLogger(UserInfoController.class);
    }

    /**
     * 根据用户ID查询用户信息
     * @param uid 用户ID
     * @return API response json
     */
    @GetMapping("/{uid}")
    public ApiResponse getUserInfo(@PathVariable Long uid) {
        logger.info("/api/userinfo/{} get request", uid);

        Optional<UserInfo> userInfo = userInfoService.getUserInfoById(uid);
        if (userInfo.isPresent()) {
            return ApiResponse.ok(userInfo.get());
        } else {
            return ApiResponse.error("用户不存在");
        }
    }

    /**
     * 查询所有用户信息
     * @return API response json
     */
    @GetMapping
    public ApiResponse getAllUserInfo() {
        logger.info("/api/userinfo get all request");

        List<UserInfo> userInfoList = userInfoService.getAllUserInfo();
        return ApiResponse.ok(userInfoList);
    }

    /**
     * 创建用户信息
     * @param request 用户信息请求体
     * @return API response json
     */
    @PostMapping
    public ApiResponse createUserInfo(@RequestBody UserInfoRequest request) {
        logger.info("/api/userinfo post request, uid: {}", request.getUid());

        if (request.getUid() == null) {
            return ApiResponse.error("用户ID不能为空");
        }

        // 检查用户是否已存在
        Optional<UserInfo> existingUser = userInfoService.getUserInfoById(request.getUid());
        if (existingUser.isPresent()) {
            return ApiResponse.error("用户已存在");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUid(request.getUid());
        userInfo.setUserName(request.getUserName());
        userInfo.setAddress(request.getAddress());
        userInfo.setSelectSetId(request.getSelectSetId());
        userInfo.setSelectTs(request.getSelectTs());
        userInfo.setPostInfo(request.getPostInfo());
        userInfo.setStatus(request.getStatus());

        boolean success = userInfoService.insertUserInfo(userInfo);
        if (success) {
            return ApiResponse.ok(userInfo);
        } else {
            return ApiResponse.error("创建用户失败");
        }
    }

    /**
     * 更新用户信息
     * @param uid 用户ID
     * @param request 用户信息请求体
     * @return API response json
     */
    @PutMapping("/{uid}")
    public ApiResponse updateUserInfo(@PathVariable Long uid, @RequestBody UserInfoRequest request) {
        logger.info("/api/userinfo/{} put request", uid);

        // 检查用户是否存在
        Optional<UserInfo> existingUser = userInfoService.getUserInfoById(uid);
        if (!existingUser.isPresent()) {
            return ApiResponse.error("用户不存在");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUid(uid);
        userInfo.setUserName(request.getUserName());
        userInfo.setAddress(request.getAddress());
        userInfo.setSelectSetId(request.getSelectSetId());
        userInfo.setSelectTs(request.getSelectTs());
        userInfo.setPostInfo(request.getPostInfo());
        userInfo.setStatus(request.getStatus());

        boolean success = userInfoService.updateUserInfo(userInfo);
        if (success) {
            return ApiResponse.ok(userInfo);
        } else {
            return ApiResponse.error("更新用户失败");
        }
    }

    /**
     * 删除用户信息
     * @param uid 用户ID
     * @return API response json
     */
    @DeleteMapping("/{uid}")
    public ApiResponse deleteUserInfo(@PathVariable Long uid) {
        logger.info("/api/userinfo/{} delete request", uid);

        // 检查用户是否存在
        Optional<UserInfo> existingUser = userInfoService.getUserInfoById(uid);
        if (!existingUser.isPresent()) {
            return ApiResponse.error("用户不存在");
        }

        boolean success = userInfoService.deleteUserInfo(uid);
        if (success) {
            return ApiResponse.ok("删除成功");
        } else {
            return ApiResponse.error("删除用户失败");
        }
    }
}
