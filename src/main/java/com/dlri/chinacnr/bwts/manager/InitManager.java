package com.dlri.chinacnr.bwts.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dlri.chinacnr.bwts.dao.ProtocolDao;
import com.dlri.chinacnr.bwts.entity.Protocol;

public class InitManager {
	@Autowired
	ProtocolDao protocolDao;
	public int createTable(){
		List<Protocol> list=protocolDao.getAllList();
		for(Protocol protocol:list){
			protocol.getFdefines();
		}
		return 1;
	}

}
