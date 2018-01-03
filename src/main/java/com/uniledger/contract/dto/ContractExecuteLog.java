package com.uniledger.contract.dto;

import com.uniledger.contract.model.ContractUser;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wxcsdb88 on 2017/6/15 10:04.
 */
public class ContractExecuteLog implements Serializable{
    private static final long serialVersionUID = -6555152735162002395L;
    private String contractHashId;
    /* transaction.Relation.TaskId */
    private String taskId;
    /* transaction timestamp */
    private Date timestamp;
    /* ContractComponents.Caption */
    private String caption;
    /* ContractComponents.Cname */
    private String cname;
    /* ContractComponents.Ctype */
    private String ctype;
    /* ContractComponents.Description */
    private String description;
    /* ContractComponents.State */
    private String state;
    /* ContractBody.CreateTime */
    private Date createTime;
    /* ContractComponents.MetaAttribute */
    private Map<String, String> metaAttribute;

    public String getContractHashId() {
        return contractHashId;
    }

    public void setContractHashId(String contractHashId) {
        this.contractHashId = contractHashId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Map<String, String> getMetaAttribute() {
        return metaAttribute;
    }

    public void setMetaAttribute(Map<String, String> metaAttribute) {
        this.metaAttribute = metaAttribute;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ContractExecuteLog{");
        sb.append("contractHashId='").append(contractHashId).append('\'');
        sb.append(", taskId='").append(taskId).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", caption='").append(caption).append('\'');
        sb.append(", cname='").append(cname).append('\'');
        sb.append(", ctype='").append(ctype).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", metaAttribute=").append(metaAttribute);
        sb.append('}');
        return sb.toString();
    }
}
