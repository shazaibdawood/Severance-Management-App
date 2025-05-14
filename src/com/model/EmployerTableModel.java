package com.model;

import javax.swing.table.AbstractTableModel;

import com.util.DatabaseConnection;
import com.util.Employer;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployerTableModel extends AbstractTableModel {
    private List<Object[]> data = new ArrayList<>();
    private final String[] columnNames = {
            "employer_id", "employer_name", "address1", "address2", "attention_line",
            "phone", "alternate_phone", "notes", "contract_effective_date",
            "contract_expiry_date", "status", "created_date", "created_by",
            "last_updated_date", "last_updated_by"
    };
    
    private final String[] displayColumnNames = {
            "Employer ID", "Employer Name", "Address 1", "Address 2",
            "Attention Line", "Phone", "Alternate Phone", "Notes",
            "Contract Effective Date", "Contract Expiry Date", "Status",
            "Created Date", "Created By", "Last Updated Date", "Last Updated By"
    };


    public EmployerTableModel() {
        loadData();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return displayColumnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex){
    	String columnName = getColumnName(columnIndex);
    	if(columnName.contains("Date")) {
    		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    		String dateData = "";
    		if(data.get(rowIndex)[columnIndex] instanceof LocalDateTime) {
	    		dateData = formatter.format((LocalDateTime)data.get(rowIndex)[columnIndex]);
    		} else {
    			dateData = (String) data.get(rowIndex)[columnIndex];
    		}
    		return dateData;

    	} else {
    		return data.get(rowIndex)[columnIndex];
    	}
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data.get(rowIndex)[columnIndex] = aValue;
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0; // employer_id is the primary key, so it should not be editable.
    }

    public void loadData() {
        data.clear();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM employer")) {

            while (resultSet.next()) {
                Object[] row = new Object[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {
                    row[i] = resultSet.getObject(columnNames[i]);
                }
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fireTableDataChanged();
    }

    public void addRow(Object[] row) throws ParseException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO employer VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            for (int i = 0; i < row.length; i++) {
            	
            	String columnName = getColumnName(i);
            	if(columnName.contains("Date")) {
            		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy"); // your template here
            		java.util.Date dateStr = formatter.parse((String) row[i]);
            		java.sql.Date dateDB = new java.sql.Date(dateStr.getTime());
            		preparedStatement.setObject(i + 1, dateDB);
            	} else {
            		preparedStatement.setObject(i + 1, row[i]);
            	}

            }
            preparedStatement.executeUpdate();
            data.add(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fireTableDataChanged();
    }

    public void updateRow(int rowIndex) throws ParseException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE employer SET employer_name=?, address1=?, address2=?, attention_line=?, phone=?, " +
                             "alternate_phone=?, notes=?, contract_effective_date=?, contract_expiry_date=?, status=?, " +
                             "created_date=?, created_by=?, last_updated_date=?, last_updated_by=? WHERE employer_id=?")) {

            Object[] row = data.get(rowIndex);
            for (int i = 1; i < columnNames.length; i++) {
            	String columnName = getColumnName(i);
            	if(columnName.contains("Date")) {
            		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy"); // your template here
            		java.util.Date dateStr = formatter.parse((String) row[i]);
            		java.sql.Date dateDB = new java.sql.Date(dateStr.getTime());
            		preparedStatement.setObject(i, dateDB);
            	} else {
            		preparedStatement.setObject(i, row[i]);
            	}
            }
            preparedStatement.setObject(columnNames.length, row[0]); // employer_id as WHERE condition
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRow(int rowIndex) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM employer WHERE employer_id=?")) {

            preparedStatement.setObject(1, data.get(rowIndex)[0]);
            preparedStatement.executeUpdate();
            data.remove(rowIndex);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fireTableDataChanged();
    }
    
    public void search(String query) {
        data.clear();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM employer WHERE employer_id LIKE ? OR employer_name LIKE ?")) {

            String likeQuery = "%" + query + "%";
            preparedStatement.setString(1, likeQuery);
            preparedStatement.setString(2, likeQuery);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Object[] row = new Object[columnNames.length];
                    for (int i = 0; i < columnNames.length; i++) {
                        row[i] = resultSet.getObject(columnNames[i]);
                    }
                    data.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fireTableDataChanged();
    }

	public int getColumnIndex(String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (getColumnName(i).equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1; // Return -1 if the column name is not found
	}
	
	public HashMap<String, Employer> getAllEmployers() {
		HashMap<String, Employer> mapEmployers = new HashMap<String, Employer>();
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM employer order by employer_name")) {

               try (ResultSet resultSet = preparedStatement.executeQuery()) {
                   while (resultSet.next()) {
                       Employer employer = new Employer();
                       String employerId = resultSet.getString("EMPLOYER_ID");
                       employer.setEmployerId(resultSet.getString("EMPLOYER_ID"));
                       employer.setEmployerName(resultSet.getString("EMPLOYER_NAME"));
                       mapEmployers.put(employerId, employer);
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
		
		
		return mapEmployers;
		
	}

}

