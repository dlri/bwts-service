package com.dlri.chinacnr.bwts.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dlri.chinacnr.bwts.entity.ScheduleJob;
import com.dlri.chinacnr.bwts.manager.OnlineState;
import com.dlri.chinacnr.bwts.service.DetectionRecordService;

public class QuartzJobFactory implements Job {
	
	//private static Logger logger = Logger.getLogger(QuartzJobFactory.class);  
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get("scheduleJob");
		DetectionRecordService detectionRecordService = (DetectionRecordService) context.getMergedJobDataMap().get("detectionRecordService");
			String jobName = scheduleJob.getJobName();
			String jobGroup = scheduleJob.getJobGroup();
			String serverPath=scheduleJob.getServerPath();
			String ftpIP=scheduleJob.getFtpIp();
			String ftpPort=scheduleJob.getFtpPort();
			String ftpName=scheduleJob.getFtpName();
			String ftpPassword=scheduleJob.getFtpPassword();
			FtpUtil ftpUtil = new FtpUtil();
			boolean isConnected;
			isConnected = ftpUtil.connectServer(ftpIP, ftpPort,ftpName,ftpPassword,serverPath);
			if (isConnected == true) {
				//在线值为1
				OnlineState.map.put(jobName,"1");
				System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " "+ftpIP+"->编号为:"
						+ jobName + " 跑合台正在运行!");
				List<String> listName = ftpUtil.transferAndDelFiles();
				
				for (String fileName : listName) {
					Map<String,Object>map=new HashMap<String,Object>();
					//String fileName="G:/ftp1/2018/07/08/H63-2304_三级_20180303094041.txt";
					map.put("fileName", fileName);
					map.put("equType", jobGroup);
					map.put("equCode", jobName);
					
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
				OnlineState.map.put(jobName,"0");
				System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +" "+ftpIP+ "->编号为:"
						+ jobName + " 跑合台不在线!");
				//logger.info(ftpIP+" "+jobName + " 跑合台不在线!");
			}
	}
}