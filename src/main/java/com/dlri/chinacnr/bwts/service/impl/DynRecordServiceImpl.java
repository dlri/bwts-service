package com.dlri.chinacnr.bwts.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.dao.DynRecordDao;
import com.dlri.chinacnr.bwts.entity.DynRecord;
import com.dlri.chinacnr.bwts.service.DynRecordService;

@Service("dynRecordService")
public class DynRecordServiceImpl implements DynRecordService {

	@Autowired
	DynRecordDao dynRecordDao;
	
	public List<DynRecord> getAllList() {
		return dynRecordDao.getAllList();
	}

	public List<DynRecord> queryDynRecordService(Map<String, Object> map) {
		
		return dynRecordDao.queryDynRecordDao(map);
	}

	public long queryDynRecordTotalDao(Map<String, Object> map) {
		
		return dynRecordDao.queryDynRecordTotalDao(map);
	}
}

