package com.uniledger.contract.common;

import com.uniledger.contract.constants.BaseConstant;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wxcsdb88 on 2017/5/12 9:37.
 */
public class ResponseData<T> implements Serializable{
    private static final long serialVersionUID = -225863381103524707L;

    @ApiModelProperty(value = "状态码")
    private Integer code = BaseConstant.HTTP_STATUS_CODE_OK;
    @ApiModelProperty(value = "消息")
    private String msg = "";
    @ApiModelProperty(value = "返回数据")
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /*--------------------------  set Ok start -----------------------*/
    public void setOK(){
        setOK(BaseConstant.HTTP_STATUS_CODE_OK, BaseConstant.HTTP_SUCCESS_VAL, this.data);
    }
    public void setOK(Integer code){
        setOK(code, BaseConstant.HTTP_SUCCESS_VAL, this.data);
    }
    public void setOK(String msg){
        setOK(BaseConstant.HTTP_STATUS_CODE_OK, msg, this.data);
    }
    public void setOK(Integer code, String msg){
        setOK(code, msg, this.data);
    }
    public void setOK(Integer code, T data){
        setOK(code, BaseConstant.HTTP_SUCCESS_VAL, data);
    }
    public void setOK(String msg, T data){
        setOK(BaseConstant.HTTP_STATUS_CODE_OK, msg, data);
    }
    public void setOK(Integer code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
/*--------------------------  set Ok end -----------------------*/
/*--------------------------  set BadRequest start -----------------------*/
    public void setBadRequest(){
        setBadRequest(BaseConstant.HTTP_STATUS_CODE_BadRequest, BaseConstant.HTTP_BAD_REQUEST_VAL, this.data);
    }
    public void setBadRequest(Integer code){
        setBadRequest(code, BaseConstant.HTTP_BAD_REQUEST_VAL, this.data);
    }
    public void setBadRequest(String msg){
        setBadRequest(BaseConstant.HTTP_STATUS_CODE_BadRequest, msg, this.data);
    }
    public void setBadRequest(Integer code, String msg){
        setBadRequest(code, msg, this.data);
    }
    public void setBadRequest(Integer code, T data){
        setBadRequest(code, BaseConstant.HTTP_BAD_REQUEST_VAL, data);
    }
    public void setBadRequest(String msg, T data){
        setBadRequest(BaseConstant.HTTP_STATUS_CODE_BadRequest, msg, data);
    }
    public void setBadRequest(Integer code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
/*--------------------------  set BadRequest end -----------------------*/
/*--------------------------  set Exception start -----------------------*/
    public void setException(){
        setException(BaseConstant.HTTP_STATUS_CODE_DEFAULT_EXCEPTION, BaseConstant.HTTP_EXCEPTION_VAL, this.data);
    }
    public void setException(Integer code){
        setException(code, BaseConstant.HTTP_EXCEPTION_VAL, this.data);
    }
    public void setException(String msg){
        setException(BaseConstant.HTTP_STATUS_CODE_DEFAULT_EXCEPTION, msg, this.data);
    }
    public void setException(Integer code, String msg){
        setException(code, msg, this.data);
    }
    public void setException(Integer code, T data){
        setException(code, BaseConstant.HTTP_EXCEPTION_VAL, data);
    }
    public void setException(String msg, T data){
        setException(BaseConstant.HTTP_STATUS_CODE_DEFAULT_EXCEPTION, msg, data);
    }
    public void setException(Integer code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

/*--------------------------  set Exception end -----------------------*/

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ResponseData{");
        sb.append("code=").append(code);
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
