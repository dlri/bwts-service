package com.dlri.chinacnr.bwts.service;

import java.util.List;
import java.util.Map;

import com.dlri.chinacnr.bwts.entity.DynRecord;

public interface DynRecordService {
	
	public List<DynRecord> getAllList();
	
	public List<DynRecord> queryDynRecordService(Map<String,Object> map);
	
	public long queryDynRecordTotalDao(Map<String,Object> map);

}
