package com.dlri.chinacnr.bwts.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.dao.ProtocolDao;
import com.dlri.chinacnr.bwts.entity.Protocol;
import com.dlri.chinacnr.bwts.service.ProtocolService;

@Service("protocolService")
public class ProtocolServiceImpl implements ProtocolService {

	@Autowired
	ProtocolDao protocolDao;
	public String monitorValue="";
	
	public List<Protocol> getAllList() {
		return protocolDao.getAllList();
	}
	public int createNewTable(String newTableName, String originalTableName) {
		
		return protocolDao.createNewTable(newTableName, originalTableName);
	}
	
	
	public int createDynamicTable(String tableName) {
		String sql="id bigint(11) NOT NULL AUTO_INCREMENT,";
		List<Protocol> list=protocolDao.getAllList();
		for(Protocol pro:list){
			/*
			 * 为1时，字符串长度为30;
			 * 为2时，字符串长度为100；
			 * 为3时，datetime日期类型；
			 * 为4时，字符串长度为8。
			 */
			if(pro.getFenable().equals("1")){
				sql+=pro.getFposition()+" varchar(30) DEFAULT NULL COMMENT '"+pro.getFdefines()+"',";
			}else if(pro.getFenable().equals("2")){
				sql+=pro.getFposition()+" varchar(100) DEFAULT NULL COMMENT '"+pro.getFdefines()+"',";
			}else if(pro.getFenable().equals("3")){
				sql+=pro.getFposition()+" datetime COMMENT '"+pro.getFdefines()+"',";
			}else if(pro.getFenable().equals("4")){
				sql+=pro.getFposition()+" varchar(8) DEFAULT 0 COMMENT '"+pro.getFdefines()+"',";
			}
		}
		sql+=" PRIMARY KEY (id)";
		System.out.println("创建detection_data检测数据表的sql语句为："+sql);
		return protocolDao.createDynamicTable(tableName,sql);
	}
	
}

