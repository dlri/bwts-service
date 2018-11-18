package com.dlri.chinacnr.bwts.manager;

import java.util.HashMap;
import java.util.Map;

public class OnlineState {
	public static Map <String,String> map=new HashMap<String,String>();
	//懒汉式单例类.在第一次调用的时候实例化自己 
	    private OnlineState() {}
	    private static OnlineState single=null;
	    //静态工厂方法 
	    public static OnlineState getInstance() {
	         if (single == null) {  
	             single = new OnlineState();
	             map.put("RUN001", "0");
	             map.put("RUN002", "0");
	             map.put("RUN003", "0");
	             map.put("RUN004", "0");
	             map.put("RUN005", "0");
	             map.put("RUN006", "0");
	             map.put("RUN007", "0");
	             map.put("RUN008", "0");
	             map.put("RUN009", "0");
	             map.put("RUN010", "0");
	             map.put("RUN011", "0");
	             map.put("WASH012", "0");
	             map.put("WASH013", "0");
	             map.put("WASH014", "0");
	             map.put("WASH015", "0");
	        //   map.put("RUN012", "0");
		     //  map.put("RUN013", "0");
		     //  map.put("RUN014", "0");
		     //  map.put("RUN015", "0");
		     //  map.put("RUN016", "0");
		     //  map.put("RUN017", "0");
		     //  map.put("RUN018", "0");
		     //   map.put("RUN019", "0");
		     //   map.put("RUN020", "0");
	             System.out.println("设备在线状态个数："+OnlineState.map.size());
	         }  
	        return single;
	    }
}
