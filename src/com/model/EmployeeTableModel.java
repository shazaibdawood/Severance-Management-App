package com.model;

import javax.swing.table.AbstractTableModel;

import com.util.DatabaseConnection;
import com.util.Employee;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployeeTableModel extends AbstractTableModel {
    private List<Object[]> data = new ArrayList<>();
    private final String[] columnNames = {
            "employee_id", "SSN", "Employer_id", "first_name", "last_name", "middle_name",
            "dob", "date_Hired", "gender", "Marital_status", "Current_Spouse", "married_date",
            "Address", "Phone", "City", "State", "Zip", "previous_ssn", "previous_Employer_id",
            "previous_first_name", "previous_last_name", "previous_middle_name", "previous_Spouse",
            "created_date", "created_by", "last_updated_date", "updated_by",
            "status", "balance_forward", "terminated_date"
    };
    
    private final String[] displayColumnNames = {
            "Employee id", "SSN", "Employer id", "First Name", "Last Name", "Middle Name",
            "Date of Birth", "Date Hired", "Gender", "Marital Status", "Current Spouse", "Married Date",
            "Address", "Phone", "City", "State", "Zip", "Previous SSN", "Previous Employer Id",
            "Previous First Name", "Previous Last Name", "Previous Middle Name", "Previous Spouse",
            "Created Date", "Created By", "Last Updated Date", "Last Updated By",
            "Status", "Balance Forward", "Terminated Date"
    };

    public EmployeeTableModel() {
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
    public Object getValueAt(int rowIndex, int columnIndex) {
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
        return columnIndex != 0; // Only employee_id is non-editable as it's auto-incremented.
    }

    public void loadData() {
        data.clear();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM employee")) {

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
                     "INSERT INTO employee (SSN, Employer_id, first_name, last_name, middle_name, dob, date_Hired, gender, Marital_status, "
                     + "Current_Spouse, married_date, Address, Phone, City, State, Zip, previous_ssn, previous_Employer_id, previous_first_name,"
                     + " previous_last_name, previous_middle_name, previous_Spouse, created_date, created_by, last_updated_date, updated_by, "
                     + "status, balance_forward,terminated_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            for (int i = 1; i < row.length; i++) { // Skip employee_id as it is auto-incremented.
            	String columnName = getColumnName(i);
            	if(columnName.contains("Date")) {
            		if(row[i] == null || row[i].toString().isBlank()) {
            			preparedStatement.setObject(i, "");
            			continue;
            		}
            		if(row[i] instanceof String ) {
	            		String value = (String) row[i];
	            		if(value == null || value.equals("0") || value.equals("")) {
	            			preparedStatement.setObject(i, null);
	            		} else {
	            			preparedStatement.setObject(i, row[i]);
	            		}
            		}else if(row[i] instanceof LocalDateTime ) {
	            		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy"); // your template here
	            		java.util.Date dateStr = formatter.parse((String) row[i]);
	            		java.sql.Date dateDB = new java.sql.Date(dateStr.getTime());
	            		preparedStatement.setObject(i, dateDB);
            		}
            	} else {
            		preparedStatement.setObject(i, row[i]);
            	}

            }
            preparedStatement.executeUpdate();
            loadData(); // Refresh the table after insertion.
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRow(int rowIndex) throws ParseException, SQLException {
    	Connection connection = DatabaseConnection.getConnection();
    	boolean ssnChanged = false;
    	boolean employerIdChanged = false;
    	Object[] row = data.get(rowIndex);
    	String newSSN = (String)row[1];
    	String newEmployerId = (String)row[2];
    	
    	String employeeId = String.valueOf(row[0]);
    	Employee oldEmployeeData = this.searchByEmployeeId(employeeId);
    	String oldSSN = oldEmployeeData.getSsn();
    	String oldEmployerId = oldEmployeeData.getEmployerId();
    	if(!oldSSN.equalsIgnoreCase(newSSN)) {
    		ssnChanged = true;
    	}
    	if(!oldEmployerId.equalsIgnoreCase(newEmployerId)) {
    		employerIdChanged = true;
    	}
    	
        try (
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE employee SET SSN=?, Employer_id=?, first_name=?, last_name=?, middle_name=?, dob=?, date_Hired=?, gender=?, Marital_status=?, Current_Spouse=?, married_date=?, Address=?, Phone=?, City=?, State=?, Zip=?, previous_ssn=?, previous_Employer_id=?, previous_first_name=?, previous_last_name=?, previous_middle_name=?, previous_Spouse=?, created_date=?, created_by=?, last_updated_date=?, updated_by=?, status=?, balance_forward=?, terminated_date=? WHERE employee_id=?")) {

            
            for (int i = 1; i < columnNames.length; i++) {
            	String columnName = getColumnName(i);
            	if(columnName.contains("Date")) {
            		if(row[i] instanceof String ) {
	            		String value = (String) row[i];
	            		if(value == null || value.equals("0") || value.equals("")) {
	            			preparedStatement.setObject(i, null);
	            		} else {
	            			preparedStatement.setObject(i, row[i]);
	            		}
            		}else if(row[i] instanceof LocalDateTime ) {
	            		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy"); // your template here
	            		java.util.Date dateStr = formatter.parse((String) row[i]);
	            		java.sql.Date dateDB = new java.sql.Date(dateStr.getTime());
	            		preparedStatement.setObject(i, dateDB);            		
            		}
            	} else {
            		preparedStatement.setObject(i, row[i]);
            	}
            }
            preparedStatement.setObject(columnNames.length, row[0]); // employee_id as WHERE condition
            preparedStatement.executeUpdate();
            loadData(); // Refresh the table after update.
            
            if(ssnChanged || employerIdChanged) {
	            String updateQuery = "UPDATE contribution_entry_detail SET employer_id = ?, ssn = ? WHERE ssn = ?";
	            PreparedStatement prepStmtUpdateContrDetail = connection.prepareStatement(updateQuery);
	            prepStmtUpdateContrDetail.setObject(1, newEmployerId);
	            prepStmtUpdateContrDetail.setObject(2, newSSN);
	            prepStmtUpdateContrDetail.setObject(3, oldSSN);
	            prepStmtUpdateContrDetail.executeUpdate();   
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRow(int rowIndex) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM employee WHERE employee_id=?")) {

            preparedStatement.setObject(1, data.get(rowIndex)[0]);
            preparedStatement.executeUpdate();
            data.remove(rowIndex); // Remove locally
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fireTableDataChanged(); // Refresh the table
    }
    
    public void search(String query) {
        data.clear();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM employee WHERE first_name LIKE ? OR last_name LIKE ?")) {

            String likeQuery = "%" + query + "%";
            preparedStatement.setString(1, likeQuery);
            preparedStatement.setString(2, likeQuery);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
            	loadDataFromResultSet(resultSet);
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
	
	public void searchBySSN(String ssn) {
	    try (Connection connection = DatabaseConnection.getConnection();
	         PreparedStatement statement = connection.prepareStatement("SELECT * FROM employee WHERE SSN = ?")) {
	        statement.setString(1, ssn);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            loadDataFromResultSet(resultSet);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	public Employee searchByEmployeeId(String employeeId) {
		Employee employee = null;
	    try {
	    	Connection connection = DatabaseConnection.getConnection();
	    
	         PreparedStatement statement = connection.prepareStatement("SELECT * FROM employee WHERE employee_id = ?");
	        statement.setString(1, employeeId);
	        ResultSet resultSet = statement.executeQuery();
	        if(resultSet.next()) {
	        	employee = new Employee();
	        	employee.setEmployerId(resultSet.getString("EMPLOYER_ID"));
	        	employee.setSsn(resultSet.getString("SSN"));
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return employee;
	}

	public void searchByEmployerId(String employerId) {
	    try (Connection connection = DatabaseConnection.getConnection();
	         PreparedStatement statement = connection.prepareStatement("SELECT * FROM employee WHERE Employer_id = ?")) {
	        statement.setString(1, employerId);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            loadDataFromResultSet(resultSet);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	private void loadDataFromResultSet(ResultSet resultSet) throws SQLException {
	    data.clear();
	    while (resultSet.next()) {
	        Object[] row = new Object[columnNames.length];
	        for (int i = 0; i < columnNames.length; i++) {
	            row[i] = resultSet.getObject(columnNames[i]);
	        }
	        data.add(row);
	    }
	    fireTableDataChanged();
	}

    public Employee searchForEligibility(String query) throws SQLException {
    	Employee employee = new Employee();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM employee WHERE first_name LIKE ? OR last_name LIKE ? OR SSN LIKE ?")) {

            String likeQuery = "%" + query + "%";
            preparedStatement.setString(1, likeQuery);
            preparedStatement.setString(2, likeQuery);
            preparedStatement.setString(3, likeQuery);

            try {
            	ResultSet resultSet = preparedStatement.executeQuery();
            	int count = 0;

            	while (resultSet.next()) {
            	    ++count;
					String ssn = resultSet.getString("SSN");
					String employerId = resultSet.getString("EMPLOYER_ID");
					String dateHired = resultSet.getString("DATE_HIRED");
					String status = resultSet.getString("STATUS");
					String balanceForward = resultSet.getString("BALANCE_FORWARD");
					
					employee.setSsn(ssn);
					employee.setFirstName(resultSet.getString("FIRST_NAME"));
					employee.setLastName(resultSet.getString("LAST_NAME"));
					employee.setEmployerId(employerId);
					employee.setEmployeeId(resultSet.getString("EMPLOYEE_ID"));
					employee.setDateHired(dateHired);
					employee.setStatus(status);  
					employee.setBalanceForward(balanceForward);
            	}

            	if (count == 0) {
            		employee = null;
            	} else if (count > 1) {
            		employee.setSsn("X");
            	}

            }
         catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
        return employee;
    }

    public HashMap<String, Employee> getAllEmployees() {
        HashMap<String, Employee> mapEmployees = new HashMap<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM employee ORDER BY first_name, last_name")) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Employee employee = new Employee();
                    String ssn = resultSet.getString("SSN");
                    employee.setEmployeeId(resultSet.getString("EMPLOYEE_ID"));
                    employee.setSsn(ssn);
                    employee.setEmployerId(resultSet.getString("EMPLOYER_ID"));
                    employee.setFirstName(resultSet.getString("FIRST_NAME"));
                    employee.setLastName(resultSet.getString("LAST_NAME"));
                    employee.setInitial(resultSet.getString("MIDDLE_NAME"));
                    employee.setDob(resultSet.getString("DOB"));
                    employee.setDateHired(resultSet.getString("DATE_HIRED"));
//                    employee.setGender(resultSet.getString("GENDER"));
//                    employee.setMaritalStatus(resultSet.getString("MARITAL_STATUS"));
//                    employee.setCurrentSpouse(resultSet.getString("CURRENT_SPOUSE"));
//                    employee.setMarriedDate(resultSet.getString("MARRIED_DATE"));
//                    employee.setAddress(resultSet.getString("ADDRESS"));
//                    employee.setPhone(resultSet.getString("PHONE"));
                    employee.setCity(resultSet.getString("CITY"));
                    employee.setState(resultSet.getString("STATE"));
                    employee.setZip(resultSet.getString("ZIP"));
//                    employee.setPreviousSsn(resultSet.getString("PREVIOUS_SSN"));
//                    employee.setPreviousEmployerId(resultSet.getString("PREVIOUS_EMPLOYER_ID"));
//                    employee.setPreviousFirstName(resultSet.getString("PREVIOUS_FIRST_NAME"));
//                    employee.setPreviousLastName(resultSet.getString("PREVIOUS_LAST_NAME"));
//                    employee.setPreviousMiddleName(resultSet.getString("PREVIOUS_MIDDLE_NAME"));
//                    employee.setPreviousSpouse(resultSet.getString("PREVIOUS_SPOUSE"));
//                    employee.setCreatedDate(resultSet.getString("CREATED_DATE"));
//                    employee.setCreatedBy(resultSet.getString("CREATED_BY"));
//                    employee.setLastUpdatedDate(resultSet.getString("LAST_UPDATED_DATE"));
//                    employee.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                    employee.setStatus(resultSet.getString("STATUS"));
                    employee.setBalanceForward(resultSet.getString("BALANCE_FORWARD"));

                    // Add to HashMap with SSN as the key
                    mapEmployees.put(ssn, employee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapEmployees;
    }


}

