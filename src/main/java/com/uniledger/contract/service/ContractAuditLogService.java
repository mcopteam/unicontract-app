package com.uniledger.contract.service;

import com.uniledger.contract.model.CompanyUser;
import com.uniledger.contract.model.ContractAuditLog;

import java.util.List;

/**
 * Created by wxcsdb88 on 2017/6/10 19:05.
 */
public interface ContractAuditLogService {

    /**
     * 根据条件查询审核记录
     * @param id
     * @param contractPK
     * @param description
     * @param isShow
     * @return
     */
    List<ContractAuditLog> getContractAuditLogs(Long id,  Long contractPK, String description, boolean isShow);

    /**
     * 插入审核记录log
     * @param contractAuditLog
     * @return
     */
    boolean insert(ContractAuditLog contractAuditLog);

}
