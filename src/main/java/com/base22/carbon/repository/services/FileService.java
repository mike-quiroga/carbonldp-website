package com.base22.carbon.repository.services;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ConfigurationService;
import com.base22.carbon.apps.Application;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.ldp.models.WrapperForLDPNR;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class FileService {

	@Autowired
	private ConfigurationService configurationService;

	static final Logger LOG = LoggerFactory.getLogger(FileService.class);

	public void saveFile(URIObject wrapperURIObject, Application application, MultipartFile file, WrapperForLDPNR wrapper) throws CarbonException {
		String folderPath = configurationService.getApplicationUploadsPath(application);
		String filePath = folderPath.concat("/").concat(wrapper.getFileName());

		boolean folderExists = false;
		try {
			folderExists = folderExists(folderPath);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx saveFile() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< saveFile() > The folder: '{}', couldn't be accessed.", folderPath);
			}
			// TODO: FT
			throw new CarbonException("The upload folder couldn't be accessed.");
		}

		if ( ! folderExists ) {
			boolean folderCreated = false;
			try {
				folderCreated = createFolder(folderPath);
			} catch (Exception e) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx saveFile() > Exception Stacktrace:", e);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< saveFile() > The folder: '{}', couldn't be created.", folderPath);
				}
				// TODO: FT
				throw new CarbonException("The upload folder couldn't be created.");
			}
			if ( ! folderCreated ) {
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< saveFile() > The folder: '{}', couldn't be created.", folderPath);
				}
				// TODO: FT
				throw new CarbonException("The upload folder couldn't be created.");
			}
		}

		try {
			file.transferTo(new File(filePath));
		} catch (IllegalStateException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx saveFile() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< saveFile() > The file: '{}', couldn't be saved.", folderPath);
			}
			throw new CarbonException("");
		} catch (IOException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx saveFile() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< saveFile() > The file: '{}', couldn't be saved.", folderPath);
			}
			throw new CarbonException("The file couldn't be saved.");
		}
	}

	public void deleteFile(URIObject wrapperURIObject, Application application, WrapperForLDPNR wrapper) throws CarbonException {
		String folderPath = configurationService.getApplicationUploadsPath(application);
		String filePath = folderPath.concat("/").concat(wrapper.getFileName());

		File file = new File(filePath);
		boolean fileExists = false;
		try {
			fileExists = file.exists();
		} catch (SecurityException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx deleteFile() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< deleteFile() > The file: '{}', couldn't be accessed.", filePath);
			}
			// TODO: FT
			throw new CarbonException("The file couldn't be accessed.");
		}

		if ( ! fileExists ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< saveFile() > The file: '{}', doesn't exist.", filePath);
			}
			// TODO: FT
			throw new CarbonException("The file doesn't exist.");
		}

		boolean fileDeleted = false;
		try {
			fileDeleted = file.delete();
		} catch (SecurityException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx deleteFile() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< deleteFile() > The file: '{}', couldn't be accessed.", filePath);
			}
			// TODO: FT
			throw new CarbonException("The file couldn't be accessed.");
		}

		if ( ! fileDeleted ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< deleteFile() > The file: '{}', couldn't be deleted.", filePath);
			}
			// TODO: FT
			throw new CarbonException("The file couldn't be deleted.");

		}
	}

	private boolean folderExists(String path) {
		File f = new File(path);
		return f.exists() && f.isDirectory();
	}

	private boolean createFolder(String path) {
		return (new File(path)).mkdirs();
	}
}
