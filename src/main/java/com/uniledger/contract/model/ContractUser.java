package com.uniledger.contract.model;

import com.uniledger.contract.constants.ContractConstant;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wxcsdb88 on 2017/5/15 17:47.
 */
public class ContractUser implements Serializable {
    private static final long serialVersionUID = -2695565332226694611L;

    private Long id;
    // 系统唯一标识用户身份的id，可以为空，标识未记录到中央系统中
    private Long uniqueId;
    /*--------- temp user login start ----- */
    // 账号
    private String username;
    // 加盐密码
    private String password;
    // 原始密码-temp
    private String originPassword;
    // 角色 默认0
    private Short role = ContractConstant.CONTRACT_USER_ROLE_NORMAL;
    /*--------- temp user login end ----- */

    private String pubkey;
    private String prikey;
    /*当前用户key最大数目*/
    private Integer maxKeyCount;
    private String name;
    private Integer age;
    private String phone;

    /*用户是否有效，暂时无用*/
    private Short status;
    /*账户是否被锁定,锁定将不能进行操作,锁定策略可以放在另一张表！*/
    private boolean isLocked = false;

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

    public Long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOriginPassword() {
        return originPassword;
    }

    public void setOriginPassword(String originPassword) {
        this.originPassword = originPassword;
    }

    public Short getRole() {
        return role;
    }

    public void setRole(Short role) {
        this.role = role;
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

    public Integer getMaxKeyCount() {
        return maxKeyCount;
    }

    public void setMaxKeyCount(Integer maxKeyCount) {
        this.maxKeyCount = maxKeyCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
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
        final StringBuffer sb = new StringBuffer("ContractUser{");
        sb.append("id=").append(id);
        sb.append(", uniqueId=").append(uniqueId);
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", originPassword='").append(originPassword).append('\'');
        sb.append(", role=").append(role);
        sb.append(", pubkey='").append(pubkey).append('\'');
        sb.append(", prikey='").append(prikey).append('\'');
        sb.append(", maxKeyCount=").append(maxKeyCount);
        sb.append(", name='").append(name).append('\'');
        sb.append(", age=").append(age);
        sb.append(", phone='").append(phone).append('\'');
        sb.append(", status=").append(status);
        sb.append(", isLocked=").append(isLocked);
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
