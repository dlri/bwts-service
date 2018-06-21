package com.dlri.chinacnr.bwts.quartz;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.manager.Util;
import com.dlri.chinacnr.bwts.service.DetectionRecordService;

@Service("taskSchedulerRun001")
public class TaskSchedulerRun001 {
	private static Logger logger = Logger.getLogger(TaskSchedulerRun001.class);  
	@Autowired
	DetectionRecordService detectionRecordService;
	private Properties props;

	public TaskSchedulerRun001() {
		// 读取配置参数
		Resource resource = new ClassPathResource("/jdbc.properties");
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void taskScheduler() {
		FtpUtil ftpUtil = new FtpUtil();
		boolean isConnected;
		isConnected = ftpUtil.connectServer(
				props.getProperty("ftp_run_001.ip"), 
				props.getProperty("ftp_run_001.port"),
				props.getProperty("ftp_run_001.username"),
				props.getProperty("ftp_run_001.password"),
				props.getProperty("ftp_path.defaultPath"));
		if (isConnected == true) {
			//在线值为1
			Util.map.put(props.getProperty("ftp_run_001.equcode"),"1");
			System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " "+props.getProperty("ftp_run_001.ip")+"->编号为:"
					+ props.getProperty("ftp_run_001.equcode") + " 跑合台正在运行!");
			List<String> listName = ftpUtil.transferAndDelFiles();
			for (String fileName : listName) {
				Map<String,Object>map=new HashMap<String,Object>();
				map.put("fileName", fileName);
				map.put("equType", props.getProperty("ftp_run_001.equtype"));
				map.put("equCode", props.getProperty("ftp_run_001.equcode"));
				if (detectionRecordService.insertCallProcedureRecord(map) > 0) {
					WebSocketTest webSocketTest = new WebSocketTest();
					//首页实时推送数据
					webSocketTest.sendMsg(detectionRecordService.getMonitorValue());
					ftpUtil.deleteLocalFile(fileName);
					System.out.println("TXT文件删除成功: " + fileName);
				} else {
					System.out.println(fileName + " TXT文件删除不成功: ");
				}
			}
		} else {
			Util.map.put(props.getProperty("ftp_run_001.equcode"),"0");
			System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +" "+props.getProperty("ftp_run_001.ip")+ "->编号为:"
					+ props.getProperty("ftp_run_001.equcode") + " 跑合台不在线!");
			logger.info(props.getProperty("ftp_run_001.ip")+" "+props.getProperty("ftp_run_001.equcode") + " 跑合台不在线!");
		}
	}

}
