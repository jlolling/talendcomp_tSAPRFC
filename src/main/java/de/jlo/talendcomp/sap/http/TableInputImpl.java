package de.jlo.talendcomp.sap.http;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.jlo.talendcomp.sap.TableInput;

public class TableInputImpl implements TableInput {
	
	private ObjectNode requestNode = null;

	@Override
	public void setTableName(String tableName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addField(String fieldName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFilter(String whereCondition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTableResultFieldDelimiter(String delimiter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMaxRows(Integer maxRows) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRowsToSkip(Integer rowsToSkip) {
		// TODO Auto-generated method stub
		
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
