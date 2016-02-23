package com.carbonldp.jobs;

import com.carbonldp.ldp.AbstractSesameLDPService;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class SesameTriggerService extends AbstractSesameLDPService implements TriggerService {
	TriggerRepository triggerRepository;

	public void executeTrigger( URI triggerURI ) {
		triggerRepository.executeTrigger( triggerURI );

	}

	@Autowired
	public void setTriggerRepository( TriggerRepository triggerRepository ) {
		this.triggerRepository = triggerRepository;
	}
}
