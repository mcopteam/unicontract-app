package com.uniledger.contract.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wxcsdb88 on 2017/5/24 12:21.
 */
public interface BaseConstant {

    String DEFAULT_SESSION_NAME = "currentUser";
 /*------------------------------  HTTP Response code start ----------------------------*/
    String HTTP_SUCCESS_VAL = "请求成功!";
    String HTTP_BAD_REQUEST_VAL = "请求参数错误!";
    String HTTP_EXCEPTION_VAL = "服务器异常!";
    /**
     * 请求成功
     */
    Integer HTTP_STATUS_CODE_OK             = 200; //200 - 客户端请求已成功end
    /**
     * Bas Request 参数错误等
     */
    Integer HTTP_STATUS_CODE_BadRequest     = 400; //400 - 请求出现语法错误
    //    Integer HTTP_STATUS_CODE_Unauthorized   = 401; //401 - 访问被拒绝
    /**
     * 权限问题，token或者用户角色等，保留
     */
    Integer HTTP_STATUS_CODE_Forbidden      = 403; //403 - 禁止访问 资源不可用
    /**
     * 页面处理未发现，API不建议使用
     */
    Integer HTTP_STATUS_CODE_NotFound       = 404; //404 - 无法找到指定位置的资源
    //    Integer HTTP_STATUS_CODE_NotAcceptable  = 406; //406 - 指定的资源已经找到，但它的MIME类型和客户在Accpet头中所指定的不兼容
//    Integer HTTP_STATUS_CODE_RequestTimeout = 408; //408 - 在服务器许可的等待时间内，客户一直没有发出任何请求。客户可以在以后重复同一请求。
    /**
     * 服务器内部错误！
     */
    Integer HTTP_STATUS_CODE_INTERVAL_ERROR  = 500;
    /**
     * 服务器异常或处理错误！
     */
    Integer HTTP_STATUS_CODE_DEFAULT_EXCEPTION  = HTTP_STATUS_CODE_INTERVAL_ERROR;
     /*------------------------------   HTTP Response code  ----------------------------*/



    /*------------------------------  用户keypair状态 start ----------------------------*/
    Short CONTRACT_USER_KEYPAIR_COUNT_MAX = 8;
    /**
     * [用户keypair状态] CONTRACT_USER_KEYPAIR_STATUS_INVALID = 0 已废弃或已删除
     */
    short CONTRACT_USER_KEYPAIR_STATUS_INVALID = 0;
    /**
     * [用户keypair状态] CONTRACT_USER_KEYPAIR_STATUS_INACTIVE = 1 未激活，新申请或者手工设置的
     */
    short CONTRACT_USER_KEYPAIR_STATUS_INACTIVE = 1;
    /**
     * [用户keypair状态] CONTRACT_USER_KEYPAIR_STATUS_ACTIVE = 2 激活中，使用中，正常的
     */
    short CONTRACT_USER_KEYPAIR_STATUS_ACTIVE = 2;
    /**
     * [用户keypair状态] CONTRACT_USER_KEYPAIR_STATUS_TEMP = 3 临时，未使用此状态！
     */
//    short CONTRACT_USER_KEYPAIR_STATUS_TEMP = 3;

    List<Short> CONTRACT_USER_KEYPAIR_STATUS = new ArrayList<Short>() {
        private static final long serialVersionUID = 7153527092471440983L;

        {
            add(CONTRACT_USER_KEYPAIR_STATUS_INVALID);
            add(CONTRACT_USER_KEYPAIR_STATUS_INACTIVE);
            add(CONTRACT_USER_KEYPAIR_STATUS_ACTIVE);
        }
    };
/*------------------------------  用户keypair状态 end ----------------------------*/


    Short USER_ROLE_DESIGNER = 4;
    Short USER_ROLE_AUDIT = 5;
    Short USER_ROLE_CUSTOM = 6;
    Short USER_ROLE_TRANSFER = 10;
    HashMap<Short, String> USER_ROLE_MAP = new HashMap<Short, String>(){
        private static final long serialVersionUID = -5548366316514960658L;
        {
            put(USER_ROLE_DESIGNER, "设计者");
            put(USER_ROLE_AUDIT, "审核者");
            put(USER_ROLE_CUSTOM, "用户");
            put(USER_ROLE_TRANSFER, "账户");
        }
    };

}
