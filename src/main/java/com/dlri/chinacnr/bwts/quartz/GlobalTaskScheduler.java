package com.dlri.chinacnr.bwts.quartz;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.entity.ScheduleJob;
import com.dlri.chinacnr.bwts.service.DetectionRecordService;
import com.dlri.chinacnr.bwts.service.ProtocolService;
import com.dlri.chinacnr.bwts.service.ScheduleJobService;

@Service("globalTaskScheduler")
public class GlobalTaskScheduler {

	@Autowired
	ProtocolService protocolService;
	@Autowired
	DetectionRecordService detectionRecordService;
	@Autowired
	ScheduleJobService scheduleJobService;
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	public void taskScheduler() throws IOException, SchedulerException{
		Map<String,Object>mapQuery=new HashMap<String, Object>();
		mapQuery.put("jobStatus", "1");
		List<ScheduleJob> list=scheduleJobService.queryScheduleJobByCondition(mapQuery);
		
		for(int i=0;i<list.size();i++){
			ScheduleJob scheduleJob = (ScheduleJob) list.get(i);
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
			// 获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			//System.out.println(i+"===============yuan============"+scheduleJob.getJobName()+"=="+scheduleJob.getJobGroup()+"=="+scheduleJob.getFtpPassword());
			// 不存在，创建一个
			if (null == trigger) {
				JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class)
						.withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup()).build();
				jobDetail.getJobDataMap().put("scheduleJob", scheduleJob);
				jobDetail.getJobDataMap().put("detectionRecordService", detectionRecordService);
				// 表达式调度构建器
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
				// 按新的cronExpression表达式构建一个新的trigger
				trigger = TriggerBuilder.newTrigger().withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup())
						.withSchedule(scheduleBuilder).build();
				//System.out.println("===============new============"+scheduleJob.getJobName()+"=="+scheduleJob.getJobGroup()+"=="+scheduleJob.getFtpPassword());
				scheduler.scheduleJob(jobDetail, trigger);
			} else {
				//System.out.println("==============yyyy============"+scheduleJob.getJobName()+"=="+scheduleJob.getJobGroup()+"=="+scheduleJob.getFtpPassword());
				// Trigger已存在，那么更新相应的定时设置
				// 表达式调度构建器
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());
				// 按新的cronExpression表达式重新构建trigger
				trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
				// 按新的trigger重新设置job执行
				scheduler.rescheduleJob(triggerKey, trigger);
			}
		}
	}
}
