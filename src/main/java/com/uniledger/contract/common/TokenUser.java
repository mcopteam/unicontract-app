package com.uniledger.contract.common;

import java.io.Serializable;

/**
 * Created by wxcsdb88 on 2017/5/24 11:31.
 */
public class TokenUser implements Serializable{
    private static final long serialVersionUID = 2687180341537772470L;

    private Long id;
    private String username;
    private Short role;
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
        final StringBuffer sb = new StringBuffer("TokenUser{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append(", role=").append(role);
        sb.append(", pubkey='").append(pubkey).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
