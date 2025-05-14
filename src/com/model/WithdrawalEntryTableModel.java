package com.model;

import javax.swing.table.AbstractTableModel;

import com.util.DatabaseConnection;
import com.util.WithdrawalSummary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WithdrawalEntryTableModel extends AbstractTableModel {
    private final List<Object[]> data = new ArrayList<>();
    private final String[] columnNames = {
            "Withdrawal Entry ID", "Employer ID", "SSN", "Employee ID",
            "Withdrawal Amount", "Additional Dividend", "Withdrawal Date",
            "Withdrawal Date (String)", "Created Date", "Created By",
            "Last Updated Date", "Last Updated By"
    };

    public WithdrawalEntryTableModel() {
        loadData();
    }

    public void loadData() {
        data.clear();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM withdrawal_entry")) {

            while (resultSet.next()) {
                Object[] row = new Object[columnNames.length];
                row[0] = resultSet.getInt("withdrawal_entry_id");
                row[1] = resultSet.getString("employer_id");
                row[2] = resultSet.getString("ssn");
                row[3] = resultSet.getString("employee_id");
                row[4] = resultSet.getString("withdrawal_amount");
                row[5] = resultSet.getString("add_divd");
                row[6] = resultSet.getDate("withdrawal_date");
                row[7] = resultSet.getString("withdrawal_date_str");
                row[8] = resultSet.getTimestamp("created_date");
                row[9] = resultSet.getString("created_by");
                row[10] = resultSet.getTimestamp("last_updated_date");
                row[11] = resultSet.getString("last_updated_by");
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fireTableDataChanged();
    }

    public void addRow(Object[] row) {
        String insertQuery = """
            INSERT INTO withdrawal_entry 
            (employer_id, ssn, employee_id, withdrawal_amount, add_divd, withdrawal_date, withdrawal_date_str, created_date, created_by, last_updated_date, last_updated_by) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, (String) row[1]); // Employer ID
            preparedStatement.setString(2, (String) row[2]); // SSN
            preparedStatement.setString(3, (String) row[3]); // Employee ID
            preparedStatement.setString(4, (String) row[4]); // Withdrawal Amount
            preparedStatement.setString(5, (String) row[5]); // Additional Dividend
            //preparedStatement.setDate(6, row[6] != null ? Date.valueOf((String) row[6]) : null); // Withdrawal Date
            preparedStatement.setDate(6, (Date) row[6] != null ? (Date) row[6] : null);
            preparedStatement.setString(7, (String) row[7]); // Withdrawal Date (String)
            preparedStatement.setTimestamp(8, (Timestamp) row[8]); // Created Date
            preparedStatement.setString(9, (String) row[9]); // Created By
            preparedStatement.setTimestamp(10, (Timestamp) row[10]); // Last Updated Date
            preparedStatement.setString(11, (String) row[11]); // Last Updated By

            preparedStatement.executeUpdate();

            // Retrieve the generated ID
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    row[0] = generatedKeys.getInt(1); // Set the generated ID
                }
            }

            data.add(row); // Add to local data model
            fireTableDataChanged(); // Notify table
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRow(int rowIndex) {
        Object[] row = data.get(rowIndex);
        String query = "DELETE FROM withdrawal_entry WHERE withdrawal_entry_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, (Integer) row[0]);
            preparedStatement.executeUpdate();
            data.remove(rowIndex);
            fireTableDataChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex)[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Make the table read-only for direct edits
    }
    
	public int getColumnIndex(String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (getColumnName(i).equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1; // Return -1 if the column name is not found
	}
	
	public WithdrawalSummary  search(String ssn) {
		WithdrawalSummary withdrawalSummary = new WithdrawalSummary();
        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM withdrawal_entry where ssn = '" + ssn + "'")) {

               if (resultSet.next()) {
            	   withdrawalSummary.setWithdrawalAmount(resultSet.getString("withdrawal_amount"));
            	   withdrawalSummary.setWithdrawalDate(resultSet.getString("withdrawal_date_str"));
            	   withdrawalSummary.setAdditionalDividend(resultSet.getString("add_divd"));
            	   withdrawalSummary.setEmployeeId(resultSet.getString("employee_id"));
              
               } else {
            	   withdrawalSummary = null;
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
        return withdrawalSummary;
	}
	
	public void search(String ssn, String fromDate, String toDate) {
	    // Construct the base query
	    String query = "SELECT * FROM withdrawal_entry WHERE 1=1";

	    // Add conditions based on parameters
	    if (ssn != null && !ssn.isEmpty()) {
	        query += " AND ssn = ?";
	    }
	    if (fromDate != null && !fromDate.isEmpty()) {
	        query += " AND withdrawal_date >= ?";
	    }
	    if (toDate != null && !toDate.isEmpty()) {
	        query += " AND withdrawal_date <= ?";
	    }

	    try (Connection connection = DatabaseConnection.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

	        int paramIndex = 1;

	        // Set parameters
	        if (ssn != null && !ssn.isEmpty()) {
	            preparedStatement.setString(paramIndex++, ssn);
	        }
	        if (fromDate != null && !fromDate.isEmpty()) {
	            preparedStatement.setString(paramIndex++, fromDate);
	        }
	        if (toDate != null && !toDate.isEmpty()) {
	            preparedStatement.setString(paramIndex++, toDate);
	        }

	        try (ResultSet resultSet = preparedStatement.executeQuery()) {
	            // Clear the current data in the table model
	        	data.clear();

	            // Populate the table model with new data
	            while (resultSet.next()) {
	                Object[] row = new Object[getColumnCount()];
	                row[0] = resultSet.getInt("withdrawal_entry_id");
	                row[1] = resultSet.getString("employer_id");
	                row[2] = resultSet.getString("ssn");
	                row[3] = resultSet.getString("employee_id");
	                row[4] = resultSet.getString("withdrawal_amount");
	                row[5] = resultSet.getString("add_divd");
	                row[6] = resultSet.getDate("withdrawal_date");
	                row[7] = resultSet.getString("withdrawal_date_str");
	                row[8] = resultSet.getTimestamp("created_date");
	                row[9] = resultSet.getString("created_by");
	                row[10] = resultSet.getTimestamp("last_updated_date");
	                row[11] = resultSet.getString("last_updated_by");

	                data.add(row);
	            }

	        }
	        fireTableDataChanged();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        //JOptionPane.showMessageDialog(null, "Error while searching withdrawal entries: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
	    }
	}


}

