package com.dlri.chinacnr.bwts.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.dao.ScheduleJobDao;
import com.dlri.chinacnr.bwts.entity.ScheduleJob;
import com.dlri.chinacnr.bwts.service.ScheduleJobService;
@Service("scheduleJobService")
public class ScheduleJobServiceImpl implements ScheduleJobService {

	@Autowired
	ScheduleJobDao scheduleJobDao;

	public List<ScheduleJob> queryScheduleJobByCondition(Map<String, Object> map) {
		return scheduleJobDao.queryScheduleJobByCondition(map);
	}

	public void updateScheduleJob(ScheduleJob job) {
		scheduleJobDao.updateScheduleJob(job);
	}
	
}
