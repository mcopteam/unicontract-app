package com.uniledger.contract.dao;

import com.uniledger.contract.model.LocalContract;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * local ContractOriginal store in mysql
 * Created by wxcsdb88 on 2017/5/15 14:56.
 */
@Repository
public interface ContractMapper {

    /**
     * 本地合约创建
     * @param localContract {@link LocalContract}
     * @return int affect rows count
     */
    int createFileContract(LocalContract localContract);

    /**
     * 删除 本地合约
     * @param id
     * @return
     */
    int delete(Long id);

    LocalContract getFileContract(Long id);

    /**
     * 根据合约id查询
     * @param contractId
     * @return
     */
    LocalContract getFileContractByContractId(String contractId);

    /**
     * 查询送审合约列表
     * @param contractId 合约编号或contractId
     * @param name 合约名称
     * @param status 合约状态
     * @return {@link List<LocalContract>}
     */
    List<LocalContract> getFileContractList(@Param("contractId")String contractId, @Param("name")String name, @Param("status")Short ...status);



    /**
     * 查询本地合约
     * @param id 本地合约文件id
     * @param ownerKey 用户public key
     * @return {@link LocalContract}
     */
    LocalContract getLocalContractByIdAndOwnerKey(@Param("id")Long id, @Param("owner")String ownerKey);


    /**
     * 根据合约名称和拥有者 查询合约列表
     * @param name 合约名称
     * @param ownerKey 合约拥有者
     * @return {@link List< LocalContract >}
     */
    List<LocalContract> getFileContractListByNameAndOwner(@Param("name")String name, @Param("owner")String ownerKey);


    /**
     * 本地合约存储
     * @param localContract {@link LocalContract}
     * @return int affect rows count
     */
    int updateLocalContract(LocalContract localContract);

    /**
     * 更新本地库合约状态
     * @param id primary key
     * @param status 要更新的状态
     * @return {@link boolean}
     */
    int updateLocalContractStatus(@Param("id")Long id, @Param("status")Short status);



    /**
     * 获取所有存在本地数据库的xml 合约
     * @return {@link List<LocalContract>}
     */
    List<LocalContract> getAllLocalContract();


    /**
     * 根据合约id，获取存在本地数据库的xml 合约
     * @param contractId 合约id
     * @return {@link LocalContract}
     */
    LocalContract getLocalContractById(String contractId);

    /**
     * 根据合约id，查询当前用户本地合约[xml格式合约]
     * @param owner 当前用户
     * @param contractId 合约id
     * @return {@link LocalContract}
     */
    LocalContract getLocalContractByOwnerAndContractId(@Param("owner") String owner, @Param("contractId") String contractId);

    /**
     * 根据用户公钥获取当前用户的所有合约
     * @param pubkey
     * @return
     */
    List<LocalContract> getUserAllLocalContract(String pubkey);
}
