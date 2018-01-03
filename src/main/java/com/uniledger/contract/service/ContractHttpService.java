package com.uniledger.contract.service;

import com.uniledger.contract.dto.ContractExecuteLog;
import com.uniledger.contract.dto.ContractProduct;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by wxcsdb88 on 2017/5/23 14:57.
 */
public interface ContractHttpService {

    /**
     * according the contractId and contract publish ownerkey
     * query publish contract
     * @param token
     * @param requestMap
     * @return
     */
    String queryPublishContract(String token, Map<String, Object> requestMap);

    /**
     * according the contractId and contract owner query the contract content
     * in contactState ["Contract_Create","Contract_Signature"]
     * @param token
     * @param requestMap
     * @return
     */
    String queryContractContent(String token, Map<String, Object> requestMap);

    /**
     * query single Product
     * @param token token 标识
     * @param requestMap 请求参数
     * @return {@link ContractProduct}
     */
    ContractProduct queryExecuteContract(String token, Map<String, Object> requestMap);

    /**
     * query ContractOriginal accoring the uni contract API
     * @param token 标识
     * @param requestMap 请求参数
     * @return {@link List<ContractProduct>}
     */
    List<ContractProduct> queryExecuteContractList(String token, Map<String, Object> requestMap);

    /**
     * 查询合约执行日志
     * @param token
     * @param requestMap
     * @return {@link List< ContractExecuteLog >}
     */
    List<ContractExecuteLog> queryContractProductLog(String token, Map<String, Object> requestMap);

    /**
     * 创建合约，发送的是proto型合约
     * @param token
     * @param protContract
     * @return
     */
    boolean createContract(String token, byte[] protContract);

    /**
     * json或xml格式
     * @param token 标识
     * @param contract json或xml格式
     * @param isXMLContract [null or false =json, true=xml]
     * @return
     */
    boolean createContract(String token, String contract, Boolean isXMLContract) throws Exception;

    /**
     * 签名合约，发送的是完整的proto型合约[签名个数不同，合约hashId不同]
     * @param token
     * @param protContract
     * @return
     */
    boolean signContract(String token, byte[] protContract);

    /**
     * 合约测试，发送的是完整的proto型合约
     * @param token
     * @param protContract
     * @return
     */
    boolean testContract(String token, byte[] protContract);


    /**
     * 获取合约运行时间
     * @param token
     * @param contractId
     * @return
     */
    JSONObject getRunTimeQuery(String token, String contractId);

    /**
     * 获取总交易量
     * @param token
     * @param contractId
     * @return
     */
    JSONObject getTransactionNumQuery(String token, String contractId);

    /**
     * get data from uni contract for demo wyp
     * @param token
     * @param methodName
     * @return
     */
    String getDemoDataForWYP(String token, String methodName);

}
