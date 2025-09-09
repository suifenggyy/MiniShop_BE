package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class ExpressQueryRequest {

    private String trackingNumber;

    private String carrierCode; // 可选，快递公司代码，如：SF(顺丰)、YTO(圆通)、STO(申通)等
}
