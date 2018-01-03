package com.uniledger.contract.constants;

import java.util.*;

/**
 * Created by wxcsdb88 on 2017/5/10 19:48.
 */
public interface ContractConstant {

//-- 合约操作状态: 0-等待审核，1-其他人审核, 2-审核完成

    short CONTRACT_EXECUTE_NO = 0;
    short CONTRACT_EXECUTE_RUN = 1;
    short CONTRACT_EXECUTE_STOP = 2;
    short[] CONTRACT_EXECUTE_STATUS = new short[]{
            CONTRACT_EXECUTE_NO,
            CONTRACT_EXECUTE_RUN,
            CONTRACT_EXECUTE_STOP
    };

/*------------------------------  合约文件管理列表状态 start ----------------------------*/
    /**
     * [文件管理列表状态] CONTRACT_FILE_STATUS_ALL = -1 所有状态
     */
    short CONTRACT_FILE_STATUS_ALL = -1;
    /**
     * [文件管理列表状态] CONTRACT_FILE_STATUS_CREATING = 0 创建中
     */
    short CONTRACT_FILE_STATUS_CREATING = 0;
    /**
     * [文件管理列表状态] CONTRACT_FILE_STATUS_AUDITING = 1 审核中
     */
    short CONTRACT_FILE_STATUS_AUDITING = 1;
    /**
     * [文件管理列表状态] CONTRACT_FILE_STATUS_MODIFYING = 2 修改中
     */
    short CONTRACT_FILE_STATUS_MODIFYING = 2;
    /**
     * [文件管理列表状态] CONTRACT_FILE_STATUS_WAIT_PUBLISH = 3 等待发布
     */
    short CONTRACT_FILE_STATUS_WAIT_PUBLISH = 3;
    /**
     * [文件管理列表状态] CONTRACT_FILE_STATUS_PUBLISH = 4 已发布
     */
    short CONTRACT_FILE_STATUS_PUBLISH = 4;
    List<Short> CONTRACT_FILE_STATUS = new ArrayList<Short>(){
        private static final long serialVersionUID = 8737879855135353001L;
        {
            add(CONTRACT_FILE_STATUS_ALL);
            add(CONTRACT_FILE_STATUS_CREATING);
            add(CONTRACT_FILE_STATUS_AUDITING);
            add(CONTRACT_FILE_STATUS_MODIFYING);
            add(CONTRACT_FILE_STATUS_WAIT_PUBLISH);
            add(CONTRACT_FILE_STATUS_PUBLISH);
        }
    };

    /*文件列表展示状态- 设计者*/
    List<Short> CONTRACT_FILE_DISPLAY_STATUS_DESIGNER = new ArrayList<Short>(){
        private static final long serialVersionUID = 846822344696327825L;
        {
            add(CONTRACT_FILE_STATUS_CREATING);
            add(CONTRACT_FILE_STATUS_MODIFYING);
        }
    };
/*------------------------------  合约文件管理列表状态 end ----------------------------*/

/*------------------------------  合约产品审核操作 start ----------------------------*/
    /**
     * [合约产品操作] CONTRACT_AUDIT_OP_ALL = -1 所有状态
     */
    Short CONTRACT_AUDIT_OP_ALL = -1;

    /**
     *  [合约产品操作] CONTRACT_AUDIT_OP_FAIL = 2 未通过 --对应-- CONTRACT_FILE_STATUS_MODIFYING
     */
    Short CONTRACT_AUDIT_OP_FAIL = CONTRACT_FILE_STATUS_MODIFYING;// 建议修改操作
    /**
     *  [合约产品操作] CONTRACT_AUDIT_OP_PASS = 3 审批通过 --对应-- CONTRACT_FILE_STATUS_WAIT_PUBLISH
     */
    Short CONTRACT_AUDIT_OP_PASS = CONTRACT_FILE_STATUS_WAIT_PUBLISH; // 同意操作
    /**
     *  [合约产品操作] CONTRACT_AUDIT_OP_PUBLISH = 4 已发布 --对应-- CONTRACT_FILE_STATUS_AUDIT
     */
    Short CONTRACT_AUDIT_OP_PUBLISH = CONTRACT_FILE_STATUS_PUBLISH; // 发布操作
    /**
     * 合约产品操作接受状态字段
     */
    List<Short> CONTRACT_AUDIT_OP = new ArrayList<Short>(){
        private static final long serialVersionUID = 846822344696327825L;
        {
            add(CONTRACT_AUDIT_OP_FAIL);
            add(CONTRACT_AUDIT_OP_PASS);
            add(CONTRACT_AUDIT_OP_PUBLISH);
        }
    };

    /**
     * 合约产品查询接受字段
     */
    List<Short> CONTRACT_PRODUCT_ALL = new ArrayList<Short>(){
        private static final long serialVersionUID = 4643044183253775246L;
        {
            //审核中
            add(CONTRACT_FILE_STATUS_AUDITING);
            //审核通过，未发布
            add(CONTRACT_FILE_STATUS_WAIT_PUBLISH);
            //已发布
            add(CONTRACT_FILE_STATUS_PUBLISH);
        }
    };

/*------------------------------  合约文件审核操作 end ----------------------------*/



    String HTTP_CONTENT_TYPE_APPLICATION_X_PROTOBUF = "application/x-protobuf";
    String HTTP_CONTENT_TYPE_APPLICATION_JSON = "application/json";
    String HTTP_CONTENT_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";
    String[]  HTTP_CONTENT_TYPE = new String[]{
            HTTP_CONTENT_TYPE_APPLICATION_X_PROTOBUF,
            HTTP_CONTENT_TYPE_APPLICATION_JSON,
            HTTP_CONTENT_TYPE_APPLICATION_OCTET_STREAM
    };
    /*local indicate query contract from local db (json format)*/
    String CONTRACT_QUERY_FLAG_LOCAL = "local";
    /*remote indicate query contract from remote unicontract rethinkdb (json format)*/
    String CONTRACT_QUERY_FLAG_REMOTE = "remote";
    List<String> CONTRACT_QUERY_FLAG = new ArrayList<String>(){
        private static final long serialVersionUID = 8737879855135353001L;
        {
            add(CONTRACT_QUERY_FLAG_LOCAL);
            add(CONTRACT_QUERY_FLAG_REMOTE);
        }
    };


    /*合约用户表角色*/
    short CONTRACT_USER_ROLE_ADMIN= 4;
    /*设计者*/
    short CONTRACT_USER_ROLE_DESIGNER = 3;
    /*审核者*/
    short CONTRACT_USER_ROLE_AUDIT = 2;
    /*用户*/
    short CONTRACT_USER_ROLE_NORMAL = 1;
    /*转账用户*/
    short CONTRACT_USER_ROLE_TRANSFER = 10;
    HashMap<String, Short> CONTRACT_USER_ROLES = new HashMap<String, Short>(){
        private static final long serialVersionUID = 8737879855135353001L;
        {
        put("admin", CONTRACT_USER_ROLE_ADMIN);
        put("aduit", CONTRACT_USER_ROLE_AUDIT);
        put("designer", CONTRACT_USER_ROLE_DESIGNER);
        put("normal", CONTRACT_USER_ROLE_NORMAL);
        put("xiaoming", CONTRACT_USER_ROLE_TRANSFER);
    }};

}
