package com.uniledger.contract.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wxcsdb88 on 2017/6/10 17:45.
 */
public class ContractContent implements Serializable{

    private static final long serialVersionUID = 7355084990934907199L;

    /*合约名称*/
    private String cname;
    /*合约名称，中文*/
    private String caption;

    /*合约编号*/
    private String contractId;
    /*合约主体, include creator*/
    private String contractOwners;
    /*卖方，甲方，创建者*/
    private String contractCreator;

    /*创建时间*/
    private String createTime;
    /*合约起始日期*/
    private String startTime;
    /*合约截止日期*/
    private String endTime;

    /*------------------- 合约资产描述 -------------------*/
    private List<ContractAssest> contractAssets;

    /*todo 合约内容*/
    private String content;

    /*------------------- 合约签名 -------------------*/
    private List<ContractSignature> contractSignatures;

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
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

    public String getContractOwners() {
        return contractOwners;
    }

    public void setContractOwners(String contractOwners) {
        this.contractOwners = contractOwners;
    }

    public String getContractCreator() {
        return contractCreator;
    }

    public void setContractCreator(String contractCreator) {
        this.contractCreator = contractCreator;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<ContractAssest> getContractAssets() {
        return contractAssets;
    }

    public void setContractAssets(List<ContractAssest> contractAssets) {
        this.contractAssets = contractAssets;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ContractSignature> getContractSignatures() {
        return contractSignatures;
    }

    public void setContractSignatures(List<ContractSignature> contractSignatures) {
        this.contractSignatures = contractSignatures;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ContractContent{");
        sb.append("cname='").append(cname).append('\'');
        sb.append(", caption='").append(caption).append('\'');
        sb.append(", contractId='").append(contractId).append('\'');
        sb.append(", contractOwners='").append(contractOwners).append('\'');
        sb.append(", contractCreator='").append(contractCreator).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", contractAssets=").append(contractAssets);
        sb.append(", content='").append(content).append('\'');
        sb.append(", contractSignatures=").append(contractSignatures);
        sb.append('}');
        return sb.toString();
    }
}
