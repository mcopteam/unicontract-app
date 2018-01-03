package com.uniledger.contract.controller;

import com.uniledger.contract.common.ResponseData;
import com.uniledger.contract.common.TokenUser;
import com.uniledger.contract.constants.BaseConstant;
import com.uniledger.contract.dto.PreSignContractDTO;
import com.uniledger.contract.model.ContractUser;
import com.uniledger.contract.service.ContractHttpService;
import com.uniledger.contract.service.ContractService;
import com.uniledger.contract.service.ContractUserService;
import com.uniledger.contract.service.UnichainHttpService;
import com.uniledger.contract.utils.CommonUtils;
import com.uniledger.contract.utils.ProtoConvertUtils;
import com.uniledger.contract.utils.RequestUtils;
import com.uniledger.contract.utils.SessionUtil;
import io.swagger.annotations.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wade on 2017/6/20.
 */
@Api(protocols = "http,https", description = "智能微网demo")
@Controller
@RequestMapping(value = "/intelligentNetwork")
public class IntelligentNetworkController {
    private static Logger log = LoggerFactory.getLogger(IntelligentNetworkController.class);

    @Autowired
    private ContractHttpService contractHttpService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractUserService contractUserService;

    @Autowired
    private UnichainHttpService unichainHttpService;


    /**
     * 设置微网参数
     * 1. 取自本地数据库的合约xml，转为json格式至web
     * @param request   {@link HttpServletRequest}
     * @param model     {@link Model}
     * @param token     token标记
     * @return  {@link ResponseData}
     */
    @ApiOperation(value = "设置微网参数", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/paramSetting", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData transferSetting(HttpServletRequest request, Model model,
                                        @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                        @ApiParam(name = "contract", required = true, value = "json格式合约")@RequestParam(name = "contract", required = true) String contract) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token,contract)){
            responseData.setBadRequest("token or id is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_CUSTOM.equals(currentUser.getRole())&& !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_CUSTOM)));
            return responseData;
        }
        if(CommonUtils.isEmpty(currentUser.getPubkey())){
            responseData.setException("请先激活密钥或者申请后再进行此操作!");
            return responseData;
        }
        if(CommonUtils.isEmpty(contract)){
            responseData.setException("合约内容错误!");
            return responseData;
        }
        byte[] protoContract = ProtoConvertUtils.jsonContractToProtoByte(contract);
        boolean signContractOk = contractHttpService.createContract(token, protoContract);
        if(!signContractOk){
            responseData.setException("执行失败!!!", false);
            return responseData;
        }
        responseData.setOK("开始执行!!!", true);
        return responseData;
    }

    /**
     * 获取合约运行时间
     * @param request {@link HttpServletRequest}
     * @param model {@link Model}
     * @return
     */
    @ApiOperation(value = "获取合约运行时间", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/runTimeQuery", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData runTimeQuery(HttpServletRequest request, Model model,
                                      @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                      @ApiParam(name = "contranctId", required = true, value = "标识", defaultValue = "170614121259039057")@RequestParam(name = "contranctId", required = true) String contranctId) {

        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<JSONObject> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)){
            responseData.setBadRequest("token is blank!");
            return responseData;
        }

//        TokenUser currentUser = SessionUtil.getCurrentUser(request);
//        if (!BaseConstant.USER_ROLE_CUSTOM.equals(currentUser.getRole())) {
//            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_CUSTOM)));
//            return responseData;
//        }
        JSONObject result = contractHttpService.getRunTimeQuery(token,contranctId);
        if (CommonUtils.isEmpty(result)) {
            responseData.setOK("no match result");
            return responseData;
        }
        responseData.setData(result);
        responseData.setOK("query contract time success");
        return responseData;
    }


    /**
     * 获取合约总交易量
     * @param request {@link HttpServletRequest}
     * @param model {@link Model}
     * @return
     */
    @ApiOperation(value = "获取合约总交易量", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/transactionNumQuery", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData transactionNumQuery(HttpServletRequest request, Model model,
                                     @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                     @ApiParam(name = "contranctId", required = true, value = "标识", defaultValue = "170614121259039057")@RequestParam(name = "contranctId", required = true) String contranctId) {

        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<JSONObject> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)){
            responseData.setBadRequest("token is blank!");
            return responseData;
        }

        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_CUSTOM.equals(currentUser.getRole())&& !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_CUSTOM)));
            return responseData;
        }
        JSONObject result = contractHttpService.getTransactionNumQuery(token,contranctId);
        if (CommonUtils.isEmpty(result)) {
            responseData.setOK("no match result");
            return responseData;
        }
        responseData.setData(result);
        responseData.setOK("query transaction num success");
        return responseData;
    }

    /**
     * 获取合约总交易
     * @param request {@link HttpServletRequest}
     * @param model {@link Model}
     * @return
     */
    @ApiOperation(value = "获取合约总交易", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/transactionAllQuery", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData transactionAllQuery(HttpServletRequest request, Model model,
                                     @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                     @ApiParam(name = "publicKey", required = true, value = "标识", defaultValue = "EYXHEsPzKTZTgVRmUAWkhEEW8Qc9x2ig7iNCs1r8CD1H")@RequestParam(name = "publicKey", required = true) String publicKey) {

        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<JSONArray> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)){
            responseData.setBadRequest("token is blank!");
            return responseData;
        }

        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_CUSTOM.equals(currentUser.getRole())&& !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_CUSTOM)));
            return responseData;
        }
        JSONArray result = unichainHttpService.getTransferTransaction(publicKey);
        if (CommonUtils.isEmpty(result)) {
            responseData.setOK("no match result");
            return responseData;
        }
        responseData.setData(result);
        responseData.setOK("query all transaction success");
        return responseData;
    }

    /**
     * 单个合约查询
     * @param request {@link HttpServletRequest}
     * @param model {@link Model}
     * @return
     */
    @ApiOperation(value = "单个合约查询", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/contractQuerySingle", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData contractQuerySingle(HttpServletRequest request, Model model,
                                            @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                            @ApiParam(name = "contractId", required = true, value = "合约id", defaultValue = "170614121259039057")@RequestParam(name = "contractId", required = true) String contractId){

        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData< Map<String,Object>> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, contractId)) {
            responseData.setBadRequest("token or contractId is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_AUDIT.equals(currentUser.getRole()) &&
                !BaseConstant.USER_ROLE_CUSTOM.equals(currentUser.getRole())&& !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s,%s,%s]可以进行此操作！",
                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER),
                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_AUDIT),
                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_CUSTOM)));
            return responseData;
        }

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("contractId", contractId);
        requestMap.put("owner", currentUser.getPubkey());
        String jsonContract =  contractHttpService.queryContractContent(token,requestMap);
        if (CommonUtils.isEmpty(jsonContract)) {
            responseData.setOK("no match result");
            return responseData;
        }
        ContractUser contractUser = contractUserService.getUserById(currentUser.getId());
        PreSignContractDTO preSignContractDTO = new PreSignContractDTO();
        preSignContractDTO.setUsername(currentUser.getUsername());
        preSignContractDTO.setContract(jsonContract);
        preSignContractDTO.setCurrentUserPubkey(currentUser.getPubkey());
        preSignContractDTO.setCurrentUserPrikey(contractUser.getPrikey());
        preSignContractDTO.setUserId(currentUser.getId());

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("contractData",preSignContractDTO);
        resultMap.put("contractId",CommonUtils.generateSerialNo());
        responseData.setOK("preSign op success", resultMap);
        return responseData;
    }





}
