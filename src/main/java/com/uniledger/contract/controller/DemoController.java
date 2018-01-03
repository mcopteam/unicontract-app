package com.uniledger.contract.controller;

import com.uniledger.contract.common.ResponseData;
import com.uniledger.contract.service.ContractHttpService;
import com.uniledger.contract.utils.CommonUtils;
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

/**
 * Created by wxcsdb88 on 2017/6/24 16:00.
 */
@Api(protocols = "http,https", description = "demo for elect")
@Controller
@RequestMapping(value = "/electric", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
public class DemoController {
    private static Logger log = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private ContractHttpService contractHttpService;

    @ApiOperation(value = "购电demo API", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value="/api", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseData queryAccountBalance(HttpServletRequest request, Model model,
                                            @ApiParam(name = "token", required = true, value = "标识", defaultValue = "futurever") @RequestParam(name = "token", required = true) String token,
                                            @ApiParam(name = "methodName", required = true, value = "方法") @RequestParam(name = "methodName", required = true) String methodName) throws Exception{
        ResponseData<String> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(token, methodName)) {
            responseData.setBadRequest("token or methodName is blank!");
            return responseData;
        }
        String data = contractHttpService.getDemoDataForWYP(token, methodName);
        if(CommonUtils.isEmpty(data)){
            responseData.setException("query fail");
            return responseData;
        }
        responseData.setOK("query success!", data);
        return responseData;
    }


}
