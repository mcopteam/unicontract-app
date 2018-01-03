package com.uniledger.contract.dao;

import com.uniledger.contract.model.ContractAuditLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxcsdb88 on 2017/6/10 19:13.
 */
@Repository
public interface ContractAuditLogMapper {

    /**
     * 根据条件查询审核记录
     * @param id
     * @param contractPK
     * @param description
     * @param isShow
     * @return
     */
    List<ContractAuditLog> getContractAuditLogs(@Param("id") Long id, @Param("contractPK")Long contractPK, @Param("description")String description, @Param("isShow")boolean isShow);

    /**
     * 插入审核记录log
     * @param contractAuditLog
     * @return
     */
    int insert(ContractAuditLog contractAuditLog);
}
