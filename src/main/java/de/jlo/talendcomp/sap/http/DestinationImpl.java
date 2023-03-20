/**
 * 
 */
package de.jlo.talendcomp.sap.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.jlo.talendcomp.sap.ApplicationServerProperties;
import de.jlo.talendcomp.sap.ConnectionProperties;
import de.jlo.talendcomp.sap.Destination;
import de.jlo.talendcomp.sap.MessageServerProperties;
import de.jlo.talendcomp.sap.TableInput;

/**
 * Implementation of the SAP Destination using an http client
 * 
 * @author jan.lolling@gmail.com
 *
 */
public class DestinationImpl implements Destination {
	
	private HttpClient httpClient = null;
	private ObjectNode destinationNode = null;
	private final static ObjectMapper objectMapper = new ObjectMapper();
	
	public DestinationImpl(HttpClient httpClient, ConnectionProperties cp) throws Exception {
		this.httpClient = httpClient;
		destinationNode = setupDestinationNode(cp);
	}
	
	@Override
	public void ping() throws Exception {
		try {
			ObjectNode requestNode = objectMapper.createObjectNode();
			requestNode.set("destination", destinationNode);
			this.httpClient.ping(requestNode);
		} catch (Exception e) {
			throw new Exception("Connection check (ping) failed: " + e.getMessage(), e);
		}
	}
	
	private ObjectNode setupDestinationNode(ConnectionProperties cp) {
		ObjectNode destNode = objectMapper.createObjectNode();
		if (cp instanceof ApplicationServerProperties) {
			ApplicationServerProperties ap = (ApplicationServerProperties) cp;
			destNode.put("destinationType", "application_server");
			destNode.put("host", ap.getHost());
			destNode.put("client", ap.getClient());
			destNode.put("user", ap.getUser());
			destNode.put("password", ap.getPassword());
			destNode.put("language", ap.getLanguage());
			destNode.put("systemNumber", ap.getSystemNumber());
		} else if (cp instanceof MessageServerProperties) {
			MessageServerProperties mp = (MessageServerProperties) cp;
			destNode.put("destinationType", "message_server");
			destNode.put("host", mp.getHost());
			destNode.put("client", mp.getClient());
			destNode.put("user", mp.getUser());
			destNode.put("password", mp.getPassword());
			destNode.put("language", mp.getLanguage());
			destNode.put("group", mp.getGroup());
			destNode.put("r3name", mp.getR3Name());
		} else {
			throw new IllegalArgumentException("Invalid ConnectionProperties: " + cp);
		}
		return destNode;
	}

	@Override
	public TableInput createTableInput() {
		TableInputImpl in = new TableInputImpl(httpClient, destinationNode);
		return in;
	}

}
