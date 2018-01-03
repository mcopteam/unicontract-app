package com.uniledger.contract.dto;

import java.io.Serializable;

/**
 * Created by wxcsdb88 on 2017/6/10 18:17.
 */
public class ContractSignature implements Serializable {
    private static final long serialVersionUID = 7180034435594784139L;

    private String ownerPubkey;
    private String signature;
    private String signTimestamp;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getOwnerPubkey() {
        return ownerPubkey;
    }

    public void setOwnerPubkey(String ownerPubkey) {
        this.ownerPubkey = ownerPubkey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignTimestamp() {
        return signTimestamp;
    }

    public void setSignTimestamp(String signTimestamp) {
        this.signTimestamp = signTimestamp;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ContractSignature{");
        sb.append("ownerPubkey='").append(ownerPubkey).append('\'');
        sb.append(", signature='").append(signature).append('\'');
        sb.append(", signTimestamp='").append(signTimestamp).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
