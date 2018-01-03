package com.uniledger.contract.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户角色表RBAC
 * Created by wxcsdb88 on 2017/6/9 13:46.
 */
public class Role implements Serializable {
    /*角色id*/
    private Long id;
    /*角色名称英文*/
    private String name;
    /*角色名称中文*/
    private String nameCh;
    /*角色编号*/
    private Integer roleNo;
    /*角色有效状态*/
    private boolean isValid;
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

    public String getNameCh() {
        return nameCh;
    }

    public void setNameCh(String nameCh) {
        this.nameCh = nameCh;
    }

    public Integer getRoleNo() {
        return roleNo;
    }

    public void setRoleNo(Integer roleNo) {
        this.roleNo = roleNo;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
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
        final StringBuffer sb = new StringBuffer("Role{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", nameCh='").append(nameCh).append('\'');
        sb.append(", roleNo=").append(roleNo);
        sb.append(", isValid=").append(isValid);
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
