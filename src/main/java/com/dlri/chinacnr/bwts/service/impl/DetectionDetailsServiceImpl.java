package com.dlri.chinacnr.bwts.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.dao.DetectionDetailsDao;
import com.dlri.chinacnr.bwts.entity.DetectionDetails;
import com.dlri.chinacnr.bwts.service.DetectionDetailsService;
@Service("detectionDetailsService")
public class DetectionDetailsServiceImpl implements DetectionDetailsService {

	@Autowired
	DetectionDetailsDao detectionDetailsDao;
	public List<DetectionDetails> queryDetectionDetailsByCondition(Map<String, Object> map) {
		return detectionDetailsDao.queryDetectionDetailsByCondition(map);
	}

}
