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
 * @created 2014年7月25日
 */
public class ApiClient {
	/**
	 * 共用post方法，可以上传文件
	 * 
	 * @param actionURL
	 *            请求路径
	 * @param params
	 *            普通文本集合
	 * @param files
	 *            上传文件数组
	 * @return 请求输入流
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
		connection.setUseCaches(false);// 禁止使用缓存
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundaryString + enterNewLine);
		DataOutputStream ds = new DataOutputStream(connection.getOutputStream());

		Set<String> keySet = params.keySet();
		Iterator<String> iterator = keySet.iterator();

		// 循环写入普通文本域
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

		// 循环写入上传文件
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
	 * 公用GET方法
	 * 
	 * @param actionURL
	 *            请求路径
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
	 * 根据图片路径获取服务器图片
	 * 
	 * @param actionURL
	 *            请求路径
	 * @return
	 * @throws IOException
	 *             网络异常
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
	 * 根据旧网址加新添加参数组成新网址
	 * 
	 * @param oldURL
	 *            旧网址
	 * @param addParameters
	 *            新添加参数
	 * @return 新网址
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
	 * 为请求图片生成图片网址
	 * 
	 * @param requestImageCategory
	 *            请求图片种类
	 * @param imageName
	 *            请求图片名称
	 * @return 图片网址
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
	 * 登录
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
	 * 注册 根据注册用户信息返回注册后在系统中存在的用户信息实例
	 * 
	 * @param user
	 * @return 注册后用户
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
	 * 按活动类别请求活动列表
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
	 * 按活动类别编号请求活动列表
	 * 
	 * @param requestCategoryId
	 *            请求类别Id
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
	 * 请求活动列表
	 * 
	 * @param requestName
	 *            请求名称
	 * @param uid
	 *            请求用户编号
	 * @return 活动列表
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
	 * 请求活动列表输入流
	 * 
	 * @param requestCategoryName
	 *            请求类别名称
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
	 * 根据活动编号获取活动实例
	 * 
	 * @param activityId活动编号
	 * @throws IOException
	 * @throws JSONException
	 * @return 活动实例
	 */
	public static MyActivity getActivity(int activityId) throws JSONException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("requestName", "getActivityById");
		params.put("activityId", String.valueOf(activityId));
		String actionURL = formCommonNewURL(URLs.ACTIVITIES, params);
		return MyActivity.parse(_get(actionURL)).get(0);
	}

	/**
	 * 根据活动编号获取活动发起人信息
	 * 
	 * @param activityId
	 *            活动编号
	 * @return 发起人信息
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
	 * 根据发起人编号获取活动发起人信息
	 * 
	 * @param uid
	 *            发起人编号
	 * @return 发起人信息
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
	 * 根据活动编号获取已参与活动人员
	 * 
	 * @param activityId
	 *            活动编号
	 * @return 参与活动人员
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
	 * 根据活动编号获取所有该活动职责
	 * 
	 * @param activityId
	 *            活动编号
	 * @return 活动职责列表
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
	 * 取消用户活动申请
	 * 
	 * @param cancelRecordIdList
	 *            记录编号列表
	 * @return 返回操作结果
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
	 * 根据用户编号和活动编号获取该用户参加该活动状态
	 * 
	 * @param uid
	 *            用户编号
	 * @param activityId
	 *            活动编号
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
	 * 根据用户编号，职责编号处理用户的加入活动申请
	 * 
	 * @param uid
	 *            用户编号
	 * @param dutyId
	 *            职责编号
	 * @return 操作结果
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
	 * 创建活动
	 * 
	 * @param createActivityJSON
	 *            创建活动JSON数据
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
	 * 上传用户头像
	 * 
	 * @param uid
	 *            用户ID
	 * @param protraitFile
	 *            头像文件
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
	 * 更新个人信息，以post请求提交
	 * 
	 * @param nowUser
	 *            用户实体
	 * @return 返回更新实体
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
	 * 更新活动状态
	 * 
	 * @param activityId
	 *            活动编号
	 * @param stateId
	 *            活动状态编号
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
	 * 用户评价发起人发起的活动
	 * 
	 * @param evaluate
	 *            评价实体
	 * @return 操作结果
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
	 * 检测创建者是否评论过该活动
	 * 
	 * @param activityId
	 *            活动编号
	 * @return 参与评论状态
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
	 * 活动创建者评价活动参与者
	 * 
	 * @param appraiseJson
	 *            评价封装数据
	 * @param activityId
	 *            活动编号
	 * @return 操作结果
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
	 * 搜索活动
	 * 
	 * @param searchString
	 *            搜索关键字
	 * @return 搜索结果：活动列表
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
	 * 通过类别编号获取不同种类消息
	 * 
	 * @param uid
	 *            用户编号
	 * @param requestCategoryId
	 *            消息类别
	 * @return 输入流
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
	 * 根据活动编号获取活动讨论记录
	 * 
	 * @param activityId
	 *            活动编号
	 * @return 讨论记录
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
	 * 发送消息
	 * @param chatRecord 消息实体
	 * @return 发送结果
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
