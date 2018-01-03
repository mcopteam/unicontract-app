package com.uniledger.contract.filter;

import com.uniledger.contract.controller.TransferAccountController;
import com.uniledger.contract.exception.DefaultExceptionHadler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by wxcsdb88 on 2017/5/27 11:17.
 */
public class CORSFilter implements Filter {
    private static Logger log = LoggerFactory.getLogger(CORSFilter.class);

    private List<String> corsOriginList;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String curOrigin = req.getHeader("Origin");
        log.info("当前访问来源是：{}", curOrigin);
        // 从列表中获取，可以将数据放入缓存
        if (corsOriginList.contains(curOrigin)) {
            res.setHeader("Access-Control-Allow-Origin", curOrigin);
        } else {
            return ;
        }

        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        corsOriginList = new ArrayList<String>();
        // 初始化可访问的域名列表
        corsOriginList.add("http://localhost:8082");
        corsOriginList.add("http://localhost:8082");
        corsOriginList.add("http://localhost:8082");
    }
}
