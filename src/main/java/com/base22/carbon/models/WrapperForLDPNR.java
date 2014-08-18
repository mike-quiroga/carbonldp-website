package com.base22.carbon.models;

import java.io.FileInputStream;

public interface WrapperForLDPNR extends LDPRSource {

	public String getFileName();

	public void setFileName(String fileName);

	public String getOriginalFileName();

	public void setOriginalFileName(String originalFileName);

	public String getFileExtension();

	public void setFileExtension(String extension);

	public String getContentType();

	public void setContentType(String contentType);

	public Long getFileSize();

	public void setFileSize(Long size);

	public FileInputStream getFileInputStream();

	public void setFileInputStream(FileInputStream fileInputStream);
}
