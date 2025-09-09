package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.model.ExpressStatus;
import com.tencent.wxcloudrun.service.ExpressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpressServiceImpl implements ExpressService {

    private final Logger logger = LoggerFactory.getLogger(ExpressServiceImpl.class);

    @Override
    public ExpressStatus queryExpressStatus(String trackingNumber) {
        return queryExpressStatus(trackingNumber, null);
    }

    @Override
    public ExpressStatus queryExpressStatus(String trackingNumber, String carrierCode) {
        logger.info("查询快递状态，快递单号：{}，快递公司：{}", trackingNumber, carrierCode);

        // 这里模拟快递查询逻辑，实际项目中可以调用第三方快递查询API
        // 比如快递鸟、快递100等API服务
        ExpressStatus expressStatus = new ExpressStatus();
        expressStatus.setTrackingNumber(trackingNumber);

        // 根据快递单号前缀识别快递公司
        String detectedCarrier = detectCarrier(trackingNumber, carrierCode);
        expressStatus.setCarrierName(detectedCarrier);

        // 模拟快递状态数据
        expressStatus.setCurrentStatus(generateMockStatus(trackingNumber));
        expressStatus.setRecipientName("收件人姓名");
        expressStatus.setRecipientPhone("138****8888");
        expressStatus.setRecipientAddress("北京市朝阳区xxx街道xxx号");
        expressStatus.setLastUpdateTime(LocalDateTime.now());

        // 生成模拟的物流轨迹
        List<ExpressStatus.ExpressTrack> trackList = generateMockTrackList();
        expressStatus.setTrackList(trackList);

        return expressStatus;
    }

    /**
     * 根据快递单号识别快递公司
     */
    private String detectCarrier(String trackingNumber, String carrierCode) {
        if (carrierCode != null && !carrierCode.isEmpty()) {
            return getCarrierName(carrierCode);
        }

        // 根据单号规则识别快递公司
        if (trackingNumber.startsWith("SF")) {
            return "顺丰快递";
        } else if (trackingNumber.length() == 12 && trackingNumber.matches("\\d+")) {
            return "中通快递";
        } else if (trackingNumber.length() == 13 && trackingNumber.matches("\\d+")) {
            return "圆通快递";
        } else if (trackingNumber.length() == 12 && trackingNumber.matches("[A-Z0-9]+")) {
            return "申通快递";
        } else {
            return "未知快递公司";
        }
    }

    /**
     * 根据快递公司代码获取快递公司名称
     */
    private String getCarrierName(String carrierCode) {
        switch (carrierCode.toUpperCase()) {
            case "SF":
                return "顺丰快递";
            case "YTO":
                return "圆通快递";
            case "STO":
                return "申通快递";
            case "ZTO":
                return "中通快递";
            case "EMS":
                return "中国邮政EMS";
            case "JD":
                return "京东快递";
            default:
                return "其他快递";
        }
    }

    /**
     * 生成模拟的快递状态
     */
    private String generateMockStatus(String trackingNumber) {
        // 根据单号末位数字模拟不同状态
        int lastDigit = Character.getNumericValue(trackingNumber.charAt(trackingNumber.length() - 1));
        switch (lastDigit % 5) {
            case 0:
                return "已揽收";
            case 1:
                return "运输中";
            case 2:
                return "派送中";
            case 3:
                return "已签收";
            case 4:
                return "异常";
            default:
                return "未知状态";
        }
    }

    /**
     * 生成模拟的物流轨迹
     */
    private List<ExpressStatus.ExpressTrack> generateMockTrackList() {
        List<ExpressStatus.ExpressTrack> trackList = new ArrayList<>();

        ExpressStatus.ExpressTrack track1 = new ExpressStatus.ExpressTrack();
        track1.setTime(LocalDateTime.now().minusDays(2));
        track1.setLocation("深圳市");
        track1.setDescription("快件已在深圳分拣中心完成分拣");
        track1.setStatus("运输中");
        trackList.add(track1);

        ExpressStatus.ExpressTrack track2 = new ExpressStatus.ExpressTrack();
        track2.setTime(LocalDateTime.now().minusDays(1));
        track2.setLocation("北京市");
        track2.setDescription("快件已到达北京转运中心");
        track2.setStatus("运输中");
        trackList.add(track2);

        ExpressStatus.ExpressTrack track3 = new ExpressStatus.ExpressTrack();
        track3.setTime(LocalDateTime.now().minusHours(6));
        track3.setLocation("北京市朝阳区");
        track3.setDescription("快件正在派送中，配送员：张师傅，电话：138****1234");
        track3.setStatus("派送中");
        trackList.add(track3);

        return trackList;
    }
}
