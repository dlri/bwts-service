package com.dlri.chinacnr.bwts.quartz;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.service.ProtocolService;

@Service("taskSchedulerRun001")
public class TaskSchedulerRun001 {

	@Autowired
	ProtocolService protocolService;
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
		System.out.println(new Date() + "->跑合台ftp_run_001在线!");
		
			// 连接跑合台FTP服务器，远程传输文件
			FtpUtil ftpUtil = new FtpUtil(props.getProperty("ftp_run_001.localpath"), props.getProperty("ftp_run_001.ip"),
					props.getProperty("ftp_run_001.port"), props.getProperty("ftp_run_001.username"), props.getProperty("ftp_run_001.password"));
			List<String>listName=ftpUtil.transferAndDelFiles();
			for(String fileName:listName){
				if(protocolService.addRun(fileName, props.getProperty("ftp_run_001.equtype"), props.getProperty("ftp_run_001.equcode"),props.getProperty("ftp_run_001.tablename"))>0){
					ftpUtil.deleteLocalFile(fileName);
					System.out.println("TXT文件删除成功: "+fileName);
				}else{
					System.out.println(fileName+" TXT文件删除不成功: ");
				}
				
			}
	}

}
