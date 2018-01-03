package com.uniledger.contract.dto;

import com.uniledger.contract.model.ContractUser;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by wxcsdb88 on 2017/5/10 10:56.
 */
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractDTO implements Serializable{

    private static final long serialVersionUID = 1242064131243177586L;

    private String id;
    /*合约名称*/
    private String name;
    /*合约编号*/
    private String contractId;
    /*创建时间*/
    private Date createTime;
    /*执行时间*/
    private Date executeTime;
    /*合约执行状态*/
    private Short executeStatus;
    /*合约操作状态*/
    private Short operateStatus;

    /*合约人*/
    private List<ContractUser> owners;
    /*合约描述*/
    private String caption;
    /*合约内容*/
    private String contractContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Short getOperateStatus() {
        return operateStatus;
    }

    public void setOperateStatus(Short operateStatus) {
        this.operateStatus = operateStatus;
    }

    public List<ContractUser> getOwners() {
        return owners;
    }

    public void setOwners(List<ContractUser> owners) {
        this.owners = owners;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ContractDTO{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", contractId='").append(contractId).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", executeTime=").append(executeTime);
        sb.append(", executeStatus=").append(executeStatus);
        sb.append(", operateStatus=").append(operateStatus);
        sb.append(", owners=").append(owners);
        sb.append(", caption='").append(caption).append('\'');
        sb.append(", contractContent='").append(contractContent).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
