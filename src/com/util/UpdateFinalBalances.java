package com.util;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UpdateFinalBalances {
	
	public static void main(String args[]) {
		UpdateFinalBalances.updateBalances();
	}
	
	public static void updateBalances() {
		try {
			String pattern = "MM-dd-yyyy_HH-mm-ss";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			Date currentDate = new Date();
			
			String logFileName = simpleDateFormat.format(currentDate) + "_updateBalanceForward.txt";
			String logFilePath = "C://Severence//UpdateBalances//Logs//" + logFileName;
			
			
			File file = new File(logFilePath);
			file.createNewFile();
			
			FileWriter logfile = new FileWriter(file);
			System.out.println("Started deleting history records....");
			
			Connection mySqlConnection = getConnection(logfile);

			Statement stmtDeleteOldBalances = mySqlConnection.createStatement();
			long result = stmtDeleteOldBalances.executeLargeUpdate("delete from employee_balance_history where year = (select max(year) from dividend_year_map)");
			stmtDeleteOldBalances.close();
			
			currentDate = new Date();
			logfile.write(simpleDateFormat.format(currentDate) + "\tDeleted old Balance Forwards with result code: " + result + "\n");

			System.out.println("Deleted all history records successfully....");
			
			
			
			// inserting new balances
			
			System.out.println("Inserting New balances in History table");
			Statement stmtInsertNewBalances = mySqlConnection.createStatement();
			result = stmtInsertNewBalances.executeLargeUpdate("insert into employee_balance_history(ssn, year, beginning_balance) "
					+ "select a.ssn, max(b.year), a.balance_after_dividend from contribution_summary a, dividend_year_map b "
					+ "group by a.ssn, a.balance_after_dividend");
			currentDate = new Date();
			logfile.write(simpleDateFormat.format(currentDate) + "\tInserted Balance Forwards with result code: " + result + "\n");
			stmtInsertNewBalances.close();
			
			System.out.println("Inserted ne records in history table");
			
			// getting the year
			String year = "";
			Statement stmtGetMaxYear = mySqlConnection.createStatement();
			ResultSet yearSet = stmtGetMaxYear.executeQuery("select max(year) as year from dividend_year_map;");
			if(yearSet.next()) {
				year = yearSet.getString("year");
			}
//			currentDate = new Date();
//			logfile.write(simpleDateFormat.format(currentDate) + "\tInserted Balance Forwards with result code: " + result + "\n");
			stmtGetMaxYear.close();
			
			System.out.println("Taking a bakcup of existing employee table.....");
			String backupEmployeeTableName = "employee_" + year;
			Statement stmtDeleteBackupEmployeeTable = mySqlConnection.createStatement();
			result = stmtDeleteBackupEmployeeTable.executeLargeUpdate("drop table if exists " + backupEmployeeTableName);
			currentDate = new Date();
			logfile.write(simpleDateFormat.format(currentDate) + "\tDropped old backup Employee table with result code: " + result + "\n");
			stmtDeleteBackupEmployeeTable.close();
			
			
			Statement stmtCopyEmployeeTable = mySqlConnection.createStatement();
			result = stmtCopyEmployeeTable.executeLargeUpdate("CREATE TABLE " + backupEmployeeTableName +" AS SELECT * FROM employee;");
			currentDate = new Date();
			logfile.write(simpleDateFormat.format(currentDate) + "\tCopied new backup Employee table with result code: " + result + "\n");
			stmtCopyEmployeeTable.close();
			System.out.println("Backups taken for employee table ...");
			
			System.out.println("Updating new balances ....");
			Statement stmtGetNewBalance = mySqlConnection.createStatement();
			ResultSet newBalanceForward = stmtGetNewBalance.executeQuery("select ssn, beginning_balance from employee_balance_history where year = " + year);
			int i = 0;
			while (newBalanceForward.next()) {
				++i;
				String ssn = newBalanceForward.getString("SSN");
				String balanceForward = newBalanceForward.getString("beginning_balance");
				
				Statement stmtUpdateNewBalance = mySqlConnection.createStatement();
				result = stmtUpdateNewBalance.executeLargeUpdate("update employee set balance_forward = '" + balanceForward + "' where ssn = '" + ssn + "'");
				currentDate = new Date();
				logfile.write(simpleDateFormat.format(currentDate) + "\tUpdated ssn: " + ssn + " new backup Employee table with result code: " + result + "\n");
				stmtUpdateNewBalance.close();
				
				if(i%100 == 0) {
					System.out.println("Updated " + i + "records so far...");
					
				}
				
			}
			
			System.out.println("Balances updated successfully ....");
//			currentDate = new Date();
//			logfile.write(simpleDateFormat.format(currentDate) + "\tUpdated balance forwards in Employee table with result code: " + result + "\n");
			stmtGetNewBalance.close();
			
			Statement stmtUpdateDividendYearMap = mySqlConnection.createStatement();
			result = stmtUpdateDividendYearMap.executeLargeUpdate("update dividend_year_map set status = 'C' where year = " + year);
			currentDate = new Date();
			logfile.write(simpleDateFormat.format(currentDate) + "\tUpdated Dividend Year Map table with result code: " + result + "\n");
			stmtUpdateDividendYearMap.close();
						
			
			logfile.close();
			mySqlConnection.close();
			System.out.println("Update Balance process completed successfully ....");
			
			System.out.println("Please press Close button to close the window");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
	
}
