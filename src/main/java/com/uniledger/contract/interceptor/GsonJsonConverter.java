package com.uniledger.contract.interceptor;

import com.google.gson.GsonBuilder;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

/**
 * Created by wxcsdb88 on 2017/5/22 19:15.
 */
public class GsonJsonConverter extends GsonHttpMessageConverter {
    public GsonJsonConverter() {
        super.setGson(new GsonBuilder()
//                .serializeNulls() // serialize the null
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create());
    }
}
