package com.base22.carbon.ldp;

import org.springframework.beans.factory.annotation.Autowired;

import com.base22.carbon.AbstractService;
import com.base22.carbon.authorization.PermissionService;
import com.base22.carbon.ldp.patch.PATCHService;
import com.base22.carbon.repository.services.ModelService;
import com.base22.carbon.repository.services.RepositoryService;
import com.base22.carbon.sparql.SPARQLService;

public abstract class AbstractLDPService extends AbstractService {

	@Autowired
	protected PATCHService patchService;

	@Autowired
	protected SPARQLService sparqlService;

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected RepositoryService repositoryService;

	@Autowired
	protected URIObjectDAO uriObjectDAO;

	@Autowired
	protected PermissionService permissionService;

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	public void setSparqlService(SPARQLService sparqlService) {
		this.sparqlService = sparqlService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public void setPatchService(PATCHService patchService) {
		this.patchService = patchService;
	}

	public void setUriObjectDAO(URIObjectDAO uriObjectDAO) {
		this.uriObjectDAO = uriObjectDAO;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
}
