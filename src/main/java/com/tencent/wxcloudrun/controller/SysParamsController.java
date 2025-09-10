package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.SysParams;
import com.tencent.wxcloudrun.service.SysParamsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 系统参数查询控制器（只读）
 */
@RestController
@RequestMapping("/api/sysparams")
public class SysParamsController {

    final SysParamsService sysParamsService;
    final Logger logger;

    public SysParamsController(@Autowired SysParamsService sysParamsService) {
        this.sysParamsService = sysParamsService;
        this.logger = LoggerFactory.getLogger(SysParamsController.class);
    }

    /**
     * 根据ID查询系统参数
     * @param id 参数ID
     * @return API response json
     */
    @GetMapping("/{id}")
    public ApiResponse getSysParams(@PathVariable Integer id) {
        logger.info("/api/sysparams/{} get request", id);

        Optional<SysParams> sysParams = sysParamsService.getSysParamsById(id);
        if (sysParams.isPresent()) {
            return ApiResponse.ok(sysParams.get());
        } else {
            return ApiResponse.error("系统参数不存在");
        }
    }

    /**
     * 查询所有系统参数
     * @return API response json
     */
    @GetMapping
    public ApiResponse getAllSysParams() {
        logger.info("/api/sysparams get request");

        List<SysParams> sysParamsList = sysParamsService.getAllSysParams();
        return ApiResponse.ok(sysParamsList);
    }

    /**
     * 获取商店配置信息（便捷接口）
     * @return API response json
     */
    @GetMapping("/shop-config")
    public ApiResponse getShopConfig() {
        logger.info("/api/sysparams/shop-config get request");

        // 通常系统参数表只有一条记录，ID为1
        Optional<SysParams> sysParams = sysParamsService.getSysParamsById(1);
        if (sysParams.isPresent()) {
            return ApiResponse.ok(sysParams.get());
        } else {
            return ApiResponse.error("商店配置不存在");
        }
    }
}
