package com.uniledger.contract.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wxcsdb88 on 2017/6/10 18:35.
 */
public class ContractAuditLog implements Serializable{
    private static final long serialVersionUID = -6553575816235756607L;

    private Long id;
    /*合约文件主键*/
    private Long contractPK;
    /*
    * 操作描述
    * createTime, userName[id]description
    * */
    /*操作码, [未通过(修改意见)=2,审批通过=3,发布=4]*/
    private Short status;

    private String description;

    private String other;
    /**
     * 创建者id
     */
    private Long createUserId;
    /*创建者标识*/
    private String createUserName;
    /*创建时间*/
    private Date createTime;
    /*是否展示此记录*/
    private boolean isShow = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractPK() {
        return contractPK;
    }

    public void setContractPK(Long contractPK) {
        this.contractPK = contractPK;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
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

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ContractAuditLog{");
        sb.append("id=").append(id);
        sb.append(", contractPK=").append(contractPK);
        sb.append(", status=").append(status);
        sb.append(", description='").append(description).append('\'');
        sb.append(", other='").append(other).append('\'');
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createUserName='").append(createUserName).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", isShow=").append(isShow);
        sb.append('}');
        return sb.toString();
    }
}
