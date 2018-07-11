package com.dlri.chinacnr.bwts.service;

import java.util.List;
import java.util.Map;

import com.dlri.chinacnr.bwts.entity.DetectionRecord;
import com.dlri.chinacnr.bwts.entity.RecordTotal;
import com.dlri.chinacnr.bwts.entity.ScheduleJob;

public interface ScheduleJobService {
	
	public List<ScheduleJob> queryScheduleJobByCondition(Map<String,Object> map);
	
	public void updateScheduleJob(ScheduleJob job);
}
