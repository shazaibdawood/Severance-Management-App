package com.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class GenerateStatementOfAccounts {
	
	public static void generate() {
		try {
			String pattern = "MM-dd-yyyy_HH-mm-ss";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			Date currentDate = new Date();
			
			String logFileName = simpleDateFormat.format(currentDate) + "_soa.txt";
			String logFilePath = "C://Severence//YearEndReports//StatementOfAccounts//Logs//" + logFileName;
			
			
			File file = new File(logFilePath);
			file.createNewFile();
			
			FileWriter logfile = new FileWriter(file);
			
//			System.out.println("Generating Accounts...");
			
			
			DecimalFormat df = new DecimalFormat("####0.00");
			double finalContribution = 0.0;
			
			System.out.println("Getting all Employers");
			HashMap<String,String> hsEmployers = getAllEmployers(logfile);
			createEmployersDirectory(hsEmployers, logfile);
			ArrayList<Employee> lstEmployees = getAllEmployees(logfile);
			System.out.println("Getting all Employees");
			HashMap<String, BalanceSummary> hsBalanceSummary = getAllBalanceSummary(logfile);
			System.out.println("Generating PDFs");
			
			
			for(int i = 0; i<lstEmployees.size(); ++i) {
				Employee employee = lstEmployees.get(i);
				String companyCode = employee.getCompanyCode();
				String ssn = employee.getSsn();
				double totalContribution = 0.0;
				double totalReserveAmount = 0.0;
				double withdrawalAmount = 0.0;
				double totalShare = 0.0; 
				double totalDividend = 0.0; 
				double balanceFwdNextYear = 0.0; 
				
				String last4DigitsSSN = "";
				if(ssn.length() >= 4) {
					last4DigitsSSN = ssn.substring(ssn.length() - 4);
				} else  {
					last4DigitsSSN = ssn;
				}
				String companyName = hsEmployers.get(companyCode);
				
				BalanceSummary balanceSummary = hsBalanceSummary.get(ssn);
				
				if(balanceSummary == null ) {
					logfile.write("Unable to find balance Summary for: " + ssn + " with company code: " + companyCode + "\n");
					totalContribution = 0.0;
					totalReserveAmount = 0.0;
				} else {
					totalContribution = Double.parseDouble(balanceSummary.getShareAmount());
					totalReserveAmount = Double.parseDouble(balanceSummary.getReserveAmount());
					totalShare = Double.parseDouble(balanceSummary.getTotalShare());
					withdrawalAmount = Double.parseDouble(balanceSummary.getWithdrawalAmount());
					totalDividend = Double.parseDouble(balanceSummary.getDividend());
					balanceFwdNextYear = Double.parseDouble(balanceSummary.getBalanceAfterDividend());
//					logfile.write("Balance Summary found for: " + ssn + " with company code: " + companyCode + "\n");
				}
				
				
				if(balanceFwdNextYear <= 0) continue; // Do not print negatives or zeroes
				
				PdfReader reader = new PdfReader(DatabaseConnection.statementTemplateFilePath);
				PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(DatabaseConnection.statementDataFilePath + companyCode + "\\SOA-" + ssn + ".pdf"));
				
				AcroFields form = stamper.getAcroFields();
				form.setField("name", employee.getFirstName() + " " + employee.getInitial() + " " + employee.getLastName());
				form.setField("address1", employee.getAddress());
				form.setField("address2", employee.getCity() + " " + employee.getState() + " " + employee.getZip());
				form.setField("ttl_balance", df.format(balanceFwdNextYear));
				form.setField("SSN", last4DigitsSSN);
				form.setField("ttl_contr", df.format(totalContribution)); // contribution for the whole year
				form.setField("ttl_share", df.format(totalShare));
				form.setField("ttl_withdrawal", df.format(withdrawalAmount));
				form.setField("ttl_reserve", df.format(totalReserveAmount));
				form.setField("ttl_dividend", df.format(totalDividend));
				form.setField("balance_fwd", df.format(balanceFwdNextYear));
				form.setField("pg2_name",  employee.getFirstName() + " " + employee.getInitial() + " " + employee.getLastName());
				form.setField("pg2_address1", employee.getAddress());
				form.setField("pg2_address2",  employee.getCity() + " " + employee.getState() + " " + employee.getZip());
				form.setField("pg2_company_name", companyName);
				form.setField("pg2_company_code", companyCode);
				

				stamper.setFormFlattening(true);
				stamper.close();
				reader.close();
				
				finalContribution += totalContribution;
				if(i % 100 == 0) {
					System.out.println(i + " of " + lstEmployees.size() + " is completed.");
				}

			}
			
			logfile.write("finalContribution: " + finalContribution + "\n");	

			logfile.close();

			System.out.println("\nPDFs have been generated successfully");
			System.out.println("Statements located in C:\\Severence\\YearEndReports\\StatementOfAccounts\\Reports\\");
			System.out.println("Press Close button to exit");

			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	private static void createEmployersDirectory(HashMap<String, String> hsEmployers, FileWriter logfile) throws Exception {
		String pattern = "MM-dd-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date logDate = new Date();
		logfile.write(simpleDateFormat.format(logDate) + "\tEntering createEmployersDirectory()\n");
		for (Map.Entry<String, String> set : hsEmployers.entrySet()) {
			File theDir = new File("C:\\Severence\\YearEndReports\\StatementOfAccounts\\Reports\\" + set.getKey());
			
			if (!theDir.exists()){
				theDir.mkdirs();
			}
			
//			System.out.println("Creating folder for Employer code: " + set.getKey());
		}
		logfile.write(simpleDateFormat.format(logDate) + "\tExiting createEmployersDirectory()\n");
	}

	private static Connection getConnection(FileWriter logfile) throws Exception{
		String pattern = "MM-dd-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date logDate = new Date();
		logfile.write(simpleDateFormat.format(logDate) + "\tEntering getConnection()\n");
		//Connection mySqlConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/severance_sandbox?useSSL=false","root","Only1Allah");
		Connection mySqlConnection = DatabaseConnection.getConnection();
		logfile.write(simpleDateFormat.format(logDate) + "\tExiting getConnection()\n");

		return mySqlConnection;
	}
	
	
	private static HashMap<String,String> getAllEmployers(FileWriter logfile) throws Exception{
		String pattern = "MM-dd-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date logDate = new Date();
		logfile.write(simpleDateFormat.format(logDate) + "\tEntering getAllEmployers()\n");
		
		HashMap<String,String> hsEmployers = new HashMap<String, String>();
		Connection mySqlConnection = getConnection(logfile);
		Statement stmtCompany = mySqlConnection.createStatement();
		ResultSet rsCompany = stmtCompany.executeQuery("SELECT Employer_id, employer_name FROM EMPLOYER");
		while(rsCompany.next()) {
			String companyCode = rsCompany.getString("employer_id");
			String companyName = rsCompany.getString("employer_name");
			hsEmployers.put(companyCode, companyName);
			
			System.out.println("Retrieving Employees from " + companyCode + ": " + companyName);
		}
		
		logfile.write(simpleDateFormat.format(logDate) + "\tExiting getAllEmployers()\n");

		return hsEmployers;
	}
	
	private static ArrayList<Employee> getAllEmployees(FileWriter logfile) throws Exception {
		String pattern = "MM-dd-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date logDate = new Date();
		logfile.write(simpleDateFormat.format(logDate) + "\tEntering getAllEmployees()\n");
		
		ArrayList<Employee> lstAllEmployees = new ArrayList<Employee> ();
		Connection mySqlConnection = getConnection(logfile);
		Statement stmtEmployee = mySqlConnection.createStatement();
		ResultSet rsEmployee = stmtEmployee.executeQuery("SELECT * FROM EMPLOYEE WHERE STATUS = 'AA' ");
		while(rsEmployee.next()) {
			Employee employee = new Employee();
			employee.setSsn(rsEmployee.getString("SSN"));
			employee.setCompanyCode(rsEmployee.getString("EMPLOYER_id"));
			employee.setFirstName(rsEmployee.getString("First_NAME"));
			employee.setLastName(rsEmployee.getString("Last_NAME"));
			employee.setInitial(rsEmployee.getString("Middle_name"));
			employee.setAddress(rsEmployee.getString("ADDRESS"));
			employee.setCity(rsEmployee.getString("CITY"));
			employee.setState(rsEmployee.getString("STATE"));
			employee.setZip(rsEmployee.getString("ZIP"));
			employee.setStatus(rsEmployee.getString("STATUS"));
			employee.setBalanceForward(rsEmployee.getString("BALANCE_FORWARD"));
			lstAllEmployees.add(employee);	
			
			
		}
		
		logfile.write(simpleDateFormat.format(logDate) + "\tExiting getAllEmployees()\n");

		return lstAllEmployees;
		
	}
	
	private static HashMap<String, BalanceSummary> getAllBalanceSummary(FileWriter logfile) throws Exception {
		String pattern = "MM-dd-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date logDate = new Date();
		logfile.write(simpleDateFormat.format(logDate) + "\tEntering getAllBalanceSummary()\n");
		
		HashMap<String, BalanceSummary> hsBalanceSummary = new HashMap<String, BalanceSummary>();
		Connection mySqlConnection = getConnection(logfile);
		Statement stmtBalanceSummary = mySqlConnection.createStatement();
		ResultSet rsBalanceSummary = stmtBalanceSummary.executeQuery("SELECT * FROM CONTRIBUTION_SUMMARY");		
		while (rsBalanceSummary.next()) {
			String ssn = rsBalanceSummary.getString("SSn");
			BalanceSummary balanceSummary = new BalanceSummary();
			//balanceSummary.setBalanceForward(rsBalanceSummary.getString("BALANCE_FORWARD"));
			if(hsBalanceSummary.containsKey(ssn)) {
				logfile.write("Duplicates for Summary existed: " + ssn + " - Employer Id: " + rsBalanceSummary.getString("EMPLOYER_ID") + "\n");
				balanceSummary = hsBalanceSummary.get(ssn);
				double existingReserveAmount = Double.parseDouble(balanceSummary.getReserveAmount());
				double existingShareAmount = Double.parseDouble(balanceSummary.getShareAmount());
				double newReserveAmount = existingReserveAmount + Double.parseDouble(rsBalanceSummary.getString("RESERVE_AMOUNT"));
				double newShareAmount = existingShareAmount + Double.parseDouble(rsBalanceSummary.getString("total_contribution"));
				balanceSummary.setReserveAmount(String.valueOf(newReserveAmount));
				balanceSummary.setShareAmount(String.valueOf(newShareAmount));
				
			} else {
				
				balanceSummary.setSsn(rsBalanceSummary.getString("SSn"));
				balanceSummary.setShareAmount(rsBalanceSummary.getString("total_contribution"));
				balanceSummary.setReserveAmount(rsBalanceSummary.getString("RESERVE_AMOUNT"));
				balanceSummary.setTotalShare(rsBalanceSummary.getString("total_share"));
				balanceSummary.setWithdrawalAmount(rsBalanceSummary.getString("withdrawal_amount"));
				balanceSummary.setDividend(rsBalanceSummary.getString("dividend"));
				balanceSummary.setBalanceAfterDividend(rsBalanceSummary.getString("balance_after_dividend"));
			}
			hsBalanceSummary.put(ssn, balanceSummary);
		}
		
		logfile.write(simpleDateFormat.format(logDate) + "\tExiting getAllBalanceSummary()\n");

		return hsBalanceSummary;
	}

}
