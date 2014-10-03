package com.kingtime.elderlyapt.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author xp
 * @created 2014��4��24��
 */
public class StreamTool {
	/**
	 * ��ȡ����������
	 * 
	 * @param inStream
	 * @return
	 */
	public static byte[] read(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * ������������ָ����ʽΪJSON���ݣ���[{result:1}]�������ؽ������
	 * 
	 * @param inputStream
	 *            ������
	 * @return result�����ݷ������
	 * @throws JSONException
	 */
	public static boolean getResult(InputStream inputStream)
			throws JSONException {
		byte[] data = null;
		try {
			data = StreamTool.read(inputStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String JSON = new String(data);
		System.out.println(JSON);
		JSONArray array = new JSONArray(JSON);
		if (array.length() > 1) {
			return false;
		}
		JSONObject object = array.getJSONObject(0);
		return object.getInt("result") == 1 ? true : false;
	}
}
