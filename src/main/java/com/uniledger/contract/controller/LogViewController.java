package com.uniledger.contract.controller;

import com.uniledger.contract.common.ResponseData;
import com.uniledger.contract.service.ContractHttpService;
import com.uniledger.contract.utils.RequestUtils;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lz on 2017/6/20.
 */

@Api(protocols = "http,https", value = "/contractLog", description = "contractLog")
@Controller
@RequestMapping("/contractLog")
public class LogViewController {
    private static Logger log = LoggerFactory.getLogger(LogViewController.class);

    @Autowired
    private ContractHttpService contractHttpService;
    private long lastTimeFileSize = 0;  //上次文件大小
    /**
     * logView
     * @param request   {@link HttpServletRequest}
     * @param response  {@link HttpServletResponse}
     * @param line     行号
     * @return  {@link ResponseData}
     */
    @ApiOperation(value = "Log监测")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/logView", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public List<String> queryContractProductLog(HttpServletRequest request, HttpServletResponse response,
            @ApiParam(name = "line", required = true, value = "行号")@RequestParam(name = "line", required = true) int line,
            @ApiParam(name="path", required=true,value="路径")@RequestParam(name = "path", required = true) String path) {

            log.info("request URI [" + request.getRequestURI() + "] | param " + RequestUtils.requestParamsOutput(request));

        final File tmpLogFile = new File(path);
        List<String> result = new ArrayList<String>();
        long count = 0;
        RandomAccessFile randomFile = null;
        try{
            OutputStream out = response.getOutputStream();
            //指定文件可读可写
            randomFile = new RandomAccessFile(tmpLogFile,"r");
            //启动一个线程每1秒钟读取新增的日志信息
            long length = randomFile.length();
            if (length == 0L) {
                System.out.println("null log".getBytes("ISO8859-1"));
                out.write(("waiting...").getBytes("ISO8859-1"));
                out.close();
            } else {
                //初始化游标
                long pos = length - 1;
                while (pos > 0)
                {
                    pos--;
                    //开始读取
                    randomFile.seek(pos);
                    //如果读取到\n代表是读取到一行
                    if (randomFile.readByte() == '\n')
                    {
                        //使用readLine获取当前行
                        String lineStr = randomFile.readLine();
                        //保存结果
                        System.out.println(new String(lineStr.getBytes("ISO-8859-1"),"utf-8"));
                        result.add(new String(lineStr.getBytes("ISO-8859-1"),"utf-8"));

                        //打印当前行
//                        System.out.println(count);

                        //行数统计，如果到达了numRead指定的行数，就跳出循环
                        count++;
                        if (count == line)
                        {
                            break;
                        }
                    }
                }
                if (pos == 0)
                {
                    randomFile.seek(0);
                    result.add(randomFile.readLine());
                }
                Collections.reverse(result);
                return result;
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }finally {
            if (randomFile != null)
            {
                try
                {
                    randomFile.close();
                }
                catch (Exception e)
                {
                }
            }
        }
        return result;
    }
//
//    /**
//     * 实时输出日志信息
//     * @param logFile 日志文件
//     * @throws IOException
//     */
//    public void realtimeShowLog(File logFile) throws IOException{
//
//    }
//
//    public static void main(String[] args) throws Exception {
//        LogView view = new LogView();
//        final File tmpLogFile = new File("mock.log");
//        view.realtimeShowLog(tmpLogFile);
//    }
}
