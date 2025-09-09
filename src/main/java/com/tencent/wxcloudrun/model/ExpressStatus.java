package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExpressStatus implements Serializable {

    private String trackingNumber;

    private String currentStatus;

    private String carrierName;

    private String recipientName;

    private String recipientPhone;

    private String recipientAddress;

    private LocalDateTime lastUpdateTime;

    private List<ExpressTrack> trackList;

    @Data
    public static class ExpressTrack implements Serializable {
        private LocalDateTime time;
        private String location;
        private String description;
        private String status;
    }
}
