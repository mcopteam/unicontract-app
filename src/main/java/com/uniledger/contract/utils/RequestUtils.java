package com.uniledger.contract.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by wxcsdb88 on 2017/5/16 9:51.
 */
public class RequestUtils {

    public static String requestParamsOutput(HttpServletRequest request) {
        StringBuilder requestParamMap = new StringBuilder("[");
        String requestParams = "";
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] val = entry.getValue();
            if (val.length == 1) {
                requestParamMap.append(entry.getKey() + "=" + entry.getValue()[0] + ",");
            } else {
                requestParamMap.append(entry.getKey() + "=" + Arrays.toString(entry.getValue()) + ",");
            }
        }
        requestParams = requestParamMap.subSequence(0, requestParamMap.length() - 1).toString() + "]";
        return requestParams;
    }

}
