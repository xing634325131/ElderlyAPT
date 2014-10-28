package com.kingtime.elderlyapt.api;

import java.io.Serializable;

public class URLs implements Serializable {
	public static final String HOST = "http://ftpxing.jsp103.jzidc88.com/servlet/";
	public static final String Pic_HOST = "http://ftpxing.jsp103.jzidc88.com/servlet/RequestImageServlet";
	public static final String URL_SPLITTER = "/";
	/**
	 * 分隔符
	 */
	public static final String SEPARATE = "?";
	/**
	 * 分离符
	 */
	public static final String PART = "&";
	
	public static final String REGISTER = HOST + "RegisterServlet";
	public static final String PHOTO_DEFAULT = Pic_HOST + "Photo/default.jpg";
	
	public static final String LOGIN_VALIDATE_HTTP = HOST + "LoginServlet";
	public static final String REGISTER_HTTP = HOST + "RegisterServlet";
	public static final String ACTIVITIES = HOST + "ActivityServlet";
	public static final String USER_REQUIRE = HOST + "UserServlet";
	public static final String DUTY_REQUEST = HOST + "DutyServlet";
	public static final String RECORD_REQUEST = HOST + "RecordServlet";
	public static final String IMAGE_UPLOAD = HOST + "UploadImageServlet";
	public static final String EVALUATE = HOST + "EvaluateServlet";
	public static final String SEARCH = HOST + "SearchServlet";
	public static final String MESSAGE_CENTER = HOST + "MessagePushServlet";
	public static final String CHAT_RECORD = HOST + "ChatRecordServlet";
	
	/**
	 * 请求头像图片
	 */
	public static final int REQUEST_PHOTO_IMAGE = 0x01;
	/**
	 * 请求普通活动图片
	 */
	public static final int REQUEST_COMMON_IMAGE = 0x02;
}
