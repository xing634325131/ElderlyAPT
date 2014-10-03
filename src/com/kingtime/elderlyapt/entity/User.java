package com.kingtime.elderlyapt.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kingtime.elderlyapt.util.StreamTool;

/**
 * �û�ʵ��
 * 
 * @author xp
 * @created 2014��4��24��
 */
public class User {

	/**
	 * ��ͨ�û���ɫ���
	 */
	public static final int COMMON_USER = 0x01;
	/**
	 * ־Ը�߱��
	 */
	public static final int VOLUNTEER_USER = 0x02;
	/**
	 * ����������
	 */
	public static final int SERVICE_USER = 0x03;
	/**
	 * ��������Ա���
	 */
	public static final int MANAGE_USER = 0x04;
	/**
	 * ������ɫ�û����
	 */
	public static final int OTHER_USER = 0x05;
	/**
	 * �Ա���
	 */
	public static final String GENDER_MALE = "��";
	/**
	 * �Ա�Ů
	 */
	public static final String GENDER_FEMALE = "Ů";
	private static final String[] ROLES = new String[] { "��ͨ�û�", "־Ը��", "�������",
			"��������Ա", "�����û�" };

	private int uid;
	private int roleId;
	private int locationId;
	private String name;
	private String pwd;
	private String gender;
	private String email;
	private String phone;
	private String resPhone;
	private int integral;
	private float credibility;
	private String interest;
	private String photoName;
	private int evaluateTimes;
	private String address;
	private String createTime;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getResPhone() {
		return resPhone;
	}

	public void setResPhone(String resPhone) {
		this.resPhone = resPhone;
	}

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public float getCredibility() {
		return credibility;
	}

	public void setCredibility(float credibility) {
		this.credibility = credibility;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public String getPhotoName() {
		return photoName;
	}

	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}

	public int getEvaluateTimes() {
		return evaluateTimes;
	}

	public void setEvaluateTimes(int evaluateTimes) {
		this.evaluateTimes = evaluateTimes;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCreateTime() {
		if(createTime == null){
			return null;
		}
		return createTime.substring(0, createTime.length() - 2);
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getRole() {
		return ROLES[getRoleId() - 1];
	}

	/**
	 * ����JSON����
	 * 
	 * @param inputStream
	 *            ������
	 * @return �û���Ϣ
	 * @throws JSONException
	 * @throws Exception
	 */
	public static List<User> parse(InputStream inputStream)
			throws JSONException {
		List<User> userList = new ArrayList<User>();
		byte[] data = null;
		try {
			data = StreamTool.read(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String json = new String(data);
		System.out.println(json + "\n");
		JSONArray array = new JSONArray(json);
		for (int i = 0; i < array.length(); i++) {
			User user = new User();
			JSONObject jsonObject = array.getJSONObject(i);
			user.uid = jsonObject.getInt("uid");
			user.roleId = jsonObject.getInt("roleId");
			user.locationId = jsonObject.getInt("locationId");
			user.pwd = jsonObject.getString("pwd");
			user.photoName = jsonObject.getString("photoName");
			user.name = jsonObject.getString("name");
			user.interest = jsonObject.getString("interest");
			user.gender = jsonObject.getString("gender");
			user.email = jsonObject.getString("email");
			user.phone = jsonObject.getString("phone");
			user.resPhone = jsonObject.getString("resPhone");
			user.address = jsonObject.getString("address");
			user.integral = jsonObject.getInt("integral");
			user.evaluateTimes = jsonObject.getInt("evaluateTimes");
			user.credibility = jsonObject.getLong("credibility");
			user.createTime = jsonObject.getString("createTime");
			userList.add(user);
		}
		return userList;
	}

}
