package com.uniledger.contract.utils;

import com.uniledger.contract.constants.BaseConstant;
import com.uniledger.contract.common.TokenUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by wxcsdb88 on 2017/5/24 12:18.
 */
public class SessionUtil {

    public static TokenUser getCurrentUser(HttpServletRequest request){
        HttpSession session = request.getSession();
        return (TokenUser) session.getAttribute(BaseConstant.DEFAULT_SESSION_NAME);
    }

    public static void updateCurrentUser(HttpServletRequest request, TokenUser tokenUser){
        HttpSession session = request.getSession();
        if(tokenUser != null){
            session.setAttribute(BaseConstant.DEFAULT_SESSION_NAME, tokenUser);
        }
    }
}
