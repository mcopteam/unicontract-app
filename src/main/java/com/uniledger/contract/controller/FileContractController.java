package com.uniledger.contract.controller;

import com.uniledger.contract.common.PageInfo;
import com.uniledger.contract.common.ResponseData;
import com.uniledger.contract.common.TokenUser;
import com.uniledger.contract.constants.BaseConstant;
import com.uniledger.contract.constants.ContractConstant;
import com.uniledger.contract.model.LocalContract;
import com.uniledger.contract.service.ContractService;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wxcsdb88 on 2017/6/9 17:02.
 */
@Api(protocols = "http,https", description = "文件管理")
@Controller
@RequestMapping(value = "/fileContract", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
public class FileContractController {
    private static Logger log = LoggerFactory.getLogger(FileContractController.class);

    @Autowired
    private ContractService contractService;

    /**
     * 用户点击创建合约，首先创建一个空的包含基本信息的合约
     *
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @param token   token标记
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "合约文件创建", notes = "创建时分配编号,不包含合约内容,只有设计者可以创建合约")
    @ApiResponses({
            @ApiResponse(code = 200, message = "创建成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData createContract(HttpServletRequest request, Model model,
                                       @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                       @ApiParam(name = "name", value = "合约文件名", required = true) @RequestParam(name = "name", required = true) String name) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<LocalContract> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, name)) {
            responseData.setBadRequest("token or name is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole())&& !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
            return responseData;
        }
        LocalContract localContract = new LocalContract();
        localContract.setName(name);
        localContract.setContractId(CommonUtils.generateSerialNo());
        localContract.setStatus(ContractConstant.CONTRACT_FILE_STATUS_CREATING);
        localContract.setCreateUserId(currentUser.getId());
        localContract.setCreateUserName(currentUser.getUsername());
        localContract.setCreateTime(DateUtils.currentDateTime());
        localContract.setOwner(currentUser.getPubkey());
        boolean isOk = contractService.createFileContract(localContract);
        if (!isOk) {
            responseData.setException("createContract fail!");
            return responseData;
        }
        responseData.setOK("createContract success", localContract);
        return responseData;
    }


    /**
     * 文件管理列表[local]
     *
     * @param request    {@link HttpServletRequest}
     * @param model      {@link Model}
     * @param token      token标记
     * @param status     合约状态
     * @param contractId 合约文件编号
     * @param name       合约文件名称[非合约名称]
     * @param pageNum    当前页码
     * @param pageSize   页面记录数
     * @return {@link ResponseData< List <LocalContract>>}
     */
    @ApiOperation(value = "合约文件管理列表", notes = "设计者查看")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData fileContractList(HttpServletRequest request, Model model,
                                         @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                         @ApiParam(name = "status", required = true, value = "状态", allowableValues = "range[-1,4]") @RequestParam(name = "status", required = true) Short status,
                                         @ApiParam(name = "contractId", value = "编号", required = false) @RequestParam(name = "contractId", required = false) String contractId,
                                         @ApiParam(name = "name", value = "名称", required = false) @RequestParam(name = "name", required = false) String name,
                                         @ApiParam(name = "pageNum", value = "页码", required = false, defaultValue = "1") @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                         @ApiParam(name = "pageSize", value = "每页记录", required = false, defaultValue = "10") @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        //1. 根据用户pubkey，状态及查询条件name id 查询合约列表
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<PageInfo> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, status)) {
            responseData.setBadRequest("token or status is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
            return responseData;
        }
        Short[] statusQuery = null;
        if (status == -1) {
            //查询所有 对于设计者查询创建中及修改中的
            statusQuery = new Short[]{ContractConstant.CONTRACT_FILE_STATUS_CREATING, ContractConstant.CONTRACT_FILE_STATUS_MODIFYING};
        } else {
            if (!ContractConstant.CONTRACT_FILE_DISPLAY_STATUS_DESIGNER.contains(status)) {
                responseData.setBadRequest("status value is error!");
                return responseData;
            }
            statusQuery = new Short[]{status};
        }
//        List<LocalContract> localContractList = contractService.getFileContractList(contractId, name, statusQuery);
        PageInfo<LocalContract> pageInfo = contractService.getFileContractListPageInfo(pageNum, pageSize, contractId, name, statusQuery);

        if (pageInfo.getSize() == 0) {
            responseData.setOK("no match result");
            return responseData;
        }
        responseData.setData(pageInfo);
        responseData.setMsg("query fileContractList success");
        return responseData;
    }


    /**
     * 编辑合约文件, 处于创建 或修改状态
     *
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @param token   token标记
     * @param id      本地库已存储的合约数据对应的主键id
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "合约文件编辑", notes = "处于创建或修改状态才能编辑")
    @ApiResponses({
            @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/edit", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData editContract(HttpServletRequest request, Model model,
                                     @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                     @ApiParam(name = "id", required = true, value = "合约文件id") @RequestParam(name = "id", required = true) Long id) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<LocalContract> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, id)) {
            responseData.setBadRequest("token or id is blank!");
            return responseData;
        }

        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
            return responseData;
        }
        LocalContract localContract = contractService.getFileContract(id);
        if (localContract == null) {
            responseData.setException("contract is not exist!");
            return responseData;
        }
        if (CommonUtils.isEmpty(localContract.getContractContent())) {
            responseData.setException("contract content is blank!");
            return responseData;
        }
//        String xmlContract = localContract.getContractContent();
//        xmlContract = StringUtils.unescapeXml(xmlContract);
//        localContract.setContractContent(xmlContract);
        Short status = localContract.getStatus();
        if (ContractConstant.CONTRACT_FILE_STATUS_CREATING != status && ContractConstant.CONTRACT_FILE_STATUS_MODIFYING != status) {
            responseData.setException("只有处于创建或修改中的合约才可以进行修改!");
            return responseData;
        }
        responseData.setOK("get ContractOriginal success!", localContract);
        return responseData;
    }

    /**
     * 编辑合约文件, 处于创建 或修改状态
     *
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @param token   token标记
     * @param id      本地库已存储的合约数据对应的主键id
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "合约文件删除", notes = "处于创建或修改状态才能编辑")
    @ApiResponses({
            @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData deleteContract(HttpServletRequest request, Model model,
                                       @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                       @ApiParam(name = "id", required = true, value = "合约文件id") @RequestParam(name = "id", required = true) Long id) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, id)) {
            responseData.setBadRequest("token or id is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
            return responseData;
        }
        LocalContract localContract = contractService.getFileContract(id);
        if (localContract == null) {
            responseData.setException("contract is not exist!", false);
            return responseData;
        }
        Short status = localContract.getStatus();
        if (ContractConstant.CONTRACT_FILE_STATUS_CREATING != status && ContractConstant.CONTRACT_FILE_STATUS_MODIFYING != status) {
            responseData.setException("只有处于创建或修改中的合约才可以删除!");
            return responseData;
        }
        boolean deleteOk = contractService.delete(id);
        if (!deleteOk) {
            responseData.setException("contract delete fail", false);
            return responseData;
        }

        responseData.setOK("contract delete success!", true);
        return responseData;
    }

    /**
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @param token   token标记
     * @param id      本地库已存储的合约数据对应的主键id
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "合约文件送审", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/sendAudit", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData sendContractAudit(HttpServletRequest request, Model model,
                                          @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                          @ApiParam(name = "id", required = true, value = "合约文件id") @RequestParam(name = "id", required = true) Long id) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, id)) {
            responseData.setBadRequest("token or id is blank!");
            return responseData;
        }

        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
            return responseData;
        }

        LocalContract localContract = contractService.getFileContract(id);
        if (localContract == null) {
            responseData.setException("contract is not exist!");
            return responseData;
        }
        if (CommonUtils.isEmpty(localContract.getContractContent())) {
            responseData.setException("contract content is blank!");
            return responseData;
        }
        if (ContractConstant.CONTRACT_FILE_STATUS_CREATING != localContract.getStatus() && ContractConstant.CONTRACT_FILE_STATUS_MODIFYING != localContract.getStatus()) {
            responseData.setBadRequest("只有处于创建或修改中的合约才可以进行此操作!");
            return responseData;
        }

        localContract.setUpdateTime(DateUtils.currentDateTime());
        localContract.setUpdateUserId(currentUser.getId());
        localContract.setUpdateUserName(currentUser.getUsername());
        localContract.setStatus(ContractConstant.CONTRACT_FILE_STATUS_AUDITING);
        boolean isOk = contractService.updateLocalContract(localContract);
        if (!isOk) {
            responseData.setException("sendAudit fail!");
            return responseData;
        }
        responseData.setOK("sendAudit success!");
        return responseData;
    }

    /**
     * 合约更新保存
     *
     * @param request     {@link HttpServletRequest}
     * @param model       {@link Model}
     * @param token       token标记
     * @param id          合约文件id
     * @param xmlContract xml格式合约
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "合约文件更新保存", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData updateContract(HttpServletRequest request, Model model,
                                       @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                       @ApiParam(name = "id", required = true, value = "合约文件id") @RequestParam(name = "id", required = true) Long id,
                                       @ApiParam(name = "xmlContract", required = true, value = "xml格式合约") @RequestParam(name = "xmlContract", required = true) String xmlContract) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, id, xmlContract)) {
            responseData.setBadRequest("token or owner or contractId or xmlContract is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
            return responseData;
        }
        LocalContract localContract = contractService.getFileContract(id);
        if (localContract == null) {
            responseData.setException("contract is not exist!");
            return responseData;
        }
        Short status = localContract.getStatus();
        if (ContractConstant.CONTRACT_FILE_STATUS_CREATING != status && ContractConstant.CONTRACT_FILE_STATUS_MODIFYING != status) {
            responseData.setException("只有处于创建或修改中的合约才可以进行修改!", false);
            return responseData;
        }
        localContract.setUpdateTime(DateUtils.currentDateTime());
        localContract.setUpdateUserId(currentUser.getId());
        localContract.setUpdateUserName(currentUser.getUsername());
        localContract.setContractContent(xmlContract);
        localContract.setAutoContractContent(xmlContract);
        String jsonContract = ProtoConvertUtils.parseXMLToJson(xmlContract, localContract.getContractId());
        if (!CommonUtils.isEmpty(jsonContract)) {
            localContract.setJsonContract(jsonContract);
        }
        boolean isOk = contractService.updateLocalContract(localContract);
        if (!isOk) {
            responseData.setException("update fail!", false);
            return responseData;
        }
        responseData.setOK("update success", true);
        return responseData;
    }

    /**
     * 自动定时保存合约
     *
     * @param request     {@link HttpServletRequest}
     * @param model       {@link Model}
     * @param token       token标记
     * @param id          本地库已存储的合约数据对应的主键id
     * @param xmlContract mxGraph xml 格式合约
     * @return {@link ResponseData}
     */
    @ApiOperation(value = "合约文件自动保存", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/autoSave", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData autoSaveContract(HttpServletRequest request, Model model,
                                         @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                         @ApiParam(name = "id", required = true, value = "合约文件id") @RequestParam(name = "id", required = true) Long id,
                                         @ApiParam(name = "xmlContract", required = true, value = "xml格式合约") @RequestParam(name = "xmlContract", required = true) String xmlContract) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, id, xmlContract)) {
            responseData.setBadRequest("token, id or xmlContract is blank!");
            return responseData;
        }

        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
            return responseData;
        }
        LocalContract localContract = contractService.getFileContract(id);
        if (localContract == null) {
            responseData.setException("localContract is not exist!");
            return responseData;
        }
        Short status = localContract.getStatus();
        if (ContractConstant.CONTRACT_FILE_STATUS_CREATING != status && ContractConstant.CONTRACT_FILE_STATUS_MODIFYING != status) {
            responseData.setException("只有处于创建或修改中的合约才可以进行修改!", false);
            return responseData;
        }
        localContract.setUpdateTime(DateUtils.currentDateTime());
        localContract.setUpdateUserId(currentUser.getId());
        localContract.setUpdateUserName(currentUser.getUsername());
        localContract.setAutoContractContent(xmlContract);
        boolean isOk = contractService.updateLocalContract(localContract);
        if (!isOk) {
            responseData.setException("autoSaveContract fail!");
            return responseData;
        }
        responseData.setOK("autoSaveContract success!");
        return responseData;
    }

    @ApiOperation(value = "合约文件预览内容", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/queryContent", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData queryProductContent(HttpServletRequest request, Model model,
                                            @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                            @ApiParam(name = "id", required = true, value = "合约文件id") @RequestParam(name = "id", required = true) Long id,
                                            @ApiParam(name = "xmlContract", required = true, value = "xml格式合约") @RequestParam(name = "xmlContract", required = true) String xmlContract) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<String> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, xmlContract)) {
            responseData.setBadRequest("token or xmlContract is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！",
                    BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
            return responseData;
        }
        LocalContract localContract = contractService.getFileContract(id);
        if (localContract == null) {
            responseData.setException("localContract is not exist!");
            return responseData;
        }
//        ContractContent contractContent = ProtoConvertUtils.parseXMLToContractContent(xmlContract, false, localContract.getContractId());
//        String jsonContract = ProtoConvertUtils.parseXMLToJson(xmlContract, false, false, null);
        String jsonContract = null;
        try {
            jsonContract = XMLContractParseUtil.parseXMLToJsonFromString(xmlContract);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.setException("parseXMLToJsonFromString error!");
            return responseData;
        }

        if (CommonUtils.isEmpty(jsonContract)) {
            responseData.setException("contract content is error!");
            return responseData;
        }
        responseData.setOK("convert json success!", jsonContract);
        return responseData;
    }

    @ApiOperation(value = "合约存在判断", notes = "是否存在合约，contractId为")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/exist", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData existContract(HttpServletRequest request, Model model,
                                      @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                      @ApiParam(name = "contractId", required = true, value = "合约id") @RequestParam(name = "contractId", required = true) String contractId) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, contractId)) {
            responseData.setBadRequest("token or contractId is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
            return responseData;
        }
        List<LocalContract> localContractList = contractService.getFileContractList(contractId, null, new Short[]{});
        if (CommonUtils.isEmpty(localContractList)) {
            responseData.setOK("localContract is not exist!", false);
            return responseData;
        } else {
            responseData.setOK("localContract is exist!", true);
            return responseData;
        }
    }

    /**
     * 自动定时保存合约
     * @param request {@link HttpServletRequest}
     * @param model {@link Model}
     * @param token token标记
     * @param contractId contractId
     * @param xmlContract mxGraph xml 格式合约
     * @return  {@link ResponseData}
     */

    /**
     * 导入合约
     *
     * @param request           {@link HttpServletRequest}
     * @param model             {@link Model}
     * @param token             token标记
     * @param contractId        contractId
     * @param xmlContract       mxGraph xml 格式合约
     * @param createOrOverWrite true,新建; false,如果存在覆盖！
     * @return
     */
    @ApiOperation(value = "合约文件导入", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/import", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public ResponseData importContract(HttpServletRequest request, Model model,
                                       @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                       @ApiParam(name = "contractId", required = false, value = "合约id") @RequestParam(name = "contractId", required = false) String contractId,
                                       @ApiParam(name = "xmlContract", required = true, value = "xml格式合约") @RequestParam(name = "xmlContract", required = true) String xmlContract,
                                       @ApiParam(name = "name", required = true, value = "文件名") @RequestParam(name = "name", required = true) String name,
                                       @ApiParam(name = "createFlag", required = false, value = "xml格式合约") @RequestParam(name = "createFlag", required = false, defaultValue = "true") boolean createOrOverWrite) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, xmlContract)) {
            responseData.setBadRequest("token or xmlContract is blank!");
            return responseData;
        }

        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        if (!BaseConstant.USER_ROLE_DESIGNER.equals(currentUser.getRole()) && !BaseConstant.USER_ROLE_TRANSFER.equals(currentUser.getRole())) {
            responseData.setException(String.format("只有[%s]可以进行此操作！", BaseConstant.USER_ROLE_MAP.get(BaseConstant.USER_ROLE_DESIGNER)));
            return responseData;
        }

        List<LocalContract> localContractList = contractService.getFileContractList(contractId, null, new Short[]{});
        boolean createContractFlag = false;
        if (createOrOverWrite) {
            createContractFlag = true;
        } else {
            if (CommonUtils.isEmpty(localContractList)) {
                createContractFlag = true;
            }
        }
        String contractName = "";
        String reg = "label=\"开始\"[^>]*Caption=\"([^>\"]*)(?=\")\\S";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(xmlContract);
        if (matcher.find()) {
            contractName = matcher.group(1);
        }
        if (createContractFlag) {
            LocalContract localContract = new LocalContract();
            localContract.setName(contractName);
            localContract.setContractId(CommonUtils.generateSerialNo());
            localContract.setStatus(ContractConstant.CONTRACT_FILE_STATUS_CREATING);
            localContract.setCreateUserId(currentUser.getId());
            localContract.setCreateUserName(currentUser.getUsername());
            localContract.setCreateTime(DateUtils.currentDateTime());
            localContract.setOwner(currentUser.getPubkey());
            localContract.setAutoContractContent(xmlContract);
            localContract.setContractContent(xmlContract);
            boolean isOk = contractService.createFileContract(localContract);
            if (!isOk) {
                responseData.setOK("createContract fail!", false);
                return responseData;
            }
            responseData.setOK("import success", true);
            return responseData;
        } else {
            LocalContract localContract = localContractList.get(0);
            localContract.setAutoContractContent(xmlContract);
            localContract.setContractContent(xmlContract);
            localContract.setStatus(ContractConstant.CONTRACT_FILE_STATUS_MODIFYING);
            localContract.setUpdateTime(DateUtils.currentDateTime());
            localContract.setUpdateUserId(currentUser.getId());
            localContract.setUpdateUserName(currentUser.getUsername());
            boolean isOk = contractService.updateLocalContract(localContract);
            if (!isOk) {
                responseData.setOK("import fail!", false);
                return responseData;
            }
            responseData.setOK("import success", true);
            return responseData;
        }
    }

}
