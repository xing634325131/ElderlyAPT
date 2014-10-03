package com.kingtime.elderlyapt.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kingtime.elderlyapt.util.StringUtils;

/**
 * 消息推送实体
 * 
 * @author xp
 * 
 * @created 2014-8-22
 */
public class MessagePush {

	/**
	 * 活动参与推送[REMARK:activityId]
	 */
	public static final int CATEGORY_JOIN = 0x01;
	/**
	 * 活动状态[REMARK:activityId]
	 */
	public static final int CATEGORY_STATE = 0x02;
	/**
	 * 积分变化[REMARK:uid,& unnecessary]
	 */
	public static final int CATEGORY_INTEGRAL = 0x03;
	/**
	 * 系统消息
	 */
	public static final int CATEGORY_SYSTEM = 0x04;
	/**
	 * 用户消息，暂定于给创建者发用户参与消息[REMARK:activityId]
	 */
	public static final int CATEGORY_USER = 0x05;
	
	/**
	 * 活动待审核[REMARK:activityId]
	 */
	public static final int CATEGORY_VERIFY = 0x06;
	
	/**
	 * 活动评论状况[REMARK:evaluateContent,& part]
	 */
	public static final int CATEGORY_CREDIBILITY = 0x07;

	private int messageId;
	private int toPushUserId;
	private String pushUserName;
	private String createTime;
	private String pushContent;
	private int pushCategoryId;
	private int isPushed;
	private String remark;

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getToPushUserId() {
		return toPushUserId;
	}

	public void setToPushUserId(int toPushUserId) {
		this.toPushUserId = toPushUserId;
	}

	public String getPushUserName() {
		return pushUserName;
	}

	public void setPushUserName(String pushUserName) {
		this.pushUserName = pushUserName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getPushContent() {
		return pushContent;
	}

	public void setPushContent(String pushContent) {
		this.pushContent = pushContent;
	}

	public int getPushCategoryId() {
		return pushCategoryId;
	}

	public void setPushCategory(int pushCategoryId) {
		this.pushCategoryId = pushCategoryId;
	}

	public int getIsPushed() {
		return isPushed;
	}

	public void setIsPushed(int isPushed) {
		this.isPushed = isPushed;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	/*
	 * 生成JSON数据
	 */
	public String toString() {
		List<MessagePush> pushList = new ArrayList<MessagePush>();
		pushList.add(this);
		return toString(pushList);
	}
	
	/**
	 * 解析JSON数据
	 * 
	 * @param stream
	 *            输入流
	 * @return 消息信息
	 * @throws JSONException
	 */
	public static List<MessagePush> parse(String content) throws JSONException {
		List<MessagePush> messagePushs = new ArrayList<MessagePush>();
		if(StringUtils.isEmpty(content)){
			System.out.println("Json is empty!");
			return messagePushs;
		}
		System.out.println(content + "\n");
		JSONArray array = new JSONArray(content);
		for (int i = 0; i < array.length(); i++) {
			MessagePush messagePush = new MessagePush();
			JSONObject jsonObject = array.getJSONObject(i);
			messagePush.messageId = jsonObject.getInt("messageId");
			messagePush.pushCategoryId = jsonObject.getInt("pushCategoryId");
			messagePush.pushContent = jsonObject.getString("pushContent");
			messagePush.pushUserName = jsonObject.getString("pushUserName");
			messagePush.isPushed = jsonObject.getInt("isPushed");
			messagePush.toPushUserId = jsonObject.getInt("toPushUserId");
			messagePush.createTime = jsonObject.getString("createTime");
			messagePush.remark = jsonObject.getString("remark");
			messagePushs.add(messagePush);
		}
		return messagePushs;
	}

	/**
	 * 由MessagePush实体生成JSON数据
	 * 
	 * @param pushList
	 * @return
	 */
	public static String toString(List<MessagePush> pushList) {
		if (pushList.size() < 1) {
			return null;
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		for (MessagePush messagePush : pushList) {
			stringBuilder.append("{");
			stringBuilder.append("messageId:").append(messagePush.getMessageId()).append(",");
			stringBuilder.append("toPushUserId:").append(messagePush.getToPushUserId()).append(",");
			stringBuilder.append("pushUserName:\"").append(messagePush.getPushUserName()).append("\",");
			stringBuilder.append("pushContent:\"").append(messagePush.getPushContent()).append("\",");
			stringBuilder.append("createTime:\"").append(messagePush.getCreateTime()).append("\",");
			stringBuilder.append("pushCategoryId:").append(messagePush.getPushCategoryId()).append(",");
			stringBuilder.append("isPushed:").append(messagePush.getIsPushed()).append(",");
			stringBuilder.append("remark:\"").append(messagePush.getRemark()).append("\",");
			stringBuilder.append("},");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}
