package com.util;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GenerateDividendSummaryReportCSV {
	

	
	public static void generateReport(int year, double dividendFactor) {
		try {
			String pattern = "MM-dd-yyyy_HH-mm-ss";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			Date currentDate = new Date();
			Date logDate;
			
			String logFileName = simpleDateFormat.format(currentDate) + "_dividendLogs.txt";
			String logFilePilePath = DatabaseConnection.dividendLogFilePath + logFileName; //teamsters filepath
			String dataFilePilePath = DatabaseConnection.dividendDataFilePath;

			
			pattern = "MM-dd-yyyy HH:mm:ss";
			simpleDateFormat = new SimpleDateFormat(pattern);
			File logFile = new File(logFilePilePath);
			logFile.createNewFile();
			
			FileWriter logFileWriter = new FileWriter(logFile);
			
			String filename = dataFilePilePath + "DividendEditReport - " + year +".csv";
			
			Statement stmtGetDividendYearMap = DatabaseConnection.getConnection().createStatement();
			ResultSet rsGetDividendYearMap = stmtGetDividendYearMap.executeQuery("select * from dividend_year_map where status = 'C' and year = " + year);
			if(rsGetDividendYearMap.next()) {
				System.out.println("Dividend was already calculated for this year.");
				return;
			}
			
			if(dividendFactor < 0 || dividendFactor > 100) {
				System.out.println(dividendFactor + "% is invalid");
				System.out.println("Enter a valid dividend rate (0 - 100)");
				return;
			}
			dividendFactor = dividendFactor/100;
			System.out.println("Generating Report...\n");
			
			//DecimalFormat df = new DecimalFormat("####0.00");
			Locale locale = new Locale("en", "US"); 
			NumberFormat df = NumberFormat.getCurrencyInstance(locale);
			
			ArrayList<String> lstEmployeeStatuses = new ArrayList<String>();
			lstEmployeeStatuses.add("AA");
			lstEmployeeStatuses.add("SU");
			lstEmployeeStatuses.add("IU");
			lstEmployeeStatuses.add("PT");
			
			FileWriter fileWriter = new FileWriter(filename); //Changed
			

			
			StringBuffer sbHeader = new StringBuffer();
			StringBuffer sbEmployerEmployee = new StringBuffer();
			
			sbHeader.append("`Dividend Entry Report`");
			sbHeader.append(System.lineSeparator());
			sbHeader.append(System.lineSeparator());
			
			double grndTtlContribution = 0.0;
			double grndTtlEmployerShareAmount = 0.0;
			double grndTtlReserveAmount  = 0.0;
			double grndTtlWithdrawalAmount  = 0.0;
			double grndTtlShareAfterWithdrawal = 0.0;
			double grndTtlDividend  = 0.0;
			double grndTtlBalanceFwdNextYear  = 0.0;
			
			// delete existing records from summary table
			Connection mySqlConnection = getConnection(logFileWriter);
			Statement stmtDeleteSummaryTable = mySqlConnection.createStatement();
			long result = stmtDeleteSummaryTable.executeLargeUpdate("DELETE FROM CONTRIBUTION_SUMMARY");
			logDate = new Date();
			logFileWriter.write(simpleDateFormat.format(logDate) + "\tDeleted Summary Table with result code: " + result + "\n");
			
			Statement stmtPopulateSummaryTable = mySqlConnection.createStatement();
			String insertSummaryTable = "insert into contribution_summary "
					+ "SELECT b.ssn, a.employer_id,  sum(b.share_Amount), sum(b.reserve_amount), '0','0','0','0' FROM contribution_entry a, "
					+ "`contribution_entry_detail` b WHERE "
					+ "a.Cash_Received_Date LIKE '%" + year + "%' and "
					+ "a.contribution_id = b.contribution_entry_id "
					+ "group by b.ssn, a.employer_id";
			result = stmtPopulateSummaryTable.executeLargeUpdate(insertSummaryTable);
			logDate = new Date();
			logFileWriter.write(simpleDateFormat.format(logDate) + "\tInserted Summary Table with result code: " + result + "\n");
			
			HashMap<String,String> hsEmployers = getAllEmployers(logFileWriter);
			HashMap<String, BalanceSummary> hsBalanceSummary = getAllBalanceSummary(logFileWriter);
			HashMap<String, WithdrawalSummary> hsWithdrawalSummary = getAllWithdrawalSummary(year, logFileWriter);
			
			// populate summary table
	
	        for (Map.Entry<String, String> entry : hsEmployers.entrySet()) {
	            String companyCode = entry.getKey();
	            String companyName = entry.getValue();

	            System.out.println("Started calculations for : " + companyCode + "\t" + companyName);
	            logDate = new Date();
	            logFileWriter.write(simpleDateFormat.format(logDate) + "Company Code =" + companyCode + ", Company Name =" + companyName + "\n");
	            
	            sbEmployerEmployee.append(System.lineSeparator());
	            sbEmployerEmployee.append(System.lineSeparator());
	            sbEmployerEmployee.append("`Company Code`" +"," + "`" + companyCode + "`" );
	            sbEmployerEmployee.append(System.lineSeparator());
	            sbEmployerEmployee.append("`Company Name`" +"," + "`" + companyName + "`" );
	            sbEmployerEmployee.append(System.lineSeparator());            
				
				double subTtlEmployerContribution  = 0.0;
				double subTtlEmployerShareAmount  = 0.0;
				double subTtlEmployerReserveAmount  = 0.0;
				double subTtlEmployerWithdrawalAmount  = 0.0;
				double subTtlEmployerShareAfterWithdrawal = 0.0;
				double subTtlEmployerDividend  = 0.0;
				double subTtlEmployerBalanceFwdNextYear  = 0.0;
				
				for(int indexStatuses = 0; indexStatuses < lstEmployeeStatuses.size(); ++ indexStatuses) {
					double subTtlContribution = 0.0;
					double subTtlShareAmount = 0.0;
					double subTtlReserveAmount = 0.0;
					double subTtlWithdrawalAmount = 0.0;
					double subTtlShareAfterWithdrawal = 0.0;
					double subTtlDividend = 0.0;
					double subTtlBalanceFwdNextYear = 0.0;
					
					sbEmployerEmployee.append(System.lineSeparator());
					sbEmployerEmployee.append("`Employee Status`" + "," + "`" + lstEmployeeStatuses.get(indexStatuses) + "`");
					sbEmployerEmployee.append(System.lineSeparator());

					sbEmployerEmployee.append("`S.No`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`SSN`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`Name`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`Total Contrbution`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`Total Share`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`Total Reserve`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`Withdrawal`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`Balance Before Dividend`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`Dividend`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`Balance after Dividend`");
					sbEmployerEmployee.append(System.lineSeparator());
					
					ArrayList<Employee> lstEmployees = getAllEmployees(companyCode, lstEmployeeStatuses.get(indexStatuses), logFileWriter);
					for(int i = 0; i<lstEmployees.size(); ++i) {
						Employee employee = lstEmployees.get(i);
						String ssn = employee.getSsn();
						String fullName = employee.getFirstName() + " " + employee.getInitial() + " " + employee.getLastName();
						double balanceForward = Double.parseDouble(employee.getBalanceForward());
						double totalContribution = 0.0;
						double totalReserveAmount = 0.0;
						double withdrawalAmount = 0.0;
						
						
						BalanceSummary balanceSummary = hsBalanceSummary.get(ssn);
						
						if(balanceSummary == null ) {
							logDate = new Date();
							logFileWriter.write(simpleDateFormat.format(logDate) + "Unable to find balance Summary for: " + ssn + " with company code: " + companyCode + "\n");
//							System.out.println("Unable to find balance Summary for: " + ssn + " with company code: " + companyCode);
							totalContribution = 0.0;
							totalReserveAmount = 0.0;
						} else {
							totalContribution = Double.parseDouble(balanceSummary.getShareAmount());
							totalReserveAmount = Double.parseDouble(balanceSummary.getReserveAmount());
						}
						
						WithdrawalSummary withdrawalSummary = hsWithdrawalSummary.get(ssn);
						
						if(withdrawalSummary == null) {
							withdrawalAmount = 0.0;
						} else {
							withdrawalAmount = Double.parseDouble(withdrawalSummary.getWithdrawalAmount());
						}
						
						double totalShare = balanceForward + totalContribution; 
						
						double totalShareAfterWithdrawal = totalShare - withdrawalAmount;
//						double dividendFactor = 0.05; // HARD CODED. WILL CHANGE EVERY YEAR
						double totalDividend = totalShareAfterWithdrawal *  dividendFactor; 
						double balanceFwdNextYear = totalShareAfterWithdrawal + totalDividend; 
						if(balanceFwdNextYear < 0) {
							totalDividend = 0.0;
							balanceFwdNextYear = 0.0;
						} // If negative set the dividend and balance fwd for next year as zero
						
						
						// insert total share, totalDividend and balanceFwdNextYear to the summary table.
						// if the record is found in summary table, do an update
						// if the record is not found, do an insert
						
						totalContribution = Math.round(totalContribution * 100.0) / 100.0;
						totalReserveAmount = Math.round(totalReserveAmount * 100.0) / 100.0;
						totalShare = Math.round(totalShare * 100.0) / 100.0;
						totalDividend = Math.round(totalDividend * 100.0) / 100.0;
						balanceFwdNextYear = Math.round(balanceFwdNextYear * 100.0) / 100.0;
						
						Statement stmtInsertUpdateSummaryTable = mySqlConnection.createStatement(); 
						String strInsertUpdateSummaryTable = "";
						if(balanceSummary == null ) {
							// insert
							strInsertUpdateSummaryTable ="INSERT INTO `contribution_summary`" +
							"(`SSN`, " +
							"`employer_id`, " +
							"`total_contribution`, " +
							"`reserve_amount`, " +
							"`total_share`, " +
							"`dividend`, " +
							"`balance_after_dividend`, " +
							"`withdrawal_amount`) " +
							"VALUES " +
							"('" + ssn + "','" + companyCode + "'," + "'" + totalContribution + "','" + totalReserveAmount + "','" +
							totalShare + "','" + totalDividend + "','" + balanceFwdNextYear + "','" + withdrawalAmount + "')";


						} else {
							// update
							strInsertUpdateSummaryTable ="UPDATE `contribution_summary`" + 
							"SET" + 
							"`total_share` = '" + totalShare + "'," +
							"`dividend` = '" + totalDividend + "'," +
							"`balance_after_dividend` = '" + balanceFwdNextYear + "'," +
							
							"`withdrawal_amount` = '" + withdrawalAmount +
							"' WHERE ssn = '" + ssn + "'";

							
						}
//						System.out.println(strInsertUpdateSummaryTable);
						
						result = stmtInsertUpdateSummaryTable.executeLargeUpdate(strInsertUpdateSummaryTable);
						logDate = new Date();
						logFileWriter.write(simpleDateFormat.format(logDate) + "Inserted Summary Table with new employee : " + result + "\n");
						
						
						subTtlContribution += totalContribution;
						subTtlShareAmount += totalShare;
						subTtlReserveAmount += totalReserveAmount;
						subTtlWithdrawalAmount += withdrawalAmount;
						subTtlShareAfterWithdrawal+= totalShareAfterWithdrawal;
						subTtlDividend += totalDividend;
						subTtlBalanceFwdNextYear += balanceFwdNextYear;

						int serialNumber = i + 1;
						sbEmployerEmployee.append("`" + serialNumber + "`");
						sbEmployerEmployee.append(",");
						sbEmployerEmployee.append("`" + ssn + "`");
						sbEmployerEmployee.append(",");
						sbEmployerEmployee.append("`" + fullName + "`");
						sbEmployerEmployee.append(",");
						sbEmployerEmployee.append("`" + df.format(totalContribution) + "`");
						sbEmployerEmployee.append(",");
						sbEmployerEmployee.append("`" + df.format(totalShare) + "`");
						sbEmployerEmployee.append(",");
						sbEmployerEmployee.append("`" + df.format(totalReserveAmount) + "`");
						sbEmployerEmployee.append(",");
						sbEmployerEmployee.append("`" + df.format(withdrawalAmount) + "`");
						sbEmployerEmployee.append(",");
						sbEmployerEmployee.append("`" + df.format(totalShareAfterWithdrawal) + "`");
						sbEmployerEmployee.append(",");
						sbEmployerEmployee.append("`" + df.format(totalDividend) + "`");
						sbEmployerEmployee.append(",");
						sbEmployerEmployee.append("`" + df.format(balanceFwdNextYear) + "`");
						sbEmployerEmployee.append(System.lineSeparator());

					}
					sbEmployerEmployee.append(System.lineSeparator());
					sbEmployerEmployee.append("` `");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`Total`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("` `");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`" + df.format(subTtlContribution) + "`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`" + df.format(subTtlShareAmount) + "`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`" + df.format(subTtlReserveAmount) + "`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`" + df.format(subTtlWithdrawalAmount) + "`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`" + df.format(subTtlShareAfterWithdrawal) + "`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`" + df.format(subTtlDividend) + "`");
					sbEmployerEmployee.append(",");
					sbEmployerEmployee.append("`" + df.format(subTtlBalanceFwdNextYear) + "`");
					sbEmployerEmployee.append(System.lineSeparator());
					
					subTtlEmployerContribution += subTtlContribution;
					subTtlEmployerShareAmount += subTtlShareAmount;
					subTtlEmployerReserveAmount += subTtlReserveAmount;
					subTtlEmployerWithdrawalAmount += subTtlWithdrawalAmount;
					subTtlEmployerShareAfterWithdrawal+= subTtlShareAfterWithdrawal;
					subTtlEmployerDividend += subTtlDividend;
					subTtlEmployerBalanceFwdNextYear += subTtlBalanceFwdNextYear;
				}
								
				sbEmployerEmployee.append(System.lineSeparator());
				sbEmployerEmployee.append(System.lineSeparator());
				sbEmployerEmployee.append("`" + companyCode + " / " + companyName + "`");
				sbEmployerEmployee.append(System.lineSeparator());
				sbEmployerEmployee.append("`Contribution: " + df.format(subTtlEmployerContribution) + "`");
				sbEmployerEmployee.append(",");
				sbEmployerEmployee.append("`Share" + df.format(subTtlEmployerShareAmount) + "`");
				sbEmployerEmployee.append(",");
				sbEmployerEmployee.append("`Reserve: " + df.format(subTtlEmployerReserveAmount) + "`");
				sbEmployerEmployee.append(",");
				sbEmployerEmployee.append("`Withdrawal: " + df.format(subTtlEmployerWithdrawalAmount) + "`");
				sbEmployerEmployee.append(",");
				sbEmployerEmployee.append("`Balance Before dividend: " + df.format(subTtlEmployerShareAfterWithdrawal) + "`");
				sbEmployerEmployee.append(",");
				sbEmployerEmployee.append("`Dividend: " + df.format(subTtlEmployerDividend) + "`");
				sbEmployerEmployee.append(",");
				sbEmployerEmployee.append("`Balance after dividend: " + df.format(subTtlEmployerBalanceFwdNextYear) + "`");
				sbEmployerEmployee.append(System.lineSeparator());
				
				grndTtlContribution += subTtlEmployerContribution;
				grndTtlEmployerShareAmount += subTtlEmployerShareAmount;
				grndTtlReserveAmount += subTtlEmployerReserveAmount;
				grndTtlWithdrawalAmount += subTtlEmployerWithdrawalAmount;
				grndTtlShareAfterWithdrawal+= subTtlEmployerShareAfterWithdrawal;
				grndTtlDividend += subTtlEmployerDividend;
				grndTtlBalanceFwdNextYear += subTtlEmployerBalanceFwdNextYear;
				
	            System.out.println("Completed calculations for : " + companyCode + "\t" + companyName);
	            logDate = new Date();
	            logFileWriter.write(simpleDateFormat.format(logDate) + "Company Code =" + companyCode + ", Company Name =" + companyName + "\n");
	            
	            
	            
	        }  //Employer loop ends

	        
	        
	        sbHeader.append(System.lineSeparator());
	        sbHeader.append(System.lineSeparator());
	        sbHeader.append("`Contribution`" + "," + "`"+ df.format(grndTtlContribution) + "`");
	        sbHeader.append(System.lineSeparator());
	        sbHeader.append("`Share`" + "," + "`" + df.format(grndTtlEmployerShareAmount) + "`");
	        sbHeader.append(System.lineSeparator());
	        sbHeader.append("`Reserve`" + "," + "`" + df.format(grndTtlReserveAmount) + "`");
	        sbHeader.append(System.lineSeparator());
	        sbHeader.append("`Withdrawal`" + "," + "`" + df.format(grndTtlWithdrawalAmount) + "`");
	        sbHeader.append(System.lineSeparator());
	        sbHeader.append("`Balance before dividend`" + "," + "`" + df.format(grndTtlShareAfterWithdrawal) + "`");
	        sbHeader.append(System.lineSeparator());
	        sbHeader.append("`Dividend`" + "," + "`"+ df.format(grndTtlDividend) + "`");
	        sbHeader.append(System.lineSeparator());
	        sbHeader.append("`Balance after dividend`" + "," + "`" + df.format(grndTtlBalanceFwdNextYear) + "`");
	        sbHeader.append(System.lineSeparator());
	        sbHeader.append(System.lineSeparator());
	        
	        String header = sbHeader.toString().replaceAll("`", "\"");
	        String employerEmployee = sbEmployerEmployee.toString().replaceAll("`", "\"");
			fileWriter.write(header + employerEmployee);

			

			
			Statement stmtDeleteDividendYearMap = mySqlConnection.createStatement();
			String deleteDividendYearMap = "delete from dividend_year_map where year = " + year;
			result = stmtDeleteDividendYearMap.executeLargeUpdate(deleteDividendYearMap);
			logDate = new Date();
			logFileWriter.write(simpleDateFormat.format(logDate) + "\tDeleted if any record is present in Dividend Year Map table with result code: " + result + "\n");			
			
			Statement stmtDividendYearMap = mySqlConnection.createStatement();
			String insertDividendYearMap = "insert into dividend_year_map values (" + year + ",'" + dividendFactor + "','O')";
			result = stmtDividendYearMap.executeLargeUpdate(insertDividendYearMap);
			logDate = new Date();
			logFileWriter.write(simpleDateFormat.format(logDate) + "\tInserted Dividend Year Map table with result code: " + result + "\n");
			
			stmtDeleteDividendYearMap.close();
			stmtDividendYearMap.close();
			fileWriter.close();
			logFileWriter.close();
			
			System.out.println("");
			System.out.println("Report has been generated successfully");
			System.out.print("Please open the report from the following location:");
			System.out.println(filename);
			//System.out.println("<a src=" + filename + "> Dividend Report </a>");
			System.out.println("Click Close button to close this window");

			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	private static HashMap<String, WithdrawalSummary> getAllWithdrawalSummary(int year, FileWriter logfile) throws Exception{
		String pattern = "MM-dd-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date logDate = new Date();
		logfile.write(simpleDateFormat.format(logDate) + "\tEntering getAllWithdrawalSummary()\n");
		
		
		HashMap<String, WithdrawalSummary> hsWithdrawalSummary = new HashMap<String, WithdrawalSummary>();
		Connection mySqlConnection = getConnection(logfile);
		Statement stmtWithdrawalSummary = mySqlConnection.createStatement();
		ResultSet rsWithdrawalSummary = stmtWithdrawalSummary.executeQuery("select SSN, WITHDRAWAL_AMOUNT from withdrawal_entry where withdrawal_date_str like '%" + year +"%'");		
		while (rsWithdrawalSummary.next()) {
			String ssn = rsWithdrawalSummary.getString("SSN");
			WithdrawalSummary withdrawalSummary = new WithdrawalSummary();
			withdrawalSummary.setSsn(rsWithdrawalSummary.getString("SSN"));
			withdrawalSummary.setWithdrawalAmount(rsWithdrawalSummary.getString("WITHDRAWAL_AMOUNT"));
			hsWithdrawalSummary.put(ssn, withdrawalSummary);
		}
		
		logfile.write(simpleDateFormat.format(logDate) + "\tExiting getAllWithdrawalSummary()\n");
		return hsWithdrawalSummary;
	}


	private static Connection getConnection(FileWriter logfile) throws Exception{
		String pattern = "MM-dd-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date logDate = new Date();
		logfile.write(simpleDateFormat.format(logDate) + "\tEntering getConnection()\n");
		
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
		ResultSet rsCompany = stmtCompany.executeQuery("SELECT EMPLOYER_ID, EMPLOYER_NAME FROM EMPLOYER ORDER BY EMPLOYER_ID");
		while(rsCompany.next()) {
			String companyCode = rsCompany.getString("EMPLOYER_ID");
			String companyName = rsCompany.getString("EMPLOYER_NAME");
			hsEmployers.put(companyCode, companyName);
		}
		
		if(mySqlConnection!=null) {
			mySqlConnection.close();
		}
		
		logfile.write(simpleDateFormat.format(logDate) + "\tExiting getAllEmployers()\n");
		return hsEmployers;
	}
	
	private static ArrayList<Employee> getAllEmployees(String companyCode, String status, FileWriter logfile) throws Exception {
		String pattern = "MM-dd-yyyy HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date logDate = new Date();
		logfile.write(simpleDateFormat.format(logDate) + "\tEntering getAllEmployees()\n");
		
		ArrayList<Employee> lstAllEmployees = new ArrayList<Employee> ();
		Connection mySqlConnection = getConnection(logfile);
		Statement stmtEmployee = mySqlConnection.createStatement();
		String strEmployee = "SELECT * FROM EMPLOYEE WHERE EMPLOYER_ID =" + companyCode +  " AND STATUS = '" + status + "'";
		ResultSet rsEmployee = stmtEmployee.executeQuery(strEmployee);
		while(rsEmployee.next()) {
			Employee employee = new Employee();
			employee.setSsn(rsEmployee.getString("SSN"));
			employee.setCompanyCode(rsEmployee.getString("EMPLOYER_ID"));
			employee.setFirstName(rsEmployee.getString("FIRST_NAME"));
			employee.setLastName(rsEmployee.getString("LAST_NAME"));
			employee.setInitial(rsEmployee.getString("MIDDLE_NAME"));
			employee.setAddress(rsEmployee.getString("ADDRESS"));
			employee.setCity(rsEmployee.getString("CITY"));
			employee.setState(rsEmployee.getString("STATE"));
			employee.setZip(rsEmployee.getString("ZIP"));
			employee.setStatus(rsEmployee.getString("STATUS"));
			employee.setBalanceForward(rsEmployee.getString("BALANCE_FORWARD"));
			lstAllEmployees.add(employee);			
		}		
		
		if(mySqlConnection!=null) {
			mySqlConnection.close();
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
			String ssn = rsBalanceSummary.getString("SSN");
			BalanceSummary balanceSummary = new BalanceSummary();
			//balanceSummary.setBalanceForward(rsBalanceSummary.getString("BALANCE_FORWARD"));
			if(hsBalanceSummary.containsKey(ssn)) {
				
				logDate = new Date();
				logfile.write(simpleDateFormat.format(logDate) + "\tDuplicates for Summary existed: " + ssn + " - Employer Id: " + rsBalanceSummary.getString("EMPLOYER_ID") + "\n");
//				System.out.println("Duplicates for Summary existed: " + ssn + " - Employer Id: " + rsBalanceSummary.getString("EMPLOYER_ID"));
				balanceSummary = hsBalanceSummary.get(ssn);
				double existingReserveAmount = Double.parseDouble(balanceSummary.getReserveAmount());
				double existingShareAmount = Double.parseDouble(balanceSummary.getShareAmount());
				double newReserveAmount = existingReserveAmount + Double.parseDouble(rsBalanceSummary.getString("RESERVE_AMOUNT"));
				double newShareAmount = existingShareAmount + Double.parseDouble(rsBalanceSummary.getString("total_contribution"));
				balanceSummary.setReserveAmount(String.valueOf(newReserveAmount));
				balanceSummary.setShareAmount(String.valueOf(newShareAmount));
				
			} else {
				
				balanceSummary.setSsn(rsBalanceSummary.getString("SSN"));
				balanceSummary.setShareAmount(rsBalanceSummary.getString("total_contribution"));
				balanceSummary.setReserveAmount(rsBalanceSummary.getString("RESERVE_AMOUNT"));
			}
			hsBalanceSummary.put(ssn, balanceSummary);
		}
		
		if(mySqlConnection!=null) {
			mySqlConnection.close();
		}
		
		logfile.write(simpleDateFormat.format(logDate) + "\tExiting getAllBalanceSummary()\n");
		return hsBalanceSummary;
	}

}
