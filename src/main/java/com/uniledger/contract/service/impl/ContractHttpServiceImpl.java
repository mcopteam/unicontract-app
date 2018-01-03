package com.uniledger.contract.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.uniledger.contract.constants.ContractConstant;
import com.uniledger.contract.dao.ContractUserMapper;
import com.uniledger.contract.dto.ContractDTO;
import com.uniledger.contract.dto.ContractExecuteLog;
import com.uniledger.contract.dto.ContractProduct;
import com.uniledger.contract.dto.ContractUserDTO;
import com.uniledger.contract.service.ContractHttpService;
import com.uniledger.contract.utils.*;
import com.uniledger.protos.ContractExecuteLogs;
import com.uniledger.protos.ProtoContract;
import com.uniledger.protos.ProtoContractList;
import com.uniledger.protos.ProtoResponseData;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wxcsdb88 on 2017/5/23 14:57.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ContractHttpServiceImpl implements ContractHttpService {
    private static final String CONFIG = "remote.properties";
    private static Logger log = LoggerFactory.getLogger(ContractHttpServiceImpl.class);

    private static String base_url;
    private static String token;
    private static String contentType;
    private static Properties properties;

    static {
        properties = new Properties();
        try {
            InputStream inputStream = DateUtils.class.getClassLoader().getResourceAsStream(CONFIG);
            properties.load(inputStream);
            base_url = properties.getProperty("unicontract.url");
            token = properties.getProperty("unicontract.token");
            contentType = properties.getProperty("unicontract.contentType");
            log.info(String.format("remote_server[base_url=%s, token=%s, contentType=%s]", base_url, token, contentType));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    @Autowired
    private ContractUserMapper contractUserMapper;

    //todo
    public static String convertXmlContractToJson(String xmlContract) {
        // todo convertXmlToJson
        String str_json = "";

        try{
            str_json = XMLContractParseUtil.parseXMLToJsonFromString(xmlContract);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return str_json;
    }

    /**
     * convert jsonContract to contractProto byte[]
     *
     * @param jsonContract
     * @return
     */
    public static byte[] convertJsonToProto(String jsonContract) {
        ProtoContract.Contract.Builder builder = ProtoContract.Contract.newBuilder();
        return ProtoConvertUtils.jsonToBuilderToByte(jsonContract, builder);
    }

    public static boolean testContract(String id, String token) {
        // todo test 测试花合约
        return true;
    }

    public static boolean terminalContract(String id, String token) {
        // todo terminalContract
        return true;
    }

    public static ProtoContract.Contract queryContractProto(String id, String token) {
        String url = base_url + "query";

        log.info("[API-queryContract]查询合约处理");
        ProtoContract.Contract.Builder contractBuilder = ProtoContract.Contract.newBuilder();
        contractBuilder.setId(id);
        ProtoContract.Contract contract = contractBuilder.build();
        byte[] buf = contract.toByteArray();
        HttpUtils httpClient = new HttpUtils();

        byte[] response = null;
        ContractDTO contractDTO = new ContractDTO();
        ProtoContract.Contract contractResult = contractBuilder.build();
        try {
            response = httpClient.post(url, buf, token, contentType, null);
            Base64 base64 = new Base64();
            ProtoResponseData.ResponseData responseData = ProtoResponseData.ResponseData.parseFrom(response);
            System.out.println(responseData);
            byte[] contractByte = base64.decode(responseData.getData());
//            ContractOriginal contractResponse = ContractOriginal.parseFrom(contractByte);
            contractResult = ProtoContract.Contract.parseFrom(contractByte);
            return contractResult;
//            System.out.println("contractDTO is " + contractDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contractResult;
    }

    @Override
    public String queryPublishContract(String token, Map<String, Object> requestMap) {
        String url = base_url + "queryPublishContract";

        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = new JsonObject();
        // 合约处于不同状态时，hashId唯一，合约id一致，可能存在多条[多状态，签名个数不一样]
        jsonObject.addProperty("status", (String) requestMap.get("status"));
        jsonObject.addProperty("contractId", (String) requestMap.get("contractId"));
        String ownerPubkey = (String) requestMap.get("owner");
        jsonObject.addProperty("owner", ownerPubkey);

        HttpUtils httpClient = new HttpUtils();
        byte[] response = null;
        try {
            response = httpClient.post(url, jsonObject.toString(), token, ContractConstant.HTTP_CONTENT_TYPE_APPLICATION_JSON, "UTF-8");
            Base64 base64 = new Base64();
            ProtoResponseData.ResponseData responseData = ProtoResponseData.ResponseData.parseFrom(response);
            log.error("responseData:\n" + responseData);
            byte[] responseDataStrByte = base64.decode(responseData.getData());
            String s = new String(responseDataStrByte, "UTF-8");
            return s;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public String queryContractContent(String token, Map<String, Object> requestMap) {
        String url = base_url + "queryContractContent";

        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("contractId", (String) requestMap.get("contractId"));
        String ownerPubkey = (String) requestMap.get("owner");
        jsonObject.addProperty("owner", ownerPubkey);

        HttpUtils httpClient = new HttpUtils();
        byte[] response = null;
        try {
            response = httpClient.post(url, jsonObject.toString(), token, ContractConstant.HTTP_CONTENT_TYPE_APPLICATION_JSON, "UTF-8");
            Base64 base64 = new Base64();
            ProtoResponseData.ResponseData responseData = ProtoResponseData.ResponseData.parseFrom(response);
            log.error("responseData:\n" + responseData);
            String responseDataStr = responseData.getData();
            byte[] responseDataStrByte = base64.decode(responseData.getData());
            String s = new String(responseDataStrByte, "UTF-8");
            return s;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public ContractProduct queryExecuteContract(String token, Map<String, Object> requestMap) {
        String url = base_url + "query";

        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = new JsonObject();
        // 合约处于不同状态时，hashId唯一，合约id一致，可能存在多条[多状态，签名个数不一样]
        jsonObject.addProperty("status", (String) requestMap.get("status"));
        jsonObject.addProperty("contractId", (String) requestMap.get("contractId"));
        String ownerPubkey = (String) requestMap.get("owner");
        jsonObject.addProperty("owner", ownerPubkey);

        HttpUtils httpClient = new HttpUtils();

        byte[] response = null;
        ContractProduct contractProduct;
        try {
            response = httpClient.post(url, jsonObject.toString(), token, ContractConstant.HTTP_CONTENT_TYPE_APPLICATION_JSON, "UTF-8");
            Base64 base64 = new Base64();
            ProtoResponseData.ResponseData responseData = ProtoResponseData.ResponseData.parseFrom(response);
            log.error("responseData:\n" + responseData);
            byte[] contractByte = base64.decode(responseData.getData());
            ProtoContract.Contract contractResponse = ProtoContract.Contract.parseFrom(contractByte);
            if (contractResponse == null || CommonUtils.isEmpty(contractResponse.getId())) {
                return null;
            }
            contractProduct = new ContractProduct();
            if (!CommonUtils.isEmpty(contractResponse.getId())) {
                contractProduct.setHashId(contractResponse.getId());
            }

            ProtoContract.ContractHead contractHead = contractResponse.getContractHead();
            if(!CommonUtils.isEmpty(contractHead.getOperateTime()) ){
                //todo 可以根据这个字段排序查询 合约执行记录
                contractProduct.setStatusChangeTime(DateUtils.timestampToDateTime(contractHead.getOperateTime()));
            }
            ProtoContract.ContractBody contractBody = contractResponse.getContractBody();
            if (contractBody != null) {
                if (!CommonUtils.isEmpty(contractBody.getCaption())) {
                    contractProduct.setCaption(contractBody.getCaption());
                }
                if (!CommonUtils.isEmpty(contractBody.getCname())) {
                    contractProduct.setContractName(contractBody.getCaption());
                }
                if (!CommonUtils.isEmpty(contractBody.getCreateTime())) {
                    contractProduct.setCreateTime(DateUtils.timestampToDateTime(contractBody.getCreateTime()));
                }
                if (!CommonUtils.isEmpty(contractBody.getContractId())) {
                    contractProduct.setContractId(contractBody.getContractId());
                }
                List<String> owners = contractBody.getContractOwnersList();
                //todo 需要过滤
                if (!CommonUtils.isEmpty(owners)) {
                    List<ContractUserDTO> contractUserDTOS = contractUserMapper.getUserDTOListByOwnersPubkeyList(owners);
                    if (contractUserDTOS != null && contractUserDTOS.size() > 0) {
                        contractProduct.setOwners(contractUserDTOS);
                    }
                }
                List<ProtoContract.ContractSignature> contractSignatureList = contractBody.getContractSignaturesList();
                //todo lazy load, should use contractBody.getContractSignaturesCount()
                contractProduct.setSignCount(contractBody.getContractSignaturesCount());
                if (!CommonUtils.isEmpty(contractSignatureList)) {
                    for (ProtoContract.ContractSignature contractSignature: contractSignatureList) {
                        if(ownerPubkey.equals(contractSignature.getOwnerPubkey())){
                            // 只设置当前用户的相关签名信息
                            contractProduct.setSignTime(DateUtils.timestampToDateTime(contractSignature.getSignTimestamp()));
                            contractProduct.setSignature(contractSignature.getSignature());
                            break;
                        }
                    }
                }

                if (!CommonUtils.isEmpty(contractBody.getCname())) {
                    contractProduct.setName(contractBody.getCname());
                }
                if(!CommonUtils.isEmpty(contractBody.getContractState())){
                    contractProduct.setStatus(contractBody.getContractState());
                }
                if (!CommonUtils.isEmpty(contractBody.getStartTime())) {
                    contractProduct.setStart(DateUtils.timestampToDateTime(contractBody.getStartTime()));
                }
                if (!CommonUtils.isEmpty(contractBody.getEndTime())) {
                    contractProduct.setEnd(DateUtils.timestampToDateTime(contractBody.getEndTime()));
                }
            }
            log.warn("contractProduct: \n" + contractProduct);
            return contractProduct;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public List<ContractProduct> queryExecuteContractList(String token, Map<String, Object> requestMap) {
        String url = base_url + "queryAll";

        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", (String) requestMap.get("status"));
        jsonObject.addProperty("contractId", (String) requestMap.get("contractId"));
        String ownerPubkey = (String) requestMap.get("owner");
        jsonObject.addProperty("owner", ownerPubkey);

        HttpUtils httpClient = new HttpUtils();

        byte[] response = null;
        List<ContractProduct> contractProductList;
        try {
            ContractProduct contractProduct;
            response = httpClient.post(url, jsonObject.toString(), token, ContractConstant.HTTP_CONTENT_TYPE_APPLICATION_JSON, "UTF-8");
            Base64 base64 = new Base64();
            ProtoResponseData.ResponseData responseData = ProtoResponseData.ResponseData.parseFrom(response);
            log.warn("list responseData:\n" + responseData);
            byte[] contractListByte = base64.decode(responseData.getData());
            ProtoContractList.ContractList contractList = ProtoContractList.ContractList.parseFrom(contractListByte);
            System.out.println(contractList);
            contractProductList = new ArrayList<>(contractList.getContractsCount());
            for (ProtoContract.Contract contractResponse : contractList.getContractsList()) {
                log.warn("contractResponse: \n" + contractResponse);
                if (contractResponse == null || CommonUtils.isEmpty(contractResponse.getId())) {
                    return null;
                }
                contractProduct = new ContractProduct();
                if (!CommonUtils.isEmpty(contractResponse.getId())) {
                    contractProduct.setHashId(contractResponse.getId());
                }

                ProtoContract.ContractHead contractHead = contractResponse.getContractHead();
                if(!CommonUtils.isEmpty(contractHead.getOperateTime()) ){
                    //todo 可以根据这个字段排序查询 合约执行记录
                    contractProduct.setStatusChangeTime(DateUtils.timestampToDateTime(contractHead.getOperateTime()));
                }

                ProtoContract.ContractBody contractBody = contractResponse.getContractBody();
                if (contractBody != null) {
                    if (!CommonUtils.isEmpty(contractBody.getCaption())) {
                        contractProduct.setCaption(contractBody.getCaption());
                    }
                    if (!CommonUtils.isEmpty(contractBody.getCname())) {
                        contractProduct.setContractName(contractBody.getCaption());
                    }
                    if (!CommonUtils.isEmpty(contractBody.getCreateTime())) {
                        contractProduct.setCreateTime(DateUtils.timestampToDateTime(contractBody.getCreateTime()));
                    }
                    if (!CommonUtils.isEmpty(contractBody.getContractId())) {
                        contractProduct.setContractId(contractBody.getContractId());
                    }
                    List<String> owners = contractBody.getContractOwnersList();
                    //todo 需要过滤
                    if (!CommonUtils.isEmpty(owners)) {
                        List<ContractUserDTO> contractUserDTOS = contractUserMapper.getUserDTOListByOwnersPubkeyList(owners);
                        if (contractUserDTOS != null && contractUserDTOS.size() > 0) {
                            contractProduct.setOwners(contractUserDTOS);
                        }
                    }
                    List<ProtoContract.ContractSignature> contractSignatureList = contractBody.getContractSignaturesList();
                    //todo lazy load, should use contractBody.getContractSignaturesCount()
                    contractProduct.setSignCount(contractBody.getContractSignaturesCount());
                    if (!CommonUtils.isEmpty(contractSignatureList)) {
                        for (ProtoContract.ContractSignature contractSignature: contractSignatureList) {
                            if(ownerPubkey.equals(contractSignature.getOwnerPubkey())){
                                // 只设置当前用户的相关签名信息
                                contractProduct.setSignTime(DateUtils.timestampToDateTime(contractSignature.getSignTimestamp()));
                                contractProduct.setSignature(contractSignature.getSignature());
                                break;
                            }
                        }
                    }

                    if (!CommonUtils.isEmpty(contractBody.getCname())) {
                        contractProduct.setName(contractBody.getCname());
                    }
                    if(!CommonUtils.isEmpty(contractBody.getContractState())){
                        contractProduct.setStatus(contractBody.getContractState());
                    }
                    if (!CommonUtils.isEmpty(contractBody.getStartTime())) {
                        contractProduct.setStart(DateUtils.timestampToDateTime(contractBody.getStartTime()));
                    }
                    if (!CommonUtils.isEmpty(contractBody.getEndTime())) {
                        contractProduct.setEnd(DateUtils.timestampToDateTime(contractBody.getEndTime()));
                    }
                }
                contractProductList.add(contractProduct);
            }
            log.warn("contractDTOList: \n" + contractProductList);
            return contractProductList;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public List<ContractExecuteLog> queryContractProductLog(String token, Map<String, Object> requestMap) {
        //todo
        String url = base_url + "queryLog";

        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("status", (String) requestMap.get("status"));
        jsonObject.addProperty("contractId", (String) requestMap.get("contractId"));
        String ownerPubkey = (String) requestMap.get("owner");
        jsonObject.addProperty("owner", ownerPubkey);

        HttpUtils httpClient = new HttpUtils();

        byte[] response = null;
        List<ContractExecuteLog> contractExecuteLogList;
        try {
            ContractExecuteLog contractExecuteLog;
            response = httpClient.post(url, jsonObject.toString(), token, ContractConstant.HTTP_CONTENT_TYPE_APPLICATION_JSON, "UTF-8");
            Base64 base64 = new Base64();
            ProtoResponseData.ResponseData responseData = ProtoResponseData.ResponseData.parseFrom(response);
            log.warn("list responseData:\n" + responseData);
            byte[] contractExecuteListByte = base64.decode(responseData.getData());
            ContractExecuteLogs.ContractExecuteLogList contractExecuteList = ContractExecuteLogs.ContractExecuteLogList.parseFrom(contractExecuteListByte);
            System.out.println(contractExecuteList);
            contractExecuteLogList = new ArrayList<>(contractExecuteList.getContractLogsCount());
            for (ContractExecuteLogs.ContractExecuteLog contractExecuteLogItem : contractExecuteList.getContractLogsList()) {
                log.warn("contractExecuteLogItem: \n" + contractExecuteLogItem);
                if (contractExecuteLogItem == null || CommonUtils.isEmpty(contractExecuteLogItem.getTaskId())) {
                    return null;
                }
                contractExecuteLog = new ContractExecuteLog();
                contractExecuteLog.setTaskId(contractExecuteLogItem.getTaskId());
                if (!CommonUtils.isEmpty(contractExecuteLogItem.getContractHashId())) {
                    contractExecuteLog.setContractHashId(contractExecuteLogItem.getContractHashId());
                }

                if (!CommonUtils.isEmpty(contractExecuteLogItem.getTimestamp())) {
                    contractExecuteLog.setTimestamp(DateUtils.timestampToDateTime(contractExecuteLogItem.getTimestamp()));
                }
                if (!CommonUtils.isEmpty(contractExecuteLogItem.getCaption())) {
                    contractExecuteLog.setCaption(contractExecuteLogItem.getCaption());
                }
                if (!CommonUtils.isEmpty(contractExecuteLogItem.getCname())) {
                    contractExecuteLog.setCname(contractExecuteLogItem.getCname());
                }
                if (!CommonUtils.isEmpty(contractExecuteLogItem.getCtype())) {
                    contractExecuteLog.setCtype(contractExecuteLogItem.getCtype());
                }
                if (!CommonUtils.isEmpty(contractExecuteLogItem.getDescription())) {
                    contractExecuteLog.setDescription(contractExecuteLogItem.getDescription());
                }
                if (!CommonUtils.isEmpty(contractExecuteLogItem.getState())) {
                    contractExecuteLog.setState(contractExecuteLogItem.getState());
                }
                if (!CommonUtils.isEmpty(contractExecuteLogItem.getCreateTime())) {
                    contractExecuteLog.setCreateTime(DateUtils.timestampToDateTime(contractExecuteLogItem.getCreateTime()));
                }
                contractExecuteLog.setMetaAttribute(contractExecuteLogItem.getMetaAttributeMap());
                contractExecuteLogList.add(contractExecuteLog);
            }
            log.warn("contractExecuteLogList: \n" + contractExecuteLogList);
            return contractExecuteLogList;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean createContract(String token, byte[] protContract) {
        String url = base_url + "create";
        HttpUtils httpClient = new HttpUtils();

        byte[] response = null;
        try {
            response = httpClient.post(url, protContract, token, ContractConstant.HTTP_CONTENT_TYPE_APPLICATION_X_PROTOBUF, "UTF-8");
            Base64 base64 = new Base64();
            ProtoResponseData.ResponseData responseData = ProtoResponseData.ResponseData.parseFrom(response);
            boolean isOk = responseData.getOk();
            log.info("createContract responseData:\n" + responseData);
            log.warn("createContract responseData MSG:\n" +  responseData.getMsg());
            return  isOk;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createContract(String token, String contract, Boolean isXMLContract) throws Exception{
        // todo createContract 入链操作
        // todo 1. convertXmlToJson 转换xml型合约到json
        // todo 2. convertJsonToProto 转换json到可以直接发送post的byte
        // todo 3. response 处理
        if(isXMLContract != null && isXMLContract){
            contract = convertXmlContractToJson(contract);
        }

        byte[] contractProtoByteArray = convertJsonToProto(contract);
        String url = base_url + "create";
//        ProtoContract.ContractOriginal.Builder contractBuilder = ProtoContract.ContractOriginal.newBuilder();
//        ProtoContract.ContractOriginal contract = contractBuilder.build();
//        byte[] buf = contract.toByteArray();
        HttpUtils httpClient = new HttpUtils();
        byte[] response = null;
        response = httpClient.post(url, contractProtoByteArray, token, ContractConstant.HTTP_CONTENT_TYPE_APPLICATION_X_PROTOBUF, "UTF-8");
        Base64 base64 = new Base64();
        ProtoResponseData.ResponseData responseData = ProtoResponseData.ResponseData.parseFrom(response);
        boolean isOk = responseData.getOk();
        if(isOk){
            log.warn("createContract responseData:\n" + responseData);
        }else{
            throw new Exception(responseData.getMsg());
        }
        return true;
    }

    @Override
    public boolean signContract(String token, byte[] protContract) {
        return false;
    }

    @Override
    public boolean testContract(String token, byte[] protContract) {
        return false;
    }

    @Override
    public JSONObject getRunTimeQuery(String token, String contractId) {
        String url = base_url + "queryOutputDuration";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("contractId", contractId);
        HttpUtils httpClient = new HttpUtils();
        byte[] response = null;
        try {
            response = httpClient.post(url, jsonObject.toString(), token, ContractConstant.HTTP_CONTENT_TYPE_APPLICATION_JSON, "UTF-8");
            Base64 base64 = new Base64();
            ProtoResponseData.ResponseData responseData = ProtoResponseData.ResponseData.parseFrom(response);
            String data = responseData.getData();
            if(data == null && "".equals(data)){
                return null;
            }
            byte[] decode = base64.decode(data);
            data = new String(decode,"utf-8");
            JSONObject dataJson = JSONObject.fromObject(data);
            return dataJson;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public JSONObject getTransactionNumQuery(String token, String contractId) {
        String url = base_url + "queryOutputNum";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("contractId", contractId);
        HttpUtils httpClient = new HttpUtils();
        byte[] response = null;
        try {
            response = httpClient.post(url, jsonObject.toString(), token, ContractConstant.HTTP_CONTENT_TYPE_APPLICATION_JSON, "UTF-8");
            Base64 base64 = new Base64();
            ProtoResponseData.ResponseData responseData = ProtoResponseData.ResponseData.parseFrom(response);
            String data = responseData.getData();
            if(data == null && "".equals(data)){
                return null;
            }
            byte[] decode = base64.decode(data);
            data = new String(decode,"utf-8");
            JSONObject dataJson = JSONObject.fromObject(data);
            return dataJson;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public String getDemoDataForWYP(String token, String methodName) {
        String url = base_url;
        if ("queryAccountBalance".equals(methodName) || "queryAmmeterBalance".equals(methodName) || "queryRecords".equals(methodName)) {
            url = base_url + methodName;
            HttpUtils httpClient = new HttpUtils();
            byte[] response = null;
            try {
                response = httpClient.post(url, null, token, ContractConstant.HTTP_CONTENT_TYPE_APPLICATION_JSON, "UTF-8");
                Base64 base64 = new Base64();
                byte[] decode = base64.decode(response);
                String responseData = new String(decode, "utf-8");
                return responseData;
            } catch (Exception e) {
                log.warn(e.getMessage());
                return null;
            }
        }
        return null;
    }
}
