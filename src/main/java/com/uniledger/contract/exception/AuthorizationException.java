package com.uniledger.contract.exception;

/**
 * Created by wxcsdb88 on 2017/5/23 23:58.
 */
public class AuthorizationException extends Exception {

    public AuthorizationException() {
        super();
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
