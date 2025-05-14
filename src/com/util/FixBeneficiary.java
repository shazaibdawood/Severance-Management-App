package com.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class FixBeneficiary {
	public static void main(String args[]) throws Exception {
		
		FixBeneficiary fix = new FixBeneficiary();
		fix.getAllEmployeesBeneficiaries();
	}
	
	private void getAllEmployeesBeneficiaries() throws Exception {
		
		ArrayList<Employee> lstAllEmployees = new ArrayList<Employee> ();
		DatabaseConnection.loadProperties();
		Connection mySqlConnection = DatabaseConnection.getConnection();
		Statement stmtEmployee = mySqlConnection.createStatement();
		String strEmployee = "select a.EMPLOYEE_ID AS EMP_ID, A.SSN AS SSN from employee a, employee_beneficiary b where a.ssn = b.ssn;";
		ResultSet rsEmployee = stmtEmployee.executeQuery(strEmployee);
		while(rsEmployee.next()) {
			String employeeId = rsEmployee.getString("EMP_ID");
			String ssn = rsEmployee.getString("SSN");
			Statement stmtUpdateDateFormats = mySqlConnection.createStatement(); 	
			long result = stmtUpdateDateFormats.executeLargeUpdate("UPDATE EMPLOYEE_BENEFICIARY SET EMPLOYEE_ID = " + employeeId + " WHERE SSN = '" + ssn + "';");
			stmtUpdateDateFormats.close();
		}		
		
		if(mySqlConnection!=null) {
			mySqlConnection.close();
		}
		
		
	}


}
