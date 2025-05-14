package com.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import com.util.DatabaseConnection;

public class ContributionEntryDetailTableModel extends AbstractTableModel {
    private final List<Object[]> data = new ArrayList<>();
    private final List<Object[]> newRows = new ArrayList<>();

    private String employerId; // Employer ID fetched from database
    private String periodMonth;
    private String periodYear;
    private final String[] columnNames = {
            "Detail ID", "Employer ID", "Employee ID", "SSN",
            "Period Month", "Period Year", "Share Amount",
            "Reserve Amount", "Contribution ID",
            "Created Date", "Created By",
            "Last Updated Date", "Last Updated By",  
            "Employer Name",  "Employee Name"
    };

    private final int contributionId; // Foreign key to filter data

    public ContributionEntryDetailTableModel(int contributionId) {
        this.contributionId = contributionId;
        fetchEmployerAndPeriods(); // Fetch employerId from database
        loadData();

    }
    
    private void fetchEmployerAndPeriods() {
        String query = "SELECT employer_id, period_month, period_year FROM contribution_entry WHERE contribution_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, contributionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.employerId = resultSet.getString("employer_id");
                    this.periodMonth = resultSet.getString("period_month");
                    this.periodYear = resultSet.getString("period_year");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        data.clear();
        String query = "SELECT entry_detail.*, er.employer_name,"
        		+ "concat(ee.first_name,\" \", ee.last_name) as employee_name "
        				+ "FROM contribution_entry_detail entry_detail, employer er, employee ee "
        				+ "WHERE contribution_entry_id = ? "
        				+ "and entry_detail.employer_id = er.employer_id  and entry_detail.ssn = ee.ssn";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, contributionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Object[] row = new Object[columnNames.length];
                    row[0] = resultSet.getInt("contribution_entry_detail_id");
                    row[1] = resultSet.getString("employer_id");
                    row[2] = resultSet.getString("employee_id");
                    row[3] = resultSet.getString("ssn");
                    row[4] = resultSet.getString("period_month");
                    row[5] = resultSet.getString("period_year");
                    row[6] = resultSet.getString("share_amount");
                    row[7] = resultSet.getString("reserve_amount");
                    row[8] = resultSet.getString("contribution_entry_id");
                    row[9] = resultSet.getTimestamp("created_date");
                    row[10] = resultSet.getString("created_by");
                    row[11] = resultSet.getTimestamp("last_updated_date");
                    row[12] = resultSet.getString("last_updated_by");
                    row[13] = resultSet.getString("employer_name");
                    row[14] = resultSet.getString("employee_name");
                    data.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fireTableDataChanged();
    }
    
    public void saveDetailEntry(List<Object[]> data) {
        // Insert new rows
    	String insertQuery = "INSERT INTO contribution_entry_detail (contribution_entry_detail_id, employer_id, employee_id, ssn, period_month, period_year, share_amount, reserve_amount, contribution_entry_id, created_date, created_by, last_updated_date, last_updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
        		PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            for (Object[] row : data) {
                insertStatement.setInt(1, (Integer) row[0]);         // Contribution Entry Detail ID
                insertStatement.setString(2, (String) row[1]);       // Employer ID
                insertStatement.setString(3, String.valueOf(row[2]));         // Employee ID
                insertStatement.setString(4, (String) row[3]);       // SSN
                insertStatement.setString(5, (String) row[4]);       // Period Month
                insertStatement.setString(6, (String) row[5]);       // Period Year
                insertStatement.setString(7, (String) row[6]);       // Share Amount
                insertStatement.setString(8, (String) row[7]);       // Reserve Amount
                insertStatement.setString(9, String.valueOf(row[8]));         // Contribution Entry ID
                insertStatement.setTimestamp(10, (Timestamp) row[9]); // Created Date
                insertStatement.setString(11, (String) row[10]);     // Created By
                insertStatement.setTimestamp(12, (Timestamp) row[11]);  // Last Updated Date
                insertStatement.setString(13, (String) row[12]);     // Last Updated By
                insertStatement.executeUpdate();
            }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saving changes: " + e.getMessage());
    }

    // Refresh the table data after saving
    fireTableDataChanged();
   	
    }

    public void saveChanges() {
    	String updateQuery = "UPDATE contribution_entry_detail SET employer_id = ?, employee_id = ?, ssn = ?, period_month = ?, period_year = ?, share_amount = ?, reserve_amount = ?, created_date = ?, created_by = ?, last_updated_date = ?, last_updated_by = ? WHERE contribution_entry_detail_id = ?";
        String insertQuery = "INSERT INTO contribution_entry_detail (contribution_entry_detail_id, employer_id, employee_id, ssn, period_month, period_year, share_amount, reserve_amount, contribution_entry_id, created_date, created_by, last_updated_date, last_updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            for (Object[] row : data) {
            	if (!newRows.contains(row)) { // Skip new rows
	                preparedStatement.setString(1, (String) row[1]);
	                preparedStatement.setString(2, String.valueOf(row[2]));
	                preparedStatement.setString(3, (String) row[3]);
	                preparedStatement.setString(4, (String) row[4]);
	                preparedStatement.setString(5, (String) row[5]);
	                preparedStatement.setString(6, (String) row[6]);
	                preparedStatement.setString(7, (String) row[7]);
	                preparedStatement.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
	                preparedStatement.setString(9, (String) row[10]);
	                preparedStatement.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
	                preparedStatement.setString(11, (String) row[12]);
	                preparedStatement.setInt(12, (Integer) row[0]);
	
	                preparedStatement.executeUpdate();
            	}
            }
            
         // Insert new rows
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                for (Object[] row : newRows) {
                    insertStatement.setInt(1, (Integer) row[0]);         // Contribution Entry Detail ID
                    insertStatement.setString(2, (String) row[1]);       // Employer ID
                    insertStatement.setString(3, String.valueOf(row[2]));         // Employee ID
                    insertStatement.setString(4, (String) row[3]);       // SSN
                    insertStatement.setString(5, (String) row[4]);       // Period Month
                    insertStatement.setString(6, (String) row[5]);       // Period Year
                    insertStatement.setString(7, (String) row[6]);       // Share Amount
                    insertStatement.setString(8, (String) row[7]);       // Reserve Amount
                    insertStatement.setString(9, String.valueOf(row[8]));         // Contribution Entry ID
                    insertStatement.setTimestamp(10, (Timestamp) row[9]); // Created Date
                    insertStatement.setString(11, (String) row[10]);     // Created By
                    insertStatement.setTimestamp(12, (Timestamp) row[11]);  // Last Updated Date
                    insertStatement.setString(13, (String) row[12]);     // Last Updated By
                    insertStatement.executeUpdate();
                }
            }

            // Clear the newRows list after saving
            newRows.clear();
            
         // Update the summary column in the contribution_entry table
            updateSummaryInContributionEntry();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving changes: " + e.getMessage());
        }

        // Refresh the table data after saving
        fireTableDataChanged();
    }
    
    private void updateSummaryInContributionEntry() {
        double total = 0.0;

        // Calculate the total of share_amount + reserve_amount
        for (Object[] row : data) {
            try {
                double shareAmount = Double.parseDouble(row[6].toString()); // Share Amount
                double reserveAmount = Double.parseDouble(row[7].toString()); // Reserve Amount
                total += shareAmount + reserveAmount;
            } catch (NumberFormatException e) {
                // Skip invalid numeric values
            }
        }

        // Update the summary_amount in the contribution_entry table
        String updateQuery = "UPDATE contribution_entry SET summary_amount = ? WHERE contribution_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, String.format("%.2f", total)); // Total as a string
            preparedStatement.setInt(2, contributionId); // contribution_id as the identifier

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating summary in contribution_entry: " + e.getMessage());
        }
    }
    
    public void addEmployee(Object[] employeeRow) {
    	
    	// Calculate the next contribution_entry_detail_id
        int nextDetailId = getNextContributionEntryDetailId();
        
        // Add the employee to the model
        Object[] newRow = new Object[columnNames.length];
        newRow[0] = nextDetailId;  // Auto-increment primary key
        newRow[1] = employerId;
        newRow[2] = employeeRow[0]; // Employee ID
        newRow[3] = employeeRow[1]; // SSN
        newRow[4] = periodMonth;    // Use existing periodMonth from current contribution
        newRow[5] = periodYear;     // Use existing periodYear from current contribution
        newRow[6] = "0.00";         // Default Share Amount
        newRow[7] = "0.00";         // Default Reserve Amount
        newRow[8] = contributionId; // Contribution Entry ID
        newRow[9] = new Timestamp(System.currentTimeMillis()); // Created Date
        newRow[10] = "Admin";       // Created By (adjust as necessary)
        newRow[11] = new Timestamp(System.currentTimeMillis()); // Last Updated Date
        newRow[12] = "Admin";       // Last Updated By (adjust as necessary)
        newRow[14] = employeeRow[2] + " " + employeeRow[3]; // Employee Last Name

        data.add(newRow);
        newRows.add(newRow); // Add to the list of new row
        fireTableDataChanged();
    }
    
    public int getNextContributionEntryDetailId() {
        int maxId = 0;

        // Check the current max ID in the existing data
        for (Object[] row : data) {
            if (row[0] != null) {
                maxId = Math.max(maxId, (int) row[0]);
            }
        }

        // Alternatively, fetch the max ID from the database
        String query = "SELECT MAX(contribution_entry_detail_id) AS max_id FROM contribution_entry_detail";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                maxId = Math.max(maxId, resultSet.getInt("max_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return maxId + 1; // Increment the max ID to get the next ID
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
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    	data.get(rowIndex)[columnIndex] = aValue;
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0; // Allow editing except for the ID field
    }
    
    
	public int getColumnIndex(String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (getColumnName(i).equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1; // Return -1 if the column name is not found
	}

}

