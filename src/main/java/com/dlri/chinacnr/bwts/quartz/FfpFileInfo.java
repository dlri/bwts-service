package com.dlri.chinacnr.bwts.quartz;

import java.sql.Timestamp;
import java.util.Calendar;

public class FfpFileInfo {
	private String name;
	private long size;
	private Calendar timestamp;
	private boolean type;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public Calendar getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}
	public boolean isType() {
		return type;
	}
	public void setType(boolean type) {
		this.type = type;
	}
	
}
