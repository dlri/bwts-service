package com.dlri.chinacnr.bwts.quartz;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.dlri.chinacnr.bwts.entity.ScheduleJob;
import com.dlri.chinacnr.bwts.manager.OnlineState;
import com.dlri.chinacnr.bwts.service.DetectionRecordService;

public class QuartzJobFactory implements Job {
	private Properties props;
	public QuartzJobFactory(){
		// 读取配置参数
		Resource resource = new ClassPathResource("/jdbc.properties");
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//private static Logger logger = Logger.getLogger(QuartzJobFactory.class);  
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get("scheduleJob");
		DetectionRecordService detectionRecordService = (DetectionRecordService) context.getMergedJobDataMap().get("detectionRecordService");
			String jobName = scheduleJob.getJobName();
			String jobGroup = scheduleJob.getJobGroup();
			//不从数据库中读取，从配置文件中读取其存放路径，便于移植
			//String serverPath=scheduleJob.getServerPath();
			String serverPath=props.getProperty("server.serverPath");
			//System.out.println("成功从配置文件中读取其存放路径： "+serverPath);
			String ftpIP=scheduleJob.getFtpIp();
			String ftpPort=scheduleJob.getFtpPort();
			String ftpName=scheduleJob.getFtpName();
			String ftpPassword=scheduleJob.getFtpPassword();
			FtpUtil ftpUtil = new FtpUtil();
			boolean isConnected;
			isConnected = ftpUtil.connectServer(ftpIP, ftpPort,ftpName,ftpPassword,serverPath);
			//System.out.println("读取到的机位上FTP配置信息："+isConnected+"==="+ftpIP+"=="+ftpName+"==="+ftpPassword);
			if (isConnected == true) {
				//在线值为1
				OnlineState.map.put(jobName,"1");
				System.out.println("[OnLine] "+ jobName +" 的车间机位正在接收数据！其IP为： "+ftpIP+"->时间："+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
				List<String> listName = ftpUtil.transferAndDelFiles();
				System.out.println("是否要入库：===="+listName.size());
				for (String fileName : listName) {
					Map<String,Object>map=new HashMap<String,Object>();
					//String fileName="G:/ftp1/2018/07/08/H63-2304_三级_20180303094041.txt";
					map.put("fileName", fileName);
					map.put("equType", jobGroup);
					map.put("equCode", jobName);
					int status=detectionRecordService.insertCallProcedureRecord(map);
					System.out.println("入库后返回的状态是否大于零："+status);
					if (status > 0) {
						System.out.println("======大于零:成功=====");
						WebSocketTest webSocketTest = new WebSocketTest();
						//首页实时推送数据
						webSocketTest.sendMsg(detectionRecordService.getMonitorValue());
						ftpUtil.deleteLocalFile(fileName);
						System.out.println("TXT文件删除成功: " + fileName);
					} else {
						System.out.println("======小于零:失败=====");
						System.out.println(fileName + " TXT文件删除不成功: ");
					}
				}
			} else {
				OnlineState.map.put(jobName,"0");
				System.out.println("    [OffLine] "+ jobName +" 的车间机位不在线！其IP为： "+ftpIP+"->时间："+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
				//logger.info(ftpIP+" "+jobName + " 跑合台不在线!");
			}
	}
}