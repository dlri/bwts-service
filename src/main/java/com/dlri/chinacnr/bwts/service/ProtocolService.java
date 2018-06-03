package com.dlri.chinacnr.bwts.service;

import java.util.List;

import com.dlri.chinacnr.bwts.entity.Protocol;

public interface ProtocolService {
	
	public List<Protocol> getAllList();
	
	public int createNewTable(String newTableName,String originalTableName);
	
	public int createDynamicTable(String tableName);
}
