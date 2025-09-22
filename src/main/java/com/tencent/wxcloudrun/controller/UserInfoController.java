package com.tencent.wxcloudrun.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.UserInfoRequest;
import com.tencent.wxcloudrun.model.SysParams;
import com.tencent.wxcloudrun.model.UserInfo;
import com.tencent.wxcloudrun.service.SysParamsService;
import com.tencent.wxcloudrun.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 用户信息控制器
 */
@RestController
@RequestMapping("/api/userinfo")
public class UserInfoController {

    @Autowired
    private RestTemplate restTemplate;
    final UserInfoService userInfoService;
    final Logger logger;
    final SysParamsService sysParamsService;

    public UserInfoController(@Autowired UserInfoService userInfoService, @Autowired SysParamsService sysParamsService) {
        this.userInfoService = userInfoService;
        this.sysParamsService = sysParamsService;
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

        boolean success = userInfoService.insertUserInfo(userInfo);
        if (success) {
            return ApiResponse.ok(userInfo);
        } else {
            return ApiResponse.error("创建用户失败");
        }
    }


    /**
     * 检查用户信息
     * @param request 用户信息请求体
     * @return API response json
     */
    @PostMapping("/checkUser")
    public ApiResponse checkUser(@RequestBody UserInfoRequest request) {
        logger.info("/api/userinfo/checkUser post request: {}", JSON.toJSONString(request));
        Long phoneNumber = request.getUid();
        if (phoneNumber == null) {
            if (StringUtils.isEmpty(request.getCode())) {
                return new ApiResponse(401, "uid_empty", null);
            }
            // 使用code获取手机号
            String phoneUrl = "https://api.weixin.qq.com/wxa/business/getuserphonenumber";
            // 构建请求体
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("code", request.getCode());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> phoneResponse = restTemplate.postForEntity(phoneUrl, entity, String.class);
            // 解析返回的手机号信息
            JSONObject phoneObj = JSON.parseObject(phoneResponse.getBody());
            // 根据微信接口返回的JSON结构解析手机号
            // 正常返回示例：{ "errcode":0, "errmsg":"ok", "phone_info": { "phoneNumber":"xxxx", "purePhoneNumber": "xxxx", "countryCode": 86, "watermark": { "timestamp": 1637743194, "appid": "xxxx" } } }
            if (phoneObj.getInteger("errcode") == 0) {
                JSONObject phoneInfo = phoneObj.getJSONObject("phone_info");
                phoneNumber = phoneInfo.getLong("phoneNumber");
            } else {
                // 处理错误
                return new ApiResponse(401, "uid_empty", null);
            }

        }

        // 根据uid查询数据库
        Optional<UserInfo> userInfo = userInfoService.getUserInfoById(phoneNumber);

        // 若不存在，返回错误信息
        if (!userInfo.isPresent()) {
            return new ApiResponse(501, "uid_not_match", null);
        }

        // 若存在，但是用户姓名与传入的不一致
        UserInfo existingUser = userInfo.get();

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

        int type = existingUser.getRole() != null ? existingUser.getRole() : 0;
        Optional<SysParams> sysParamsOpt = sysParamsService.getSysParamsById(type);
        if (sysParamsOpt.isPresent()) {
            SysParams sysParams = sysParamsOpt.get();
            // sysParams中信息转存到existingUser
            existingUser.setShopText(sysParams.getShopText());
            existingUser.setPackage1Name(sysParams.getPackage1Name());
            existingUser.setPackage1Desc(sysParams.getPackage1Desc());
            existingUser.setPackage2Name(sysParams.getPackage2Name());
            existingUser.setPackage2Desc(sysParams.getPackage2Desc());
            existingUser.setPackage1Img(sysParams.getPackage1Img());
            existingUser.setPackage2Img(sysParams.getPackage2Img());
            existingUser.setEndTime(sysParams.getEndTime());
            existingUser.setContactHide(sysParams.getContactHide());
            // 新增的delivery相关字段
            existingUser.setDeliveryIdTitle(sysParams.getDeliveryIdTitle());
            existingUser.setDeliveryIdText(sysParams.getDeliveryIdText());
            existingUser.setDeliveryNameTitle(sysParams.getDeliveryNameTitle());
            existingUser.setDeliveryNameText(sysParams.getDeliveryNameText());
            existingUser.setDeliveryAddressTitle(sysParams.getDeliveryAddressTitle());
            existingUser.setDeliveryAddressText(sysParams.getDeliveryAddressText());
            // 新增的页面标题和电话字段
            existingUser.setAddressPageTitle(sysParams.getAddressPageTitle());
            existingUser.setSystemPhone(sysParams.getSystemPhone());
            existingUser.setMushroomPhone(sysParams.getMushroomPhone());
            existingUser.setOtherPhone(sysParams.getOtherPhone());
            // 新增的检查提醒字段
            existingUser.setCheckAlert(sysParams.getCheckAlert());
            // 新增的套餐详情字段
            existingUser.setPackage1Details(sysParams.getPackage1Details());
            existingUser.setPackage2Details(sysParams.getPackage2Details());
            // 新增的背景图片字段
            existingUser.setBackimg(sysParams.getBackimg());
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
        logger.info("/api/userinfo/confirmDeliveryInfo post request: {}",
                   JSON.toJSONString(request));

        if (request.getUid() == null) {
            return new ApiResponse(2401, "uid_empty", null);
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

        // 检查status字段值是否大于10
        String status = existingUser.getStatus();
        if (status != null && !status.trim().isEmpty()) {
            int statusValue = Integer.parseInt(status);
            if (statusValue > 10) {
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
        //if (request.getSelectPackageId() == 1) {
        //    updateUser.setPostInfo(
        //        "[{\"delivery_id\":\"464577584850772\",\"item_name\":\"大米\"},{\"delivery_id\":\"77128481867058\",\"item_name\":\"杂粮\"},{\"delivery_id\":\"464577584850772\",\"item_name\":\"五样\"},{\"delivery_id\":\"77128481867058\",\"item_name\":\"榛蘑\"}]"
        //    );
        //} else {
        //    updateUser.setPostInfo(
        //        "[{\"delivery_id\":\"464577584850772\",\"item_name\":\"枣|小米\"},{\"delivery_id\":\"77128481867058\",\"item_name\":\"黑豆油\"},{\"delivery_id\":\"464577584850772\",\"item_name\":\"五样\"},{\"delivery_id\":\"77128481867058\",\"item_name\":\"榛蘑\"}]"
        //    );
        //}
        updateUser.setRole(existingUser.getRole());

        boolean success = userInfoService.updateUserInfo(updateUser);
        if (success) {
            // 返回更新后的用户信息
            Optional<UserInfo> updatedUserInfo = userInfoService.getUserInfoById(request.getUid());
            return new ApiResponse(200, "", updatedUserInfo.orElse(null));
        } else {
            return new ApiResponse(504, "更新配送信息失败", null);
        }
    }

    /**
     * 重置用户状态
     * @param uid 用户ID
     * @return API response json
     */
    @PostMapping("/resetStatus/{uid}")
    public ApiResponse resetStatus(@PathVariable Long uid) {
        logger.info("/api/userinfo/resetStatus/{} post request", uid);

        if (uid == null) {
            return new ApiResponse(401, "uid_empty", null);
        }

        // 根据uid查询数户是否存在
        Optional<UserInfo> userInfo = userInfoService.getUserInfoById(uid);
        if (!userInfo.isPresent()) {
            return new ApiResponse(501, "uid_not_match", null);
        }

        // 更新status为0
        UserInfo updateUser = new UserInfo();
        updateUser.setUid(uid);
        updateUser.setStatus("0");

        boolean success = userInfoService.updateUserInfo(updateUser);
        if (success) {
            return new ApiResponse(200, "status reset successfully", null);
        } else {
            return new ApiResponse(504, "重置状态失败", null);
        }
    }
}
