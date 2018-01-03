package com.uniledger.contract.service.impl;

import com.uniledger.contract.dao.ContractUserMapper;
import com.uniledger.contract.dto.ContractUserDTO;
import com.uniledger.contract.model.ContractUser;
import com.uniledger.contract.service.ContractUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxcsdb88 on 2017/5/15 20:29.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ContractUserServiceImpl implements ContractUserService {
    @Autowired
    private ContractUserMapper contractUserMapper;

    @Override
    public List<ContractUser> getAllUser(Long id, String pubkey, String name) {
        return contractUserMapper.getAllUser(id, pubkey, name);
    }

    @Override
    public List<ContractUser> getUserListByContractId(String contractId) {
        return contractUserMapper.getUserListByContractId(contractId);
    }

    @Override
    public List<ContractUser> getUserListByOwnersPubkey(String ownersPubkey) {
        return contractUserMapper.getUserListByOwnersPubkey(ownersPubkey);
    }

    @Override
    public List<ContractUser> getUserListByOwnersPubkeyList(List<String> ownersPubkeyList) {
        return contractUserMapper.getUserListByOwnersPubkeyList(ownersPubkeyList);
    }

    @Override
    public List<ContractUserDTO> getUserDTOListByOwnersPubkeyList(List<String> ownersPubkeyList) {
        return contractUserMapper.getUserDTOListByOwnersPubkeyList(ownersPubkeyList);
    }

    @Override
    public ContractUser getUserById(Long id) {
        return contractUserMapper.getUserById(id);
    }

    @Override
    public ContractUser getUserByPubkey(String pubkey) {
        return contractUserMapper.getUserByPubkey(pubkey);
    }

    @Override
    public ContractUser getUserByContractName(String contractName) {
        return contractUserMapper.getUserByContractName(contractName);
    }

    @Override
    public ContractUser getUserByUserName(String username) {
        return contractUserMapper.getUserByUserName(username);
    }

    @Override
    public boolean updateContractUserKeyPair(ContractUser contractUser) {
        int affectRows = contractUserMapper.updateContractUserKeyPair(contractUser);
        return affectRows >= 1;
    }

    @Override
    public List<ContractUser> getAllContractUser() {
        return contractUserMapper.getAllContractUser();
    }

}
