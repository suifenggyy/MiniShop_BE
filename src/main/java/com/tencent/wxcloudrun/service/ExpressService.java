package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.ExpressStatus;

public interface ExpressService {

    /**
     * 根据快递单号查询快递状态
     * @param trackingNumber 快递单号
     * @return 快递状态信息
     */
    ExpressStatus queryExpressStatus(String trackingNumber);

    /**
     * 根据快递单号和快递公司代码查询快递状态
     * @param trackingNumber 快递单号
     * @param carrierCode 快递公司代码
     * @return 快递状态信息
     */
    ExpressStatus queryExpressStatus(String trackingNumber, String carrierCode);
}
