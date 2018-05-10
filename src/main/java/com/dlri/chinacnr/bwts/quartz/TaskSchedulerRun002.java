package com.dlri.chinacnr.bwts.quartz;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.service.ProtocolService;

@Service("taskSchedulerRun002")
public class TaskSchedulerRun002 {

	@Autowired
	ProtocolService protocolService;
	private Properties props;

	public TaskSchedulerRun002() {
		// 读取配置参数
		Resource resource = new ClassPathResource("/jdbc.properties");
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void taskScheduler() {
		
		
			// 连接跑合台FTP服务器，远程传输文件
			//FtpUtil ftpUtil = new FtpUtil(props.getProperty("ftp_run_002.localpath"), props.getProperty("ftp_run_002.ip"),
			//		props.getProperty("ftp_run_002.port"), props.getProperty("ftp_run_002.username"), props.getProperty("ftp_run_002.password"));
		FtpUtil ftpUtil=new FtpUtil();
		boolean isConnected;
			isConnected = ftpUtil.connectServer(props.getProperty("ftp_run_002.ip"), props.getProperty("ftp_run_002.port"), props.getProperty("ftp_run_002.username"), props.getProperty("ftp_run_002.password"), props.getProperty("ftp_run_path.defaultPath"));
			if(isConnected==true){
				System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "->编号为:"+props.getProperty("ftp_run_002.equcode")+" 跑合台正在运行!");
				List<String>listName=ftpUtil.transferAndDelFiles();
				for(String fileName:listName){
					if(protocolService.addRun(fileName, props.getProperty("ftp_run_002.equtype"), props.getProperty("ftp_run_002.equcode"))>0){
						ftpUtil.deleteLocalFile(fileName);
						System.out.println("TXT文件删除成功: "+fileName);
					}else{
						System.out.println(fileName+" TXT文件删除不成功: ");
					}
				}
			}else{
				System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "->编号为:"+props.getProperty("ftp_run_002.equcode")+" 跑合台不在线!");
			}
		
		
		
	}

}
