package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoRequest {

    private Long uid;

    private String userName;

    private String address;

    private Integer selectSetId;

    private LocalDateTime selectTs;

    private String postInfo;

    private String status;
}
