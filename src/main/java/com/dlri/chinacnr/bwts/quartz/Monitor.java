package com.dlri.chinacnr.bwts.quartz;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.service.ProtocolService;
import com.dlri.chinacnr.bwts.service.impl.ProtocolServiceImpl;

@Service("monitor")
public class Monitor implements Runnable {

	ProtocolService protocolService=new ProtocolServiceImpl();
    public void run() {
        WebSocketTest webSocketTest = new WebSocketTest();
       // webSocketTest.sendMsg("当前时间:" + new Date());
        webSocketTest.sendMsg("===="+protocolService.getMonitorValue());
    }

    public void sendMsg() {
        ScheduledExecutorService newScheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
        newScheduledThreadPool.scheduleWithFixedDelay(new Monitor(), 20, 5, TimeUnit.SECONDS);

    }
}
