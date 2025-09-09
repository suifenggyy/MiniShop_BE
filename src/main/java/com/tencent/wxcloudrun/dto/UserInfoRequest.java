package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserInfoRequest {

    private Long uid;

    private String userName;

    private String deliveryAddress;

    private String deliveryPhone;

    private String deliveryUserName;

    private Integer selectPackageId;

    private String selectPackageName;
}

