package com.uniledger.contract.schedule;

import com.uniledger.contract.utils.DateUtils;
import org.springframework.stereotype.Component;

/**
 * Created by wxcsdb88 on 2017/5/25 1:11.
 */
@Component("anotherBean")
public class AnotherBean {

    public void printAnotherMessage(){
        System.out.println("I am called by Quartz jobBean using CronTriggerFactoryBean " + "now is " + DateUtils.currentDateTimeStr(null));
    }

}
