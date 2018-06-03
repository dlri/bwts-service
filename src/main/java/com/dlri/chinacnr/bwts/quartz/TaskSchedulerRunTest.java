package com.dlri.chinacnr.bwts.quartz;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.service.ProtocolService;

@Service("taskSchedulerRunTest")
public class TaskSchedulerRunTest {

	@Autowired
	ProtocolService protocolService;
	private Properties props;

	public TaskSchedulerRunTest() {
		// 读取配置参数
		Resource resource = new ClassPathResource("/jdbc.properties");
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void taskScheduler() {
		/* 测试数据*/
		WebSocketTest webSocketTest = new WebSocketTest();
		String str="'run','RUN002','4','H63-2301','三级','5-15-3','5-15-1','20180303094041','IN0:A1','35.28','50.55','0.41','0.00','0.00','4.00','合格','','IN0:A2','35.53','50.47','0.43','0.00','0.00','4.00','合格','','IN1:B1','40.04','53.62','0.41','0.00','0.00','1.00','合格','','IN1:B2','37.58','51.04','0.41','0.00','0.00','3.00','合格',''";
	    webSocketTest.sendMsg(str);
	    String str1="'run','RUN001','4',H63-2302','三级','5-15-3','5-15-1','20180303094041','IN0:A1','35.28','50.55','0.41','0.00','0.00','4.00','合格','','IN0:A2','35.53','50.47','0.43','0.00','0.00','4.00','合格','','IN1:B1','40.04','53.62','0.41','0.00','0.00','1.00','合格','','IN1:B2','37.58','51.04','0.41','0.00','0.00','3.00','合格',''";
	    webSocketTest.sendMsg(str1);
	    String str2="'run','RUN004','4','H63-2304','三级','5-15-3','5-15-1','20180303094041','IN0:A1','35.28','50.55','0.41','0.00','0.00','4.00','合格','','IN0:A2','35.53','50.47','0.43','0.00','0.00','4.00','合格','','IN1:B1','40.04','53.62','0.41','0.00','0.00','1.00','合格','','IN1:B2','37.58','51.04','0.41','0.00','0.00','3.00','合格',''";
	    webSocketTest.sendMsg(str2);
	    String str3="'wash','WASH003','4','W63-2305','三级','5-15-3','5-15-1','20180303094041','IN0:A1','35.28','50.55','0.41','0.00','0.00','4.00','合格','','IN0:A2','35.53','50.47','0.43','0.00','0.00','4.00','合格','','IN1:B1','40.04','53.62','0.41','0.00','0.00','1.00','合格','','IN1:B2','37.58','51.04','0.41','0.00','0.00','3.00','合格',''";
	    webSocketTest.sendMsg(str3);
	   
			// 连接跑合台FTP服务器，远程传输文件
			//FtpUtil ftpUtil = new FtpUtil(props.getProperty("ftp_run_003.localpath"), props.getProperty("ftp_run_003.ip"),
			//		props.getProperty("ftp_run_003.port"), props.getProperty("ftp_run_003.username"), props.getProperty("ftp_run_003.password"));
		FtpUtil ftpUtil=new FtpUtil();
		boolean isConnected;
			isConnected = ftpUtil.connectServer(props.getProperty("ftp_run_003.ip"), props.getProperty("ftp_run_003.port"), props.getProperty("ftp_run_003.username"), props.getProperty("ftp_run_003.password"), props.getProperty("ftp_run_path.defaultPath"));
			if(isConnected==true){
				/*
				System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "->编号为:"+props.getProperty("ftp_run_003.equcode")+" 跑合台正在运行!");
				List<String>listName=ftpUtil.transferAndDelFiles();
				for(String fileName:listName){
					if(protocolService.addRun(fileName, props.getProperty("ftp_run_003.equtype"), props.getProperty("ftp_run_003.equcode"),props.getProperty("ftp_run_003.tablename"))>0){
					//	WebSocketTest webSocketTest = new WebSocketTest();
					 //   webSocketTest.sendMsg(protocolService.getMonitorValue());
						// System.out.println(protocolService.getMonitorValue());
						ftpUtil.deleteLocalFile(fileName);
						System.out.println("TXT文件删除成功: "+fileName);
					}else{
						System.out.println(fileName+" TXT文件删除不成功: ");
					}
				}
				*/
			}else{
				System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "->编号为:"+props.getProperty("ftp_run_003.equcode")+" 跑合台不在线!");
			}
		
		
		
	}

}
