package com.dlri.chinacnr.bwts.quartz;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.manager.OnlineState;
import com.dlri.chinacnr.bwts.service.ProtocolService;
import com.dlri.chinacnr.bwts.service.impl.ProtocolServiceImpl;

@Service("monitor")
public class Monitor implements Runnable {
	ProtocolService protocolService = new ProtocolServiceImpl();
	public void run() {
		String str = "";
		Iterator<Map.Entry<String, String>> entries =com.dlri.chinacnr.bwts.manager.OnlineState.map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = entries.next();
			str += entry.getKey() + ":" + entry.getValue() + ",";
			// System.out.println("Key = " + entry.getKey() + ", Value = " +
			// entry.getValue());
		}
		//System.out.println(str + "==============1============" + Util.map.get("RUN001"));
		WebSocketTest webSocketTest = new WebSocketTest();
		// webSocketTest.sendMsg("当前时间:" + new Date());
		webSocketTest.sendMsg(str);
	}

	public void sendMsg() {
		ScheduledExecutorService newScheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
		newScheduledThreadPool.scheduleWithFixedDelay(new Monitor(), 20, 5, TimeUnit.SECONDS);

	}
}
