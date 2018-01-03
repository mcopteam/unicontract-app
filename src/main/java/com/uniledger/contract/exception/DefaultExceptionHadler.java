package com.uniledger.contract.exception;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.uniledger.contract.constants.BaseConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * global exception return json view[replace exception message]
 * Created by wxcsdb88 on 2017/5/24 9:33.
 */
public class DefaultExceptionHadler implements HandlerExceptionResolver{
    private Logger log = LoggerFactory.getLogger(DefaultExceptionHadler.class);


    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView mv = new ModelAndView();
        FastJsonJsonView view = new FastJsonJsonView();
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("msg", ex.getMessage());
        attributes.put("code", BaseConstant.HTTP_STATUS_CODE_INTERVAL_ERROR);//
        attributes.put("data", "");
        view.setAttributesMap(attributes);
        mv.setView(view);
        log.debug("异常:" + ex.getMessage(), ex);
        return mv;
    }
}
