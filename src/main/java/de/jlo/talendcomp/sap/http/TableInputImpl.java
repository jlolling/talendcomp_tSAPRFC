package de.jlo.talendcomp.sap.http;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.jlo.talendcomp.sap.TableInput;

public class TableInputImpl implements TableInput {
	
	private ObjectNode requestNode = null;
	private HttpClient httpClient = null;
	private final static ObjectMapper objectMapper = new ObjectMapper();

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean next() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getCurrentRow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFunctionDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentRawDataEscaped() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTotalRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentRowIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

}
