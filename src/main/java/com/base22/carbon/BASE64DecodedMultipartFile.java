package com.base22.carbon;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.entity.ContentType;
import org.springframework.web.multipart.MultipartFile;

public class BASE64DecodedMultipartFile implements MultipartFile {
	private String name;
	private final byte[] fileContent;
	private final ContentType contentType;

	public BASE64DecodedMultipartFile(String name, byte[] fileContent, ContentType contentType) {
		this.name = name;
		this.fileContent = fileContent;
		this.contentType = contentType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getOriginalFilename() {
		return name;
	}

	@Override
	public String getContentType() {
		return this.contentType.getMimeType();
	}

	@Override
	public boolean isEmpty() {
		if ( fileContent == null )
			return true;
		if ( fileContent.length == 0 )
			return true;
		return false;
	}

	@Override
	public long getSize() {
		return fileContent.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return fileContent;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(fileContent);
	}

	@Override
	public void transferTo(File destination) throws IOException, IllegalStateException {
		FileOutputStream fileOutputStream = null;
		fileOutputStream = new FileOutputStream(destination);
		fileOutputStream.write(fileContent);
		fileOutputStream.close();
	}
}
