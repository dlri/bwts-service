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

@Service("job1Demo")
public class Job1Demo {

	@Autowired
	ProtocolService protocolService;
	private Properties props;

	public Job1Demo() {
		// 读取配置参数
		Resource resource = new ClassPathResource("/jdbc.properties");
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sayHello() {
		System.out.println(new Date() + "->Hello,调用进行1!");
		
			// 连接FTP服务器，远程传输文件
			FtpUtil ftpUtil = new FtpUtil(props.getProperty("ftp.localpath"), props.getProperty("ftp1.ip"),
					props.getProperty("ftp1.port"), props.getProperty("ftp1.username"), props.getProperty("ftp1.password"));
			List<String>listName=ftpUtil.transferAndDelFiles();
			for(String name:listName){
				/*
				if(protocolService.add(name)>0){
					FtpUtil.deleteLocalFile(name);
					System.out.println("TXT文件删除成功: "+name);
				}else{
					System.out.println(name+" TXT文件删除不成功: ");
				}*/
				
			}
	}

}
