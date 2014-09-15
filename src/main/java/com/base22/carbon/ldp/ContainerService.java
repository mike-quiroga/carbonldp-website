package com.base22.carbon.ldp;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service("securedContainerService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class ContainerService extends AbstractLDPService {

}
