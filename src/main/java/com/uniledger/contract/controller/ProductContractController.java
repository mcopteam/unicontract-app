package com.uniledger.contract.controller;

import com.uniledger.contract.common.PageInfo;
import com.uniledger.contract.common.ResponseData;
import com.uniledger.contract.common.TokenUser;
import com.uniledger.contract.constants.BaseConstant;
import com.uniledger.contract.constants.ContractConstant;
import com.uniledger.contract.dto.PreSignContractDTO;
import com.uniledger.contract.model.CompanyUser;
import com.uniledger.contract.model.ContractAuditLog;
import com.uniledger.contract.model.ContractUser;
import com.uniledger.contract.model.LocalContract;
import com.uniledger.contract.service.*;
import com.uniledger.contract.utils.*;
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
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxcsdb88 on 2017/5/10 15:28.
 */
@Api(protocols = "http,https", description = "合约产品")
@Controller
@RequestMapping(value = "/productContract", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ProductContractController {
    private static Logger log = LoggerFactory.getLogger(ProductContractController.class);

    @Autowired
    private ContractService contractService;
    @Autowired
    private ContractAuditLogService contractAuditLogService;

    @Autowired
    private ContractHttpService contractHttpService;

    @Autowired
    private CompanyUserService companyUserService;

    @Autowired
    private ContractUserService contractUserService;

    /**
     * 合约产品列表[local]
     * @param request   {@link HttpServletRequest}
     * @param model     {@link Model}
     * @param token     token标记
     * @param status    合约状态
     * @param contractId        合约文件编号或contractId
     * @param name      合约文件名称[非合约名称]
     * @param pageNum    当前页码
     * @param pageSize   页面记录数
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "合约产品列表", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData productContractList(HttpServletRequest request, Model model,
                                          @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                          @ApiParam(name = "status", required = true, value = "状态", allowableValues = "range[-1,4]")@RequestParam(name = "status", required = true) Short status,
                                          @ApiParam(name = "contractId", value = "编号", required = false)@RequestParam(name = "contractId", required = false) String contractId,
                                          @ApiParam(name = "name", value = "合约文件名", required = false)@RequestParam(name = "name", required = false) String name,
                                            @ApiParam(name = "pageNum", value = "页码", required = false, defaultValue = "1") @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                            @ApiParam(name = "pageSize", value = "每页记录", required = false, defaultValue = "10") @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize){
        //1. 根据用户pubkey，状态及查询条件name id 查询合约列表
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<PageInfo> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, status)){
            responseData.setBadRequest("token or status is blank!");
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
        Short[] statusQuery = null;
        if(status == -1){
            //审核中，等待发布，已发布的合约
            statusQuery = new Short[]{ContractConstant.CONTRACT_FILE_STATUS_AUDITING, ContractConstant.CONTRACT_FILE_STATUS_WAIT_PUBLISH, ContractConstant.CONTRACT_FILE_STATUS_PUBLISH};
        }else{
            if (!ContractConstant.CONTRACT_PRODUCT_ALL.contains(status)){
                responseData.setBadRequest("status value is error!");
                return responseData;
            }
            statusQuery = new Short[]{status};
        }
        PageInfo<LocalContract> pageInfo = contractService.getFileContractListPageInfo(pageNum, pageSize, contractId, name, statusQuery);
        if (pageInfo.getSize() == 0){
            responseData.setOK("no match result");
            return responseData;
        }
        responseData.setData(pageInfo);
        responseData.setOK("query auditContractList success");
        return responseData;
    }

    /**
     * 合约产品[local]
     * @param request   {@link HttpServletRequest}
     * @param model     {@link Model}
     * @param token     token标记
     * @param id        合约文件id
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "合约产品", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData queryProductContract(HttpServletRequest request, Model model,
                                            @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                            @ApiParam(name = "id", value = "id", required = true)@RequestParam(name = "id", required = true) Long id){
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<LocalContract> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, id)){
            responseData.setBadRequest("token or status is blank!");
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
        LocalContract localContractList = contractService.getFileContract(id);
        if (CommonUtils.isEmpty(localContractList)){
            responseData.setOK("no match result");
            return responseData;
        }
        responseData.setData(localContractList);
        responseData.setOK("query auditContractList success");
        return responseData;
    }

    /**
     * 审核操作[同意，修改] 操作
     * @param request {@link HttpServletRequest}
     * @param model  {@link Model}
     * @param token token标记
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "合约文件审核修改", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/operate", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData auditAndModifyContract(HttpServletRequest request, Model model,
                                      @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                      @ApiParam(name = "id", required = true, value = "合约文件id")@RequestParam(name = "id", required = true) Long id,
                                      @ApiParam(name = "auditOp", required = true, value = "操作码", allowableValues = "range[2,4]")@RequestParam(name = "auditOp", required = true) Short auditOp,
                                      @ApiParam(name = "suggestion", required = false, value = "修改建议")@RequestParam(name = "suggestion", required = false) String suggestion) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, id, auditOp)){
            responseData.setBadRequest("token, id owner or auditOp is blank!");
            return responseData;
        }
        if(!ContractConstant.CONTRACT_AUDIT_OP_FAIL.equals(auditOp) && !ContractConstant.CONTRACT_AUDIT_OP_PASS.equals(auditOp)){
            responseData.setBadRequest("auditOp is error!");
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
        LocalContract localContract = contractService.getFileContract(id);
        if (localContract == null ){
            responseData.setException("contract is not exist!");
            return responseData;
        }
        if (CommonUtils.isEmpty(localContract.getContractContent())) {
            responseData.setException("contract content is blank!");
            return responseData;
        }
        localContract.setUpdateTime(DateUtils.currentDateTime());
        localContract.setUpdateUserId(currentUser.getId());
        localContract.setUpdateUserName(currentUser.getUsername());

        if(ContractConstant.CONTRACT_AUDIT_OP_FAIL.equals(auditOp)){
            localContract.setStatus(ContractConstant.CONTRACT_FILE_STATUS_MODIFYING);
            if(CommonUtils.isEmpty(suggestion)){
                responseData.setBadRequest("审核不通过需填写修改意见!");
                return responseData;
            }
        }
        localContract.setStatus(auditOp);
        if(!CommonUtils.isEmpty(suggestion)){
            localContract.setSuggestion(suggestion);
        }
        boolean isOk = contractService.updateLocalContract(localContract);
        if(!isOk){
            responseData.setException("auditContract fail!", false);
            return responseData;
        }
         /*------------------- need deal -------------*/
        ContractAuditLog contractAuditLog = new ContractAuditLog();
        contractAuditLog.setCreateTime(DateUtils.currentDateTime());
        contractAuditLog.setCreateUserName(currentUser.getUsername());
        contractAuditLog.setCreateUserId(currentUser.getId());
        contractAuditLog.setContractPK(localContract.getId());
        contractAuditLog.setStatus(auditOp);
        String auditDesc = null;
        if(ContractConstant.CONTRACT_AUDIT_OP_FAIL.equals(auditOp)){
            auditDesc = "[不同意审核]";
        }else if(ContractConstant.CONTRACT_AUDIT_OP_PASS.equals(auditOp)){
            auditDesc = "[通过审核]";
        }
        contractAuditLog.setDescription(auditDesc);
        contractAuditLogService.insert(contractAuditLog);
        responseData.setOK("auditContract success!", true);
        return responseData;
    }

    /**
     * 合约产品操作[发布]
     * @param request {@link HttpServletRequest}
     * @param model  {@link Model}
     * @param token token标记
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "合约产品发布", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/publish", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData publishContract(HttpServletRequest request, Model model,
                                        @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                        @ApiParam(name = "id", required = true, value = "合约文件id")@RequestParam(name = "id", required = true) Long id,
                                        @ApiParam(name = "auditOp", required = true, value = "操作码")@RequestParam(name = "auditOp", required = true) Short auditOp,
                                        @ApiParam(name = "contract", required = true, value = "json格式合约")@RequestParam(name = "contract", required = true) String contract,
                                        @ApiParam(name = "publishOwner", required = true, value = "合约方")@RequestParam(name = "publishOwner", required = true) String publishOwner
                                        ) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, id, auditOp, contract, publishOwner)){
            responseData.setBadRequest("token, id owner, auditOp, contract or publishOwner is blank!");
            return responseData;
        }
        if(!ContractConstant.CONTRACT_AUDIT_OP_PUBLISH.equals(auditOp)){
            responseData.setBadRequest("auditOp is error!");
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

        LocalContract localContract = contractService.getFileContract(id);
        if (localContract == null ){
            responseData.setException("contract is not exist!");
            return responseData;
        }
        if (CommonUtils.isEmpty(localContract.getContractContent())) {
            responseData.setException("contract content is blank!");
            return responseData;
        }
        localContract.setPublishUser(publishOwner);
        localContract.setUpdateTime(DateUtils.currentDateTime());
        localContract.setUpdateUserId(currentUser.getId());
        localContract.setUpdateUserName(currentUser.getUsername());
        localContract.setStatus(ContractConstant.CONTRACT_AUDIT_OP_PUBLISH);
        localContract.setJsonContract(contract);
        String xmlContract = localContract.getContractContent();
        //todo 元合约验证，是否验证附带owner的合约
        if(CommonUtils.isEmpty(xmlContract)){
            responseData.setException("合约内容不能为空!");
            return responseData;
        }
        byte[] protoContract = ProtoConvertUtils.jsonContractToProtoByte(contract);
        boolean publishOk = contractHttpService.createContract(token, protoContract);
        if(!publishOk){
            responseData.setException("合约发布失败!");
            return responseData;
        }

        boolean isOk = contractService.updateLocalContract(localContract);
        if(!isOk){
            responseData.setException("publish success, but update status fail!", false);
            return responseData;
        }
        /*------------------- need deal -------------*/
        ContractAuditLog contractAuditLog = new ContractAuditLog();
        contractAuditLog.setCreateTime(DateUtils.currentDateTime());
        contractAuditLog.setCreateUserName(currentUser.getUsername());
        contractAuditLog.setCreateUserId(currentUser.getId());
        contractAuditLog.setContractPK(localContract.getId());
        contractAuditLog.setStatus(auditOp);
        String auditDesc = "[发布产品]";
        contractAuditLog.setDescription(auditDesc);
        contractAuditLogService.insert(contractAuditLog);

        responseData.setOK("publish success!", true);
        return responseData;
    }

    @ApiOperation(value = "根据合约图查询合约产品内容", notes = "xml图合约转换为json")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/queryOriginContract", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData queryOriginContract(HttpServletRequest request, Model model,
                                            @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                            @ApiParam(name = "contractId", required = true, value = "contractId")@RequestParam(name = "contractId", required = true) String contractId) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<String> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)){
            responseData.setBadRequest("token or owner is blank!");
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
        if (localContract == null ){
            responseData.setException("localContract is not exist!");
            log.warn("remote localContract is blank!");
            return responseData;
        }
        String xmlContract = localContract.getContractContent();
        System.err.println(xmlContract);
        if (CommonUtils.isEmpty(xmlContract)) {
            responseData.setException("contract content is blank!");
            log.warn("local localContract is blank!");
            return responseData;
        }

        String jsonContract = ProtoConvertUtils.parseXMLToJson(xmlContract, localContract.getContractId());
        if(CommonUtils.isEmpty(jsonContract)){
            responseData.setException("contract content is blank!");
            return responseData;
        }
        responseData.setOK("queryContent success", jsonContract);
        return responseData;
    }

    @ApiOperation(value = "合约产品内容", notes = "xml转换提取数据")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/queryContent", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData queryProductContent(HttpServletRequest request, Model model,
                                                @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                                @ApiParam(name = "contractId", required = true, value = "contractId")@RequestParam(name = "contractId", required = true) String contractId) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<String> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)){
            responseData.setBadRequest("token or owner is blank!");
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
        if (localContract == null ){
            responseData.setException("localContract is not exist!");
            log.warn("remote localContract is blank!");
            return responseData;
        }
        String xmlContract = localContract.getContractContent();
        if (CommonUtils.isEmpty(xmlContract)) {
            responseData.setException("contract content is blank!");
            log.warn("local localContract is blank!");
            return responseData;
        }
//        ContractContent contractContent = ProtoConvertUtils.parseXMLToContractContent(xmlContract, false, localContract.getContractId());
//        if (contractContent == null) {
//            responseData.setException("contract content is error!");
//            return responseData;
//        }

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("contractId", contractId);
        requestMap.put("owner", currentUser.getPubkey());
        String jsonContract = contractHttpService.queryContractContent(token, requestMap);
        if(CommonUtils.isEmpty(jsonContract)){
            jsonContract = localContract.getJsonContract();
        }
        if(CommonUtils.isEmpty(jsonContract)){
            responseData.setException("contract content is blank!");
            return responseData;
        }
        responseData.setOK("queryContent success", jsonContract);
        return responseData;
    }

    @ApiOperation(value = "合约产品审核记录", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/queryAuditLog", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData queryAuditLog(HttpServletRequest request, Model model,
                                            @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                            @ApiParam(name = "id", required = true, value = "合约id")@RequestParam(name = "id", required = true) Long id) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<List<ContractAuditLog>> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)){
            responseData.setBadRequest("token or owner is blank!");
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
        List<ContractAuditLog> contractAuditLogList = contractAuditLogService.getContractAuditLogs(null, id,null,true);
        if (CommonUtils.isEmpty(contractAuditLogList) ){
            responseData.setException("审核记录不存在!");
            return responseData;
        }
        responseData.setOK("审核记录查询成功!",contractAuditLogList);
        return responseData;
    }

    /**
     * 合约产品获取[from uni contract and localContract]
     *
     * @param request    request {@link HttpServletRequest}
     * @param model      {@link Model}
     * @param token      token标记
     * @param contractId 合约id
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "合约产品获取", notes = "签约用")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/preSign", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData preSign(HttpServletRequest request, Model model,
                                @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                @ApiParam(name = "contractId", required = true, value = "合约id") @RequestParam(name = "contractId", required = true) String contractId) {
        //1. 根据用户pubkey，状态及查询条件name id 查询合约列表
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<PreSignContractDTO> responseData = new ResponseData<>();
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
        String contractOwner = currentUser.getPubkey();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("status", "Contract_Create");
        requestMap.put("contractId", contractId);
        requestMap.put("owner", currentUser.getPubkey());

        LocalContract localContract = contractService.getFileContractByContractId(contractId);
        String jsonContractFull = localContract.getJsonContract();
        //        String jsonContractFull = contractHttpService.queryPublishContract(token, requestMap);
        if (CommonUtils.isEmpty(jsonContractFull)) {
            responseData.setOK("no match result");
            return responseData;
        }
        String publishUser = localContract.getPublishUser();
        CompanyUser companyUser = companyUserService.getCompanyUserByCompanyUserPubkey(publishUser);
        ContractUser contractUser = contractUserService.getUserById(currentUser.getId());
        PreSignContractDTO preSignContractDTO = new PreSignContractDTO();
        preSignContractDTO.setPublishPubkey(publishUser);
        preSignContractDTO.setPublishPrikey(companyUser.getPrikey());
        preSignContractDTO.setUsername(currentUser.getUsername());
        preSignContractDTO.setContract(jsonContractFull);
        preSignContractDTO.setCurrentUserPubkey(currentUser.getPubkey());
        preSignContractDTO.setCurrentUserPrikey(contractUser.getPrikey());
        preSignContractDTO.setUserId(currentUser.getId());
        responseData.setOK("preSign op success", preSignContractDTO);
        return responseData;
    }


    /**
     * 合约
     * 1. 取自本地数据库的合约xml，转为json格式至web
     * @param request   {@link HttpServletRequest}
     * @param model     {@link Model}
     * @param token     token标记
     * @param id    合约文件id
     * @return  {@link ResponseData}
     */
    @ApiOperation(value = "签约", notes = "签约生成一条新记录到执行记录列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/signContract", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData signContract(HttpServletRequest request, Model model,
                                     @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                     @ApiParam(name = "id", required = true, value = "合约文件id")@RequestParam(name = "id", required = true) Long id,
                                     @ApiParam(name = "contract", required = true, value = "json格式合约")@RequestParam(name = "contract", required = true) String contract) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, id)){
            responseData.setBadRequest("token or id is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if(CommonUtils.isEmpty(currentUser.getPubkey())){
            responseData.setException("请先激活密钥或者申请后再进行此操作!");
            return responseData;
        }
        if(CommonUtils.isEmpty(contract)){
            responseData.setException("合约内容错误!");
            return responseData;
        }
        byte[] protoContract =ProtoConvertUtils.jsonContractToProtoByte(contract);
        boolean signContractOk = contractHttpService.createContract(token, protoContract);
        if(!signContractOk){
            responseData.setException("签约失败!", false);
            return responseData;
        }
        responseData.setOK("签约成功", true);
        return responseData;
    }

    /**
     * 合约测试操作
     * @param request   {@link HttpServletRequest}
     * @param model     {@link Model}
     * @param token     token标记
     * @param id    合约id
     * @return      {@link ResponseData}
     */
    @ApiOperation(value = "合约测试操作", notes = "待开发！")
    @ApiResponses({
            @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/test", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData testContract(HttpServletRequest request, Model model,
                                     @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")@RequestParam(name = "token", required = true) String token,
                                     @ApiParam(name = "id", required = true, value = "合约文件id")@RequestParam(name = "id", required = true) Long id) {
        log.info("testContract developing...");
       log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, id)){
            responseData.setBadRequest("token or owner or contractId is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        //todo test 流程
        responseData.setOK("signature success");
        responseData.setData(true);
        return responseData;
    }


    @ApiOperation(value = "合约创建测试-from-文件获取", notes = "测试接口！")
    @ApiResponses({
            @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/fromFileCreate", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData fromFileCreateContract(HttpServletRequest request, Model model,
                                           @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever")
                                           @RequestParam(name = "token", required = true) String token) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)){
            responseData.setBadRequest("token or owner or contractId is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);

        try {
            File directory = new File(".");
            String path = "C:\\workspace\\test2017\\Demo.xml";
            String jsonContract = XMLContractParseUtil.parseXMLToJsonFromFile(path, true);

            System.out.println(jsonContract);
            boolean result =  contractHttpService.createContract(token, jsonContract,false);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setException(e.getMessage());
            responseData.setCode(BaseConstant.HTTP_STATUS_CODE_BadRequest);
            return responseData;
        }
        //todo test 流程
        responseData.setOK("signature success", true);
        return responseData;
    }
}

