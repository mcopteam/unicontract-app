package com.uniledger.contract.logView;

/**
 * Created by lz on 2017/6/19.
 */

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.io.Writer;
import java.io.FileWriter;
import java.text.SimpleDateFormat;

public class LogView {

    public static void main(String[] args) throws Exception{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final LogView logSvr = new LogView();
        final File tmpLogFile = new File("C://tmp/log.log");
        if(!tmpLogFile.exists()) {
            tmpLogFile.createNewFile();
        }
        //启动一个线程每1秒钟向日志文件写一次数据
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleWithFixedDelay(new Runnable(){
            public void run() {
                try {
                    if(tmpLogFile == null) {
                        throw new IllegalStateException("logFile can not be null!");
                    }
                    Writer txtWriter = new FileWriter(tmpLogFile,true);
                    txtWriter.write(dateFormat.format(new Date()) +"\t"+Math.random()+"\n");
                    txtWriter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

}

