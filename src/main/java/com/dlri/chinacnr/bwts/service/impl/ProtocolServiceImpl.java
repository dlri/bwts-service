package com.dlri.chinacnr.bwts.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
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
	 /**
	  * 从跑合台终端的FTP服务器上生成的TXT文件上传到总服务方法，
	  * 同时向数据库中插入一条从TXT文档中解析后的数据
	  * fileName:文件名称
	  * equtype:设备类型，如run,代表跑合台
	  * equcode:设备编码，如RUN001
	  * tablename:表名
	  */
	public int addRun(String fileName,String equtype,String equcode,String tablename) {
		//String fileName = "G:/2018年工作/1.项目管理/动车组轮对轴承数据传输展示系统/分析过程/三级修/Record/H63-2304_三级_20180303094041.txt";
		// 获取文件后缀名并转化为写，用于后续比较
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
		if(fileType.equals("txt")){
			String sql=readFileByLines(fileName,equtype,equcode,tablename);
			protocolDao.add(sql);
			return 1;
		}else{
			return -1;
		}
		
	}
	
	/**
	 * 拼接SQL入库语句
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 * fileName:文件名称
	  * equtype:设备类型，如run,代表跑合台
	  * equcode:设备编码，如RUN001
	 * 
	 */
	public  String readFileByLines(String fileName,String equtype,String equcode,String tablename) {
		monitorValue="";
		String value = "insert into "+tablename+"  values(0,"; // insert的值
		value += "'"+equtype+"',";//添加设备类型
		value += "'"+equcode+"',";//添加同一类型设备的编码
		File file = new File(fileName);
		String txtName=fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
		if (file.isFile() && file.exists()) {
			BufferedReader reader = null;
			try {
				String strName=txtName.substring(0, txtName.length()-4);
				java.util.Date date = new java.util.Date();
				String path = new SimpleDateFormat("yyyy/MM/").format(date);
				value += "'/"+path+"',";//添加路径
				value += "'"+strName+".pdf',";//添加PDF文件名称
				value += "'"+strName+".bgm',";//添加PDF文件名称
				
				System.out.println("以行为单位读取文件内容，一次读一整行：");
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), "gbk");
				reader = new BufferedReader(read);
				String tempString = null;
				int line = 1;
				// 一次读入一行，直到读入null为文件结束
				while ((tempString = reader.readLine()) != null) {
					String[] arrayStr = null;
					if (line < 3) {
						arrayStr = tempString.split("	");
						for (String element : arrayStr) {
							String[] line1 = element.split("：");
							if(line1[0].equals("检测时间")){
								value +="str_to_date('"+line1[1]+"', '%Y%m%d%H%i%s'),"; 
								monitorValue+="'"+line1[1]+"',";
							}else{
								value += "'" + line1[1] + "',";
								monitorValue+="'"+line1[1]+"',";
							}
						}
					} else if (line > 4) {
						String str = tempString.replace(":", "-");
						arrayStr = str.split("	");
						for (String element : arrayStr) {

							value += "'" + element + "',";
							monitorValue+="'"+element+"',";
						}
						// 合格的数据长度是8，不合格的是9，8的情况要补一列
						if(arrayStr.length==8){
							value += "'',";
							monitorValue+="'',";
						}
					}
					line++;
				}
				read.close();
				reader.close();
				value =value.substring(0,value.length()-1)+");";
				//value += "'"+equtype+"',";//添加设备类型
				//value += "'"+equcode+"',";//添加同一类型设备的编码
				//value += "'"+txtName+"' );";//添加文件名称
				
				monitorValue += "'"+equtype+"',";//添加设备类型
				monitorValue += "'"+equcode+"',";//添加同一类型设备的编码
				monitorValue += "'"+txtName+"'";//添加文件名称
				System.out.println("value is:===" + value);
				//monitorValue=value;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e1) {
					}
				}
			}
		}
		return value;
	}
	public String getMonitorValue() {
		
		return this.monitorValue;
	}
	
}

