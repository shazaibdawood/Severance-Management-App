package com.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
	private static String user;
	private static String password;
	private static String url;
	public static String app_server;
	public static String dividendLogFilePath;
	public static String dividendDataFilePath;
	public static String statementLogFilePath;
	public static String statementTemplateFilePath;
	public static String statementDataFilePath;
	public static String contractFilePath;
	public static String eligibilityDispayTemplateFile;
	public static String eligibilityDisplayReportFile;
	public static String csvTemplateFile;
	public static String employeeDetailsTemplateFile;
	public static String employeeDetailsReportFile;

	public static void loadProperties() {
		String serverName, portNumber, databaseName;
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream("C://Severence//config//application.properties"));
			serverName = prop.getProperty("SERVER_NAME");
			portNumber = prop.getProperty("PORT_NUMBER");
			databaseName = prop.getProperty("DATABASE_NAME");
			user = prop.getProperty("USER");
			password = prop.getProperty("PASSWORD");
			app_server = prop.getProperty("APP_SERVER");
			dividendLogFilePath = prop.getProperty("DIVIDEND_LOG_FILE");
			dividendDataFilePath = prop.getProperty("DIVIDEND_DATA_FILE");
			statementLogFilePath = prop.getProperty("STATEMENTS_LOG_FILE");
			statementTemplateFilePath = prop.getProperty("STATEMENT_TEMPLATE_FILE");
			statementDataFilePath = prop.getProperty("STATEMENT_DATA_FILE_PATH");
			contractFilePath = prop.getProperty("CONTRACT_FILE_PATH");
			eligibilityDispayTemplateFile = prop.getProperty("ELIGIBILITY_DISPLAY_TEMPLATE_FILE");
			eligibilityDisplayReportFile = prop.getProperty("ELIGIBILITY_DISPLAY_REPORT_FILE");
			csvTemplateFile = prop.getProperty("CSV_TEMPLATE_FILE");
			employeeDetailsTemplateFile=prop.getProperty("EMPLOYEE_DETAILS_TEMPLATE_FILE");
			employeeDetailsReportFile=prop.getProperty("EMPLOYEE_DETAILS_REPORT_FILE");
			
			
			url = "jdbc:mysql://" + serverName+ ":" + portNumber+ "/" + databaseName;


		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}



	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	
}
