package com.gmc.sourdoughtoast.sqlite;

public class Vote {
	private int id;
	private int userid;
	private int feudid;
	private int type;
	private int report;
	
	public static final int FIRST = 1;
	public static final int SECOND = 2;
	public Vote(){}
	
	public Vote(int userid, int feudid, int type, int report) {
		super();
		this.userid = userid;
		this.feudid = feudid;
		this.type = type;
		this.report = report;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userid;
	}
	public void setUserId(int userid) {
		this.userid = userid;
	}
	public int getFeudId() {
		return feudid;
	}
	public void setFeudId(int feudid) {
		this.feudid = feudid;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public int getReport() {
		return report;
	}
	public void setReport(int report) {
		this.report = report;
	}
	
	@Override
	public String toString() {
		return "Vote [id=" + id + ", userid=" + userid + ", feudid=" + feudid + ", type="+ type + ", report=" + report + "]";
	}
	
}
