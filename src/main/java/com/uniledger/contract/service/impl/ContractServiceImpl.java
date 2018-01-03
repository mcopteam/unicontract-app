package com.uniledger.contract.service.impl;

import com.github.pagehelper.PageHelper;
import com.uniledger.contract.common.PageInfo;
import com.uniledger.contract.dao.ContractMapper;
import com.uniledger.contract.dao.ContractUserMapper;
import com.uniledger.contract.dto.ContractDTO;
import com.uniledger.contract.model.LocalContract;
import com.uniledger.contract.service.ContractHttpService;
import com.uniledger.contract.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxcsdb88 on 2017/5/11 13:57.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ContractServiceImpl implements ContractService {
    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private ContractUserMapper contractUserMapper;

    @Autowired
    private ContractHttpService contractHttpService;

    @Override
    public LocalContract getFileContract(Long id) {
        return contractMapper.getFileContract(id);
    }

    @Override
    public boolean delete(Long id) {
        int rows = contractMapper.delete(id);
        return rows >= 1;
    }

    @Override
    public LocalContract getFileContractByContractId(String contractId) {
        return contractMapper.getFileContractByContractId(contractId);
    }

    @Override
    public List<LocalContract> getFileContractList(String contractId, String name, Short... status) {
        return contractMapper.getFileContractList(contractId, name, status);
    }

    @Override
    public LocalContract getFileContractByNameAndOwner(String name, String ownerKey) {
        List<LocalContract> localContractList = contractMapper.getFileContractListByNameAndOwner(name, ownerKey);
        if(localContractList != null && localContractList.size() >= 1){
            return localContractList.get(0);
        }
        return null;
    }

    @Override
    public PageInfo<LocalContract> getFileContractListPageInfo(Integer start, Integer limit, String contractId, String name, Short... status) {
        PageHelper.startPage(start, limit);
        return new PageInfo(contractMapper.getFileContractList(contractId, name, status));
    }


    @Override
    public boolean createFileContract(LocalContract localContract) {
        int rows = contractMapper.createFileContract(localContract);
        return rows >= 1;
    }


    @Override
    public LocalContract getLocalContractByIdAndOwnerKey(Long id, String ownerKey) {
        return contractMapper.getLocalContractByIdAndOwnerKey(id, ownerKey);
    }

    @Override
    public boolean updateLocalContract(LocalContract localContract) {
        int rows = contractMapper.updateLocalContract(localContract);
        return rows >= 1;
    }

    @Override
    public boolean updateLocalContractStatus(Long id, Short status) {
        int rows = contractMapper.updateLocalContractStatus(id, status);
        return rows >= 1;
    }


    @Override
    public List<ContractDTO> getContractLog(String token, String owner, String contractId, Short signatureStatus) {
        return null;
    }

    @Override
    public boolean signature(String token, String owner, String contractId, String xmlContract) {
        return false;
    }

    @Override
    public boolean create(String token, String owner, String contractId, String xmlContract) {
        return false;
    }

    @Override
    public boolean update(String token, String owner, String contractId, String xmlContract) {
        return false;
    }

    @Override
    public boolean test(String token, String owner, String contractId, String xmlContract) {
        return false;
    }


}
