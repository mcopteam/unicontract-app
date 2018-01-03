package com.uniledger.contract.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wxcsdb88 on 2017/6/10 12:14.
 */
public class CompanyUser implements Serializable {
    private static final long serialVersionUID = -6024999704536436428L;

    private Long id;
    /**
     * 公司或签约方名称
     */
    private String name;

    private String nameEn;

    /*序号，排序用*/
    private Integer no;
    /**
     * 当前公钥
     */
    private String pubkey;
    /**
     * 私钥 临时存储或者不存储，测试用
     */
    private String prikey;
    /**
     * 公私钥状态
     * 0-已废弃或已删除，禁止使用，1-未激活，新申请或者手工设置的, 2-正常
     */
    private Short status;
    /**
     * 创建者id
     */
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

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public String getPrikey() {
        return prikey;
    }

    public void setPrikey(String prikey) {
        this.prikey = prikey;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
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
        final StringBuffer sb = new StringBuffer("CompanyUser{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", nameEn='").append(nameEn).append('\'');
        sb.append(", no=").append(no);
        sb.append(", pubkey='").append(pubkey).append('\'');
        sb.append(", prikey='").append(prikey).append('\'');
        sb.append(", status=").append(status);
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
