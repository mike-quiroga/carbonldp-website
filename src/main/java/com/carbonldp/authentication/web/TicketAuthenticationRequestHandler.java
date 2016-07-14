package com.carbonldp.authentication.web;

import com.carbonldp.authentication.Ticket;
import com.carbonldp.authentication.TicketFactory;
import com.carbonldp.authentication.ticket.TicketService;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFBlankNode;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * @author NestorVenegas
 * @since 0.36.0
 */

@RequestHandler
public class TicketAuthenticationRequestHandler extends AbstractLDPRequestHandler {

	@Autowired
	TicketService ticketService;

	@Transactional
	public ResponseEntity<Object> handleRequest( RDFDocument document, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );
		IRI forIRI = getForIRI( document );
		Ticket token = ticketService.createTicket( forIRI );

		return new ResponseEntity<>( token, HttpStatus.OK );

	}

	private IRI getForIRI( RDFDocument document ) {
		Set<RDFBlankNode> blankNodes = document.getBlankNodes();
		if ( blankNodes.size() != 1 ) throw new InvalidResourceException( new Infraction( 0x2012 ) );
		RDFBlankNode blankNode = blankNodes.iterator().next();
		Ticket ticket = new Ticket( blankNode, blankNode.getSubject() );
		List<Infraction> infractions = TicketFactory.getInstance().validate( ticket );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
		return ticket.getForIRI();
	}
}
