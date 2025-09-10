package com.tencent.wxcloudrun.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class SysParams implements Serializable {

    private Integer id;

    private String shopText;

    private String package1Name;

    private String package1Desc;

    private String package2Name;

    private String package2Desc;

    private String package1Img;

    private String package2Img;

    private LocalDateTime endTime;

    private Integer contactHide;
}
