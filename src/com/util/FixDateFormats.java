package com.util;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FixDateFormats {

	public static void main(String[] args) throws Exception {
		ArrayList<Employee> employees = getAllEmployees();
		Connection con = DatabaseConnection.getConnection();
		for (int i=0; i < employees.size(); ++i) {
			Employee employee = employees.get(i);
			String employeeId = employee.getEmployeeId();
			System.out.println("picked up employee id: " + employeeId);
			String dob = employee.getDob();
			String dateHired = employee.getDateHired();
			
			dob = changeFormat(dob);
			dateHired = changeFormat(dateHired);
			if(dob == null && dateHired == null) continue;
			
			Statement stmtUpdateDateFormats = con.createStatement(); 
			String strUpdateDateFormats = "";

				// update
			strUpdateDateFormats ="UPDATE `employee`" + 
				"SET" ;
			if(dob !=null) {
				strUpdateDateFormats +="`dob` = '" + dob + "',";
			}
			if(dateHired !=null) {
				strUpdateDateFormats +="`date_hired` = '" + dateHired + "'";
			}
			strUpdateDateFormats +=" WHERE employee_id = '" + employeeId + "'";
			
			long result = stmtUpdateDateFormats.executeLargeUpdate(strUpdateDateFormats);
			System.out.println("employee id: " + employeeId + " result: " + result);
			stmtUpdateDateFormats.close();

	
		}
		con.close();
	}
	
	private static String changeFormat(String inputDate) throws ParseException {
		SimpleDateFormat formatter = null;
		if(inputDate == null || inputDate.equals("")) {
			return null;
		} else if(inputDate.contains("-")) {
    		
    		String[] dateParts = inputDate.split("-");
    		String firstPart = dateParts[0];
    		String secondPart = dateParts[1];
    		String thirdPart = dateParts[2];
    		if(thirdPart.length() > 2) {
    			return null;
    		} else if(firstPart.length() > 2) {
    			formatter = new SimpleDateFormat("yyyy-MM-dd"); 
    		} else if(thirdPart.length() == 2) {
    			formatter = new SimpleDateFormat("MM-dd-yy"); 
    		}

		} else if (inputDate.contains("/")) {
			formatter = new SimpleDateFormat("MM/dd/yyyy");
		} 
		
		java.util.Date dateStr = formatter.parse(inputDate);
		SimpleDateFormat newFormatter = new SimpleDateFormat("MM-dd-yyyy");
		String newDate = newFormatter.format(dateStr);
		return newDate;
	}


	private static ArrayList<Employee> getAllEmployees() throws Exception {
		
		ArrayList<Employee> lstAllEmployees = new ArrayList<Employee> ();
		Connection mySqlConnection = DatabaseConnection.getConnection();
		Statement stmtEmployee = mySqlConnection.createStatement();
		String strEmployee = "SELECT * FROM EMPLOYEE where employee_id =5613";
		ResultSet rsEmployee = stmtEmployee.executeQuery(strEmployee);
		while(rsEmployee.next()) {
			Employee employee = new Employee();
			employee.setEmployeeId(rsEmployee.getString("EMPLOYEE_ID"));
			employee.setDob(rsEmployee.getString("DOB"));
			employee.setDateHired(rsEmployee.getString("DATE_HIRED"));
			lstAllEmployees.add(employee);			
		}		
		
		if(mySqlConnection!=null) {
			mySqlConnection.close();
		}
		
		return lstAllEmployees;
		
	}


}
