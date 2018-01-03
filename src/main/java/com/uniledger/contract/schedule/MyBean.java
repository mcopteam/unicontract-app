package com.uniledger.contract.schedule;

import com.uniledger.contract.utils.DateUtils;
import org.springframework.stereotype.Component;

/**
 * Created by wxcsdb88 on 2017/5/25 1:12.
 */
@Component("myBean")
public class MyBean {
    public void printMessage() {
        System.out.println("I am called by MethodInvokingJobDetailFactoryBean using SimpleTriggerFactoryBean " + "now is " + DateUtils.currentDateTimeStr(null));
    }
}
