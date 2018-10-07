package com.dlri.chinacnr.bwts.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dlri.chinacnr.bwts.dao.DetectionRecordDao;
import com.dlri.chinacnr.bwts.entity.DetectionRecord;
import com.dlri.chinacnr.bwts.entity.RecordTotal;
import com.dlri.chinacnr.bwts.entity.Statistical;
import com.dlri.chinacnr.bwts.service.DetectionRecordService;
@Service("detectionRecordService")
public class DetectionRecordServiceImpl implements DetectionRecordService {

	@Autowired
	DetectionRecordDao detectionRecordDao;
	//推送到首页面的实时监测数据
	public String monitorValue="";
	public List<DetectionRecord> queryDetectionRecordByCondition(Map<String, Object> map) {
		return detectionRecordDao.queryDetectionRecordByCondition(map);
	}

	

	public int insertCallProcedureRecord(Map<String, Object> map) {
		String strGearboxNog="";
		String strGearboxNop="";
		String strBearingNo="";
		System.out.println("-----------------in-----------");
		int result=-1;
		String fileName=(String)map.get("fileName");
		String equType=(String)map.get("equType");
		String equCode=(String)map.get("equCode");
		Map<String, Object> insertMap=new HashMap<String,Object>();
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
		if(fileType.equals("txt")){
			monitorValue="";
			monitorValue += "'"+equType+"',";//添加设备类型
			monitorValue += "'"+equCode+"',";//添加同一类型设备的编码
			String testHead=""; //检测的前5个头部信息
			String testValue="";//检测的值
			String recordStr="";
			String detailsStr="";
			File file = new File(fileName);
			String txtName=fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
			//monitorValue += "'"+txtName+"'";//添加文件名称
			if (file.isFile() && file.exists()) {
				BufferedReader reader = null;
				try {
					String strName=txtName.substring(0, txtName.length()-4);
					java.util.Date date = new java.util.Date();
					String path = new SimpleDateFormat("yyyy/MM/dd").format(date);
					insertMap.put("savePath", path);
					insertMap.put("pdfFile", strName+".pdf");
					insertMap.put("bgmFile", strName+".bgm");
					System.out.println("以行为单位读取文件内容，一次读一整行：");
					InputStreamReader read = new InputStreamReader(new FileInputStream(file), "gbk");
					reader = new BufferedReader(read);
					String tempString = null;
					int line = 1;
					// 一次读入一行，直到读入null为文件结束
					while ((tempString = reader.readLine()) != null) {
						String[] arrayStr = null;
						if (line < 5) {
							arrayStr = tempString.split("	");
							for (String element : arrayStr) {
								String[] line1 = element.split("：");
								recordStr += line1[1] + ",";
								//testValue+="'"+line1[1]+"',";
							}
						} else if (line > 6) {
							//String str = tempString.replace(":", "-");
							arrayStr = tempString.split("	");
							//判断轴箱编号
							if(line==8){
								strGearboxNog=arrayStr[2];
							}
							if(line==10){
								if(strGearboxNog.equals(arrayStr[2])){
									strBearingNo=arrayStr[2];
								}else{
									strGearboxNop=arrayStr[2];
								}
							}
							if(line==12){
								strGearboxNog=arrayStr[2];
							}
							if(line==14){
								strGearboxNop=arrayStr[2];
							}
							for (String element : arrayStr) {
								//System.out.println(element.+"===============================");
								detailsStr += element + ",";
								testValue+="'"+element+"',";
							}
							// 合格的数据长度是8，不合格的是9，8的情况要补一列
							if(arrayStr.length==8){
								detailsStr += "无,";
								testValue+="'',";
							}
						}
						line++;
					}
					read.close();
					reader.close();
					String[] recordArray =recordStr.split(",");
					if(recordArray.length==10){
						//old-[0轮对编号,1修程,2A侧轴承编号,3B侧轴承编号,4检测时间]
						//new-[0车间,1机位,2设备,3轮对编号,4齿轮箱编号,5修程,6A侧轴承编号,7B侧轴承编号,8检测时间,9检验员,]
						//[B04,R01,齿轮箱跑合台,QWSE34,J43056,三级,12DE34,456321,20171115010248,张三,]
						testHead+="'"+recordArray[3]+"',";
						testHead+="'"+recordArray[5]+"',";
						testHead+="'"+recordArray[6]+"',";
						testHead+="'"+recordArray[7]+"',";
						testHead+="'"+recordArray[8]+"',";
						insertMap.put("wheelId", recordArray[3]);
						insertMap.put("repairRank", recordArray[5]);
						insertMap.put("aBearingNum", recordArray[6]);
						insertMap.put("bBearingNum", recordArray[7]);
						DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
						try {
							insertMap.put("detectionTime", sdf.parse(recordArray[8].toString()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						insertMap.put("channelNum",(line-7));
						insertMap.put("tBedNum", equCode);
						insertMap.put("workShop", recordArray[0]);//车间
						insertMap.put("placement", recordArray[1]);//机位
						insertMap.put("equipment", recordArray[2]);//设备
						insertMap.put("checker", recordArray[9]);//检验员
						insertMap.put("gearboxNum", recordArray[4]);//齿轮箱编号
						insertMap.put("gearboxNog", strGearboxNog);//齿轮箱型号(G)
						insertMap.put("gearboxNop", strGearboxNop);//齿轮箱型号(P)
						insertMap.put("bearingNod", strBearingNo);//轴箱型号
						insertMap.put("detectionData",detailsStr);
						System.out.println(strBearingNo+"==========================================");
						result=detectionRecordDao.insertCallProcedureRecord(insertMap);
						//System.out.println(result+"===="+recordArray[0]+"===="+recordArray[1]+"===="+recordArray[2]+"===="+recordArray[3]+"===="+recordArray[4]);
					}else if(recordArray.length==3){
						System.out.println("=================跳的不正确了！=========================");
						testHead+="'"+recordArray[0]+"',";
						testHead+="'"+recordArray[1]+"',";
						testHead+="'',";
						testHead+="'',";
						testHead+="'"+recordArray[2]+"',";
						insertMap.put("wheelId", recordArray[0]);
						insertMap.put("repairRank", recordArray[1]);
						insertMap.put("aBearingNum", "");
						insertMap.put("bBearingNum", "");
						DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
						try {
							insertMap.put("detectionTime", sdf.parse(recordArray[2].toString()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						insertMap.put("channelNum",(line-5));
						insertMap.put("tBedNum", equCode);
						insertMap.put("detectionData",detailsStr);
						result=detectionRecordDao.insertCallProcedureRecord(insertMap);
					}
					
					monitorValue += "'"+(line-7)+"',";//检测值行数
					monitorValue +=testHead;
					monitorValue+=testValue;
					System.out.println("DetectionRecordServiceImpl is monitorValue: "+monitorValue);
					System.out.println("recordStr is:===" + recordStr+"\n detailsStr is:"+detailsStr);
					
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
			System.out.println(result+"fileType:"+fileType);
			return result;
		}else{
			System.out.println(result+"fileType:"+fileType);
			return result;
		}
	}
	
	public String getMonitorValue() {
		
		return this.monitorValue;
	}



	public RecordTotal queryDetectionRecordTotal(Map<String, Object> map) {
		RecordTotal total=new RecordTotal();
		total=detectionRecordDao.queryDetectionRecordTotal(map);
		return total;
	}



	public List<DetectionRecord> queryRecordByLastTime() {
		return detectionRecordDao.queryRecordByLastTime();
	}



	public List<Statistical> queryStatistialRecordByCondition(Map<String, Object> map) {
		return detectionRecordDao.queryStatistialRecordByCondition(map);
	}

}
