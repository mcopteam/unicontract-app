package com.uniledger.contract.interceptor;

//import org.codehaus.jackson.JsonGenerator;
//import org.codehaus.jackson.JsonParser;
//import org.codehaus.jackson.JsonProcessingException;
//import org.codehaus.jackson.map.JsonSerializer;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.map.SerializerProvider;
//import org.codehaus.jackson.map.ser.StdSerializerProvider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by wxcsdb88 on 2017/5/22 16:52.
 */
public class ObjectMappingCustomer extends ObjectMapper {
    private static final long serialVersionUID = 4066444310306186027L;

    public ObjectMappingCustomer() {
        super();
//        this.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // ignore the null
//        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 禁用空对象转换json校验
//        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //驼峰命名法转换为小写加下划线
//        this.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

        // 允许单引号
//        this.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 字段和值都加引号
//        this.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 数字加引号 default false
//        this.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
//        this.configure(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS, true);
//        this.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        // 空值处理为空串
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jg,
                                  SerializerProvider sp) throws IOException,
                    JsonProcessingException {
                jg.writeString("");
            }

        });

    }
}
