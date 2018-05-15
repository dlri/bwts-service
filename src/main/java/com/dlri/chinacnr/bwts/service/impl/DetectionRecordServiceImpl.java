package com.dlri.chinacnr.bwts.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.dao.DetectionRecordDao;
import com.dlri.chinacnr.bwts.entity.DetectionRecord;
import com.dlri.chinacnr.bwts.service.DetectionRecordService;
@Service("detectionRecordService")
public class DetectionRecordServiceImpl implements DetectionRecordService {

	@Autowired
	DetectionRecordDao detectionRecordDao;
	public List<DetectionRecord> queryDetectionRecordByCondition(Map<String, Object> map) {
		return detectionRecordDao.queryDetectionRecordByCondition(map);
	}

	public long queryDetectionRecordTotal(Map<String, Object> map) {
		return detectionRecordDao.queryDetectionRecordTotal(map);
	}

}
