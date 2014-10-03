package com.kingtime.elderlyapt.service;

import java.util.List;

import org.json.JSONException;

import com.kingtime.elderlyapt.entity.MessagePush;

/**
 * @author xp
 * @created 2014年8月25日
 */
public class MessagePushService {
	private List<MessagePush> activityPushs;
	private List<MessagePush> evaluatePushs;
	private List<MessagePush> coinsPushs;
	public static final int ACTIVITY_CATEGORY = 0x01;
	public static final int EVALUATE_CATEGORY = 0x02;
	public static final int COINS_CATEGORY = 0x03;

	public MessagePushService(String activityMessages, String evaluateMessages, String coinsMessages) {
		try {
			this.evaluatePushs = MessagePush.parse(evaluateMessages);
			this.activityPushs = MessagePush.parse(activityMessages);
			this.coinsPushs = MessagePush.parse(coinsMessages);
		} catch (JSONException e) {
			activityPushs = null;
			evaluatePushs = null;
			coinsPushs = null;
			e.printStackTrace();
		}
	}

	private int checkIsHasNewPush(List<MessagePush> messagePushs) {
		int i = 0;
		for (MessagePush messagePush : messagePushs) {
			if (messagePush.getIsPushed() == 0) {// 未读消息检测
				i++;
			}
		}
		return i;
	}

	public int checkActivityMessage() {
		return checkIsHasNewPush(activityPushs);
	}

	public int checkEvaluateMessage() {
		return checkIsHasNewPush(evaluatePushs);
	}

	public int checkCoinsMessage() {
		return checkIsHasNewPush(coinsPushs);
	}

	public MessagePush getSingleMessage(int singleCategory) {
		MessagePush messagePush = new MessagePush();
		switch (singleCategory) {
		case ACTIVITY_CATEGORY:
			for (MessagePush push : activityPushs) {
				if (push.getIsPushed() == 0) {
					messagePush = push;
					break;
				}
			}
			break;
		case EVALUATE_CATEGORY:
			for (MessagePush push : evaluatePushs) {
				if (push.getIsPushed() == 0) {
					messagePush = push;
					break;
				}
			}
			break;
		case COINS_CATEGORY:
			for (MessagePush push : coinsPushs) {
				if (push.getIsPushed() == 0) {
					messagePush = push;
					break;
				}
			}
			break;
		default:
			break;
		}
		return messagePush;
	}
}
