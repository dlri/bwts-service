package com.dlri.chinacnr.bwts.service;

import java.util.List;
import java.util.Map;

import com.dlri.chinacnr.bwts.entity.DetectionRecord;
import com.dlri.chinacnr.bwts.entity.RecordTotal;
import com.dlri.chinacnr.bwts.entity.Statistical;

public interface DetectionRecordService {
	
	public List<DetectionRecord> queryDetectionRecordByCondition(Map<String,Object> map);
	
	public RecordTotal queryDetectionRecordTotal(Map<String,Object> map);
	
	public int insertCallProcedureRecord(Map<String,Object> map);
	
	//发送首页页面监测的值
	public String getMonitorValue();
	
	public List<DetectionRecord> queryRecordByLastTime();
	
	public List<Statistical> queryStatistialRecordByCondition(Map<String,Object> map);
}
