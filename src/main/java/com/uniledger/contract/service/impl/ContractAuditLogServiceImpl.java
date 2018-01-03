package com.uniledger.contract.service.impl;

import com.uniledger.contract.dao.ContractAuditLogMapper;
import com.uniledger.contract.model.ContractAuditLog;
import com.uniledger.contract.service.ContractAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxcsdb88 on 2017/6/10 19:12.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ContractAuditLogServiceImpl implements ContractAuditLogService {

    @Autowired
    private ContractAuditLogMapper contractAuditLogMapper;

    @Override
    public List<ContractAuditLog> getContractAuditLogs(Long id, Long contractPK, String description, boolean isShow) {
        return contractAuditLogMapper.getContractAuditLogs(id, contractPK, description, isShow);
    }

    @Override
    public boolean insert(ContractAuditLog contractAuditLog) {
        int affectRows = contractAuditLogMapper.insert(contractAuditLog);
        return affectRows >= 1;
    }
}
