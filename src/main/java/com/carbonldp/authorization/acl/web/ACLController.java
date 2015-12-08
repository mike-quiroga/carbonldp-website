package com.carbonldp.authorization.acl.web;

import com.carbonldp.ldp.web.AbstractLDPController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping( value = "/**/~acl/" )
public class ACLController extends AbstractLDPController {
}
