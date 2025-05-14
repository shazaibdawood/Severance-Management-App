package com.model;

import javax.swing.table.AbstractTableModel;

import com.util.BalanceSummary;
import com.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContributionSummaryTableModel extends AbstractTableModel {
    private final List<Object[]> data = new ArrayList<>();
    private final String[] columnNames = {
            "Contribution ID", "Employer ID", "Period Month", "Period Year",
            "Summary Amount", "Created Date", "Created By",
            "Last Updated Date", "Last Updated By", "Cash Received Date"
    };
    private final String[] dbColumnNames = {
            "Contribution_ID", "Employer_ID", "Period_Month", "Period_Year",
            "Summary_Amount", "Created_Date", "Created_By",
            "Last_Updated_Date", "Last_Updated_By", "Cash_Received_Date"
    };

    public ContributionSummaryTableModel() {
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
    	return columnIndex != 0 && columnIndex != 4;
    }

    // Load data from the database
    public void loadData() {
        data.clear();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM contribution_entry order by employer_id, period_year, period_month")) {

            while (resultSet.next()) {
                Object[] row = new Object[columnNames.length];
                row[0] = resultSet.getInt("contribution_id");
                row[1] = resultSet.getInt("employer_id");
                row[2] = resultSet.getString("period_month");
                row[3] = resultSet.getString("period_year");
                row[4] = resultSet.getString("summary_amount");
                row[5] = resultSet.getTimestamp("created_date");
                row[6] = resultSet.getString("created_by");
                row[7] = resultSet.getTimestamp("last_updated_date");
                row[8] = resultSet.getString("last_updated_by");
                row[9] = resultSet.getString("Cash_Received_Date");
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fireTableDataChanged();
    }

    // Add a new contribution to the database
    public void addRow(Object[] row) {
        String query = "INSERT INTO contribution_entry (employer_id, period_month, period_year, summary_amount, created_date, created_by, last_updated_date, last_updated_by, Cash_Received_Date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        	LocalDate today = LocalDate.now();
            preparedStatement.setInt(1, (Integer) row[1]);
            preparedStatement.setString(2, (String) row[2]);
            preparedStatement.setString(3, (String) row[3]);
            preparedStatement.setString(4, (String) row[4]);
            preparedStatement.setTimestamp(5, Timestamp.valueOf(today.atStartOfDay()));
            preparedStatement.setString(6, (String) row[6]);
            preparedStatement.setTimestamp(7, Timestamp.valueOf(today.atStartOfDay()));
            preparedStatement.setString(8, (String) row[8]);
            preparedStatement.setString(9, (String) row[9]);

            preparedStatement.executeUpdate();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a new contribution to the database
    public void addRowManual(Object[] row) {
        String query = "INSERT INTO contribution_entry (contribution_id, employer_id, period_month, period_year, summary_amount, created_date, created_by, last_updated_date, last_updated_by, Cash_Received_Date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        	LocalDate today = LocalDate.now();
        	preparedStatement.setInt(1, (Integer) row[0]);
            preparedStatement.setInt(2, (Integer) row[1]);
            preparedStatement.setString(3, (String) row[2]);
            preparedStatement.setString(4, (String) row[3]);
            preparedStatement.setString(5, (String) row[4]);
            preparedStatement.setTimestamp(6, Timestamp.valueOf(today.atStartOfDay()));
            preparedStatement.setString(7, (String) row[6]);
            preparedStatement.setTimestamp(8, Timestamp.valueOf(today.atStartOfDay()));
            preparedStatement.setString(9, (String) row[8]);
            preparedStatement.setString(10, (String) row[9]);

            preparedStatement.executeUpdate();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update an existing contribution in the database
    public void updateRow(int rowIndex) {
        String query = "UPDATE contribution_entry SET employer_id = ?, period_month = ?, period_year = ?, summary_amount = ?, created_date = ?, created_by = ?, last_updated_date = ?, last_updated_by = ?, Cash_Received_Date = ? WHERE contribution_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            Object[] row = data.get(rowIndex);
            for (int i = 1; i < columnNames.length; i++) {
                preparedStatement.setObject(i, row[i]);
            }
            preparedStatement.setObject(columnNames.length, row[0]); // employee_id as WHERE condition

            preparedStatement.executeUpdate();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a contribution from the database
    public void deleteRow(int rowIndex) {
        Object[] row = data.get(rowIndex);
        String query = "DELETE FROM contribution_entry WHERE contribution_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, (Integer) row[0]);
            preparedStatement.executeUpdate();
            loadData();
            
            String queryDetail = "DELETE FROM contribution_entry_detail WHERE contribution_entry_id = ?";
            PreparedStatement preparedStatementDetail = connection.prepareStatement(queryDetail);

        	preparedStatementDetail.setInt(1, (Integer) row[0]);
        	preparedStatementDetail.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search contributions based on Employer ID, Period Month, or Year
    public List<Object[]> search(String query) {
        List<Object[]> searchResults = new ArrayList<>();
        String sqlQuery = "SELECT * FROM contribution_entry WHERE employer_id LIKE ? OR period_month LIKE ? OR period_year LIKE ? order by employer_id, period_year, period_month";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            String likeQuery = "%" + query + "%";
            preparedStatement.setString(1, likeQuery);
            preparedStatement.setString(2, likeQuery);
            preparedStatement.setString(3, likeQuery);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Object[] row = new Object[columnNames.length];
                    row[0] = resultSet.getInt("contribution_id");
                    row[1] = resultSet.getInt("employer_id");
                    row[2] = resultSet.getString("period_month");
                    row[3] = resultSet.getString("period_year");
                    row[4] = resultSet.getString("summary_amount");
                    row[5] = resultSet.getTimestamp("created_date");
                    row[6] = resultSet.getString("created_by");
                    row[7] = resultSet.getTimestamp("last_updated_date");
                    row[8] = resultSet.getString("last_updated_by");
                    row[9] = resultSet.getString("Cash_Received_Date");
                    searchResults.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResults;
    }
    
	public int getColumnIndex(String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (getColumnName(i).equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1; // Return -1 if the column name is not found
	}
	
	public void loadDataFromResultSet(ResultSet resultSet) throws SQLException {
        data.clear();
        while (resultSet.next()) {
            Object[] row = new Object[columnNames.length];
            row[0] = resultSet.getInt("contribution_id");
            row[1] = resultSet.getInt("employer_id");
            row[2] = resultSet.getString("period_month");
            row[3] = resultSet.getString("period_year");
            row[4] = resultSet.getString("summary_amount");
            row[5] = resultSet.getTimestamp("created_date");
            row[6] = resultSet.getString("created_by");
            row[7] = resultSet.getTimestamp("last_updated_date");
            row[8] = resultSet.getString("last_updated_by");
            row[9] = resultSet.getString("Cash_Received_Date");
            data.add(row);
        }
        fireTableDataChanged();
        
        
    }

	public void searchContribution(String employerId, String periodMonth, String periodYear) {
	    try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM contribution_entry WHERE employer_id = ? AND period_month = ? AND period_year = ?  order by employer_id, period_year, period_month ")) {
               statement.setString(1, employerId);
               statement.setString(2, periodMonth);
               statement.setString(3, periodYear);
		        try (ResultSet resultSet = statement.executeQuery()) {
		            loadDataFromResultSet(resultSet);
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		
	}

	public BalanceSummary findSumContributionsBySSN(String ssn, String periodYear, String employerId) {
		BalanceSummary balanceSummary = null;
	    try {
	    	Connection connection = DatabaseConnection.getConnection();
	    
                PreparedStatement statement = connection.prepareStatement(
                		" select sum(share_amount) AS SHARE_AMOUNT from contribution_entry_detail a, "
                		+ "contribution_entry b " +
                		" where a.ssn = ? and a.contribution_entry_id = b.contribution_id " +
                		" and b.employer_id = ? and b.period_year > ?" +
                		"");
		    	statement.setString(1, ssn);
	            statement.setString(2, employerId);
	            statement.setString(3, periodYear);
	            ResultSet resultSet = statement.executeQuery(); 
            	if(resultSet.next()) {
            		balanceSummary = new BalanceSummary();
            		balanceSummary.setSsn(ssn);
            		balanceSummary.setShareAmount(resultSet.getString("SHARE_AMOUNT"));
            		
	        	}
            
            // get reserve amount
            PreparedStatement statementReseve = connection.prepareStatement(
            		" select sum(reserve_amount) AS RESERVE_AMOUNT from contribution_entry_detail a, "
            		+ "contribution_entry b " +
            		" where a.ssn = ? and a.contribution_entry_id = b.contribution_id " +
            		" and b.employer_id = ? " +
            		"");
            statementReseve.setString(1, ssn);
            statementReseve.setString(2, employerId);
        	resultSet = statementReseve.executeQuery();
        	if(resultSet.next()) {
        		balanceSummary.setReserveAmount(resultSet.getString("RESERVE_AMOUNT"));
        		
        	}
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return balanceSummary;
	}
	
	public ArrayList<BalanceSummary> searchContributionBySSN(String ssn, String periodYear, String periodMonthParam, String employerId) {
		ArrayList<BalanceSummary> listSummary = new ArrayList<>();
	    try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        //"SELECT * FROM contribution_entry_detail WHERE ssn = ? AND period_year = ? AND period_month = ? order by period_month")) {
	    	"SELECT a.ssn, a.share_amount, a.reserve_amount, b.period_month, b.period_year  FROM contribution_entry_detail a, contribution_entry b where a.ssn = ? " + 
	    			" and a.contribution_entry_id = b.contribution_id and b.period_year = ? AND b.period_month = ? and b.employer_id = ? order by b.period_month")) {
	    	statement.setString(1, ssn);
               statement.setString(2, periodYear);
               statement.setString(3, periodMonthParam);
               statement.setString(4, employerId);
		        try (ResultSet resultSet = statement.executeQuery()) {
		        	while(resultSet.next()) {
			        	BalanceSummary balanceSummary = new BalanceSummary();
			        	balanceSummary.setSsn(resultSet.getString("SSN"));
			        	balanceSummary.setPeriodMonth(resultSet.getString("PERIOD_MONTH"));
			        	balanceSummary.setPeriodYear(resultSet.getString("PERIOD_YEAR"));
			        	balanceSummary.setShareAmount(resultSet.getString("SHARE_AMOUNT"));
			        	balanceSummary.setReserveAmount(resultSet.getString("RESERVE_AMOUNT"));
			        	
			        	String periodMonth = resultSet.getString("PERIOD_MONTH");
			        	String monthStr = null;
			        	if(periodMonth.equalsIgnoreCase("01") || periodMonth.equalsIgnoreCase("1")) {
			        		monthStr = "January";
			        	} else if (periodMonth.equalsIgnoreCase("02") || periodMonth.equalsIgnoreCase("2")) {
			        		monthStr = "February";
			        	} else if (periodMonth.equalsIgnoreCase("03") || periodMonth.equalsIgnoreCase("3")) {
			        		monthStr = "March";
			        	} else if (periodMonth.equalsIgnoreCase("04") || periodMonth.equalsIgnoreCase("4")) {
			        		monthStr = "April";
			        	} else if (periodMonth.equalsIgnoreCase("05") || periodMonth.equalsIgnoreCase("5")) {
			        		monthStr = "May";
			        	} else if (periodMonth.equalsIgnoreCase("06") || periodMonth.equalsIgnoreCase("6")) {
			        		monthStr = "June";
			        	} else if (periodMonth.equalsIgnoreCase("07") || periodMonth.equalsIgnoreCase("7")) {
			        		monthStr = "July";
			        	} else if (periodMonth.equalsIgnoreCase("08") || periodMonth.equalsIgnoreCase("8")) {
			        		monthStr = "August";
			        	} else if (periodMonth.equalsIgnoreCase("09") || periodMonth.equalsIgnoreCase("9")) {
			        		monthStr = "September";
			        	} else if (periodMonth.equalsIgnoreCase("10")) {
			        		monthStr = "October";
			        	} else if (periodMonth.equalsIgnoreCase("11")) {
			        		monthStr = "November";
			        	} else if (periodMonth.equalsIgnoreCase("12")) {
			        		monthStr = "December";
			        	} 
			        	
			        	balanceSummary.setMonthString(monthStr);
			        	
			        	listSummary.add(balanceSummary);
		        	}
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	    
	    return listSummary;
		
	}
	
	public int getNextContributionId() {
		int nextContributionId = 0;
	    try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT MAX(CONTRIBUTION_ID) + 1 AS NEXT_ID FROM contribution_entry")) {

		        try (ResultSet resultSet = statement.executeQuery()) {
		        	if(resultSet.next()) {
		        		nextContributionId = resultSet.getInt("NEXT_ID");
		        	}
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	    
	    return nextContributionId;
		
	}

}

