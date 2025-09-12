package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserInfoRequest {

    private Long uid;

    // 通过微信获取手机号
    private String code;

    private String deliveryAddress;

    private String deliveryPhone;

    private String deliveryUserName;

    private Integer selectPackageId;

    private String selectPackageName;

}

