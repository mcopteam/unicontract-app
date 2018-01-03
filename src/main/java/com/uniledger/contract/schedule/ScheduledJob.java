package com.uniledger.contract.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created by wxcsdb88 on 2017/5/25 1:09.
 */
public class ScheduledJob extends QuartzJobBean {


    private AnotherBean anotherBean;


    @Override
    protected void executeInternal(JobExecutionContext arg0)
            throws JobExecutionException {
        anotherBean.printAnotherMessage();
    }

    public void setAnotherBean(AnotherBean anotherBean) {
        this.anotherBean = anotherBean;
    }
}
