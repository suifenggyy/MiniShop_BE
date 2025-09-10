package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SysParams;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysParamsMapper {

    /**
     * 根据ID查询系统参数
     * @param id 参数ID
     * @return 系统参数
     */
    SysParams getSysParamsById(Integer id);

    /**
     * 查询所有系统参数
     * @return 系统参数列表
     */
    List<SysParams> getAllSysParams();
}
