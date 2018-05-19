package com.dlri.chinacnr.bwts.service;

import java.util.List;
import java.util.Map;

import com.dlri.chinacnr.bwts.entity.DetectionDetails;

public interface DetectionDetailsService {
	
	public List<DetectionDetails> queryDetectionDetailsByCondition(Map<String,Object> map);
}
