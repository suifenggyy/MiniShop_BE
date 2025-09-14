package com.tencent.wxcloudrun.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class UserInfo implements Serializable {

    private Long uid;

    private String userName;

    private String address;

    private Integer selectPackageId;

    private LocalDateTime selectTs;

    private String postInfo;

    private List<Map<String, String>> postInfoMaps;

    private String status;

    private String selectPackageName;

    private String deliveryUserName;

    private String deliveryPhone;

    private LocalDateTime deliveryTime;

    private String valid;

    private LocalDateTime gmtModify;

    private Integer role;

    // SysParams相关字段
    private String shopText;

    private String package1Name;

    private String package1Desc;

    private String package2Name;

    private String package2Desc;

    private String package1Img;

    private String package2Img;

    private LocalDateTime endTime;

    private Integer contactHide;

    private String deliveryIdTitle;

    private String deliveryIdText;

    private String deliveryNameTitle;

    private String deliveryNameText;

    private String deliveryAddressTitle;

    private String deliveryAddressText;

    private String addressPageTitle;

    private String systemPhone;

    private String mushroomPhone;

    private String otherPhone;

    private String checkAlert;

    private String package1Details;

    private String package2Details;

    private String backimg;
}
