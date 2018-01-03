package com.uniledger.contract.dto;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by wxcsdb88 on 2017/6/10 17:57.
 */
public class ContractAssest implements Serializable{
    private static final long serialVersionUID = 1115783907575173008L;

    /*资产id, 标识*/
    private String assestId;
    /*资产名，英文名*/
    private String name;
    /*名称*/
    private String caption;
    /*描述*/
    private String description;
    /*单位*/
    private String unit;
    /*数量*/
    private Float amount;
    /*meta*/
    private HashMap<String, String> metaData;

    public String getAssestId() {
        return assestId;
    }

    public void setAssestId(String assestId) {
        this.assestId = assestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public HashMap<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(HashMap<String, String> metaData) {
        this.metaData = metaData;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ContractAssest{");
        sb.append("assestId='").append(assestId).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", caption='").append(caption).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", unit='").append(unit).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", metaData=").append(metaData);
        sb.append('}');
        return sb.toString();
    }
}
