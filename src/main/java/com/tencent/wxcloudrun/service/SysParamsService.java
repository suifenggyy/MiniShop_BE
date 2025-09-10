package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.SysParams;

import java.util.List;
import java.util.Optional;

public interface SysParamsService {

    /**
     * 根据ID查询系统参数
     * @param id 参数ID
     * @return 系统参数
     */
    Optional<SysParams> getSysParamsById(Integer id);

    /**
     * 查询所有系统参数
     * @return 系统参数列表
     */
    List<SysParams> getAllSysParams();
}
