package com.uniledger.contract.service;

import com.uniledger.contract.common.PageInfo;
import com.uniledger.contract.dto.ContractDTO;
import com.uniledger.contract.model.LocalContract;

import java.util.List;

/**
 * Created by wxcsdb88 on 2017/5/10 15:29.
 */

public interface ContractService {

    /**
     *  查询本地合约
     * @param id 主键
     * @return {@link LocalContract}
     */
    LocalContract getFileContract(Long id);

    /**
     * 删除 本地合约
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 根据合约id查询
     * @param contractId
     * @return
     */
    LocalContract getFileContractByContractId(String contractId);

    /**
     * 查询合约列表
     * @param contractId 合约编号或id
     * @param name 合约名称
     * @param status 合约状态
     * @return {@link List<LocalContract>}
     */
    List<LocalContract> getFileContractList(String contractId, String name, Short ...status);

    /**
     * 根据合约名称和拥有者 查询合约列表 for demo
     * @param name 合约名称
     * @param ownerKey 合约拥有者
     * @return {@link LocalContract}
     */
    LocalContract getFileContractByNameAndOwner(String name, String ownerKey);

    /**
     * 查询合约列表
     * @param contractId 合约编号或id
     * @param name 合约名称
     * @param status 合约状态
     * @param pageNum 当前页码
     * @param pageSize 页面记录数
     * @return {@link PageInfo<LocalContract>}
     */
    PageInfo<LocalContract> getFileContractListPageInfo(Integer pageNum, Integer pageSize, String contractId, String name, Short ...status);

    /**
     * 本地合约创建
     * @param localContract {@link LocalContract}
     * @return  {@link boolean}
     */
    boolean createFileContract(LocalContract localContract);



    /**
     * 查询本地合约
     * @param id 本地合约文件id
     * @param ownerKey 用户public key
     * @return {@link LocalContract}
     */
    LocalContract getLocalContractByIdAndOwnerKey(Long id, String ownerKey);

    /**
     * 本地合约存储
     * @param localContract {@link LocalContract}
     * @return  {@link boolean}
     */
    boolean updateLocalContract(LocalContract localContract);

    /**
     * 更新本地库合约状态
     * @param id primary kkey
     * @param status 要更新的状态
     * @return {@link boolean}
     */
    boolean updateLocalContractStatus(Long id, Short status);



    /**
     * 根据合约id及签名状态获取执行日志
     *
     * @param token           token标记
     * @param owner           合约拥有者
     * @param contractId      合约id
     * @param signatureStatus 合约签名状态
     * @return {@link List<ContractDTO>} todo
     */
    List<ContractDTO> getContractLog(String token, String owner, String contractId, Short signatureStatus);

    /**
     * 合约签名操作
     *
     * @param token       token标记
     * @param owner       合约拥有者
     * @param contractId  合约id
     * @param xmlContract xml格式合约
     * @return {@link boolean}
     */
    boolean signature(String token, String owner, String contractId, String xmlContract);

    /**
     * 合约创建
     *
     * @param token       token标记
     * @param owner       合约拥有者
     * @param contractId  合约id
     * @param xmlContract xml格式合约
     * @return {@link boolean}
     */
    boolean create(String token, String owner, String contractId, String xmlContract);

    /**
     * 合约更新
     * @param token token标记
     * @param owner 合约拥有者
     * @param contractId    合约id
     * @param xmlContract   xml格式合约
     * @return  {@link boolean}
     */
    boolean update(String token, String owner, String contractId, String xmlContract);

    /**
     * 合约测试
     * @param token token标记
     * @param owner 合约拥有者
     * @param contractId    合约id
     * @param xmlContract   xml格式合约
     * @return  {@link boolean}
     */
    boolean test(String token, String owner, String contractId, String xmlContract);

}
