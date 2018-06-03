package com.dlri.chinacnr.bwts.service;

import java.util.List;
import java.util.Map;

import com.dlri.chinacnr.bwts.entity.DetectionRecord;

public interface DetectionRecordService {
	
	public List<DetectionRecord> queryDetectionRecordByCondition(Map<String,Object> map);
	
	public long queryDetectionRecordTotal(Map<String,Object> map);
	
	public int insertCallProcedureRecord(Map<String,Object> map);
	
	//发送首页页面监测的值
	public String getMonitorValue();
}
