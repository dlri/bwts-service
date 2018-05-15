package com.dlri.chinacnr.bwts.service;

import java.util.List;

import com.dlri.chinacnr.bwts.entity.Protocol;

public interface ProtocolService {
	
	public List<Protocol> getAllList();
	
	public int createNewTable(String newTableName,String originalTableName);
	
	public int createDynamicTable(String tableName);
	
	/**
	  * 从跑合台终端的FTP服务器上生成的TXT文件上传到总服务方法，
	  * 同时向数据库中插入一条从TXT文档中解析后的数据
	  * fileName:文件名称
	  * equtype:设备类型，如run,代表跑合台
	  * equcode:设备编码，如RUN001
	*/
	public int addRun(String fileName,String equtype,String equcode);
	
	public String getMonitorValue();

}
