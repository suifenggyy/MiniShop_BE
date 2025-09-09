package com.tencent.wxcloudrun.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.UserInfoRequest;
import com.tencent.wxcloudrun.model.UserInfo;
import com.tencent.wxcloudrun.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
            return ApiResponse.error(2503, "用户不存在");
        }
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

        boolean success = userInfoService.updateUserInfo(userInfo);
        if (success) {
            return ApiResponse.ok(userInfo);
        } else {
            return ApiResponse.error("更新用户失败");
        }
    }

    /**
     * 检查用户信息
     * @param request 用户信息请求体
     * @return API response json
     */
    @PostMapping("/checkUser")
    public ApiResponse checkUser(@RequestBody UserInfoRequest request) {
        logger.info("/api/userinfo/checkUser post request, uid: {}, userName: {}", request.getUid(), request.getUserName());

        if (request.getUid() == null) {
            return new ApiResponse(401, "uid_empty", null);
        }

        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            return new ApiResponse(402, "username_empty", null);
        }

        // 根据uid查询数据库
        Optional<UserInfo> userInfo = userInfoService.getUserInfoById(request.getUid());

        // 若不存在，返回错误信息
        if (!userInfo.isPresent()) {
            return new ApiResponse(501, "uid_not_match", null);
        }

        // 若存在，但是用户姓名与传入的不一致
        UserInfo existingUser = userInfo.get();
        if (!request.getUserName().equals(existingUser.getUserName())) {
            return new ApiResponse(502, "username_not_match", null);
        }

        // 反序列化postInfo到PostInfoMaps
        try {
            if (existingUser.getPostInfo() != null && !existingUser.getPostInfo().trim().isEmpty()) {
                List<Map<String, String>> postInfoMaps = JSON.parseObject(
                    existingUser.getPostInfo(),
                    new TypeReference<List<Map<String, String>>>() {}
                );
                existingUser.setPostInfoMaps(postInfoMaps);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse postInfo for uid: {}, error: {}", existingUser.getUid(), e.getMessage());
        }

        // 匹配则返回成功
        return new ApiResponse(200, "", existingUser);
    }

    /**
     * 确认配送信息
     * @param request 用户信息请求体
     * @return API response json
     */
    @PostMapping("/confirmDeliveryInfo")
    public ApiResponse confirmDeliveryInfo(@RequestBody UserInfoRequest request) {
        logger.info("/api/userinfo/confirmDeliveryInfo post request, uid: {}, userName: {}",
                   request.getUid(), request.getUserName());

        if (request.getUid() == null) {
            return new ApiResponse(2401, "uid_empty", null);
        }

        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            return new ApiResponse(2402, "username_empty", null);
        }

        if (request.getSelectPackageId() == null || request.getSelectPackageName() == null || request.getSelectPackageName().trim().isEmpty()) {
            return new ApiResponse(504, "package_empty", null);
        }

        // 根据uid查询数据库
        Optional<UserInfo> userInfo = userInfoService.getUserInfoById(request.getUid());

        // 若不存在，返回错误信息
        if (!userInfo.isPresent()) {
            return new ApiResponse(2501, "uid_not_match", null);
        }

        // 若存在，但是用户姓名与传入的不一致
        UserInfo existingUser = userInfo.get();
        if (!request.getUserName().equals(existingUser.getUserName())) {
            return new ApiResponse(2502, "username_not_match", null);
        }

        // 检查status字段值是否大于10
        String status = existingUser.getStatus();
        if (status != null && !status.trim().isEmpty()) {
            int statusValue = Integer.parseInt(status);
            if (statusValue > 0) {
                return new ApiResponse(601, "status_error", status);
            }
        }

        // 更新数据库中的address字段
        UserInfo updateUser = new UserInfo();
        updateUser.setUid(request.getUid());
        updateUser.setAddress(request.getDeliveryAddress());
        updateUser.setSelectPackageId(request.getSelectPackageId());
        updateUser.setSelectPackageName(request.getSelectPackageName());
        updateUser.setDeliveryUserName(request.getDeliveryUserName());
        updateUser.setDeliveryPhone(request.getDeliveryPhone());
        updateUser.setStatus("1"); // 设置状态为已提交

        boolean success = userInfoService.updateUserInfo(updateUser);
        if (success) {
            // 返回更新后的用户信息
            Optional<UserInfo> updatedUserInfo = userInfoService.getUserInfoById(request.getUid());
            return new ApiResponse(200, "", updatedUserInfo.orElse(null));
        } else {
            return new ApiResponse(504, "更新配送信息失败", null);
        }
    }
}
