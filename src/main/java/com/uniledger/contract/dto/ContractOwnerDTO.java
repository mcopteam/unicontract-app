package com.uniledger.contract.dto;

import com.uniledger.contract.constants.ContractConstant;

import java.io.Serializable;

/**
 * Created by wxcsdb88 on 2017/6/15 10:07.
 */
public class ContractOwnerDTO implements Serializable {
    private static final long serialVersionUID = 4638732150745434601L;

    private Long id;
    // 账号
    private String username;
    // 角色 默认0
    private Short role = ContractConstant.CONTRACT_USER_ROLE_NORMAL;
    /*--------- temp user login end ----- */
    private String pubkey;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ContractOwnerDTO{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append(", role=").append(role);
        sb.append(", pubkey='").append(pubkey).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
