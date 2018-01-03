package com.uniledger.contract.controller;

import com.uniledger.contract.common.ResponseData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by wxcsdb88 on 2017/5/24 14:08.
 */
@ApiIgnore
@Controller
public class ErrorPageController {


    /**
     * 400 错误请求
     *
     * @return  {@link ResponseData}
     */
    @RequestMapping(value = "/error_400", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData error_400() {
        ResponseData<Boolean> responseData = new ResponseData<>();
        responseData.setBadRequest("参数错误");
        return responseData;
    }

    /**
     * 404 相关处理
     *
     * @return {@link ResponseData}
     */
    @RequestMapping(value = "/error_404", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData error_404() {
        ResponseData<Boolean> responseData = new ResponseData<>();
        responseData.setMsg("找不到页面");
        responseData.setCode(404);
        return responseData;
    }

    /**
     * 服务器异常
     *
     * @return {@link ResponseData}
     */
    @RequestMapping(value = "/error_500", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData error_500() {
        ResponseData<Boolean> responseData = new ResponseData<>();
        responseData.setException("服务器处理失败");
        return responseData;
    }
}
