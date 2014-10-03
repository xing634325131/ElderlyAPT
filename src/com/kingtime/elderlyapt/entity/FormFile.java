package com.kingtime.elderlyapt.entity;

/**
 * ��װ�ϴ��ļ�����
 * 
 * @author xp
 * @created 2014��4��24��
 */
public class FormFile {

	private byte[] data;// �ֽ�������
	private String filename;// �ļ�����
	private String formname;// ����������
	private String contentType = "image/jpeg";// ��������

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
