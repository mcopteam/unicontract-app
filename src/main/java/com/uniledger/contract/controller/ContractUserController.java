package com.uniledger.contract.controller;

import com.uniledger.contract.common.ResponseData;
import com.uniledger.contract.common.TokenUser;
import com.uniledger.contract.constants.BaseConstant;
import com.uniledger.contract.model.ContractUser;
import com.uniledger.contract.model.ContractUserKeyPair;
import com.uniledger.contract.service.ContractUserKeyPairService;
import com.uniledger.contract.service.ContractUserService;
import com.uniledger.contract.utils.CommonUtils;
import com.uniledger.contract.utils.DateUtils;
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
import java.util.List;

/**
 * Created by wxcsdb88 on 2017/5/15 20:52.
 */
@Api(protocols = "http,https", value = "/contractUser", description = "合约用户相关接口")
@Controller
@RequestMapping("/contractUser")
public class ContractUserController {
    private static Logger log = LoggerFactory.getLogger(ContractUserController.class);

    @Autowired
    private ContractUserService contractUserService;

    @Autowired
    private ContractUserKeyPairService contractUserKeyPairService;

    //todo 添加produces = "application/json;charset=utf-8" ,让swagger显示格式化输出
    @ApiOperation(value = "用户公钥列表", notes = "根据状态等获取")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取公钥列表成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/keyList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseData<List<ContractUserKeyPair>> keyList(HttpServletRequest request, Model model,
                                                           @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                                           @ApiParam(name = "pubkey", value = "公钥", required = false) @RequestParam(name = "pubkey", required = false) String pubkey,
                                                           @ApiParam(name = "status", value = "公钥状态", required = false) @RequestParam(name = "status", required = false) Short status) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<List<ContractUserKeyPair>> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)) {
            responseData.setBadRequest("token is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        List<ContractUserKeyPair> contractUserKeyPairList = contractUserKeyPairService.getContractKeyPairListByUserIdAndStatus(currentUser.getId(), status);
        if (contractUserKeyPairList == null || contractUserKeyPairList.size() == 0) {
            responseData.setException("没有密钥存在!");
            return responseData;
        }

        responseData.setOK("keyList success");
        responseData.setData(contractUserKeyPairList);
        return responseData;
    }

    @ApiOperation(value = "密钥申请", notes = "最多能申请的个数为：8")
    @ApiResponses({
            @ApiResponse(code = 200, message = "密钥申请成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/applyKey", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseData applyKey(HttpServletRequest request, Model model,
                                 @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                 @ApiParam(name = "pubkey", value = "公钥", required = true) @RequestParam(name = "pubkey", required = true) String pubkey,
                                 @ApiParam(name = "prikey", value = "私钥[测试用]", required = false) @RequestParam(name = "prikey", required = false) String prikey) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, pubkey)) {
            responseData.setBadRequest("token or pubkey is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        //todo 如果已经存在合法的公私钥，绑定操作会使处于激活状态的公私钥废弃
        // todo contract_user 表中存在的是当前有效的公私钥
        // todo 申请公私钥，默认不激活状态，如果用户当前已经有正常可用的公私钥
        List<ContractUserKeyPair> contractUserKeyPairList = contractUserKeyPairService.getContractKeyPairListByUserIdAndStatus(currentUser.getId(), null);

        if (contractUserKeyPairList != null && contractUserKeyPairList.size() >= BaseConstant.CONTRACT_USER_KEYPAIR_COUNT_MAX) {
            responseData.setException("您申请的秘钥数量已达上限: " + BaseConstant.CONTRACT_USER_KEYPAIR_COUNT_MAX);
            return responseData;
        }
        boolean existActiveKey = false;
        if (contractUserKeyPairList != null && contractUserKeyPairList.size() >= 1) {
            //TODO 判断是否有激活状态的秘钥
            boolean existKey = false;
            for (int i = 0; i < contractUserKeyPairList.size(); i++) {

                if (contractUserKeyPairList.get(i).getStatus() != null && BaseConstant.CONTRACT_USER_KEYPAIR_STATUS_ACTIVE == contractUserKeyPairList.get(i).getStatus()) {
                    existActiveKey = true;
                }
                if (contractUserKeyPairList.get(i).getPubkey().equals(pubkey)) {
                    existKey = true;
                    break;
                }
            }

            if (existKey) {
                responseData.setException("pubkey已存在，请重新操作!");
                return responseData;
            }
        }

        ContractUserKeyPair contractUserKeyPair = new ContractUserKeyPair();
        contractUserKeyPair.setUserId(currentUser.getId());
        contractUserKeyPair.setCreateUserId(currentUser.getId());
        contractUserKeyPair.setCreateTime(DateUtils.currentDateTime());
        contractUserKeyPair.setCreateUserName(currentUser.getUsername());
        contractUserKeyPair.setPubkey(pubkey);
        contractUserKeyPair.setPrikey(prikey); //todo shouldn`t store this key!!!
        boolean insertKeyPairOk = false;
        if (existActiveKey) {
            contractUserKeyPair.setStatus(BaseConstant.CONTRACT_USER_KEYPAIR_STATUS_INACTIVE);
        } else {
            contractUserKeyPair.setStatus(BaseConstant.CONTRACT_USER_KEYPAIR_STATUS_ACTIVE);
        }
        insertKeyPairOk = contractUserKeyPairService.insert(contractUserKeyPair);
        if (!insertKeyPairOk) {
            responseData.setException("applyKey store error");
            return responseData;
        }
        boolean activeUserKeyPairOk = false;
        if (!existActiveKey) {
            ContractUser contractUser = new ContractUser();
            contractUser.setId(currentUser.getId());
            contractUser.setPubkey(pubkey);
            contractUser.setPrikey(prikey); //todo shouldn`t store this key!!!
            contractUser.setUpdateUserName(currentUser.getUsername());
            contractUser.setUpdateTime(DateUtils.currentDateTime());
            contractUser.setUpdateUserId(currentUser.getId());
            activeUserKeyPairOk = contractUserService.updateContractUserKeyPair(contractUser);
        }
        if (!activeUserKeyPairOk) {
            responseData.setOK("密钥申请成功，但已有生效的密钥，激活失败，请稍后选择要激活的密钥!");
            return responseData;
        }
        responseData.setOK("applyKey success");
        return responseData;
    }

    @ApiOperation(value = "密钥激活", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "密钥激活成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/activeKey", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseData activeKey(HttpServletRequest request, Model model,
                                  @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                  @ApiParam(name = "id", required = true, value = "选择的密钥记录id") @RequestParam(name = "id", required = true) Long id) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<Boolean> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, id)) {
            responseData.setBadRequest("token or id is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        ContractUserKeyPair contractUserKeyPair = contractUserKeyPairService.getContractKeyPairById(id);
        if (contractUserKeyPair == null) {
            responseData.setException("密钥不存在");
            return responseData;
        }
        if (!currentUser.getId().equals(contractUserKeyPair.getUserId())) {
            responseData.setException("当前用户不存在此密钥");
            return responseData;
        }
        if (BaseConstant.CONTRACT_USER_KEYPAIR_STATUS_ACTIVE == contractUserKeyPair.getStatus()) {
            responseData.setException("此密钥已激活!");
            return responseData;
        }
        contractUserKeyPair.setUpdateUserName(currentUser.getUsername());
        contractUserKeyPair.setUpdateTime(DateUtils.currentDateTime());
        contractUserKeyPair.setUpdateUserId(currentUser.getId());
        boolean activeOk = contractUserKeyPairService.activeKey(contractUserKeyPair);
        if (!activeOk) {
            responseData.setException("密钥激活失败，状态未修改，请稍后重新激活!");
            return responseData;
        }
        currentUser.setPubkey(contractUserKeyPair.getPubkey());
        SessionUtil.updateCurrentUser(request, currentUser);
        ContractUser contractUser = new ContractUser();
        contractUser.setId(currentUser.getId());
        contractUser.setPubkey(contractUserKeyPair.getPubkey());
        contractUser.setPrikey(contractUserKeyPair.getPrikey()); //todo shouldn`t store this key!!!
        contractUser.setUpdateUserName(currentUser.getUsername());
        contractUser.setUpdateTime(DateUtils.currentDateTime());
        contractUser.setUpdateUserId(currentUser.getId());
        boolean activeUserKeyPairOk = contractUserService.updateContractUserKeyPair(contractUser);
        if (!activeUserKeyPairOk) {
            responseData.setException("密钥未成功激活，请重新激活!");
            return responseData;
        }
        responseData.setOK("activeKey success");
        return responseData;
    }

    @ApiOperation(value = "用户列表", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "操作成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/queryAll", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseData queryAll(HttpServletRequest request, Model model,
                                 @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                 @ApiParam(name = "id", required = false, value = "用户id") @RequestParam(name = "id", required = false) Long id,
                                 @ApiParam(name = "pubkey", value = "公钥", required = true) @RequestParam(name = "pubkey", required = false) String pubkey,
                                 @ApiParam(name = "name", value = "用户名字", required = false) @RequestParam(name = "name", required = false) String name) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<List<ContractUser>> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)) {
            responseData.setBadRequest("token is blank!");
            return responseData;
        }
        List<ContractUser> contractUsers = contractUserService.getAllUser(id, pubkey, name);
        responseData.setOK("query success");
        responseData.setData(contractUsers);
        return responseData;
    }

    @ApiOperation(value = "用户查询", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/query", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseData query(HttpServletRequest request, Model model,
                              @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                              @ApiParam(name = "id", required = false, value = "用户id") @RequestParam(name = "id", required = false) Long id) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<ContractUser> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)) {
            responseData.setBadRequest("token is blank!");
            return responseData;
        }
        ContractUser contractUsers = contractUserService.getUserById(id);
        responseData.setOK("query success");
        responseData.setData(contractUsers);
        return responseData;
    }
}
