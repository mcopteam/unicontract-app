package com.uniledger.contract.service.impl;

import com.uniledger.contract.dao.ContractMapper;
import com.uniledger.contract.dao.ContractUserMapper;
import com.uniledger.contract.model.ContractUser;
import com.uniledger.contract.model.LocalContract;
import com.uniledger.contract.service.UnichainHttpService;
import com.uniledger.contract.utils.DateUtils;
import com.uniledger.contract.utils.HttpUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by wade on 2017/6/15.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UnichainHttpServiceImpl implements UnichainHttpService{

    @Autowired
    private ContractUserMapper contractUserMapper;

    @Autowired
    private ContractMapper contractMapper;

    private static Logger log = LoggerFactory.getLogger(UnichainHttpServiceImpl.class);

    private static final String CONFIG = "remote.properties";
    private static String base_url;
    private static Properties properties;

    static {
        properties = new Properties();
        try {
            InputStream inputStream = DateUtils.class.getClassLoader().getResourceAsStream(CONFIG);
            properties.load(inputStream);
            base_url = properties.getProperty("unichain.url");
            log.info(String.format("remote_server[base_url=%s]", base_url));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    @Override
    public JSONArray getTransferTransaction(String publicKey) {

        String url = base_url + "transaction/getTxRecord?public_key=" + publicKey;
        HttpUtils httpClient = new HttpUtils();
        String result = null;
        JSONArray data = null;

        try {
            result = httpClient.get(url);
            log.warn(":getTransferTransaction: \n" + result);
            data = JSONArray.fromObject(result);
            for (int i = 0; i < data.size(); i++){
                Object ownerBefore = data.getJSONObject(i).get("owner_before");
                Object ownersAfter = data.getJSONObject(i).get("owners_after");
                Object timestamp = data.getJSONObject(i).get("timestamp");
//                时间戳转换
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String transferTime = (String)timestamp;
                long lt = new Long(transferTime);
                Date date = new Date(lt);
                transferTime = simpleDateFormat.format(date);
                data.getJSONObject(i).put("timestamp",transferTime);
                //获取转账者名字
                ContractUser ownerBeforeUser = contractUserMapper.getUserUsePubkey((String)ownerBefore);
                ContractUser ownerAfterUser = contractUserMapper.getUserUsePubkey((String)ownersAfter);
                data.getJSONObject(i).put("owner_before",ownerBeforeUser.getUsername());
                data.getJSONObject(i).put("owners_after",ownerAfterUser.getUsername());

            }
//            result = data.toString();
        } catch (Exception e) {
//            log.warn(e.getMessage());
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public int getAccountBalance(String publicKey) {
        String url = base_url + "condition/getUnspentTxs?unspent=true&public_key=" + publicKey;
        HttpUtils httpClient = new HttpUtils();
        String result = null;
        JSONArray data = null;
        int responData = 0;
        try {
            result = httpClient.get(url);
            log.warn(":getTransferTransaction: \n" + result);
            JSONObject jsonObject = JSONObject.fromObject(result);
            data = (JSONArray) jsonObject.get("data");
            for (int i = 0; i < data.size(); i++){
                data.getJSONObject(i).remove("details");
                Object amount1 = data.getJSONObject(i).get("amount");
                responData = responData + (int)amount1;
            }
//            result = data.toString();
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return responData;
    }

    @Override
    public List<LocalContract> getUserAllLocalContract(String pubkey) {
        return contractMapper.getUserAllLocalContract(pubkey);
    }
}
