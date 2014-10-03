package com.kingtime.elderlyapt.api;

import java.io.Serializable;

public class URLs implements Serializable {
	public static final String HOST = "http://192.168.199.100:8080/ElderlyAPTServer/servlet/";
	public static final String Pic_HOST = "http://192.168.199.100:8080/ElderlyAPTServer/servlet/RequestImageServlet";
	public static final String URL_SPLITTER = "/";
	/**
	 * �ָ���
	 */
	public static final String SEPARATE = "?";
	/**
	 * �����
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
	 * ����ͷ��ͼƬ
	 */
	public static final int REQUEST_PHOTO_IMAGE = 0x01;
	/**
	 * ������ͨ�ͼƬ
	 */
	public static final int REQUEST_COMMON_IMAGE = 0x02;
}
