package com.kingtime.elderlyapt.entity;

/**
 * 封装上传文件属性
 * 
 * @author xp
 * @created 2014年4月24日
 */
public class FormFile {

	private byte[] data;// 字节流数据
	private String filename;// 文件名称
	private String formname;// 表单属性名称
	private String contentType = "image/jpeg";// 内容类型

	public byte[] getData() {
		return data;
	}

	public FormFile(byte[] data, String filename, String formname,
			String contentType) {
		super();
		this.data = data;
		this.filename = filename;
		this.formname = formname;
		if (contentType != null) {
			this.contentType = contentType;
		}
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFormname() {
		return formname;
	}

	public void setFormname(String formname) {
		this.formname = formname;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
