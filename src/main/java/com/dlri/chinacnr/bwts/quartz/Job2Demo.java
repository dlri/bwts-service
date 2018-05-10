package com.dlri.chinacnr.bwts.quartz;

import java.util.Date;

import org.springframework.stereotype.Service;
@Service("job2Demo")
public class Job2Demo {
	public void sayHello(){
		System.out.println(new Date()+"->Hello,进行执行2!");
	}
}
