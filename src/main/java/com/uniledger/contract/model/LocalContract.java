package com.uniledger.contract.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * xml local store in mysql
 * Created by wxcsdb88 on 2017/5/15 15:24.
 */
public class LocalContract implements Serializable {

    private static final long serialVersionUID = 2229517086582519095L;

    private Long id;
    /*本地合约文件名，新建合约时填的名称*/
    private String name;
    /*合约编号,顺序生成*/
    private String contractNo;
    /*合约id*/
    private String contractId;
    /*执行时间*/
    private Date executeTime;
    /*合约执行状态*/
    private Short executeStatus;
    /*合约文件状态*/
    private Short status;
    /*修改意见*/
    private String suggestion;

    /*合约人pubkey 以,分割*/
    private String owner;
    /*publish user人*/
    private String publishUser;
    /*合约描述 以,分割*/
    private String caption;

    /*合约内容 xml 格式*/
    private String contractContent;
    /*合约自动保存xml*/ //todo autoSaveXmlContract method
    private String autoContractContent;
    /*contract json[xml->protoContract->json]*/
    private String jsonContract;
    /*创建者id*/
    private Long createUserId;
    /*创建者标识*/
    private String createUserName;
    /*创建时间*/
    private Date createTime;
    /*更新者id*/
    private Long updateUserId;
    /*修改时间*/
    private Date updateTime;
    /*更新者标识*/
    private String updateUserName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }

    public Short getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(Short executeStatus) {
        this.executeStatus = executeStatus;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(String publishUser) {
        this.publishUser = publishUser;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getContractContent() {
        return contractContent;
    }

    public void setContractContent(String contractContent) {
        this.contractContent = contractContent;
    }

    public String getAutoContractContent() {
        return autoContractContent;
    }

    public void setAutoContractContent(String autoContractContent) {
        this.autoContractContent = autoContractContent;
    }

    public String getJsonContract() {
        return jsonContract;
    }

    public void setJsonContract(String jsonContract) {
        this.jsonContract = jsonContract;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LocalContract{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", contractNo='").append(contractNo).append('\'');
        sb.append(", contractId='").append(contractId).append('\'');
        sb.append(", executeTime=").append(executeTime);
        sb.append(", executeStatus=").append(executeStatus);
        sb.append(", status=").append(status);
        sb.append(", suggestion='").append(suggestion).append('\'');
        sb.append(", owner='").append(owner).append('\'');
        sb.append(", publishUser='").append(publishUser).append('\'');
        sb.append(", caption='").append(caption).append('\'');
        sb.append(", contractContent='").append(contractContent).append('\'');
        sb.append(", autoContractContent='").append(autoContractContent).append('\'');
        sb.append(", jsonContract='").append(jsonContract).append('\'');
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createUserName='").append(createUserName).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", updateUserId=").append(updateUserId);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", updateUserName='").append(updateUserName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
