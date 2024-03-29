package de.jlo.talendcomp.sap.http;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.MalformedChunkCodingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.jlo.talendcomp.sap.TableInput;

public class TableInputImpl implements TableInput {
	
	private ObjectNode requestNode = null;
	private HttpClient httpClient = null;
	private final static ObjectMapper objectMapper = new ObjectMapper();
	private BufferedReader resultReader = null;
	private boolean useTestMode = false;
	private int countTestRecords = 1000;
	private List<String> currentRow = null;
	private String currentRawLine = null;
	private int currentIndex = -1;
	private int totalRows = 0;
	private int timeout = 10000;

	@Override
	public void setReadTimeout(Integer timeout) {
		if (timeout != null) {
			this.timeout = timeout;
		}
	}

	public boolean isUseTestMode() {
		return useTestMode;
	}

	public void setUseTestMode(boolean useTestMode, int countTestRecords) {
		this.useTestMode = useTestMode;
		this.countTestRecords = countTestRecords;
	}

	public TableInputImpl(HttpClient httpClient, ObjectNode destNode) {
		if (httpClient == null) {
			throw new IllegalArgumentException("http client cannot be null");
		}
		this.httpClient = httpClient;
		if (destNode == null) {
			throw new IllegalArgumentException("destNode cannot be null");
		}
		requestNode = objectMapper.createObjectNode();
		requestNode.set("destination", destNode);
	}

	@Override
	public void setTableName(String tableName) {
		if (tableName == null || tableName.trim().isEmpty()) {
			throw new IllegalArgumentException("Table name cannot null or empty");
		}
		requestNode.put("tableName", tableName);
	}

	@Override
	public String getTableName() {
		return requestNode.get("tableName").asText();
	}

	@Override
	public void addField(String fieldName) {
		ArrayNode fieldsNode = (ArrayNode) requestNode.get("fields");
		if (fieldsNode == null) {
			fieldsNode = objectMapper.createArrayNode();
			requestNode.set("fields", fieldsNode);
		}
		fieldsNode.add(fieldName);
	}

	@Override
	public void setFilter(String filter) {
		if (filter != null && filter.trim().isEmpty() == false) {
			// filter is optional
			requestNode.put("filter", filter);
		}
	}

	public ObjectNode getRequestNode() {
		return requestNode;
	}

	@Override
	public void setMaxRows(Integer maxRows) {
		if (maxRows != null && maxRows > 0) {
			requestNode.put("limit", maxRows);
		}
	}

	@Override
	public void setRowsToSkip(Integer rowsToSkip) {
		if (rowsToSkip != null && rowsToSkip > 0) {
			requestNode.put("offset", rowsToSkip);
		}
	}

	@Override
	public void execute() throws Exception {
		resultReader = httpClient.query(requestNode, useTestMode, countTestRecords, timeout);
		String totalRowStr = httpClient.getResponseHeaderValue("total-rows");
		if (totalRowStr != null && totalRowStr.trim().isEmpty()) {
			try {
				totalRows = Integer.valueOf(totalRowStr.trim());
			} catch (Exception e) {
				throw new Exception("parse total-rows header to integer failed", e);
			}
		}
	}
	
	private String readNextLine() throws Exception {
		String rawline = "executing";
		while (rawline != null && (rawline.trim().isEmpty() || rawline.startsWith("executing"))) {
			try {
				rawline = resultReader.readLine();
			} catch (MalformedChunkCodingException e) {
				if (e.getMessage().contains("CRLF expected at end of chunk")) {
					throw new Exception("No more data from stream available at the end of a chunk. Last read line index: " + currentIndex + " content: " + currentRawLine, e);
				} else {
					throw e;
				}
			}
			if (rawline != null && rawline.startsWith("ERROR")) {
				throw new Exception("Error received: " + rawline);
			}
		}
		return rawline;
	}

	@Override
	public boolean next() throws Exception {
		if (resultReader == null) {
			throw new IllegalStateException("execute is not performed or has got no results!");
		}
		String rawLine = readNextLine();
		if (rawLine != null) {
			String line = null;
			if (rawLine.contains("[") && rawLine.contains("]") == false) {
				// first root element found, we skip this line
				rawLine = resultReader.readLine();
			}
			if (rawLine.contains("]") && rawLine.contains("[") == false) {
				// we found the end
				httpClient.releaseCurrentConnections();
				resultReader.close();
				return false;
			}
			if (rawLine.endsWith(",")) {
				line = rawLine.substring(0, rawLine.length() - 1);
				currentRawLine = rawLine;
			} else if (rawLine.startsWith("[") && rawLine.endsWith("]")) {
				line = rawLine;
				currentRawLine = rawLine;
			}
			if (line != null) {
				if (line.length() == 0) {
					throw new Exception("Received an invalid line: " + rawLine);
				}
				ArrayNode arrayNode = null;
				try {
					arrayNode = (ArrayNode) objectMapper.readTree(line);
				} catch (Throwable e) {
					throw new Exception("Parse result line: " + line + "\nraw-line: " + rawLine + "\nfailed: " + e.getMessage(), e);
				}
				currentRow = new ArrayList<>();
				for (JsonNode node : arrayNode) {
					currentRow.add(node.asText());
				}
				currentIndex++;
				//Thread.sleep(2l);
				return true;
			} else {
				return false;
			}
		} else {
			try {
				resultReader.close();
			} catch (Exception e) {}
			return false;
		}
	}

	@Override
	public List<String> getCurrentRow() {
		return currentRow;
	}

	@Override
	public String getFunctionDescription() {
		return requestNode.toString();
	}

	@Override
	public String getCurrentRawDataEscaped() {
		return currentRawLine;
	}

	@Override
	public int getTotalRowCount() {
		return totalRows;
	}

	@Override
	public int getCurrentRowIndex() {
		return currentIndex;
	}

	@Override
	public void prepare() throws Exception {
	}

}
