package com.base22.carbon.ldp.models;

import java.io.FileInputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.ldp.models.NonRDFSourceClass.Properties;
import com.base22.carbon.ldp.models.NonRDFSourceClass.Resources;
import com.base22.carbon.repository.FileUtil;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class WrapperForLDPNRFactory extends RDFSourceFactory {

	public WrapperForLDPNR create(Resource resource) throws CarbonException {
		RDFSource rdfSource = super.create(resource);
		if ( ! this.isWrapperForLDPNR(rdfSource) ) {
			throw new FactoryException("The resource isn't a WrapperForLDPNR object.");
		}
		return new WrapperForLDPNRImpl(rdfSource.getResource());
	}

	public WrapperForLDPNR create(String wrapperURI, Model model) throws CarbonException {
		RDFSource rdfSource = super.create(wrapperURI, model);
		if ( ! this.isWrapperForLDPNR(rdfSource) ) {
			throw new FactoryException("The resource isn't a WrapperForLDPNR object.");
		}
		return new WrapperForLDPNRImpl(rdfSource.getResource());
	}

	public WrapperForLDPNR create(String wrapperURI, MultipartFile file) throws CarbonException {
		String fileName = UUID.randomUUID().toString();
		return create(wrapperURI, fileName, file);
	}

	public WrapperForLDPNR create(String wrapperURI, String fileName, MultipartFile file) throws CarbonException {
		WrapperForLDPNR wrapper = null;

		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(wrapperURI);

		wrapper = new WrapperForLDPNRImpl(resource);
		wrapper.setType(Resources.WRAPPER.getResource());
		wrapper.setTimestamps();

		String originalFileName = FileUtil.getFileName(file);
		String extension = FileUtil.getFileExtension(file);
		String contentType = file.getContentType();
		long size = file.getSize();

		wrapper.setFileName(fileName);
		wrapper.setOriginalFileName(originalFileName);
		wrapper.setFileExtension(extension);
		wrapper.setContentType(contentType);
		wrapper.setFileSize(size);

		return wrapper;
	}

	public boolean isWrapperForLDPNR(RDFResource ldpResource) {
		return ldpResource.isOfType(Resources.WRAPPER.getURI()) || ldpResource.isOfType(Resources.LDPNR.getURI());
	}

	private class WrapperForLDPNRImpl extends LDPRSourceImpl implements WrapperForLDPNR {
		protected FileInputStream fileInputStream;

		private WrapperForLDPNRImpl(Resource resource) {
			super(resource);
		}

		@Override
		public List<String> getLinkTypes() {
			List<String> types = super.getLinkTypes();
			types.add(NonRDFSourceClass.LINK_TYPE);
			return types;
		}

		@Override
		public String getFileName() {
			if ( ! this.getResource().hasProperty(Properties.FILE_NAME.getProperty()) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(Properties.FILE_NAME.getProperty());
			if ( statement == null ) {
				return null;
			}

			String fileName = null;
			try {
				fileName = statement.getString();
			} catch (Exception ignore) {
				return null;
			}

			return fileName;
		}

		@Override
		public void setFileName(String fileName) {
			if ( this.getResource().hasProperty(Properties.FILE_NAME.getProperty()) ) {
				this.getResource().removeAll(Properties.FILE_NAME.getProperty());
			}
			this.getResource().addProperty(Properties.FILE_NAME.getProperty(), fileName);
		}

		@Override
		public String getOriginalFileName() {
			if ( ! this.getResource().hasProperty(Properties.FILE_ORIGINAL_NAME.getProperty()) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(Properties.FILE_ORIGINAL_NAME.getProperty());
			if ( statement == null ) {
				return null;
			}

			String originalFileName = null;
			try {
				originalFileName = statement.getString();
			} catch (Exception ignore) {
				return null;
			}

			return originalFileName;
		}

		@Override
		public void setOriginalFileName(String originalFileName) {
			if ( this.getResource().hasProperty(Properties.FILE_ORIGINAL_NAME.getProperty()) ) {
				this.getResource().removeAll(Properties.FILE_ORIGINAL_NAME.getProperty());
			}
			this.getResource().addProperty(Properties.FILE_ORIGINAL_NAME.getProperty(), originalFileName);
		}

		@Override
		public String getFileExtension() {
			if ( ! this.getResource().hasProperty(Properties.FILE_EXTENSION.getProperty()) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(Properties.FILE_EXTENSION.getProperty());
			if ( statement == null ) {
				return null;
			}

			String extension = null;
			try {
				extension = statement.getString();
			} catch (Exception ignore) {
				return null;
			}

			return extension;
		}

		@Override
		public void setFileExtension(String extension) {
			if ( this.getResource().hasProperty(Properties.FILE_EXTENSION.getProperty()) ) {
				this.getResource().removeAll(Properties.FILE_EXTENSION.getProperty());
			}
			this.getResource().addProperty(Properties.FILE_EXTENSION.getProperty(), extension);
		}

		@Override
		public String getContentType() {
			if ( ! this.getResource().hasProperty(Properties.FILE_FORMAT.getProperty()) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(Properties.FILE_FORMAT.getProperty());
			if ( statement == null ) {
				return null;
			}

			String format = null;
			try {
				format = statement.getString();
			} catch (Exception ignore) {
				return null;
			}

			return format;
		}

		@Override
		public void setContentType(String contentType) {
			if ( this.getResource().hasProperty(Properties.FILE_FORMAT.getProperty()) ) {
				this.getResource().removeAll(Properties.FILE_FORMAT.getProperty());
			}
			this.getResource().addProperty(Properties.FILE_FORMAT.getProperty(), contentType);
		}

		@Override
		public Long getFileSize() {
			if ( ! this.getResource().hasProperty(Properties.FILE_SIZE.getProperty()) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(Properties.FILE_SIZE.getProperty());
			if ( statement == null ) {
				return null;
			}

			Long size = null;
			try {
				size = statement.getLong();
			} catch (Exception ignore) {
				return null;
			}

			return size;
		}

		@Override
		public void setFileSize(Long size) {
			if ( this.getResource().hasProperty(Properties.FILE_SIZE.getProperty()) ) {
				this.getResource().removeAll(Properties.FILE_SIZE.getProperty());
			}
			this.getResource().addLiteral(Properties.FILE_SIZE.getProperty(), size);
		}

		@Override
		public FileInputStream getFileInputStream() {
			return this.fileInputStream;
		}

		@Override
		public void setFileInputStream(FileInputStream fileInputStream) {
			this.fileInputStream = fileInputStream;
		}
	}
}
