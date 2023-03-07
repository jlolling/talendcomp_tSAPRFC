/**
 * 
 */
package de.jlo.talendcomp.sap.http;

import de.jlo.talendcomp.sap.Destination;
import de.jlo.talendcomp.sap.TableInput;

/**
 * @author jan
 *
 */
public class DestinationImpl implements Destination {
	
	private HttpClient httpClient = null;
	
	public DestinationImpl(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public TableInput createTableInput() {
		// TODO Auto-generated method stub
		return null;
	}

}
