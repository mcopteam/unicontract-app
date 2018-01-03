package com.uniledger.contract.controller;

import com.uniledger.contract.common.ResponseData;
import com.uniledger.contract.common.TokenUser;
import com.uniledger.contract.constants.BaseConstant;
import com.uniledger.contract.dto.ContractExecuteLog;
import com.uniledger.contract.dto.ContractProduct;
import com.uniledger.contract.service.ContractHttpService;
import com.uniledger.contract.utils.CommonUtils;
import com.uniledger.contract.utils.RequestUtils;
import com.uniledger.contract.utils.SessionUtil;
import io.swagger.annotations.*;
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
import java.util.List;
import java.util.Map;

/**
 * Created by wxcsdb88 on 2017/6/9 20:23.
 */
@Api(protocols = "http,https", description = "执行合约")
@Controller
@RequestMapping(value = "/executeContract", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ExecuteContractController {
    private static Logger log = LoggerFactory.getLogger(ExecuteContractController.class);

    @Autowired
    private ContractHttpService contractHttpService;


    /**
     * 合约产品列表[from uni contract and localContract]
     *
     * @param request        request {@link HttpServletRequest}
     * @param model          {@link Model}
     * @param token          token标记
     * @param contractStates 合约状态[链上状态]
     * @param contractId     合约id
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "执行合约列表", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/queryAll", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData productList(HttpServletRequest request, Model model,
                                    @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                    @ApiParam(name = "status", required = false, value = "合约执行状态") @RequestParam(name = "status", required = false) String contractStates,
                                    @ApiParam(name = "contractId", required = false, value = "合约id") @RequestParam(name = "contractId", required = false) String contractId){
        //1. 根据用户pubkey，状态及查询条件name id 查询合约列表
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<List<ContractProduct>> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)) {
            responseData.setBadRequest("token is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_CUSTOM.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s,%s]可以进行此操作！",
                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER),
                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_CUSTOM)));
            return responseData;
        }
        String contractOwner = currentUser.getPubkey();
        Map<String, Object> requestMap = new HashMap<>();
        if(CommonUtils.isEmpty(contractStates)){
//            requestMap.put("status", "Contract_In_Process");
            requestMap.put("status", "Contract_Signature");
        }else{
            requestMap.put("status", contractStates);
        }
        requestMap.put("contractId", contractId);
        requestMap.put("owner", currentUser.getPubkey());

        List<ContractProduct> contractProductList = contractHttpService.queryExecuteContractList(token, requestMap);
        if (CommonUtils.isEmpty(contractProductList)) {
            responseData.setOK("no match result");
            return responseData;
        }
        responseData.setData(contractProductList);
        responseData.setOK("query contract productList success");
        return responseData;
    }

    /**
     * 合约产品列表[from uni contract and localContract]
     *
     * @param request        request {@link HttpServletRequest}
     * @param model          {@link Model}
     * @param token          token标记
     * @param contractStates 合约状态[链上状态]
     * @param contractId     合约id
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "执行合约", notes = "单个合约记录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/query", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData product(HttpServletRequest request, Model model,
                                @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                @ApiParam(name = "status", required = false, value = "合约执行状态") @RequestParam(name = "status", required = false) String contractStates,
                                @ApiParam(name = "contractId", required = true, value = "合约id") @RequestParam(name = "contractId", required = true) String contractId) {
        //1. 根据用户pubkey，状态及查询条件name id 查询合约列表
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<ContractProduct> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, contractId)) {
            responseData.setBadRequest("token or contractId is blank!");
            return responseData;
        }
        //todo contractStates 验证！！！
        //TODO 合约执行状态
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_CUSTOM.equals(currentUser.getRole())&& !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s,%s]可以进行此操作！",
                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER),
                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_CUSTOM)));
            return responseData;
        }
        String contractOwner = currentUser.getPubkey();
        Map<String, Object> requestMap = new HashMap<>();
        if(CommonUtils.isEmpty(contractStates)){
//            requestMap.put("status", "Contract_In_Process");
            requestMap.put("status", "Contract_Signature");
        }else{
            requestMap.put("status", contractStates);
        }
        requestMap.put("contractId", contractId);
        requestMap.put("owner", currentUser.getPubkey());

        ContractProduct contractProductList = contractHttpService.queryExecuteContract(token, requestMap);
        if (CommonUtils.isEmpty(contractProductList)) {
            responseData.setOK("no match result");
            return responseData;
        }
        responseData.setData(contractProductList);
        responseData.setOK("query contract product success");
        return responseData;
    }

    /**
     * 执行合约-执行日志
     * @param request   {@link HttpServletRequest}
     * @param model     {@link Model}
     * @param token     token标记
     * @param contractId    合约id
     * @return  {@link ResponseData}
     */
    @ApiOperation(value = "合约执行日志", notes = "待完善！")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/queryLog", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData queryContractProductLog(HttpServletRequest request, Model model,
                                                @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                                @ApiParam(name = "contractId", required = true, value = "合约id")@RequestParam(name = "contractId", required = true) String contractId) {

        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<List<ContractExecuteLog>> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)){
            responseData.setBadRequest("token or owner is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("contractId", contractId);
        requestMap.put("owner", currentUser.getPubkey());
        List<ContractExecuteLog> contractExecuteLogList = contractHttpService.queryContractProductLog(token, requestMap);
        if (CommonUtils.isEmpty(contractExecuteLogList)){
            responseData.setOK("no match result");
            return responseData;
        }
        responseData.setData(contractExecuteLogList);
        responseData.setOK("query contract productList success");
        return responseData;
    }
}
