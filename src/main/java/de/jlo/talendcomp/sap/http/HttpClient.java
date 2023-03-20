package de.jlo.talendcomp.sap.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HttpClient {
	
	private static final Logger LOG = LogManager.getLogger(HttpClient.class);
	private int statusCode = 0;
	private String statusMessage = null;
	private int maxRetriesInCaseOfErrors = 0;
	private int currentAttempt = 0;
	private long waitMillisAfterError = 1000l;
	private CloseableHttpClient closableHttpClient = null;
	private HttpClientContext context = null;
	private BufferedReader responseContentReader = null;
	private CloseableHttpResponse httpResponse = null;
	private String baseUrl = null;
	private Header[] responseHeader = null;
	private final static ObjectMapper objectMapper = new ObjectMapper();
	
	public HttpClient(String baseUrl, String user, String password, int timeout, int socketTimeout) throws Exception {
		if (baseUrl == null || baseUrl.trim().isEmpty()) {
			throw new Exception("baseUrl cannot be null or empty");
		}
		if (baseUrl.endsWith("/") == false) {
			baseUrl = baseUrl + "/";
		}
		this.baseUrl = baseUrl;
		closableHttpClient = createCloseableClient(baseUrl, user, password, timeout, socketTimeout);
	}
	
	private HttpEntity buildEntity(JsonNode node) throws UnsupportedEncodingException {
		if (node != null && node.isNull() == false && node.isMissingNode() == false) {
			HttpEntity entity = new StringEntity(node.toString(), "UTF-8");
			return entity;
		} else {
			return null;
		}
	}
	
	private BufferedReader execute(HttpPost request, boolean expectResponse) throws Exception {
		currentAttempt = 0;
		for (currentAttempt = 0; currentAttempt <= maxRetriesInCaseOfErrors; currentAttempt++) {
			if (Thread.currentThread().isInterrupted()) {
				break;
			}
            try {
            	if (context != null) {
                	httpResponse = closableHttpClient.execute(request, context);
            	} else {
                	httpResponse = closableHttpClient.execute(request);
            	}
            	statusCode = httpResponse.getStatusLine().getStatusCode();
        		HttpEntity entity = httpResponse.getEntity();
        		responseHeader = httpResponse.getAllHeaders();
            	if (expectResponse && statusCode == 200) {
            		Header encodingHeader = entity.getContentEncoding();
            		String encoding = (encodingHeader != null ? encodingHeader.getValue() : "UTF-8");
            		responseContentReader = new BufferedReader(new InputStreamReader(entity.getContent(), encoding));
            	}
            	if (statusCode > 300) {
            		String rawErrorMessage = EntityUtils.toString(entity);
            		String actualErrorMessage = rawErrorMessage;
            		Header contentType = httpResponse.getFirstHeader("Content-Type");
            		if (contentType != null) {
            			if (contentType.getValue() != null) {
            				if (contentType.getValue().contains("html")) {
            					actualErrorMessage = extractMessageFromHtml(rawErrorMessage);
            				} else if (contentType.getValue().contains("json")) {
            					actualErrorMessage = extractMessageFromJson(rawErrorMessage);
            				}
            			}
            		}
            		throw new Exception("Code: " + statusCode + ", message: " + actualErrorMessage);
            	}
            	break;
            } catch (Throwable e) {
            	if (currentAttempt < maxRetriesInCaseOfErrors) {
                	// this can happen, we try it again
                	LOG.warn("POST request: " + request.getURI() + " failed (" + (currentAttempt + 1) + ". attempt, " + (maxRetriesInCaseOfErrors - currentAttempt) + " retries left). \n   Payload: " + EntityUtils.toString(request.getEntity()) + "\n   Waiting " + waitMillisAfterError + "ms and retry request.", e);
                	Thread.sleep(waitMillisAfterError);
            	} else {
                	throw new Exception(e.getMessage() + "\n   No retry left, max: " + maxRetriesInCaseOfErrors + ".\n   URL: " + request.getURI() + "\n   Payload: " + EntityUtils.toString(request.getEntity()), e);
            	}
            }
		} // for
        return responseContentReader;
	}
	
	public String getResponseHeaderValue(String headerName) {
		if (responseHeader != null) {
			return null;
		}
		for (Header h : responseHeader) {
			if (h.getName().equalsIgnoreCase(headerName)) {
				return h.getValue();
			}
		}
		return null;
	}

	public BufferedReader getResponseContentReader() {
		if (responseContentReader == null) {
			throw new IllegalStateException("response content reader is null. No query was executed before or query has been failed");
		}
		return responseContentReader;
	}
	
	public BufferedReader query(JsonNode requestNode, boolean useTestMode, int countTestRecords, int timeout) throws Exception {
		String path = "tableinput";
		if (useTestMode) {
			path = path + "?testrows=" + countTestRecords;
		}
		return post(path, requestNode, true, timeout);
	}

	public void sapping(JsonNode destinationNode) throws Exception {
		post("sap-ping", destinationNode, false, 5000);
	}
	
	private BufferedReader post(String path, JsonNode payload, boolean expectResponse, int timeout) throws Exception {
		String url = baseUrl + path;
		if (LOG.isDebugEnabled()) {
			LOG.debug("POST " + url + " body: " + payload.toString());
		}
        HttpPost request = new HttpPost(url);
        RequestConfig config = request.getConfig();
        if (config == null) {
        	config = RequestConfig.custom().setSocketTimeout(timeout * 1000).build();
        	request.setConfig(config);
        }
        request.addHeader("Connection", "Keep-Alive");
        request.addHeader("Keep-Alive", "timeout=5, max=0");
        if (payload != null) {
            request.setEntity(buildEntity(payload));
            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json;charset=UTF-8");
        }
        return execute(request, expectResponse);
	}

	private CloseableHttpClient createCloseableClient(String urlStr, String user, String password, int timeout, int socketTimeout) throws Exception {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        if (closableHttpClient == null) {
            if (user != null && user.trim().isEmpty() == false) {
        		URL url = new URL(urlStr);
                credsProvider.setCredentials(
                        AuthScope.ANY,
                        new UsernamePasswordCredentials(user, password));
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(socketTimeout + 100)
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout)
                        .setRedirectsEnabled(true)
                        .setRelativeRedirectsAllowed(false)
                        .setAuthenticationEnabled(true)
                        .build();
                AuthCache authCache = new BasicAuthCache();
                HttpHost httpHost = new HttpHost(url.getHost(), url.getPort());
                authCache.put(httpHost, new BasicScheme());
                context = HttpClientContext.create();
                context.setCredentialsProvider(credsProvider);
                context.setAuthCache(authCache);
                CloseableHttpClient client = HttpClients.custom()
                        .setDefaultCredentialsProvider(credsProvider)
                        .setDefaultRequestConfig(requestConfig)
                        .build();
            	closableHttpClient = client;
                return client;
            } else {
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(socketTimeout + 100)
                        .setConnectTimeout(timeout)
                        .setConnectionRequestTimeout(timeout)
                        .setRedirectsEnabled(true)
                        .setRelativeRedirectsAllowed(true)
                        .build();
                CloseableHttpClient client = HttpClients.custom()
                        .setDefaultRequestConfig(requestConfig)
                        .build();
            	closableHttpClient = client;
                return client;
            }
        } else {
        	return closableHttpClient;
        }
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public int getMaxRetriesInCaseOfErrors() {
		return maxRetriesInCaseOfErrors;
	}

	public void setMaxRetriesInCaseOfErrors(Integer maxRetriesInCaseOfErrors) {
		if (maxRetriesInCaseOfErrors != null) {
			this.maxRetriesInCaseOfErrors = maxRetriesInCaseOfErrors;
		}
	}

	public int getCurrentAttempt() {
		return currentAttempt;
	}

	public long getWaitMillisAfterError() {
		return waitMillisAfterError;
	}

	public void setWaitMillisAfterError(Long waitMillisAfterError) {
		if (waitMillisAfterError != null) {
			this.waitMillisAfterError = waitMillisAfterError;
		}
	}

	public void close() {
		if (responseContentReader != null) {
			try {
				responseContentReader.close();
				responseContentReader = null;
			} catch (Exception e) {
				// ignore
			}
		}
		if (httpResponse != null) {
	    	try {
	        	httpResponse.close();
	        	httpResponse = null;
	    	} catch (Exception ce) {
	    		// ignore
	    	}
		}
		if (closableHttpClient != null) {
			try {
				closableHttpClient.close();
				closableHttpClient = null;
			} catch (IOException e) {
				// ignore
			}
		}
	}

	public CloseableHttpClient getClosableHttpClient() {
		return closableHttpClient;
	}

	public void setClosableHttpClient(CloseableHttpClient closableHttpClient) {
		this.closableHttpClient = closableHttpClient;
	}
	
	public static String extractMessageFromHtml(String htmlResponse) {
		String start = "<th>MESSAGE:</th><td>";
		String stop = "</td></tr>";
		int p1 = htmlResponse.indexOf(start);
		if (p1 > 0) {
			p1 = p1 + start.length();
			int p2 = htmlResponse.indexOf(stop, p1);
			if (p2 > 0) {
				String message = htmlResponse.substring(p1, p2);
				System.out.println(message);
				return StringEscapeUtils.escapeHtml4(message);
			}
		}
		return htmlResponse;
	}
	
	public static String extractMessageFromJson(String jsonMessage) {
		try {
			ObjectNode doc = (ObjectNode) objectMapper.readTree(jsonMessage);
			JsonNode mn = doc.get("message");
			if (mn != null) {
				return StringEscapeUtils.unescapeXml(mn.asText());
			} else {
				return jsonMessage;
			}
		} catch (IOException e) {
			return jsonMessage;
		}
	}

}
