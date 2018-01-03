package com.uniledger.contract.dto;

import com.uniledger.contract.model.ContractUser;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 合约产品
 * Created by wxcsdb88 on 2017/5/26 10:03.
 */
public class ContractProduct implements Serializable {
    private static final long serialVersionUID = 6638386889670468265L;
    /**
     * 合约名称[本地文件名]
     */
    private String name;
    /**
     * 合约编号，来自本地库
     */
    private String contractNo;
    /**
     * 发布时间
     */
    private Date publishTime;


    /**
     * 合约hashId
     */
    private String hashId;
    /**
     * 合约描述
     */
    private String caption;
    /**
     * 合约id
     */
    private String contractId;

    /**
     * 合约名
     */
    private String contractName;

    /**
     * 合约创建时间
     */
    private Date createTime;
    /**
     * 签约人数
     */
    private Integer signCount;

    /**
     * 签约总资产
     */
    private BigDecimal signAsset;

    /**
     * 当前用户签名时间
     */
    private Date signTime;
    /**
     * 合约当前用户签名
     */
    private String signature;
    /**
     * 合约起始时间
     */
    private Date start;
    /**
     * 合约截止时间
     */
    private Date end;

    /**
     * 合约状态 ContractState
     */
    private String status;

    /**
     * 合约状态改变时间　ContractHead.Timestamp
     */
    private Date statusChangeTime;

    /**
     * 合约相应用户，如需要处理请您在service处理
     */
    private List<ContractUserDTO> owners;

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

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getSignCount() {
        return signCount;
    }

    public void setSignCount(Integer signCount) {
        this.signCount = signCount;
    }

    public BigDecimal getSignAsset() {
        return signAsset;
    }

    public void setSignAsset(BigDecimal signAsset) {
        this.signAsset = signAsset;
    }

    public Date getSignTime() {
        return signTime;
    }

    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStatusChangeTime() {
        return statusChangeTime;
    }

    public void setStatusChangeTime(Date statusChangeTime) {
        this.statusChangeTime = statusChangeTime;
    }

    public List<ContractUserDTO> getOwners() {
        return owners;
    }

    public void setOwners(List<ContractUserDTO> owners) {
        this.owners = owners;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ContractProduct{");
        sb.append("name='").append(name).append('\'');
        sb.append(", contractNo='").append(contractNo).append('\'');
        sb.append(", publishTime=").append(publishTime);
        sb.append(", hashId='").append(hashId).append('\'');
        sb.append(", caption='").append(caption).append('\'');
        sb.append(", contractId='").append(contractId).append('\'');
        sb.append(", contractName='").append(contractName).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", signCount=").append(signCount);
        sb.append(", signAsset=").append(signAsset);
        sb.append(", signTime=").append(signTime);
        sb.append(", signature='").append(signature).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", status='").append(status).append('\'');
        sb.append(", statusChangeTime=").append(statusChangeTime);
        sb.append(", owners=").append(owners);
        sb.append('}');
        return sb.toString();
    }
}
