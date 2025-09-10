package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.SysParamsMapper;
import com.tencent.wxcloudrun.model.SysParams;
import com.tencent.wxcloudrun.service.SysParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SysParamsServiceImpl implements SysParamsService {

    final SysParamsMapper sysParamsMapper;

    public SysParamsServiceImpl(@Autowired SysParamsMapper sysParamsMapper) {
        this.sysParamsMapper = sysParamsMapper;
    }

    @Override
    public Optional<SysParams> getSysParamsById(Integer id) {
        return Optional.ofNullable(sysParamsMapper.getSysParamsById(id));
    }

    @Override
    public List<SysParams> getAllSysParams() {
        return sysParamsMapper.getAllSysParams();
    }
}
