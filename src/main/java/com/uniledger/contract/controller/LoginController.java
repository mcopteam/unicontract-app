package com.uniledger.contract.controller;

import com.uniledger.contract.common.ResponseData;
import com.uniledger.contract.common.TokenUser;
import com.uniledger.contract.constants.BaseConstant;
import com.uniledger.contract.model.ContractUser;
import com.uniledger.contract.service.ContractUserService;
import com.uniledger.contract.utils.CommonUtils;
import com.uniledger.contract.utils.MD5Util;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by wxcsdb88 on 2017/5/24 0:11.
 */
@Api(protocols = "http,https", description = "登录、登出")
@Controller
public class LoginController {
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private ContractUserService contractUserService;

    @ApiOperation(value = "用户登录", notes = "", httpMethod = "POST", produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 200, message = "登录成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value="/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseData login(HttpServletResponse response, HttpSession session,
                              @ApiParam(name = "username", required = true, value = "用户名", defaultValue = "wangxin")@RequestParam(name = "username", required = true) String username,
                              @ApiParam(name = "password", required = true, value = "密码", defaultValue = "123456")@RequestParam(name = "password", required = true) String password) throws Exception{
        session.setAttribute("user", username);
        ResponseData<TokenUser> responseData = new ResponseData<>();
        if (CommonUtils.isEmpty(username, password)){
            responseData.setBadRequest("username or password is blank!");
            return responseData;
        }
        ContractUser contractUser = contractUserService.getUserByUserName(username);
        String md5Pwd;
        if (contractUser != null && contractUser.getPassword() != null){
            md5Pwd = contractUser.getPassword();
            if (MD5Util.verify(password, md5Pwd)){
                Short role = contractUser.getRole();
                if(CommonUtils.isEmpty(role)){
                    responseData.setException("用户未分配角色，禁止操作!");
                    return responseData;
                }
                TokenUser tokenUser = new TokenUser();
                BeanUtils.copyProperties(contractUser, tokenUser);
                session.setAttribute(BaseConstant.DEFAULT_SESSION_NAME,tokenUser);
                //设置超时无效 seconds
//                session.setMaxInactiveInterval(30*60);
//                response.setHeader("Access-Control-Allow-Origin", "*");
//                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//                response.setHeader("Access-Control-Max-Age", "3600");
//                response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
                responseData.setOK("login success!", tokenUser);

                return responseData;
            }else{
                responseData.setBadRequest("username or password error!");
                return responseData;
            }
        }else{
            responseData.setException("user not exist!");// not should display!
            return responseData;
        }
    }

    @ApiOperation(value = "用户登出", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "登出成功"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value="/logout", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseData logout(HttpSession session) throws Exception{
        session.invalidate();
        ResponseData<Boolean> responseData = new ResponseData<>();
        responseData.setOK("logout success!");
        return responseData;
    }
}
