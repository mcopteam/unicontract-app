package com.uniledger.contract.dto;

import java.io.Serializable;

/**
 * Created by wxcsdb88 on 2017/6/16 16:09.
 */
public class PreSignContractDTO implements Serializable{
    private static final long serialVersionUID = 3281191606906175227L;

    private Long userId;
    private String username;
    private String currentUserPubkey;
    private String currentUserPrikey;
    private String publishPubkey;
    private String publishPrikey;
    private String contract;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCurrentUserPubkey() {
        return currentUserPubkey;
    }

    public void setCurrentUserPubkey(String currentUserPubkey) {
        this.currentUserPubkey = currentUserPubkey;
    }

    public String getCurrentUserPrikey() {
        return currentUserPrikey;
    }

    public void setCurrentUserPrikey(String currentUserPrikey) {
        this.currentUserPrikey = currentUserPrikey;
    }

    public String getPublishPubkey() {
        return publishPubkey;
    }

    public void setPublishPubkey(String publishPubkey) {
        this.publishPubkey = publishPubkey;
    }

    public String getPublishPrikey() {
        return publishPrikey;
    }

    public void setPublishPrikey(String publishPrikey) {
        this.publishPrikey = publishPrikey;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PreSignContractDTO{");
        sb.append("userId=").append(userId);
        sb.append(", username='").append(username).append('\'');
        sb.append(", currentUserPubkey='").append(currentUserPubkey).append('\'');
        sb.append(", currentUserPrikey='").append(currentUserPrikey).append('\'');
        sb.append(", publishPubkey='").append(publishPubkey).append('\'');
        sb.append(", publishPrikey='").append(publishPrikey).append('\'');
        sb.append(", contract='").append(contract).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
