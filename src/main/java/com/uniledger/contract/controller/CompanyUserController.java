package com.uniledger.contract.controller;

import com.uniledger.contract.common.ResponseData;
import com.uniledger.contract.common.TokenUser;
import com.uniledger.contract.model.CompanyUser;
import com.uniledger.contract.service.CompanyUserService;
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
import java.util.List;

/**
 * Created by wxcsdb88 on 2017/6/10 14:01.
 */
@Api(protocols = "http,https", value = "/companyUser", description = "公司用户相关接口")
@Controller
@RequestMapping("/companyUser")
public class CompanyUserController {
    private Logger log = LoggerFactory.getLogger(CompanyUserController.class);

    @Autowired
    private CompanyUserService companyUserService;

    @ApiOperation(value = "公司公钥信息", notes = "发布合约产品前获取")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取公钥列表成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseData queryAll(HttpServletRequest request, Model model,
                                 @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                 @ApiParam(name = "id", value = "id", required = false) @RequestParam(name = "id", required = false) Long id,
                                 @ApiParam(name = "name", value = "中文名", required = false) @RequestParam(name = "name", required = false) String name,
                                 @ApiParam(name = "nameEn", value = "英文名", required = false) @RequestParam(name = "nameEn", required = false) String nameEn,
                                 @ApiParam(name = "pubkey", value = "公钥", required = false) @RequestParam(name = "pubkey", required = false) String pubkey,
                                 @ApiParam(name = "status", value = "公钥状态", required = false) @RequestParam(name = "status", required = false) Short status) {
        log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));
        ResponseData<List<CompanyUser>> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token)) {
            responseData.setBadRequest("token is blank!");
            return responseData;
        }
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        // todo 角色验证
        Short[] statusQuery = null;
        if (status == null || status == -1) {
            statusQuery = null;
        } else {
            statusQuery = new Short[]{status};
        }
        List<CompanyUser> companyUserList = companyUserService.getCompanyUserList(id, name, nameEn, pubkey, statusQuery);
        if (companyUserList == null || companyUserList.size() == 0) {
            responseData.setException("no math result!");
            return responseData;
        }

        responseData.setOK("keyList success");
        responseData.setData(companyUserList);
        return responseData;
    }


}
