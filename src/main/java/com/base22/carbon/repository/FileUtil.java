package com.base22.carbon.repository;

import org.springframework.web.multipart.MultipartFile;

public abstract class FileUtil {

	public static String getFileName(MultipartFile file) {
		String fileName = file.getOriginalFilename();

		int dotIndex = fileName.lastIndexOf(".");
		if ( dotIndex != - 1 ) {
			fileName = fileName.substring(0, dotIndex);
		}

		return fileName;
	}

	public static String getFileExtension(MultipartFile file) {
		String extension = null;

		String fileName = file.getOriginalFilename();

		String[] dotIndex = fileName.split("\\.");

		// Check if the name of the file contains an extension
		if ( dotIndex.length <= 1 ) {
			// It doesn't
			extension = "";
		} else {
			// It does
			extension = dotIndex[dotIndex.length - 1];
		}

		return extension;
	}

}
