package com.uniledger.contract.service.impl;

import com.uniledger.contract.dao.ContractUserKeyPairMapper;
import com.uniledger.contract.model.ContractUserKeyPair;
import com.uniledger.contract.service.ContractUserKeyPairService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxcsdb88 on 2017/5/25 10:27.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ContractUserKeyPairServiceImpl implements ContractUserKeyPairService{

    @Autowired
    private ContractUserKeyPairMapper contractUserKeyPairMapper;
    @Override
    public boolean insert(ContractUserKeyPair contractUserKeyPair) {
        int affectRows = contractUserKeyPairMapper.insert(contractUserKeyPair);
        return affectRows >= 1;
    }

    @Override
    public boolean activeKey(ContractUserKeyPair contractUserKeyPair) {
        return contractUserKeyPairMapper.activeKey(contractUserKeyPair);
    }

    @Override
    public ContractUserKeyPair getContractKeyPairById(Long id) {
        return contractUserKeyPairMapper.getContractKeyPairById(id);
    }

    @Override
    public boolean update(ContractUserKeyPair contractUserKeyPair) {
        int affectRows = contractUserKeyPairMapper.update(contractUserKeyPair);
        return affectRows >= 1;
    }

    @Override
    public List<ContractUserKeyPair> getContractKeyPairListByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Short status) {
        return contractUserKeyPairMapper.getContractKeyPairListByUserIdAndStatus(userId, status);
    }


}
