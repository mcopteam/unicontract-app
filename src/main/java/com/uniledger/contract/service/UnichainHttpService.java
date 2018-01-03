package com.uniledger.contract.service;

import com.uniledger.contract.dto.ContractProduct;
import com.uniledger.contract.model.LocalContract;
import net.sf.json.JSONArray;

import java.util.List;
import java.util.Map;

/**
 * Created by wsp on 2017/6/15 11:21.
 */
public interface UnichainHttpService {

    /**
     *  根据用户公钥获取用户交易记录
     *
     * @param publicKey 用户公钥
     * @return json串
     */
    JSONArray getTransferTransaction(String publicKey);

    /**
     *  根据用户公钥获取用户账户余额
     *
     * @param publicKey 用户公钥
     * @return json串
     */
    int getAccountBalance(String publicKey);

    /**
     * 获取用户的所有合约
     * @return
     */
    List<LocalContract> getUserAllLocalContract(String pubkey);

}
