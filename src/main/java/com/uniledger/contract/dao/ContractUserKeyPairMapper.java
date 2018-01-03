package com.uniledger.contract.dao;

import com.uniledger.contract.model.ContractUserKeyPair;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxcsdb88 on 2017/5/25 10:41.
 */
@Repository
public interface ContractUserKeyPairMapper {
    /**
     * 插入公私钥，如果不存在已激活的公私钥则激活[需要修改contractUser表]，否则不激活
     * 暂不考虑验证重复问题
     * @param contractUserKeyPair {@link ContractUserKeyPair}
     * @return 受影响行数
     */
    int insert(ContractUserKeyPair contractUserKeyPair);

    /**
     * 激活keyPair 并修改状态
     * @param contractUserKeyPair {@link ContractUserKeyPair}
     * @return boolean
     */
    boolean activeKey(ContractUserKeyPair contractUserKeyPair);

    /**
     * 根据主键查询密钥信息
     * @param id primary key
     * @return {@link ContractUserKeyPair}
     */
    ContractUserKeyPair getContractKeyPairById(@Param("id") Long id);

    /**
     * 更新keyPair,如果更新状态为2 激活，则同时更新contractUser中的keypair
     * @param contractUserKeyPair {@link ContractUserKeyPair}
     * @return boolean
     */
    int update(ContractUserKeyPair contractUserKeyPair);

    /**
     * 查询用户keyPairs
     * @param userId 用户id
     * @param status keyPair状态
     * @return {@link  List <ContractUserKeyPair>}
     */
    List<ContractUserKeyPair> getContractKeyPairListByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Short status);

}
