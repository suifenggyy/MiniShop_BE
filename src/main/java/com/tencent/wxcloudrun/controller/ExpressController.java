package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.ExpressQueryRequest;
import com.tencent.wxcloudrun.model.ExpressStatus;
import com.tencent.wxcloudrun.service.ExpressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 快递状态查询控制器
 */
@RestController
@RequestMapping("/api/express")
public class ExpressController {

    final ExpressService expressService;
    final Logger logger;

    public ExpressController(@Autowired ExpressService expressService) {
        this.expressService = expressService;
        this.logger = LoggerFactory.getLogger(ExpressController.class);
    }

    /**
     * 根据快递单号查询快递状态
     * @param trackingNumber 快递单号
     * @return API response json
     */
    @GetMapping("/status/{trackingNumber}")
    public ApiResponse queryExpressStatus(@PathVariable String trackingNumber) {
        logger.info("/api/express/status/{} get request", trackingNumber);

        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            return ApiResponse.error("快递单号不能为空");
        }

        try {
            ExpressStatus expressStatus = expressService.queryExpressStatus(trackingNumber);
            return ApiResponse.ok(expressStatus);
        } catch (Exception e) {
            logger.error("查询快递状态失败，快递单号：{}", trackingNumber, e);
            return ApiResponse.error("查询快递状态失败：" + e.getMessage());
        }
    }

    /**
     * 根据快递单号和快递公司代码查询快递状态
     * @param request 查询请求参数
     * @return API response json
     */
    @PostMapping("/status")
    public ApiResponse queryExpressStatusWithCarrier(@RequestBody ExpressQueryRequest request) {
        logger.info("/api/express/status post request, trackingNumber: {}, carrierCode: {}",
                   request.getTrackingNumber(), request.getCarrierCode());

        if (request.getTrackingNumber() == null || request.getTrackingNumber().trim().isEmpty()) {
            return ApiResponse.error("快递单号不能为空");
        }

        try {
            ExpressStatus expressStatus = expressService.queryExpressStatus(
                request.getTrackingNumber(),
                request.getCarrierCode()
            );
            return ApiResponse.ok(expressStatus);
        } catch (Exception e) {
            logger.error("查询快递状态失败，快递单号：{}，快递公司：{}",
                        request.getTrackingNumber(), request.getCarrierCode(), e);
            return ApiResponse.error("查询快递状态失败：" + e.getMessage());
        }
    }

    /**
     * 批量查询快递状态
     * @param trackingNumbers 快递单号列表（逗号分隔）
     * @return API response json
     */
    @GetMapping("/status/batch")
    public ApiResponse batchQueryExpressStatus(@RequestParam String trackingNumbers) {
        logger.info("/api/express/status/batch get request, trackingNumbers: {}", trackingNumbers);

        if (trackingNumbers == null || trackingNumbers.trim().isEmpty()) {
            return ApiResponse.error("快递单号不能为空");
        }

        String[] numbers = trackingNumbers.split(",");
        if (numbers.length > 10) {
            return ApiResponse.error("批量查询最多支持10个快递单号");
        }

        try {
            java.util.List<ExpressStatus> results = new java.util.ArrayList<>();
            for (String number : numbers) {
                if (number.trim().isEmpty()) continue;
                ExpressStatus status = expressService.queryExpressStatus(number.trim());
                results.add(status);
            }
            return ApiResponse.ok(results);
        } catch (Exception e) {
            logger.error("批量查询快递状态失败，快递单号：{}", trackingNumbers, e);
            return ApiResponse.error("批量查询快递状态失败：" + e.getMessage());
        }
    }
}
