package de.jlo.talendcomp.sap.http;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

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
			requestNode.put("offset", maxRows);
		}
	}

	@Override
	public void setRowsToSkip(Integer rowsToSkip) {
		if (rowsToSkip != null && rowsToSkip > 0) {
			requestNode.put("limit", rowsToSkip);
		}
	}

	@Override
	public void execute() throws Exception {
		resultReader = httpClient.query(requestNode, useTestMode, countTestRecords);
		String totalRowStr = httpClient.getResponseHeaderValue("total-rows");
		if (totalRowStr != null && totalRowStr.trim().isEmpty()) {
			try {
				totalRows = Integer.valueOf(totalRowStr.trim());
			} catch (Exception e) {
				throw new Exception("parse total-rows header to integer failed", e);
			}
		}
	}

	@Override
	public boolean next() throws Exception {
		if (resultReader == null) {
			throw new IllegalStateException("execute is not performed or has got no results!");
		}
		String line = resultReader.readLine();
		if (line != null) {
			line = line.trim();
			if (line.contains("[") && line.contains("]") == false) {
				// first root element found, we skip this line
				line = resultReader.readLine();
			}
			if (line.contains("]") && line.contains("[") == false) {
				// we found the end
				resultReader.close();
				return false;
			}
			if (line.endsWith(",")) {
				line = line.substring(line.length() - 1);
				currentRawLine = line;
				currentIndex++;
			}
			ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(line);
			currentRow = new ArrayList<>();
			for (JsonNode node : arrayNode) {
				currentRow.add(node.asText());
			}
			return true;
		} else {
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
		// will be implemented later
	}

}
