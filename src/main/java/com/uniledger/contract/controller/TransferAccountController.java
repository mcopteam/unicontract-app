package com.uniledger.contract.controller;

import com.uniledger.contract.common.ResponseData;
import com.uniledger.contract.common.TokenUser;
import com.uniledger.contract.constants.BaseConstant;
import com.uniledger.contract.dto.ContractProduct;
import com.uniledger.contract.dto.PreSignContractDTO;
import com.uniledger.contract.model.ContractUser;
import com.uniledger.contract.model.LocalContract;
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
 * Created by wade on 2017/6/15 14:00.
 */
@Api(protocols = "http,https", description = "转账，余额，交易查询")
@Controller
@RequestMapping(value = "/transferController")
public class TransferAccountController {
    private static Logger log = LoggerFactory.getLogger(TransferAccountController.class);

    @Autowired
    private UnichainHttpService unichainHttpService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractUserService contractUserService;

    @Autowired
    private ContractHttpService contractHttpService;

    /**
     * 转账设置
     * 1. 取自本地数据库的合约xml，转为json格式至web
     * @param request   {@link HttpServletRequest}
     * @param model     {@link Model}
     * @param token     token标记
     * @return  {@link ResponseData}
     */
    @ApiOperation(value = "转账设置", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/transferSetting", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
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
            responseData.setException("执行失败!", false);
            return responseData;
        }
        responseData.setOK("开始执行！", true);
        return responseData;
    }


    /**
     * 查询转账记录
     * @param request {@link HttpServletRequest}
     * @param model {@link Model}
     * @return
     */
    @ApiOperation(value = "转账记录查询", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/transferQuery", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData transferQuery(HttpServletRequest request, Model model,
                                      @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token) {

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
        String pubkey = currentUser.getPubkey();
        JSONArray result = unichainHttpService.getTransferTransaction(pubkey);
        if (CommonUtils.isEmpty(result)) {
            responseData.setOK("no match result");
            return responseData;
        }
        responseData.setData(result);
        responseData.setOK("query transfer account success");
        return responseData;
    }

    /**
     * 余额查询
     * @param request {@link HttpServletRequest}
     * @param model {@link Model}
     * @param token token标记
     * @return
     */
    @ApiOperation(value = "余额查询", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/balanceQuery", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData balanceQuery(HttpServletRequest request, Model model,
                                     @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token) {

        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<Map<String,Object>> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)){
            responseData.setBadRequest("token is blank!");
            return responseData;
        }

        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_CUSTOM.equals(currentUser.getRole())&& !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_CUSTOM)));
            return responseData;
        }
        String pubkey = currentUser.getPubkey();
        int result = unichainHttpService.getAccountBalance(pubkey);
        if (CommonUtils.isEmpty(result)) {
            responseData.setOK("no match result");
            return responseData;
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("amount",result);
        responseData.setData(resultMap);
        responseData.setOK("query balance account success");
        return responseData;
    }


    /**
     * 用户所有合约查询
     * @param request {@link HttpServletRequest}
     * @param model {@link Model}
     * @return
     */
    @ApiOperation(value = "用户所有合约查询", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/contractQueryAll", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData contractQueryAll(HttpServletRequest request, Model model,
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
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_CUSTOM.equals(currentUser.getRole())&& !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s,%s]可以进行此操作！",
                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER),
                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_CUSTOM)));
            return responseData;
        }
        String contractOwner = currentUser.getPubkey();
        Map<String, Object> requestMap = new HashMap<>();
        if(CommonUtils.isEmpty(contractStates)){
            requestMap.put("status", "Contract_In_Process");
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
     * 单个合约查询rethinkdb
     * @param request {@link HttpServletRequest}
     * @param model {@link Model}
     * @return
     */
    @ApiOperation(value = "单个合约查询rethinkdb", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/contractQuerySingleRethinkdb", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData contractQuerySingleRethinkdb(HttpServletRequest request, Model model,
                                            @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                            @ApiParam(name = "contractId", required = true, value = "合约id", defaultValue = "1121312312")@RequestParam(name = "contractId", required = true) String contractId){

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


    /**
     * 单个合约查询mysql
     * @param request {@link HttpServletRequest}
     * @param model {@link Model}
     * @return
     */
    @ApiOperation(value = "单个合约查询mysql", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/contractQuerySingleMysql", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData contractQuerySingleMysql(HttpServletRequest request, Model model,
                                            @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                            @ApiParam(name = "contractId", required = true, value = "合约id", defaultValue = "1121312312")@RequestParam(name = "contractId", required = true) String contractId){

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
        LocalContract localContract = contractService.getFileContractByContractId(contractId);
        String jsonContractFull = localContract.getJsonContract();
        if (CommonUtils.isEmpty(jsonContractFull)) {
            responseData.setOK("no match result");
            return responseData;
        }
        ContractUser contractUser = contractUserService.getUserById(currentUser.getId());
        PreSignContractDTO preSignContractDTO = new PreSignContractDTO();
        preSignContractDTO.setUsername(currentUser.getUsername());
        preSignContractDTO.setContract(jsonContractFull);
        preSignContractDTO.setCurrentUserPubkey(currentUser.getPubkey());
        preSignContractDTO.setCurrentUserPrikey(contractUser.getPrikey());
        preSignContractDTO.setUserId(currentUser.getId());

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("contractData",preSignContractDTO);
        resultMap.put("contractId",CommonUtils.generateSerialNo());
        responseData.setOK("preSign op success", resultMap);
        return responseData;
    }


    /**
     * 查询用户信息
     * @param request {@link HttpServletRequest}
     * @param model {@link Model}
     * @return
     */
    @ApiOperation(value = "查询用户信息", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData getUserInfo(HttpServletRequest request, Model model,
                                         @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token) {

        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<List<ContractUser>> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)){
            responseData.setBadRequest("token is blank!");
            return responseData;
        }

        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_CUSTOM.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_CUSTOM)));
            return responseData;
        }
        List<ContractUser> allContractUser = contractUserService.getAllContractUser();
        if (CommonUtils.isEmpty(allContractUser)) {
            responseData.setOK("no match result");
            return responseData;
        }
        responseData.setData(allContractUser);
        responseData.setOK("query user success");
        return responseData;
    }
//
    /**
     * 获取最新合约
     * @param request {@link HttpServletRequest}
     * @param model {@link Model}
     * @return
     */
    @ApiOperation(value = "获取最新合约", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/queryLatelyContract", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData queryLatelyContract(HttpServletRequest request, Model model,
                                    @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token) {

        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<LocalContract> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)){
            responseData.setBadRequest("token is blank!");
            return responseData;
        }

        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_CUSTOM.equals(currentUser.getRole())&& !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_CUSTOM)));
            return responseData;
        }
        LocalContract lateContract = contractService.getFileContractByNameAndOwner("单次转账合约", currentUser.getPubkey());
        if (CommonUtils.isEmpty(lateContract)) {
            responseData.setOK("no match result");
            return responseData;
        }
        responseData.setData(lateContract);
        responseData.setOK("query lately contract success");
        return responseData;
    }


//    /**
//     * 余额查询
//     * @param request {@link HttpServletRequest}
//     * @param model {@link Model}
//     * @return
//     */
//    @ApiOperation(value = "余额查询", notes = "")
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "保存成功"),
//            @ApiResponse(code = 400, message = "参数错误"),
//            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
//    })
//    @RequestMapping(value = "/balanceQuery", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseData balanceQuery(HttpServletRequest request, Model model,
//                                     @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token) {
//
//        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
//        ResponseData<JSONArray> responseData = new ResponseData<>();
////        if (CommonUtils.isEmpty(token)){
////            responseData.setBadRequest("token or xmlContract is blank!");
////            return responseData;
////        }
////
////        TokenUser currentUser = SessionUtil.getCurrentUser(request);
////        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole())) {
////            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
////            return responseData;
////        }
////        //转账记录
////        String pubkey = currentUser.getPubkey();
//        JSONArray result = unichainHttpService.getAccountBalance("EYXHEsPzKTZTgVRmUAWkhEEW8Qc9x2ig7iNCs1r8CD1H");
//        if (CommonUtils.isEmpty(result)) {
//            responseData.setOK("no match result");
//            return responseData;
//        }
//        responseData.setData(result);
//        responseData.setOK("query account balance success");
//        return responseData;
//    }


//    /**
//     * 合约查询
//     * @param request {@link HttpServletRequest}
//     * @param model {@link Model}
//     * @return
//     */
//    @ApiOperation(value = "合约记录查询", notes = "")
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "保存成功"),
//            @ApiResponse(code = 400, message = "参数错误"),
//            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
//    })
//    @RequestMapping(value = "/contractQuery", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseData contractQueryAll(HttpServletRequest request, Model model,
//                                         @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token) {
//
//        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
//        ResponseData<List<LocalContract>> responseData = new ResponseData<>();
////        if (CommonUtils.isEmpty(token)){
////            responseData.setBadRequest("token or xmlContract is blank!");
////            return responseData;
////        }
////
////        TokenUser currentUser = SessionUtil.getCurrentUser(request);
////        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole())) {
////            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
////            return responseData;
////        }
////        //转账记录
////        String pubkey = currentUser.getPubkey();
//        List<LocalContract> result = unichainHttpService.getUserAllLocalContract("8qi3Zy655uowWQUhXoRdQs1zyvVapr6KdpXsbp8YTFhM");
//        if (CommonUtils.isEmpty(result)) {
//            responseData.setOK("no match result");
//            return responseData;
//        }
//        responseData.setData(result);
//        responseData.setOK("query contract success");
//        return responseData;
//    }


//    /**
//     * 查询转账记录
//     * @param request {@link HttpServletRequest}
//     * @param model {@link Model}
//     * @return
//     */
//    @ApiOperation(value = "转账记录查询", notes = "")
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "保存成功"),
//            @ApiResponse(code = 400, message = "参数错误"),
//            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
//    })
//    @RequestMapping(value = "/transferQuery", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseData transferQuery(HttpServletRequest request, Model model,
//                                      @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token) {
//
//        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
//        ResponseData<JSONArray> responseData = new ResponseData<>();
////        if (CommonUtils.isEmpty(token)){
////            responseData.setBadRequest("token or xmlContract is blank!");
////            return responseData;
////        }
////
////        TokenUser currentUser = SessionUtil.getCurrentUser(request);
////        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole())) {
////            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
////            return responseData;
////        }
////        转账记录
////        String pubkey = currentUser.getPubkey();
//
//        JSONArray result = unichainHttpService.getTransferTransaction("EYXHEsPzKTZTgVRmUAWkhEEW8Qc9x2ig7iNCs1r8CD1H");
//        if (CommonUtils.isEmpty(result)) {
//            responseData.setOK("no match result");
//            return responseData;
//        }
//        responseData.setData(result);
//        responseData.setOK("query transfer account success");
//        return responseData;
//    }



//    /**
//     * 单个合约查询
//     * @param request {@link HttpServletRequest}
//     * @param model {@link Model}
//     * @return
//     */
//    @ApiOperation(value = "单个合约查询", notes = "")
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "保存成功"),
//            @ApiResponse(code = 400, message = "参数错误"),
//            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
//    })
//    @RequestMapping(value = "/contractQuerySingle", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseData contractQuerySingle(HttpServletRequest request, Model model,
//                                            @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
//                                            @ApiParam(name = "contractId", required = true, value = "合约id", defaultValue = "1121312312")@RequestParam(name = "contractId", required = true) String contractId){
//
//        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
//        ResponseData< Map<String,Object>> responseData = new ResponseData<>();
//        if (CommonUtils.isEmpty(token, contractId)) {
//            responseData.setBadRequest("token or contractId is blank!");
//            return responseData;
//        }
//
//        String testJsonStr = "{\n" +
//                "  \"ContractBody\": {\n" +
//                "    \"Caption\": \"购智能手机返话费合约产品协议\",\n" +
//                "    \"Cname\": \"contract_mobilecallback\",\n" +
//                "    \"ContractAssets\": [\n" +
//                "      {\n" +
//                "        \"Amount\": 1,\n" +
//                "        \"AssetId\": \"xxxxxxxxxxx\",\n" +
//                "        \"Caption\": \"智能手机\",\n" +
//                "        \"Description\": \"智能手机，返还话费产品\",\n" +
//                "        \"MetaData\": {\n" +
//                "          \"TestAsset1\": \"1111111111\",\n" +
//                "          \"TestAsset2\": \"2222222222\"\n" +
//                "        },\n" +
//                "        \"Name\": \"asset_money\",\n" +
//                "        \"Unit\": \"台\"\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"ContractComponents\": [\n" +
//                "      {\n" +
//                "        \"AgainstArguments\": null,\n" +
//                "        \"CandidateList\": null,\n" +
//                "        \"Caption\": \"查询用户账户\",\n" +
//                "        \"Cname\": \"enquiry_A\",\n" +
//                "        \"CompleteCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_comcond_A\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": null,\n" +
//                "            \"ExpressionStr\": \"expression_data_A.ExpressionResult.code ==200\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"Ctype\": \"Component_Task.Task_Enquiry\",\n" +
//                "        \"DataList\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Category\": null,\n" +
//                "            \"Cname\": \"data_A\",\n" +
//                "            \"Ctype\": \"Component_Data.Data_Float\",\n" +
//                "            \"DataRangeFloat\": null,\n" +
//                "            \"DataRangeInt\": null,\n" +
//                "            \"DataRangeUint\": null,\n" +
//                "            \"DefaultValueFloat\": 0,\n" +
//                "            \"DefaultValueInt\": 0,\n" +
//                "            \"DefaultValueString\": \"\",\n" +
//                "            \"DefaultValueUint\": 0,\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"Format\": \"\",\n" +
//                "            \"HardConvType\": \"float64\",\n" +
//                "            \"Mandatory\": false,\n" +
//                "            \"ModifyDate\": \"\",\n" +
//                "            \"Options\": null,\n" +
//                "            \"Parent\": null,\n" +
//                "            \"Unit\": \"元\",\n" +
//                "            \"ValueFloat\": 600,\n" +
//                "            \"ValueInt\": 0,\n" +
//                "            \"ValueString\": \"\",\n" +
//                "            \"ValueUint\": 0\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"DataValueSetterExpressionList\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_data_A\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_Function\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": null,\n" +
//                "            \"ExpressionStr\": \"FuncGetBalance(contract_mobilecallback.ContractOwners.0)\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"DecisionResult\": null,\n" +
//                "        \"Description\": \"查询移动用户A账户是否有500元\",\n" +
//                "        \"DiscardCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_discond_A\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"true\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"MetaAttribute\": null,\n" +
//                "        \"NextTasks\": [\n" +
//                "          \"action_B\",\n" +
//                "          \"action_C\"\n" +
//                "        ],\n" +
//                "        \"PreCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"查询用户账户前提条件\",\n" +
//                "            \"Cname\": \"expression_precond_A\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"当前日期大于等于合约生效起始日期\",\n" +
//                "            \"ExpressionResult\": null,\n" +
//                "            \"ExpressionStr\": \"contract_mobilecallback.StartTime >=\\\"2017-01-01 12:00:00\\\"&&contract_mobilecallback.EndTime <=\\\"2017-12-31 23:59:59\\\"\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"SelectBranches\": [\n" +
//                "          {\n" +
//                "            \"BranchExpressionStr\": \"data_A.Value>500\",\n" +
//                "            \"BranchExpressionValue\": \"\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"BranchExpressionStr\": \"data_A.Value<=500\",\n" +
//                "            \"BranchExpressionValue\": \"\"\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"State\": \"TaskState_Dormant\",\n" +
//                "        \"Support\": 0,\n" +
//                "        \"SupportArguments\": null,\n" +
//                "        \"TaskExecuteIdx\": 0,\n" +
//                "        \"TaskId\": \"UUID110-1234-11111\",\n" +
//                "        \"TaskList\": null,\n" +
//                "        \"Text\": null\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"AgainstArguments\": null,\n" +
//                "        \"CandidateList\": null,\n" +
//                "        \"Caption\": \"A购置手机\",\n" +
//                "        \"Cname\": \"action_B\",\n" +
//                "        \"CompleteCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_comcond_B\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"expression_data_B.ExpressionResult.code==200\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"Ctype\": \"Component_Task.Task_Action\",\n" +
//                "        \"DataList\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"用户A月消费额\",\n" +
//                "            \"Category\": null,\n" +
//                "            \"Cname\": \"data_D\",\n" +
//                "            \"Ctype\": \"Component_Data.Data_Float\",\n" +
//                "            \"DataRangeFloat\": null,\n" +
//                "            \"DataRangeInt\": null,\n" +
//                "            \"DataRangeUint\": null,\n" +
//                "            \"DefaultValueFloat\": 0,\n" +
//                "            \"DefaultValueInt\": 0,\n" +
//                "            \"DefaultValueString\": \"\",\n" +
//                "            \"DefaultValueUint\": 0,\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"Format\": \"\",\n" +
//                "            \"HardConvType\": \"float64\",\n" +
//                "            \"Mandatory\": false,\n" +
//                "            \"ModifyDate\": \"\",\n" +
//                "            \"Options\": null,\n" +
//                "            \"Parent\": null,\n" +
//                "            \"Unit\": \"元\",\n" +
//                "            \"ValueFloat\": 80,\n" +
//                "            \"ValueInt\": 0,\n" +
//                "            \"ValueString\": \"\",\n" +
//                "            \"ValueUint\": 0\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"DataValueSetterExpressionList\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"A转账500给B\",\n" +
//                "            \"Cname\": \"expression_data_B\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_Function\",\n" +
//                "            \"Description\": \"用户A转账500元给移动运营商B(运营商B将手机快递给用户A,不在线上确认)\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"FuncTanferMoney(contract_mobilecallback.ContractOwners.0,contract_mobilecallback.ContractOwners.1,500)\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"DecisionResult\": null,\n" +
//                "        \"Description\": \"移动用户A账户存在500元：用户A将500元转账给移动运营商B，运营商B将手机快递给用户A\",\n" +
//                "        \"DiscardCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_discond_B\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"true\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"MetaAttribute\": null,\n" +
//                "        \"NextTasks\": [\n" +
//                "          \"enquiry_D\"\n" +
//                "        ],\n" +
//                "        \"PreCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_precond_B\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"contract_mobilecallback.StartTime>=\\\"2017-01-01 12:00:00\\\"&&contract_mobilecallback.EndTime<=\\\"2017-12-31 23:59:59\\\"&&data_A.Value>500\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"SelectBranches\": null,\n" +
//                "        \"State\": \"TaskState_Dormant\",\n" +
//                "        \"Support\": 0,\n" +
//                "        \"SupportArguments\": null,\n" +
//                "        \"TaskExecuteIdx\": 0,\n" +
//                "        \"TaskId\": \"UUID110-1234-11112\",\n" +
//                "        \"TaskList\": null,\n" +
//                "        \"Text\": null\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"AgainstArguments\": null,\n" +
//                "        \"CandidateList\": null,\n" +
//                "        \"Caption\": \"A账户存款500元\",\n" +
//                "        \"Cname\": \"action_C\",\n" +
//                "        \"CompleteCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_comcond_C\",\n" +
//                "            \"Ctype\": \"Component_Expression.Exporess_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": null,\n" +
//                "            \"ExpressionStr\": \"expression_data_C.ExpressionResult.code==200\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"Ctype\": \"Component_Task.Task_Action\",\n" +
//                "        \"DataList\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"用户A月消费额\",\n" +
//                "            \"Category\": null,\n" +
//                "            \"Cname\": \"data_D\",\n" +
//                "            \"Ctype\": \"Component_Data.Data_Float\",\n" +
//                "            \"DataRangeFloat\": null,\n" +
//                "            \"DataRangeInt\": null,\n" +
//                "            \"DataRangeUint\": null,\n" +
//                "            \"DefaultValueFloat\": 0,\n" +
//                "            \"DefaultValueInt\": 0,\n" +
//                "            \"DefaultValueString\": \"\",\n" +
//                "            \"DefaultValueUint\": 0,\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"Format\": \"\",\n" +
//                "            \"HardConvType\": \"float64\",\n" +
//                "            \"Mandatory\": false,\n" +
//                "            \"ModifyDate\": \"\",\n" +
//                "            \"Options\": null,\n" +
//                "            \"Parent\": null,\n" +
//                "            \"Unit\": \"元\",\n" +
//                "            \"ValueFloat\": 80,\n" +
//                "            \"ValueInt\": 0,\n" +
//                "            \"ValueString\": \"\",\n" +
//                "            \"ValueUint\": 0\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"DataValueSetterExpressionList\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_data_C\",\n" +
//                "            \"Ctype\": \"Component_Expression.Exporess_Function\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"FuncDeposit(contract_mobilecallback.ContractOwners.0， 500)\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"DecisionResult\": null,\n" +
//                "        \"Description\": \"移动用户A账户不存在500元：用户Ａ往账户存入500元，然后将500元转账给移动运营商B\",\n" +
//                "        \"DiscardCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_discond_C\",\n" +
//                "            \"Ctype\": \"Component_Expression.Exporess_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"true\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"MetaAttribute\": null,\n" +
//                "        \"NextTasks\": [\n" +
//                "          \"action_B\"\n" +
//                "        ],\n" +
//                "        \"PreCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_precond_C\",\n" +
//                "            \"Ctype\": \"Component_Expression.Exporess_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"contract_mobilecallback.StartTime>=\\\"2017-01-01 12:00:00\\\"&&contract_mobilecallback.EndTime<=\\\"2017-12-31 23:59:59\\\"&.data_A.Value<500\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"SelectBranches\": null,\n" +
//                "        \"State\": \"TaskState_Dormant\",\n" +
//                "        \"Support\": 0,\n" +
//                "        \"SupportArguments\": null,\n" +
//                "        \"TaskExecuteIdx\": 0,\n" +
//                "        \"TaskId\": \"UUID110-1234-11113\",\n" +
//                "        \"TaskList\": null,\n" +
//                "        \"Text\": null\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"AgainstArguments\": null,\n" +
//                "        \"CandidateList\": null,\n" +
//                "        \"Caption\": \"查询用户月消费额\",\n" +
//                "        \"Cname\": \"enquiry_D\",\n" +
//                "        \"CompleteCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_comcond_D\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"expression_data_D.ExpressionResult.code==200\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"Ctype\": \"Component_Task.Task_Enquiry\",\n" +
//                "        \"DataList\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"用户A月消费额\",\n" +
//                "            \"Category\": null,\n" +
//                "            \"Cname\": \"data_D\",\n" +
//                "            \"Ctype\": \"Component_Data.Data_Float\",\n" +
//                "            \"DataRangeFloat\": null,\n" +
//                "            \"DataRangeInt\": null,\n" +
//                "            \"DataRangeUint\": null,\n" +
//                "            \"DefaultValueFloat\": 0,\n" +
//                "            \"DefaultValueInt\": 0,\n" +
//                "            \"DefaultValueString\": \"\",\n" +
//                "            \"DefaultValueUint\": 0,\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"Format\": \"\",\n" +
//                "            \"HardConvType\": \"float64\",\n" +
//                "            \"Mandatory\": false,\n" +
//                "            \"ModifyDate\": \"\",\n" +
//                "            \"Options\": null,\n" +
//                "            \"Parent\": null,\n" +
//                "            \"Unit\": \"元\",\n" +
//                "            \"ValueFloat\": 80,\n" +
//                "            \"ValueInt\": 0,\n" +
//                "            \"ValueString\": \"\",\n" +
//                "            \"ValueUint\": 0\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"DataValueSetterExpressionList\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_data_D\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_Function\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"FuncQueryMonthConsumption(contract_mobilecallback.ContractOwners.0)\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"DecisionResult\": null,\n" +
//                "        \"Description\": \"查询用户A当月消费额度，是否大于58元\",\n" +
//                "        \"DiscardCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_discond_D\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"true\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"MetaAttribute\": null,\n" +
//                "        \"NextTasks\": [\n" +
//                "          \"action_E\",\n" +
//                "          \"action_ F\"\n" +
//                "        ],\n" +
//                "        \"PreCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_precond_D\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"contract_mobilecallback.StartTime >=\\\"2017-01-01 12:00:00\\\"&&contract_mobilecallback.EndTime <=\\\"2017-12-31 23:59:59\\\"\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"SelectBranches\": null,\n" +
//                "        \"State\": \"TaskState_Dormant\",\n" +
//                "        \"Support\": 0,\n" +
//                "        \"SupportArguments\": null,\n" +
//                "        \"TaskExecuteIdx\": 0,\n" +
//                "        \"TaskId\": \"UUID110-1234-11114\",\n" +
//                "        \"TaskList\": null,\n" +
//                "        \"Text\": null\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"AgainstArguments\": null,\n" +
//                "        \"CandidateList\": null,\n" +
//                "        \"Caption\": \"移动运营商下月1号返移动用户A20元\",\n" +
//                "        \"Cname\": \"action_E\",\n" +
//                "        \"CompleteCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_comcond_E\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"expression_data_E.ExpressionResult.code ==200\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"Ctype\": \"Component_Task.Task_Action\",\n" +
//                "        \"DataList\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"B返话费给A日期\",\n" +
//                "            \"Category\": null,\n" +
//                "            \"Cname\": \"data_E\",\n" +
//                "            \"Ctype\": \"Component_Data.Data_Date\",\n" +
//                "            \"DataRangeFloat\": null,\n" +
//                "            \"DataRangeInt\": null,\n" +
//                "            \"DataRangeUint\": null,\n" +
//                "            \"DefaultValueFloat\": 0,\n" +
//                "            \"DefaultValueInt\": 0,\n" +
//                "            \"DefaultValueString\": \"\",\n" +
//                "            \"DefaultValueUint\": 0,\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"Format\": \"\",\n" +
//                "            \"HardConvType\": \"string\",\n" +
//                "            \"Mandatory\": false,\n" +
//                "            \"ModifyDate\": \"\",\n" +
//                "            \"Options\": null,\n" +
//                "            \"Parent\": null,\n" +
//                "            \"Unit\": \"\",\n" +
//                "            \"ValueFloat\": 0,\n" +
//                "            \"ValueInt\": 0,\n" +
//                "            \"ValueString\": \"2017-02-01 12:00:00\",\n" +
//                "            \"ValueUint\": 0\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"DataValueSetterExpressionList\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"B返话费给A20元\",\n" +
//                "            \"Cname\": \"expression_data_E\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_Function\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"FuncBackTelephoneFare(contract_mobilecallback.ContractOwners.1,contract_mobilecallback.ContractOwners.0,20)\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"DecisionResult\": null,\n" +
//                "        \"Description\": \"移动用户A当月消费58元以上：移动运营商B下月1号返还话费20元；连续返还12个月\",\n" +
//                "        \"DiscardCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_discond_E\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"true\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"MetaAttribute\": null,\n" +
//                "        \"NextTasks\": [\n" +
//                "          \"enquiry_D\"\n" +
//                "        ],\n" +
//                "        \"PreCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_precond_E\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"contract_mobilecallback.StartTime >=\\\"2017-01-01 12:00:00\\\"&&contract_mobilecallback.EndTime <=\\\"2017-12-31 23:59:59\\\"&&FuncGetNowDay()==1&&FuncGetNowDate()!= data_E.Value&& data_D.Value>=58\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"SelectBranches\": null,\n" +
//                "        \"State\": \"TaskState_Dormant\",\n" +
//                "        \"Support\": 0,\n" +
//                "        \"SupportArguments\": null,\n" +
//                "        \"TaskExecuteIdx\": 0,\n" +
//                "        \"TaskId\": \"UUID110-1234-11115\",\n" +
//                "        \"TaskList\": null,\n" +
//                "        \"Text\": null\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"AgainstArguments\": null,\n" +
//                "        \"CandidateList\": null,\n" +
//                "        \"Caption\": \"移动运行商不返话费\",\n" +
//                "        \"Cname\": \"action_F\",\n" +
//                "        \"CompleteCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_discond_F\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"expression_data_F.ExpressionResult.code==200\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"Ctype\": \"Component_Task.Task_Action\",\n" +
//                "        \"DataList\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"B返话费给A执行日期\",\n" +
//                "            \"Category\": null,\n" +
//                "            \"Cname\": \"data_F\",\n" +
//                "            \"Ctype\": \"Component_Data.Data_Date\",\n" +
//                "            \"DataRangeFloat\": null,\n" +
//                "            \"DataRangeInt\": null,\n" +
//                "            \"DataRangeUint\": null,\n" +
//                "            \"DefaultValueFloat\": 0,\n" +
//                "            \"DefaultValueInt\": 0,\n" +
//                "            \"DefaultValueString\": \"\",\n" +
//                "            \"DefaultValueUint\": 0,\n" +
//                "            \"Description\": \"移动运营商B返话费给用户A的操作日期\",\n" +
//                "            \"Format\": \"\",\n" +
//                "            \"HardConvType\": \"string\",\n" +
//                "            \"Mandatory\": false,\n" +
//                "            \"ModifyDate\": \"\",\n" +
//                "            \"Options\": null,\n" +
//                "            \"Parent\": null,\n" +
//                "            \"Unit\": \"\",\n" +
//                "            \"ValueFloat\": 0,\n" +
//                "            \"ValueInt\": 0,\n" +
//                "            \"ValueString\": \"2017-02-01 12:00:00\",\n" +
//                "            \"ValueUint\": 0\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"DataValueSetterExpressionList\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"不执行动作\",\n" +
//                "            \"Cname\": \"expression_data_F\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_Function\",\n" +
//                "            \"Description\": \"消费不足58元，不执行动作\",\n" +
//                "            \"ExpressionResult\": null,\n" +
//                "            \"ExpressionStr\": \"true\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"DecisionResult\": null,\n" +
//                "        \"Description\": \"移动用户A当月消费不足58元：移动运营商B下月1号不返还话费\",\n" +
//                "        \"DiscardCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_discond_F\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"true\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"MetaAttribute\": null,\n" +
//                "        \"NextTasks\": [\n" +
//                "          \"enquiry_D\"\n" +
//                "        ],\n" +
//                "        \"PreCondition\": [\n" +
//                "          {\n" +
//                "            \"Caption\": \"\",\n" +
//                "            \"Cname\": \"expression_precond_F\",\n" +
//                "            \"Ctype\": \"Component_Expression.Expression_LogicArgument\",\n" +
//                "            \"Description\": \"\",\n" +
//                "            \"ExpressionResult\": {\n" +
//                "              \"Code\": 200,\n" +
//                "              \"Data\": \"\",\n" +
//                "              \"Message\": \"Operatesuccess.\",\n" +
//                "              \"OutPut\": \"\"\n" +
//                "            },\n" +
//                "            \"ExpressionStr\": \"contract_mobilecallback.StartTime >=\\\"2017-01-01 12:00:00\\\"&&contract_mobilecallback.EndTime <=\\\"2017-12-31 23:59:59\\\"&&FuncGetNowDay()==1&&FuncGetNowDate()!=data_E.Value&&data_D.Value<58\",\n" +
//                "            \"LogicValue\": 1,\n" +
//                "            \"MetaAttribute\": null\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"SelectBranches\": null,\n" +
//                "        \"State\": \"TaskState_Dormant\",\n" +
//                "        \"Support\": 0,\n" +
//                "        \"SupportArguments\": null,\n" +
//                "        \"TaskExecuteIdx\": 0,\n" +
//                "        \"TaskId\": \"UUID110-1234-11116\",\n" +
//                "        \"TaskList\": null,\n" +
//                "        \"Text\": null\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"ContractId\": \"170613121259039057\",\n" +
//                "    \"ContractOwners\": [\n" +
//                "      \"3FyHdZVX4adfSSTg7rZDPMzqzM8k5fkpu43vbRLvEXLJ\"\n" +
//                "    ],\n" +
//                "    \"ContractSignatures\": [\n" +
//                "      {\n" +
//                "        \"OwnerPubkey\": \"3FyHdZVX4adfSSTg7rZDPMzqzM8k5fkpu43vbRLvEXLJ\",\n" +
//                "        \"SignTimestamp\": \"1497525603914\",\n" +
//                "        \"Signature\": \"5BxF9uBLxGZu4G8tYhzBbg5vuibgDYYmBahM9CaP8gm5PLwpy2DZzAaFmX9vypSiUMr3jiTatD1GCnafD6HZ5paS\"\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"ContractState\": \"Contract_Create\",\n" +
//                "    \"CreateTime\": \"1492619683000\",\n" +
//                "    \"Creator\": \"ABCDEFGHIJKLMNIPQRSTUVWXYZ\",\n" +
//                "    \"Ctype\": \"Component_Contract\",\n" +
//                "    \"Description\": \"移动用户A花费500元购买移动运营商B的提供的合约智能手机C后，要求用户每月消费58元以上通信费，移动运营商B便可按月返还话费（即：每月1号返还移动用户A20元话费），连续返还12个月\",\n" +
//                "    \"EndTime\": \"1527748230000\",\n" +
//                "    \"MetaAttribute\": {\n" +
//                "      \"Test1\": \"aaaaaa\",\n" +
//                "      \"Test2\": \"bbbbbb\"\n" +
//                "    },\n" +
//                "    \"NextTasks\": [\n" +
//                "      \"enquiry_A\"\n" +
//                "    ],\n" +
//                "    \"StartTime\": \"1496213113000\"\n" +
//                "  },\n" +
//                "  \"id\": \"6fea197a8c214650903be9f85f423cd09bd632e5b6c853f4b82c896ea2ad9d5f\"\n" +
//                "}";
////        TokenUser currentUser = SessionUtil.getCurrentUser(request);
////        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_AUDIT.equals(currentUser.getRole()) &&
////                !BaseConstant.USER_ROLE_CUSTOM.equals(currentUser.getRole())) {
////            responseData.setException(String.format("只有[%s,%s,%s]可以进行此操作！",
////                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER),
////                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_AUDIT),
////                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_CUSTOM)));
////            return responseData;
////        }
////        String contractOwner = currentUser.getPubkey();
//
//        LocalContract localContract = contractService.getFileContractByContractId(contractId);
//
//        String jsonContractFull = testJsonStr;
//        //        String jsonContractFull = contractHttpService.queryPublishContract(token, requestMap);
//        if (CommonUtils.isEmpty(jsonContractFull)) {
//            responseData.setOK("no match result");
//            return responseData;
//        }
//        String publishUser = localContract.getPublishUser();
//        CompanyUser companyUser = companyUserService.getCompanyUserByCompanyUserPubkey(publishUser);
//        ContractUser contractUser = contractUserService.getUserById(new Long(1));
//        PreSignContractDTO preSignContractDTO = new PreSignContractDTO();
//        preSignContractDTO.setPublishPubkey("8qi3Zy655uowWQUhXoRdQs1zyvVapr6KdpXsbp8YTFhM");
//        preSignContractDTO.setPublishPrikey("8pMZFfx6MXMjxii5dY9KkVFUNUqSA8WpGUu1pHXTTewe");
//        preSignContractDTO.setUsername("aaaaaaaasddasfas");
//        preSignContractDTO.setContract(jsonContractFull);
//        preSignContractDTO.setCurrentUserPubkey("3FyHdZVX4adfSSTg7rZDPMzqzM8k5fkpu43vbRLvEXLJ");
//        preSignContractDTO.setCurrentUserPrikey("5Pv7F7g9BvNDEMdb8HV5aLHpNTNkxVpNqnLTQ58Z5heC");
//        preSignContractDTO.setUserId(new Long(1));
//
//        Map<String,Object> resultMap = new HashMap<>();
//        resultMap.put("contractData",preSignContractDTO);
//        resultMap.put("contractId",CommonUtils.generateSerialNo());
//        responseData.setOK("preSign op success", resultMap);
//        return responseData;
//    }

}
