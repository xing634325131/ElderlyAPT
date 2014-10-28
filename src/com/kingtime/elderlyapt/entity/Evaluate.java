package com.kingtime.elderlyapt.entity;

import com.kingtime.elderlyapt.util.StringUtils;

/**
 * @author xp
 * @created 2014年8月19日
 */
public class Evaluate {

	private int uid;
	private int activityId;
	private String content;
	private float credibility;
	private String createTime;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public float getCredibility() {
		return credibility;
	}
	public void setCredibility(float credibility) {
		this.credibility = credibility;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime() {
		this.createTime = StringUtils.getFormatNowDate();
	}
	
}
