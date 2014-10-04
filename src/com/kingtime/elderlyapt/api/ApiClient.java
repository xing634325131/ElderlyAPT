package com.kingtime.elderlyapt.api;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.json.JSONException;

import android.util.Log;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.entity.ChatRecord;
import com.kingtime.elderlyapt.entity.Duty;
import com.kingtime.elderlyapt.entity.Evaluate;
import com.kingtime.elderlyapt.entity.FormFile;
import com.kingtime.elderlyapt.entity.MyActivity;
import com.kingtime.elderlyapt.entity.Record;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.util.StringUtils;

/**
 * @author xp
 * @created 2014��7��25��
 */
public class ApiClient {
	/**
	 * ����post�����������ϴ��ļ�
	 * 
	 * @param actionURL
	 *            ����·��
	 * @param params
	 *            ��ͨ�ı�����
	 * @param files
	 *            �ϴ��ļ�����
	 * @return ����������
	 * @throws IOException
	 */
	private static InputStream _post(String actionURL, Map<String, String> params, FormFile[] files) throws IOException {
		Log.i("API-POST-RequestURL", actionURL);
		String enterNewLine = "\r\n";
		String fix = "--";
		String boundaryString = "######";
		InputStream inputStream = null;

		URL url = new URL(actionURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		connection.setUseCaches(false);// ��ֹʹ�û���
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundaryString + enterNewLine);
		DataOutputStream ds = new DataOutputStream(connection.getOutputStream());

		Set<String> keySet = params.keySet();
		Iterator<String> iterator = keySet.iterator();

		// ѭ��д����ͨ�ı���
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = params.get(key);
			System.out.println("key:" + key + ",value:" + URLEncoder.encode(value, "UTF-8") + "\n");
			ds.writeBytes(fix + boundaryString + enterNewLine);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + key + "\"" + enterNewLine);
			ds.writeBytes(enterNewLine);
			ds.writeBytes(URLEncoder.encode(value, "UTF-8"));
			ds.writeBytes(enterNewLine);
		}

		// ѭ��д���ϴ��ļ�
		if (files != null && files.length > 0) {
			// System.out.println(files.length);
			for (int i = 0; i < files.length; i++) {
				ds.writeBytes(fix + boundaryString + enterNewLine);
				ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + files[i].getFormname() + "\"" + ";filename=\""
						+ files[i].getFilename() + "\"" + enterNewLine);
				ds.writeBytes(enterNewLine);
				byte[] buffer = files[i].getData();
				ds.write(buffer);
				ds.writeBytes(enterNewLine);
			}
		}
		ds.writeBytes(fix + boundaryString + fix + enterNewLine);
		ds.flush();
		if (connection.getResponseCode() == HttpStatus.SC_OK) {
			inputStream = connection.getInputStream();
		}
		ds.close();
		// System.out.println("close");
		return inputStream;
	}

	/**
	 * ����GET����
	 * 
	 * @param actionURL
	 *            ����·��
	 * @return
	 * @throws IOException
	 */
	private static InputStream _get(String actionURL) throws IOException {
		Log.i("API-GET-RequestURL", actionURL);
		URL url = new URL(actionURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(5000);
		connection.setRequestMethod("GET");
		if (connection.getResponseCode() == HttpStatus.SC_OK) {
			return connection.getInputStream();
		}
		return null;
	}

	/**
	 * ����ͼƬ·����ȡ������ͼƬ
	 * 
	 * @param actionURL
	 *            ����·��
	 * @return
	 * @throws IOException
	 *             �����쳣
	 */
	public static InputStream getPic(String actionURL) throws IOException {

		System.out.println("----------------loadingPic------->");
		URL url = new URL(URLs.Pic_HOST + actionURL);
		System.out.println("PicPath------->" + url.getPath());
		URLConnection connection = url.openConnection();
		connection.connect();
		InputStream inputStream = connection.getInputStream();
		return inputStream;
	}

	/**
	 * ���ݾ���ַ������Ӳ����������ַ
	 * 
	 * @param oldURL
	 *            ����ַ
	 * @param addParameters
	 *            ����Ӳ���
	 * @return ����ַ
	 */
	private static String formCommonNewURL(String oldURL, Map<String, String> addParameters) {
		StringBuilder newURL = new StringBuilder();
		newURL.append(oldURL).append(URLs.SEPARATE);
		Set<String> keySet = addParameters.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = null;
			try {
				value = URLEncoder.encode(addParameters.get(key), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Log.i("FormURL", key + ":" + value);
			newURL.append(key).append("=");
			newURL.append(value).append(URLs.PART);
		}
		newURL.deleteCharAt(newURL.length() - 1);
		return newURL.toString();
	}

	/**
	 * Ϊ����ͼƬ����ͼƬ��ַ
	 * 
	 * @param requestImageCategory
	 *            ����ͼƬ����
	 * @param imageName
	 *            ����ͼƬ����
	 * @return ͼƬ��ַ
	 */
	public static String formImageURL(int requestImageCategory, String imageName) {
		// StringBuilder newImageURL = new StringBuilder();
		// newImageURL.append(URLs.Pic_HOST);
		// if (requestImageCategory == URLs.REQUEST_PHOTO_IMAGE) {
		// newImageURL.append("Photo").append(URLs.URL_SPLITTER);
		// } else if (requestImageCategory == URLs.REQUEST_COMMON_IMAGE) {
		// newImageURL.append("Picture").append(URLs.URL_SPLITTER);
		// }
		// newImageURL.append(imageName);
		// return newImageURL.toString();
		String imageURL = URLs.Pic_HOST;
		Map<String, String> params = new HashMap<String, String>();
		params.put("imageName", imageName);
		if (requestImageCategory == URLs.REQUEST_PHOTO_IMAGE) {
			params.put("requestName", "Photo");
		} else if (requestImageCategory == URLs.REQUEST_COMMON_IMAGE) {
			params.put("requestName", "Picture");
		}
		return formCommonNewURL(imageURL, params);
	}

	/**
	 * ��¼
	 * 
	 * @param username
	 * @param pwd
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static User login(AppContext context, String username, String pwd) throws JSONException, IOException {
		System.out.println("remember-->" + context.getProperty("pwd"));
		if (StringUtils.isEmpty(context.getProperty("pwd")) || !context.getProperty("pwd").toString().equals(pwd.toString())) {// problem
			System.out.println(context.getProperty("pwd") + "<---->" + pwd);
			pwd = StringUtils.toMD5(pwd);
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("pwd", pwd);
		System.out.println("username=" + username + ",pwd=" + pwd + "\n");
		return User.parse(_post(URLs.LOGIN_VALIDATE_HTTP, params, null)).get(0);
	}

	/**
	 * ע�� ����ע���û���Ϣ����ע�����ϵͳ�д��ڵ��û���Ϣʵ��
	 * 
	 * @param user
	 * @return ע����û�
	 * @throws JSONException
	 * @throws IOException
	 */
	public static User register(User user) throws JSONException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		System.out.println("-------->>>>");
		params.put("Name", user.getName());
		params.put("Password", StringUtils.toMD5(user.getPwd()));
		System.out.println("Name-->" + user.getName() + ",Password=" + user.getPwd());
		return User.parse(_post(URLs.REGISTER_HTTP, params, null)).get(0);
	}

	/**
	 * �����������б�
	 * 
	 * @param requestCategory
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static List<MyActivity> getActivities(String requestCategory) throws IOException, JSONException {
		return MyActivity.parse(getActivityStream(requestCategory));
	}

	/**
	 * �������������б�
	 * 
	 * @param requestCategoryId
	 *            �������Id
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static List<MyActivity> getActivities(int requestCategoryId) throws IOException, JSONException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "categoryId");
		params.put("categoryId", String.valueOf(requestCategoryId));
		String actionURL = formCommonNewURL(URLs.ACTIVITIES, params);
		return MyActivity.parse(_get(actionURL));
	}

	/**
	 * �����б�
	 * 
	 * @param requestName
	 *            ��������
	 * @param uid
	 *            �����û����
	 * @return ��б�
	 * @throws IOException
	 * @throws JSONException
	 */
	public static List<MyActivity> getActivitiesByRequest(String requestName, int uid) throws IOException, JSONException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", requestName);
		params.put("uid", String.valueOf(uid));
		String actionURL = formCommonNewURL(URLs.ACTIVITIES, params);
		return MyActivity.parse(_get(actionURL));
	}

	/**
	 * �����б�������
	 * 
	 * @param requestCategoryName
	 *            �����������
	 * @return
	 * @throws IOException
	 */
	public static InputStream getActivityStream(String requestCategoryName) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "categoryName");
		params.put("categoryName", requestCategoryName);
		String actionURL = formCommonNewURL(URLs.ACTIVITIES, params);
		return _get(actionURL);
	}

	/**
	 * ���ݻ��Ż�ȡ�ʵ��
	 * 
	 * @param activityId����
	 * @throws IOException
	 * @throws JSONException
	 * @return �ʵ��
	 */
	public static MyActivity getActivity(int activityId) throws JSONException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "getActivityById");
		params.put("activityId", String.valueOf(activityId));
		String actionURL = formCommonNewURL(URLs.ACTIVITIES, params);
		return MyActivity.parse(_get(actionURL)).get(0);
	}

	/**
	 * ���ݻ��Ż�ȡ���������Ϣ
	 * 
	 * @param activityId
	 *            ����
	 * @return ��������Ϣ
	 * @throws IOException
	 * @throws JSONException
	 */
	public static User getUserByActivityId(int activityId) throws JSONException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("activityId", String.valueOf(activityId));
		String actionURL = formCommonNewURL(URLs.USER_REQUIRE, params);
		return User.parse(_get(actionURL)).get(0);
	}

	/**
	 * ���ݷ����˱�Ż�ȡ���������Ϣ
	 * 
	 * @param uid
	 *            �����˱��
	 * @return ��������Ϣ
	 * @throws JSONException
	 * @throws IOException
	 */
	public static User getUserByUserId(int uid) throws JSONException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", String.valueOf(uid));
		String actionURL = formCommonNewURL(URLs.USER_REQUIRE, params);
		return User.parse(_get(actionURL)).get(0);
	}

	/**
	 * ���ݻ��Ż�ȡ�Ѳ�����Ա
	 * 
	 * @param activityId
	 *            ����
	 * @return ������Ա
	 * @throws JSONException
	 * @throws IOException
	 */
	public static List<Record> getJoinUserList(int activityId) throws JSONException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "getJoinUserList");
		params.put("activityId", String.valueOf(activityId));
		String actionURL = formCommonNewURL(URLs.RECORD_REQUEST, params);
		return Record.parse(_get(actionURL));
	}

	/**
	 * ���ݻ��Ż�ȡ���иûְ��
	 * 
	 * @param activityId
	 *            ����
	 * @return �ְ���б�
	 * @throws JSONException
	 * @throws IOException
	 */
	public static List<Duty> getActivityDutyList(int activityId) throws JSONException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "getActivityDutyList");
		params.put("activityId", String.valueOf(activityId));
		String actionURL = formCommonNewURL(URLs.DUTY_REQUEST, params);
		return Duty.parse(_get(actionURL));
	}

	/**
	 * ȡ���û������
	 * 
	 * @param cancelRecordIdList
	 *            ��¼����б�
	 * @return ���ز������
	 * @throws IOException
	 */
	public static boolean cancelUserJoin(String cancelRecordIdList) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "cancelUserJoin");
		params.put("cancelRecordIdList", cancelRecordIdList);
		String actionURL = formCommonNewURL(URLs.RECORD_REQUEST, params);
		return StringUtils.parse(_get(actionURL)).equals("true") ? true : false;
	}

	/**
	 * �����û���źͻ��Ż�ȡ���û��μӸû״̬
	 * 
	 * @param uid
	 *            �û����
	 * @param activityId
	 *            ����
	 * @return
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static int getUserJoinStateId(int uid, int activityId) throws NumberFormatException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "getUserJoinStateId");
		params.put("uid", String.valueOf(uid));
		params.put("activityId", String.valueOf(activityId));
		String actionURL = formCommonNewURL(URLs.RECORD_REQUEST, params);
		return Integer.valueOf(StringUtils.parse(_get(actionURL)));
	}

	/**
	 * �����û���ţ�ְ���Ŵ����û��ļ�������
	 * 
	 * @param uid
	 *            �û����
	 * @param dutyId
	 *            ְ����
	 * @return �������
	 * @throws IOException
	 */
	public static boolean selectDutyJoin(int uid, int dutyId) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "selectDutyJoin");
		params.put("uid", String.valueOf(uid));
		params.put("dutyId", String.valueOf(dutyId));
		String actionURL = formCommonNewURL(URLs.RECORD_REQUEST, params);
		return StringUtils.parse(_get(actionURL)).equals("true") ? true : false;
	}

	public static boolean uploadActivityImage(int uid, File portrait) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "uploadActivityImage");
		params.put("uid", String.valueOf(uid));
		String actionURL = URLs.IMAGE_UPLOAD;

		System.out.println(portrait.getAbsolutePath());
		FileInputStream fStream = null;
		FormFile ff = null;
		try {
			fStream = new FileInputStream(portrait.getAbsolutePath());
			byte[] buffer = new byte[fStream.available()];
			fStream.read(buffer);
			ff = new FormFile(buffer, portrait.getName(), "picFile", "image/gif");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fStream.close();
		}

		return StringUtils.parse(_post(actionURL, params, new FormFile[] { ff })).equals("true") ? true : false;
	}

	/**
	 * �����
	 * 
	 * @param createActivityJSON
	 *            �����JSON����
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	public static MyActivity createNewActivity(String createActivityJSON) throws JSONException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("createActivityJSON", createActivityJSON);
		params.put("requestName", "createNewActivity");
		return MyActivity.parse(_post(URLs.ACTIVITIES, params, null)).get(0);
	}

	/**
	 * �ϴ��û�ͷ��
	 * 
	 * @param uid
	 *            �û�ID
	 * @param protraitFile
	 *            ͷ���ļ�
	 * @return
	 * @throws IOException
	 */
	public static boolean uploadUserPhoto(int uid, File portrait) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "uploadUserPhoto");
		params.put("uid", String.valueOf(uid));
		String actionURL = URLs.IMAGE_UPLOAD;

		System.out.println(portrait.getAbsolutePath());
		FileInputStream fStream = null;
		FormFile ff = null;
		try {
			fStream = new FileInputStream(portrait.getAbsolutePath());
			byte[] buffer = new byte[fStream.available()];
			fStream.read(buffer);
			ff = new FormFile(buffer, portrait.getName(), "picFile", "image/gif");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fStream.close();
		}

		return StringUtils.parse(_post(actionURL, params, new FormFile[] { ff })).equals("true") ? true : false;
	}

	/**
	 * ���¸�����Ϣ����post�����ύ
	 * 
	 * @param nowUser
	 *            �û�ʵ��
	 * @return ���ظ���ʵ��
	 * @throws JSONException
	 * @throws IOException
	 */
	public static User updateUserInfo(User nowUser) throws JSONException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "updateUserInfo");
		params.put("uid", String.valueOf(nowUser.getUid()));
		params.put("userName", nowUser.getName());
		params.put("gender", nowUser.getGender());
		params.put("phone", nowUser.getPhone());
		params.put("resphone", nowUser.getResPhone());
		params.put("email", nowUser.getEmail());
		params.put("interest", nowUser.getInterest());
		params.put("address", nowUser.getAddress());
		return User.parse(_post(URLs.USER_REQUIRE, params, null)).get(0);
	}

	/**
	 * ���»״̬
	 * 
	 * @param activityId
	 *            ����
	 * @param stateId
	 *            �״̬���
	 * @return
	 * @throws IOException
	 */
	public static boolean updateActivityState(int activityId, int stateId) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "updateActivityState");
		params.put("activityId", String.valueOf(activityId));
		params.put("stateId", String.valueOf(stateId));
		String actionURL = formCommonNewURL(URLs.ACTIVITIES, params);
		return StringUtils.parse(_get(actionURL)).equals("true") ? true : false;
	}

	/**
	 * �û����۷����˷���Ļ
	 * 
	 * @param evaluate
	 *            ����ʵ��
	 * @return �������
	 * @throws IOException
	 */
	public static boolean evaluateActivity(Evaluate evaluate) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "evaluateActivity");
		params.put("uid", String.valueOf(evaluate.getUid()));
		params.put("activityId", String.valueOf(evaluate.getActivityId()));
		params.put("content", evaluate.getContent());
		params.put("credibility", String.valueOf(evaluate.getCredibility()));
		String actionURL = URLs.EVALUATE;
		return StringUtils.parse(_post(actionURL, params, null)).equals("true") ? true : false;
	}

	/**
	 * ��ⴴ�����Ƿ����۹��û
	 * 
	 * @param activityId
	 *            ����
	 * @return ��������״̬
	 * @throws IOException
	 */
	public static boolean getAppraiseState(int activityId) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "getAppraiseState");
		params.put("activityId", String.valueOf(activityId));
		String actionURL = formCommonNewURL(URLs.ACTIVITIES, params);
		return StringUtils.parse(_get(actionURL)).equals("true") ? true : false;
	}

	/**
	 * ����������ۻ������
	 * 
	 * @param appraiseJson
	 *            ���۷�װ����
	 * @param activityId
	 *            ����
	 * @return �������
	 * @throws IOException
	 */
	public static boolean appriaseActivity(String appraiseJson, int activityId) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("appraiseJson", appraiseJson);
		params.put("requestName", "appriaseActivity");
		params.put("activityId", String.valueOf(activityId));
		return StringUtils.parse(_post(URLs.ACTIVITIES, params, null)).equals("true") ? true : false;
	}

	/**
	 * �����
	 * 
	 * @param searchString
	 *            �����ؼ���
	 * @return �����������б�
	 * @throws JSONException
	 * @throws IOException
	 */
	public static List<MyActivity> searchForActivity(String searchString) throws JSONException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "searchForActivity");
		params.put("searchString", searchString);
		String actionURL = formCommonNewURL(URLs.SEARCH, params);
		return MyActivity.parse(_get(actionURL));
	}

	/**
	 * ͨ������Ż�ȡ��ͬ������Ϣ
	 * 
	 * @param uid
	 *            �û����
	 * @param requestCategoryId
	 *            ��Ϣ���
	 * @return ������
	 * @throws IOException
	 */
	public static InputStream getMessageStreamByCategory(int uid, String requestCategory) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "getMessageByCategory");
		params.put("uid", String.valueOf(uid));
		params.put("requestCategory", requestCategory);
		String actionURL = formCommonNewURL(URLs.MESSAGE_CENTER, params);
		return _get(actionURL);
	}

	/**
	 * ���ݻ��Ż�ȡ����ۼ�¼
	 * 
	 * @param activityId
	 *            ����
	 * @return ���ۼ�¼
	 * @throws JSONException
	 * @throws IOException
	 */
	public static List<ChatRecord> getChatRecordsByActivity(int activityId) throws JSONException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "getChatRecords");
		params.put("activityId", String.valueOf(activityId));
		String actionURL = formCommonNewURL(URLs.CHAT_RECORD, params);
		return ChatRecord.parse(_get(actionURL));
	}

	/**
	 * ������Ϣ
	 * @param chatRecord ��Ϣʵ��
	 * @return ���ͽ��
	 * @throws IOException
	 */
	public static boolean sendChatMessage(ChatRecord chatRecord) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "sendChatMessage");
		params.put("activityId", String.valueOf(chatRecord.getActivityId()));
		params.put("uid", String.valueOf(chatRecord.getUid()));
		params.put("content", chatRecord.getContent());
		return StringUtils.parse(_post(URLs.CHAT_RECORD, params, null)).equals("true")?true:false;
	}
	

	public static InputStream getRegionProvince() throws IOException{
		return _get(com.kingtime.freeweather.api.URLs.GET_REGION_PROVINCE);
	}
	
	public static InputStream getSupportCity(int provinceCode) throws IOException{
		String actionURL = com.kingtime.freeweather.api.URLs.GET_SUPPORTCITY + String.valueOf(provinceCode);
		return _get(actionURL);
	}
	
	public static InputStream getWeather(int cityCode) throws IOException{
		String actionURL = com.kingtime.freeweather.api.URLs.GET_WEATHER  + String.valueOf(cityCode);
		return _get(actionURL);
	}
	
}
