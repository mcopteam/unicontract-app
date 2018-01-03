package com.uniledger.contract.service;

import com.uniledger.contract.dto.ContractUserDTO;
import com.uniledger.contract.model.ContractUser;

import java.util.List;

/**
 * Created by wxcsdb88 on 2017/5/15 20:24.
 */
public interface ContractUserService {

    /**
     * 用户查询
     * @param id 用户名
     * @param pubkey 用户公钥
     * @param name 用户名
     * @return  {@link List<ContractUser>}
     */
    List<ContractUser> getAllUser(Long id, String pubkey, String name);

    /**
     * 根据合约id查询相关用户部分信息
     *
     * @param contractId 合约id
     * @return {@link List < ContractUser >}
     */
    List<ContractUser> getUserListByContractId(String contractId);

    /**
     * 根据用户public key查询相关用户部分信息u
     *
     * @param ownersPubkey 用户public key
     * @return {@link List < ContractUser >}
     */
    List<ContractUser> getUserListByOwnersPubkey(String ownersPubkey);

    /**
     * 根据用户public key查询相关用户部分信息u
     *
     * @param ownersPubkeyList 用户public key list
     * @return {@link List < ContractUser >}
     */
    List<ContractUser> getUserListByOwnersPubkeyList(List<String> ownersPubkeyList);

    /**
     * 根据用户public key查询相关用户部分信息u
     *
     * @param ownersPubkeyList 用户public key list
     * @return {@link List < ContractUser >}
     */
    List<ContractUserDTO> getUserDTOListByOwnersPubkeyList(List<String> ownersPubkeyList);

    /**
     * 用户查询
     * @param id 用户名
     * @return  {@link ContractUser}
     */
    ContractUser getUserById(Long id);

    /**
     * 用户查询
     * @param pubkey 用户公钥
     * @return  {@link ContractUser}
     */
    ContractUser getUserByPubkey(String pubkey);

    /**
     * 用户查询
     * @param contractName 合约名
     * @return  {@link ContractUser}
     */
    ContractUser getUserByContractName(String contractName);

    /**
     * 用户查询
     * @param username 用户名
     * @return {@link ContractUser}
     */
    ContractUser getUserByUserName(String username);

    /**
     * 更新用户当前的keypair
     * @param contractUser {@link ContractUser}
     * @return boolean
     */
    boolean updateContractUserKeyPair(ContractUser contractUser);

    /**
     * 获取所有用户
     * @return
     */
    List<ContractUser> getAllContractUser();
}
