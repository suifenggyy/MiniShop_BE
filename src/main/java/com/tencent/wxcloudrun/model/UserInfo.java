package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserInfo implements Serializable {

    private Long uid;

    private String userName;

    private String address;

    private Integer selectSetId;

    private LocalDateTime selectTs;

    private String postInfo;

    private String status;
}
