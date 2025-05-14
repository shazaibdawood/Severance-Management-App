package com.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Sides;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.jdesktop.swingx.JXDatePicker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.model.ContributionEntryDetailTableModel;
import com.model.ContributionSummaryTableModel;
import com.model.EmployeeTableModel;
import com.model.EmployerTableModel;
import com.model.WithdrawalEntryTableModel;
import com.util.BalanceSummary;
import com.util.DatabaseConnection;
import com.util.Employee;
import com.util.Employer;
import com.util.GenerateStatementOfAccounts;
import com.util.SixConsumer;
import com.util.UpdateFinalBalances;
import com.util.WithdrawalSummary;

public class ManagementApp {
	static String edSSN = "";
	static String edName= "";
	static String edEmployerID= "";
	static String edDateHired= "";
	static String edStatus= "";
	static String edWithdrawalAmount= "";
	static String edWithdrawalDate= "";
	static String edAdditionalDividend= "";
	static String edBalanceForward= "";
	static String edShare= "";
	static String edTotalShare= "";
	static String edTotalReserve= "";
	static HashMap<String, Employer> edMapEmployers;
	
	static Object[][] contributionData = null;
	static Object[][] printData = null;
	
	static JTable contributionTable = null;
	
	/*
	private static JPanel createHomeScreen() {
	    JPanel homePanel = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            // Load and draw the background image
	            
	            ImageIcon icon = new ImageIcon("C://Severence//config//logo1.PNG");
	            ImageIcon groupIcon = new ImageIcon("C://Severence//config//group.PNG");
	            g.drawImage(icon.getImage(), 250, 10, 700, 100, this);
	            g.drawImage(groupIcon.getImage(), 250, 150, 700, 400, this);
	        }
	    };

	    homePanel.setLayout(new BorderLayout());
	    return homePanel;
	}
	*/
	
	/****** latest method 
	private static JPanel createHomeScreen() {
	    JPanel homePanel = new JPanel(new BorderLayout()) {
	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            // Set background color
	            g.setColor(new Color(245, 245, 245)); // Light Gray
	            g.fillRect(0, 0, getWidth(), getHeight());
	            ImageIcon icon = new ImageIcon("C://Severence//config//logo1.PNG");
	            g.drawImage(icon.getImage(), 250, 10, 700, 100, this);
	        }
	    };

	    // Fetch data for contributions in the last month
	    Map<String, Double> contributionData = fetchContributionDataForLastMonth();

	    // Create a bar chart
	    JFreeChart chart = createColorfulBarChart(contributionData);

	    // Wrap the chart in a ChartPanel
	    ChartPanel chartPanel = new ChartPanel(chart);
	    chartPanel.setPreferredSize(new Dimension(800, 400));
	    chartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	    homePanel.add(chartPanel, BorderLayout.SOUTH);

	    return homePanel;
	}
	
	*********/
	private static JPanel homePanel;
	
	private static void refreshHomeScreen() {
		homePanel.removeAll();
		
		homePanel.setBackground(new Color(245, 245, 245)); // Light Gray background

	    // Logo Panel
	    JPanel logoPanel = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            // Draw the logo at the center of the panel
	            ImageIcon icon = new ImageIcon("C://Severence//config//logo1.PNG");
	            g.drawImage(icon.getImage(), (getWidth() - 700) / 2, 10, 700, 100, this);
	        }
	    };
	    logoPanel.setPreferredSize(new Dimension(1000, 120));
	    logoPanel.setBackground(new Color(245, 245, 245)); // Same background as the main panel

	    // Fetch data for contributions in the last month
	    Map<String, Double> contributionData = fetchContributionDataForLastMonth();

	    // Create a bar chart
	    JFreeChart chart = createColorfulBarChart(contributionData);

	    // Wrap the chart in a ChartPanel
	    ChartPanel chartPanel = new ChartPanel(chart);
	    chartPanel.setPreferredSize(new Dimension(600, 400));
	    chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // Create the contribution status panel
//	    JPanel contributionStatusPanel = new JPanel(new BorderLayout());
//	    contributionStatusPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//	    contributionStatusPanel.setBackground(new Color(173, 216, 230)); // Light Blue background
	    
	 // Create the contribution status panel
	    JPanel contributionStatusPanel = new JPanel();
	    contributionStatusPanel.setLayout(new BoxLayout(contributionStatusPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical stacking
	    contributionStatusPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	    contributionStatusPanel.setBackground(new Color(173, 216, 230)); // Light Blue background

	    JLabel employersWithoutContributionsLabel = new JLabel("Employers with no contributions in the past 2 months: ");
	    employersWithoutContributionsLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    employersWithoutContributionsLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    employersWithoutContributionsLabel.setForeground(Color.BLUE);

	    // Fetch all employers
	    EmployerTableModel employerModel = new EmployerTableModel();
	    HashMap<String, Employer> mapEmployers = employerModel.getAllEmployers();
	    edMapEmployers = mapEmployers;
	    
	 // Fetch all employees
	    EmployeeTableModel employeeModel = new EmployeeTableModel();
	    HashMap<String, Employee> mapEmployees = employeeModel.getAllEmployees();
	    
	    // Fetch the count and data for employers with no contributions
	    List<String> nonContributingEmployers = fetchNonContributingEmployers();
	    List<String> nonContributingEmloyersWithNames = new ArrayList<String>();
	    for(int i = 0; i < nonContributingEmployers.size(); ++ i) {
	    	String employerId = nonContributingEmployers.get(i);
	    	Employer employer = mapEmployers.get(employerId);
	    	String employerName = employer.getEmployerName();
	    	nonContributingEmloyersWithNames.add(employerId + " - " + employerName);
	    	
	    }
	    
	    
	    String contributionLabel = "<html>Employers with no contributions in the past 2 months: "
	    		+ "<a href='#'>" + nonContributingEmployers.size() + "</a> out of " + mapEmployers.size() + " </html>";
	    employersWithoutContributionsLabel.setText(contributionLabel);
	    employersWithoutContributionsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
	        @Override
	        public void mouseClicked(java.awt.event.MouseEvent e) {
	            // Show the list of employers in a dialog
	            showNonContributingEmployersDialog(nonContributingEmloyersWithNames);
	        }
	    });
	    
//	    JLabel for employees that withdrew in the past 2 months
	    JLabel employeesThatWithdrewLabel = new JLabel("Employees who have withdrawn in the past 2 months: ");
	    employeesThatWithdrewLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    employeesThatWithdrewLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    employeesThatWithdrewLabel.setForeground(Color.BLUE);
	    
//	    Fetch count and data for employees that withdrew in the past 2 months
	    Map<String, String> withdrawingEmployees = fetchWithdrawingEmployees();
	    List<String> withdrawingEmployeesWithNamesAndAmounts = new ArrayList<>();
        for (Map.Entry<String,String> entry : withdrawingEmployees.entrySet()) {
        	String ssn = entry.getKey();
        	String amount = entry.getValue();
        	
	        Employee employee = mapEmployees.get(ssn);
	        String employeeName = employee.getFirstName() + " " + employee.getLastName();
	        withdrawingEmployeesWithNamesAndAmounts.add(employee.getEmployerId() + " - " + employeeName + " - $" + amount);
	    }

	    String withdrawalLabel = "<html>Employees who have withdrawn in the past 2 months: "
	            + "<a href='#'>" + withdrawingEmployees.size() + "</a> out of " + mapEmployees.size() + "</html>";
	    employeesThatWithdrewLabel.setText(withdrawalLabel);

	    employeesThatWithdrewLabel.addMouseListener(new java.awt.event.MouseAdapter() {
	        @Override
	        public void mouseClicked(java.awt.event.MouseEvent e) {
	            // Show the list of employers who withdrew in a dialog
	            showWithdrawingEmployeesDialog(withdrawingEmployeesWithNamesAndAmounts);
	        }
	    });

	 // Add labels to the contributionStatusPanel in the desired order
	    contributionStatusPanel.add(employersWithoutContributionsLabel);
	    contributionStatusPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing between the labels
	    contributionStatusPanel.add(employeesThatWithdrewLabel);
	    //contributionStatusPanel.add(employersCountLink, BorderLayout.CENTER);

	    // Create a wrapper panel to place chartPanel and contributionStatusPanel side by side
	    JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // 1 row, 2 columns
	    contentPanel.add(contributionStatusPanel);
	    contentPanel.add(chartPanel);
	    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around content

	    // Add the panels to the main home panel
	    homePanel.add(logoPanel, BorderLayout.NORTH); // Logo at the top
	    homePanel.add(contentPanel, BorderLayout.CENTER); // Content below the logo
	}
	
	private static JPanel createHomeScreen() {
	    // Main panel for the home screen
	    homePanel = new JPanel(new BorderLayout());
	    refreshHomeScreen();

	    return homePanel;
	}

	// Helper methods

	private static List<String> fetchNonContributingEmployers() {
	    List<String> employers = new ArrayList<>();
	    String query = """
	            SELECT DISTINCT e.Employer_ID
	            FROM employer e
	            WHERE NOT EXISTS (
	                SELECT 1 FROM contribution_entry ce
	                WHERE e.Employer_ID = ce.Employer_ID
	                  AND STR_TO_DATE(ce.Cash_Received_Date, '%Y-%m-%d') >= DATE_SUB(CURDATE(), INTERVAL 2 MONTH)
	            )
	        """;
	    try (Connection conn = DatabaseConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {

	        while (rs.next()) {
	            employers.add(rs.getString("Employer_ID"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return employers;
	}
	/***
	 * Added by Shazaib Dawood
	 * This method is to fetch all the employees that withdrew in the last 2 months
	 * @return
	 */
	private static Map<String, String> fetchWithdrawingEmployees() {
		Map<String, String> employees = new HashMap<>();
	    String query = """
	            SELECT DISTINCT e.ssn, e.withdrawal_amount
	            FROM withdrawal_entry e
	            WHERE STR_TO_DATE(e.withdrawal_Date, '%Y-%m-%d') >= DATE_SUB(CURDATE(), INTERVAL 2 MONTH)
	        """;
	    try (Connection conn = DatabaseConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {

	        while (rs.next()) {
	        	String ssn = rs.getString("ssn");
	            String withdrawalAmount = rs.getString("withdrawal_amount");
	            employees.put(ssn, withdrawalAmount);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return employees;
	}

	private static void showNonContributingEmployersDialog(List<String> employers) {
	    JDialog dialog = new JDialog((Frame) null, "Non-Contributing Employers", true);
	    dialog.setSize(300, 300);
	    dialog.setLayout(new BorderLayout());
	    if (employers.isEmpty()) {
	        JLabel message = new JLabel("All employers have contributed in the last 2 months.", SwingConstants.CENTER);
	        dialog.add(message, BorderLayout.CENTER);
	    } else {
	        DefaultListModel<String> listModel = new DefaultListModel<>();
	        for (String employer : employers) {
	            listModel.addElement(employer);
	        }
	        JList<String> employerList = new JList<>(listModel);
	        JScrollPane scrollPane = new JScrollPane(employerList);
	        scrollPane.getViewport().setBackground(Color.LIGHT_GRAY); // Set the viewport background
            scrollPane.setBackground(Color.DARK_GRAY);                // Set the scroll pane background

	        dialog.add(scrollPane, BorderLayout.CENTER);
	    }

	    JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(244, 164, 96)); // Light Yellow (RGB: 255, 255, 153)
        closeButton.setForeground(Color.BLACK); // Black text
	    closeButton.addActionListener(e -> dialog.dispose());
	    dialog.add(closeButton, BorderLayout.SOUTH);

	    dialog.setLocationRelativeTo(null);
	    dialog.setVisible(true);
	}
	
	private static void showWithdrawingEmployeesDialog(List<String> employees) {
	    JDialog dialog = new JDialog((Frame) null, "Employees Who Withdrew", true);
	    dialog.setSize(300, 300);
	    dialog.setLayout(new BorderLayout());
	    if (employees.isEmpty()) {
	        JLabel message = new JLabel("No employees withdrew in the last 2 months.", SwingConstants.CENTER);
	        dialog.add(message, BorderLayout.CENTER);
	    } else {
	        DefaultListModel<String> listModel = new DefaultListModel<>();
	        for (String employee : employees) {
	            listModel.addElement(employee);
	        }
	        JList<String> employeeList = new JList<>(listModel);
	        JScrollPane scrollPane = new JScrollPane(employeeList);
	        scrollPane.getViewport().setBackground(Color.LIGHT_GRAY);
	        scrollPane.setBackground(Color.DARK_GRAY);

	        dialog.add(scrollPane, BorderLayout.CENTER);
	    }

	    JButton closeButton = new JButton("Close");
	    closeButton.setBackground(new Color(244, 164, 96));
	    closeButton.setForeground(Color.BLACK);
	    closeButton.addActionListener(e -> dialog.dispose());
	    dialog.add(closeButton, BorderLayout.SOUTH);

	    dialog.setLocationRelativeTo(null);
	    dialog.setVisible(true);
	}



	// Fetch data from the database
	private static Map<String, Double> fetchContributionDataForLastMonth() {
	    Map<String, Double> data = new LinkedHashMap<>();
	    String query = """
	    	    SELECT 
	    	        DATE_FORMAT(STR_TO_DATE(cash_received_date, '%Y-%m-%d'), '%d-%b') AS day, 
	    	        SUM(CAST(summary_amount AS DECIMAL(10, 2))) AS total
	    	    FROM contribution_entry
	    	    WHERE 
	    	        STR_TO_DATE(cash_received_date, '%Y-%m-%d') >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
	    	    GROUP BY DATE_FORMAT(STR_TO_DATE(cash_received_date, '%Y-%m-%d'), '%d-%b');
	    	    """;

	    try (Connection connection = DatabaseConnection.getConnection();
	         PreparedStatement statement = connection.prepareStatement(query);
	         ResultSet rs = statement.executeQuery()) {

	        while (rs.next()) {
	            String day = rs.getString("day");
	            double total = rs.getDouble("total");
	            data.put(day, total);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return data;
	}

	// Create a colorful bar chart
	private static JFreeChart createColorfulBarChart(Map<String, Double> data) {
	    // Create a dataset
	    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	    for (Map.Entry<String, Double> entry : data.entrySet()) {
	        dataset.addValue(entry.getValue(), "Contributions", entry.getKey());
	    }

	    // Create the bar chart
	    JFreeChart chart = ChartFactory.createBarChart(
	            "Contributions - Last Month", // Chart title
	            "Date",                      // Category axis label
	            "Total Contributions ($)",   // Value axis label
	            dataset,                     // Dataset
	            PlotOrientation.VERTICAL,    // Orientation
	            false,                       // Include legend
	            true,                        // Tooltips
	            false                        // URLs
	    );

	    // Customize the chart
	    CategoryPlot plot = chart.getCategoryPlot();
	    plot.setRangeGridlinePaint(Color.GRAY);
	    plot.setBackgroundPaint(Color.WHITE);

	    // Create a renderer to assign different colors
	    BarRenderer renderer = (BarRenderer) plot.getRenderer();
	    renderer.setDefaultBarPainter(new StandardBarPainter()); // Standard painter for crisp colors
	    Color[] colors = {
	            new Color(30, 144, 255), // Dodger Blue
	            new Color(60, 179, 113), // Medium Sea Green
	            new Color(255, 165, 0),  // Orange
	            new Color(255, 69, 0),   // Red
	            new Color(106, 90, 205), // Slate Blue
	            new Color(255, 20, 147)  // Deep Pink
	    };

	    // Assign colors to bars
	    for (int i = 0; i < dataset.getColumnCount(); i++) {
	        renderer.setSeriesPaint(i % colors.length, colors[i % colors.length]);
	    }

	    return chart;
	}

	public static void main(String[] args) {
	    DatabaseConnection.loadProperties();
	    SwingUtilities.invokeLater(() -> {
	        JFrame frame = new JFrame("Management Application");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setSize(1000, 600);

	        // Set the window to maximized by default
	        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

	        JPanel homePanel = createHomeScreen();
	        JPanel mainPanel = new JPanel(new CardLayout());
	        JPanel employerPanel = createEmployerManagementPanel();
	        JPanel employeePanel = createEmployeeManagementPanel();
	        JPanel eligibilityDisplayPanel = createEligibilityDisplayPanel();
	        JPanel contributionSummaryPanel = createContributionSummaryPanel();
	        JPanel withdrawalEntriesPanel = createWithdrawalEntriesPanel();
	        JPanel yearEndReportPanel = createYearEndReportPanel();

	        mainPanel.add(homePanel, "Home");
	        mainPanel.add(employerPanel, "EmployerManagement");
	        mainPanel.add(employeePanel, "EmployeeManagement");
	        mainPanel.add(eligibilityDisplayPanel, "EligibilityDisplay");
	        mainPanel.add(contributionSummaryPanel, "ContributionSummary");
	        mainPanel.add(withdrawalEntriesPanel, "WithdrawalEntries");
	        mainPanel.add(yearEndReportPanel, "YearEndReport");

	        // Create the menu bar
	        JMenuBar menuBar = new JMenuBar();
	        menuBar.setBackground(new Color(30, 144, 255)); // Dodger Blue
	        menuBar.setForeground(Color.WHITE);
	        menuBar.setOpaque(true);

	        JMenu navigationMenu = new JMenu("Master Maintenance");
	        navigationMenu.setForeground(Color.WHITE);
	        navigationMenu.setBackground(new Color(135, 206, 250)); // Light blue background
	        navigationMenu.setFont(new Font("Arial", Font.BOLD, 14));

	        JMenuItem employerMenuItem = new JMenuItem("Employer Master Maintenance");
	        JMenuItem employeeMenuItem = new JMenuItem("Employee Master Maintenance");

	        JMenu reportsMenu = new JMenu("Reports");
	        reportsMenu.setForeground(Color.WHITE);
	        reportsMenu.setBackground(new Color(100, 200, 120)); // Medium Sea Green
	        reportsMenu.setFont(new Font("Arial", Font.BOLD, 14));

	        JMenuItem eligibilityDisplayMenuItem = new JMenuItem("Eligibility Display");

	        JMenu contributionMenu = new JMenu("Contribution");
	        contributionMenu.setForeground(Color.WHITE);
	        contributionMenu.setBackground(new Color(60, 179, 113)); // Medium Sea Green
	        contributionMenu.setFont(new Font("Arial", Font.BOLD, 14));

	        JMenuItem contributionSummaryMenuItem = new JMenuItem("Contribution Summary");

	        JMenu withdrawalMenu = new JMenu("Withdrawal Entries");
	        withdrawalMenu.setForeground(Color.WHITE);
	        withdrawalMenu.setBackground(new Color(255, 127, 80)); // Coral
	        withdrawalMenu.setFont(new Font("Arial", Font.BOLD, 14));

	        JMenuItem withdrawalMenuItem = new JMenuItem("Manage Withdrawal Entries");

	        JMenu yearEndReportMenu = new JMenu("Year End Report");
	        yearEndReportMenu.setForeground(Color.WHITE);
	        yearEndReportMenu.setBackground(new Color(135, 206, 250)); // Light blue background
	        yearEndReportMenu.setFont(new Font("Arial", Font.BOLD, 14));

	        JMenuItem yearEndReportMenuItem = new JMenuItem("Generate Reports");

	        // Action listeners for menu items
	        employerMenuItem.addActionListener(e -> switchToPanel(mainPanel, "EmployerManagement"));
	        employeeMenuItem.addActionListener(e -> switchToPanel(mainPanel, "EmployeeManagement"));
	        eligibilityDisplayMenuItem.addActionListener(e -> switchToPanel(mainPanel, "EligibilityDisplay"));
	        contributionSummaryMenuItem.addActionListener(e -> switchToPanel(mainPanel, "ContributionSummary"));
	        withdrawalMenuItem.addActionListener(e -> switchToPanel(mainPanel, "WithdrawalEntries"));
	        yearEndReportMenuItem.addActionListener(e -> switchToPanel(mainPanel, "YearEndReport"));

	        // Add menu items to menus
	        navigationMenu.add(employerMenuItem);
	        navigationMenu.add(employeeMenuItem);
	        reportsMenu.add(eligibilityDisplayMenuItem);
	        contributionMenu.add(contributionSummaryMenuItem);
	        withdrawalMenu.add(withdrawalMenuItem);
	        yearEndReportMenu.add(yearEndReportMenuItem);

	        // Add menus to menu bar
	        menuBar.add(navigationMenu);
	        menuBar.add(reportsMenu);
	        menuBar.add(contributionMenu);
	        menuBar.add(withdrawalMenu);
	        menuBar.add(yearEndReportMenu);

	        frame.setJMenuBar(menuBar);

	        // Add toolbar for shortcuts
	        JToolBar toolBar = new JToolBar();
	        toolBar.setFloatable(false); // Prevent the toolbar from being moved
	        toolBar.setBackground(new Color(240, 240, 240)); // Light gray
	        toolBar.setPreferredSize(new Dimension(1000, 75)); 

	        // Add shortcut buttons
	        JButton homeButton = createShortcutButtonWithText("Home", "C://Severence//config//home-icon.PNG", e -> switchToPanel(mainPanel, "Home"));
	        JButton employerButton = createShortcutButtonWithText("Employer", "C://Severence//config//employer-icon.PNG", e -> switchToPanel(mainPanel, "EmployerManagement"));
	        JButton employeeButton = createShortcutButtonWithText("Employee", "C://Severence//config//employee-icon.PNG", e -> switchToPanel(mainPanel, "EmployeeManagement"));
	        JButton eligibilityButton = createShortcutButtonWithText("Eligibility", "C://Severence//config//eligibility-icon.PNG", e -> switchToPanel(mainPanel, "EligibilityDisplay"));
	        JButton contributionButton = createShortcutButtonWithText("Contribution", "C://Severence//config//contribution-icon.PNG", e -> switchToPanel(mainPanel, "ContributionSummary"));
	        JButton withdrawalButton = createShortcutButtonWithText("Withdrawal", "C://Severence//config//withdrawal-icon.PNG", e -> switchToPanel(mainPanel, "WithdrawalEntries"));
	        JButton yearEndButton = createShortcutButtonWithText("Year-End", "C://Severence//config//yearend-icon.PNG", e -> switchToPanel(mainPanel, "YearEndReport"));
	        JButton exitButton = createShortcutButtonWithText("Exit", "C://Severence//config//exit-icon.PNG", e -> System.exit(0));

	        // Add buttons to the toolbar
	        toolBar.add(homeButton);
	        toolBar.add(employerButton);
	        toolBar.add(employeeButton);
	        toolBar.add(eligibilityButton);
	        toolBar.add(contributionButton);
	        toolBar.add(withdrawalButton);
	        toolBar.add(yearEndButton);
	        toolBar.add(exitButton);

	        // Add toolbar and main panel to frame
	        frame.add(toolBar, BorderLayout.NORTH);
	        frame.add(mainPanel, BorderLayout.CENTER);

	        // Show the home screen by default
	        switchToPanel(mainPanel, "Home");

	        frame.setVisible(true);
	    });
	}

	// Utility to switch panels in the CardLayout
	private static void switchToPanel(JPanel mainPanel, String panelName) {
	    CardLayout cl = (CardLayout) mainPanel.getLayout();
	    cl.show(mainPanel, panelName);
	}
	
	private static JButton createShortcutButtonWithText(String text, String iconPath, ActionListener action) {
	    // Load the icon
	    ImageIcon originalIcon = new ImageIcon(iconPath);
	    Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
	    ImageIcon scaledIcon = new ImageIcon(scaledImage);

	    // Create the button
	    JButton button = new JButton(text, scaledIcon);
	    button.setToolTipText(text);
	    button.setFocusPainted(false);
	    button.setBorderPainted(true);
	    button.setContentAreaFilled(false);
	    button.setHorizontalTextPosition(SwingConstants.CENTER); // Center text horizontally
	    button.setVerticalTextPosition(SwingConstants.BOTTOM);   // Place text below the icon
	    button.addActionListener(action);

	    return button;
	}

    private static JPanel createEligibilityDisplayPanel() {
    	EmployeeTableModel employeeModel = new EmployeeTableModel();
    	WithdrawalEntryTableModel withdrawalModel = new WithdrawalEntryTableModel();
    	
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Main layout
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around the main panel

        // 1. Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(createColoredTitledBorder("Search Panel", new Color(30, 144, 255))); // Dodger Blue

        JTextField searchField = new JTextField(10);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(144, 238, 144)); // Light Green
        searchButton.setForeground(Color.BLACK);   
        
        JButton clearButton = new JButton("Reset");
        clearButton.setBackground(new Color(173, 216, 255));
        clearButton.setForeground(Color.BLACK); 
        
        JButton printButton = new JButton("Print");
        printButton.setBackground(new Color(255, 215, 0)); // Gold
        printButton.setForeground(Color.BLACK);

        searchPanel.add(new JLabel("Search by SSN or Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        searchPanel.add(printButton);

        // 2. Employee Personal Information Panel
        JPanel personalInfoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        personalInfoPanel.setBorder(createColoredTitledBorder("Employee Personal Information", new Color(60, 179, 113))); // Medium Sea Green
        //personalInfoPanel.setPreferredSize(new Dimension(400, 400));
        
        personalInfoPanel.add(new JLabel("SSN:"));
        JTextField ssnField = new JTextField();
        ssnField.setEditable(false);
        ssnField.setFont(ssnField.getFont().deriveFont(Font.BOLD, 12f));
        personalInfoPanel.add(ssnField);

        personalInfoPanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        nameField.setEditable(false);
        nameField.setFont(ssnField.getFont().deriveFont(Font.BOLD, 12f));
        personalInfoPanel.add(nameField);

        personalInfoPanel.add(new JLabel("Employer ID:"));
        JTextField employerIdField = new JTextField();
        employerIdField.setEditable(false);
        employerIdField.setFont(ssnField.getFont().deriveFont(Font.BOLD, 12f));
        personalInfoPanel.add(employerIdField);

        personalInfoPanel.add(new JLabel("Date Hired:"));
        JTextField dateHiredField = new JTextField();
        dateHiredField.setEditable(false);
        dateHiredField.setFont(ssnField.getFont().deriveFont(Font.BOLD, 12f));
        personalInfoPanel.add(dateHiredField);

        personalInfoPanel.add(new JLabel("Status:"));
        JTextField statusField = new JTextField();
        statusField.setEditable(false);
        statusField.setFont(ssnField.getFont().deriveFont(Font.BOLD, 12f));
        personalInfoPanel.add(statusField);

        // 3. Contribution History Panel
        JPanel contributionHistoryPanel = createContributionHistoryPanel();
        contributionHistoryPanel.setPreferredSize(new Dimension(300, 350));

        // 4. Summary Information Panel
        JPanel summaryInfoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        summaryInfoPanel.setBorder(createColoredTitledBorder("Summary Information", new Color(138, 43, 226))); // Blue Violet

        JLabel lblBalanceFwd = new JLabel("Balance Forward:");
        summaryInfoPanel.add(lblBalanceFwd);
        JTextField balanceForwardField = new JTextField();
        balanceForwardField.setEditable(false);
        summaryInfoPanel.add(balanceForwardField);

        summaryInfoPanel.add(new JLabel("Share:"));
        JTextField shareField = new JTextField();
        shareField.setEditable(false);
        summaryInfoPanel.add(shareField);

        summaryInfoPanel.add(new JLabel("Total Share:"));
        JTextField totalShareField = new JTextField();
        totalShareField.setEditable(false);
        totalShareField.setFont(totalShareField.getFont().deriveFont(Font.BOLD, 14f));
        summaryInfoPanel.add(totalShareField);

        summaryInfoPanel.add(new JLabel("Total Reserve:"));
        JTextField totalReserveField = new JTextField();
        totalReserveField.setEditable(false);
        summaryInfoPanel.add(totalReserveField);

        // 5. Withdrawal Panel
        JPanel withdrawalPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        withdrawalPanel.setBorder(createColoredTitledBorder("Withdrawal Information", new Color(220, 20, 60))); // Crimson
        withdrawalPanel.setPreferredSize(new Dimension(300, 200));

        withdrawalPanel.add(new JLabel("Withdrawal Amount:"));
        JTextField withdrawalAmountField = new JTextField();
        withdrawalAmountField.setEditable(false);
        withdrawalAmountField.setFont(withdrawalAmountField.getFont().deriveFont(Font.BOLD, 12f));
        withdrawalPanel.add(withdrawalAmountField);

        withdrawalPanel.add(new JLabel("Withdrawal Date:"));
        JTextField withdrawalDateField = new JTextField();
        withdrawalDateField.setEditable(false);
        withdrawalDateField.setFont(withdrawalDateField.getFont().deriveFont(Font.BOLD, 12f));
        withdrawalPanel.add(withdrawalDateField);

        withdrawalPanel.add(new JLabel("Additional Dividend:"));
        JTextField additionalDividendField = new JTextField();
        additionalDividendField.setEditable(false);
        additionalDividendField.setFont(additionalDividendField.getFont().deriveFont(Font.BOLD, 12f));
        withdrawalPanel.add(additionalDividendField);

        // Combine Panels
        JPanel infoPanels = new JPanel(new GridLayout(4, 1, 10, 10));
        infoPanels.add(personalInfoPanel);
        infoPanels.add(contributionHistoryPanel);
        infoPanels.add(withdrawalPanel);
        infoPanels.add(summaryInfoPanel);
        
     // Add scroll bar for the entire infoPanels
        JScrollPane scrollPane = new JScrollPane(infoPanels);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        searchButton.addActionListener(e -> {
        	try {
	            String query = searchField.getText().trim();
	            if (!query.isEmpty()) {
					
					Employee employee = null;
					
					employee = employeeModel.searchForEligibility(query);
					if(employee ==  null) {
						JOptionPane.showMessageDialog(mainPanel, "Employee not found.");
					} else if(employee.getSsn().equalsIgnoreCase("X")){
						JOptionPane.showMessageDialog(mainPanel, "More than one employee found. Please refine your search.");
					} else {
						String ssn = employee.getSsn();
						String name = employee.getFirstName() + " " + employee.getLastName();
						String employerId = employee.getEmployerId();
						String dateHired = employee.getDateHired();
						String status = employee.getStatus();
						String balanceForward = employee.getBalanceForward();

						edSSN = ssn;
						edName = name;
						edEmployerID = employerId + " - " + edMapEmployers.get(employerId).getEmployerName();
						edDateHired = dateHired;
						edStatus = status;
						edBalanceForward = balanceForward;
						// populate personal information
						ssnField.setText(ssn);
						nameField.setText(name);
						employerIdField.setText(employerId);
						dateHiredField.setText(dateHired);
						statusField.setText(status);
						
						// populate contribution information
						
						/******** need to be revisted */

						Object[][] newData = getDynamicContributionData(ssn, employerId);
						printData = newData;

				        // Update the table model dynamically
				        try {
							for (int i = 0; i < contributionData.length; i++) {
							    for (int j = 0; j < contributionData[i].length; j++) {
							        contributionData[i][j] = newData[i][j];
							    }
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

				        // Refresh the table display
				        contributionTable.repaint();
				        /************/
						
						
						// populate summary information
				        String year = null;
						Statement stmtGetDividendYearMap = DatabaseConnection.getConnection().createStatement();
						ResultSet rsGetDividendYearMap = stmtGetDividendYearMap.executeQuery("select max(year) as year from dividend_year_map where status = 'C'");
						if(rsGetDividendYearMap.next()) {
							year = rsGetDividendYearMap.getString("year");
							int nextYear = Integer.parseInt(year) + 1;
							lblBalanceFwd.setText("Balance Forward (beginning balance for the year " + nextYear + "):");
						}

				        
						balanceForwardField.setText(balanceForward);
						
						// populate withdrawal summary
						WithdrawalSummary withdrawalSummary = withdrawalModel.search(ssn);
						String withdrawalDate = withdrawalSummary == null ? "0.0" : withdrawalSummary.getWithdrawalDate();
						String withdrawalAmount = withdrawalSummary == null ? "0.0" : withdrawalSummary.getWithdrawalAmount();
						String additionalDividend = withdrawalSummary == null ? "0.0" : withdrawalSummary.getAdditionalDividend();
						withdrawalAmountField.setText(withdrawalAmount);
						withdrawalDateField.setText(withdrawalDate);
						additionalDividendField.setText(additionalDividend);
						
						BalanceSummary balanceSummary = new ContributionSummaryTableModel().findSumContributionsBySSN(ssn, year, employerId);
		        		
						if(balanceSummary != null) {
							shareField.setText(balanceSummary.getShareAmount() == null ? "0.0" : balanceSummary.getShareAmount());
							totalReserveField.setText(balanceSummary.getReserveAmount());
						}
						
						double totalShareDbl = Double.parseDouble(balanceForward == null ? "0.0" : balanceForward) + Double.parseDouble(balanceSummary.getShareAmount() == null ? "0.0" :  balanceSummary.getShareAmount());
						totalShareField.setText(String.valueOf(totalShareDbl));
						

						edWithdrawalAmount = withdrawalAmount;
						edWithdrawalDate = withdrawalDate;
						edAdditionalDividend = additionalDividend;
						edShare = balanceSummary.getShareAmount();
						edTotalShare = String.valueOf(totalShareDbl);
						edTotalReserve = balanceSummary.getReserveAmount();
						
					}
	            } else {
	                JOptionPane.showMessageDialog(mainPanel, "Please enter a search term.");
	            }
	        } catch(Exception ex) {
	        	ex.printStackTrace();
	        }
        	
        });
        clearButton.addActionListener(e -> {
        	searchField.setText("");
			ssnField.setText("");
			nameField.setText("");
			employerIdField.setText("");
			dateHiredField.setText("");
			statusField.setText("");
			totalShareField.setText("");
			shareField.setText("");
			totalReserveField.setText("");
			
			// populate contribution information
			
			/******** need to be revisted */

	        // Update the table model dynamically
	        try {
	            String[] months = {
	                    "January", "February", "March", "April", "May", "June", 
	                    "July", "August", "September", "October", "November", "December"
	                };
				for (int i = 0; i < contributionData.length; i++) {
					contributionData[i][0] = months[i]; // First column: Month name
				    for (int j = 0; j < contributionData[i].length; j++) {
				        contributionData[i][j] = "";
				    }
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

	        // Refresh the table display
	        contributionTable.repaint();
	        
			withdrawalAmountField.setText("");
			withdrawalDateField.setText("");
			additionalDividendField.setText("");
			
			balanceForwardField.setText("");
        });
        
        printButton.addActionListener(e -> {

 			PdfReader reader;
 			try {
 				reader = new PdfReader(DatabaseConnection.eligibilityDispayTemplateFile);

 				PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(DatabaseConnection.eligibilityDisplayReportFile));
 				
 				AcroFields form = stamper.getAcroFields();
 				form.setField("SSN", edSSN);
 				form.setField("Name", edName);
 				form.setField("EmployerID", edEmployerID);
 				form.setField("DateHired", edDateHired);
 				form.setField("Status", edStatus);
 				form.setField("WithdrawalAmount", edWithdrawalAmount); // contribution for the whole year
 				form.setField("WithdrawalDate", edWithdrawalDate);
 				form.setField("AdditionalDividend", edAdditionalDividend);
 				form.setField("BalanceForward", edBalanceForward);
 				form.setField("Share", edShare);
 				form.setField("TotalShare", edTotalShare);
 				form.setField("TotalReserve",  edTotalReserve);
 				
 				int year = Year.now().getValue();
 				form.setField("Year1", String.valueOf(year - 2));
 				form.setField("Jan1",(String)printData[0][1]);
 				form.setField("Feb1",(String)printData[1][1]);
 				form.setField("March1",(String)printData[2][1]);
 				form.setField("April1",(String)printData[3][1]);
 				form.setField("May1",(String)printData[4][1]);
 				form.setField("June1",(String)printData[5][1]);
 				form.setField("July1",(String)printData[6][1]);
 				form.setField("August1",(String)printData[7][1]);
 				form.setField("September1",(String)printData[8][1]);
 				form.setField("October1",(String)printData[9][1]);
 				form.setField("November1",(String)printData[10][1]);
 				form.setField("December1",(String)printData[11][1]);

 				form.setField("Year2", String.valueOf(year - 1));
 				form.setField("Jan2",(String)printData[0][2]);
 				form.setField("Feb2",(String)printData[1][2]);
 				form.setField("March2",(String)printData[2][2]);
 				form.setField("April2",(String)printData[3][2]);
 				form.setField("May2",(String)printData[4][2]);
 				form.setField("June2",(String)printData[5][2]);
 				form.setField("July2",(String)printData[6][2]);
 				form.setField("August2",(String)printData[7][2]);
 				form.setField("September2",(String)printData[8][2]);
 				form.setField("October2",(String)printData[9][2]);
 				form.setField("November2",(String)printData[10][2]);
 				form.setField("December2",(String)printData[11][2]);
 				
 				form.setField("Year3", String.valueOf(year));
 				form.setField("Jan3",(String)printData[0][3]);
 				form.setField("Feb3",(String)printData[1][3]);
 				form.setField("March3",(String)printData[2][3]);
 				form.setField("April3",(String)printData[3][3]);
 				form.setField("May3",(String)printData[4][3]);
 				form.setField("June3",(String)printData[5][3]);
 				form.setField("July3",(String)printData[6][3]);
 				form.setField("August3",(String)printData[7][3]);
 				form.setField("September3",(String)printData[8][3]);
 				form.setField("October3",(String)printData[9][3]);
 				form.setField("November3",(String)printData[10][3]);
 				form.setField("December3",(String)printData[11][3]);
 				
 				form.setField("Year4", String.valueOf(year + 1));
 				form.setField("Jan4",(String)printData[0][4]);
 				form.setField("Feb4",(String)printData[1][4]);
 				form.setField("March4",(String)printData[2][4]);
 				form.setField("April4",(String)printData[3][4]);
 				form.setField("May4",(String)printData[4][4]);
 				form.setField("June4",(String)printData[5][4]);
 				form.setField("July4",(String)printData[6][4]);
 				form.setField("August4",(String)printData[7][4]);
 				form.setField("September4",(String)printData[8][4]);
 				form.setField("October4",(String)printData[9][4]);
 				form.setField("November4",(String)printData[10][4]);
 				form.setField("December4",(String)printData[11][4]);
 				
 				stamper.setFormFlattening(true);
 				stamper.close();
 				reader.close();

            	File reportFile = new File(DatabaseConnection.eligibilityDisplayReportFile);
                Desktop.getDesktop().open(reportFile);
                
/*
             // Create a FileInputStream from the file
                FileInputStream fileInputStream = new FileInputStream(reportFile);
                
                // Specify the document flavor (plain text in this case)
                //DocFlavor docFlavor = DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_8;
                DocFlavor docFlavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
                
                // Locate a print service that supports the document flavor
                PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
                if (printService == null) {
                    System.out.println("No default print service found.");
                    return;
                }
                
                // Create a print job
                DocPrintJob printJob = printService.createPrintJob();
                
                // Create a simple doc to send to the printer
                Doc document = new SimpleDoc(fileInputStream, docFlavor, null);
                
                // Print the document with default print attributes
                PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
                attributes.add(new Copies(1)); // Number of copies
                attributes.add(Sides.ONE_SIDED); // Single-sided printing
                
                printJob.print(document, attributes);
                
                System.out.println("Print job sent successfully.");
                
                // Close the file input stream
                fileInputStream.close();
                
*/                
 			} catch (IOException | DocumentException e1) {
 				// TODO Auto-generated catch block
 				e1.printStackTrace();
 			}
         
         });

        
        return mainPanel;
    }


	

 // Simulated dynamic data fetching method
    private static Object[][] getDynamicContributionData(String ssn, String employerId) {
    	int year = Year.now().getValue();
    	ContributionSummaryTableModel contributionModel = new ContributionSummaryTableModel();
        String[] months = {
            "January", "February", "March", "April", "May", "June", 
            "July", "August", "September", "October", "November", "December"
        };
        String[] numberMonths = {
                "01", "02", "03", "04", "05", "06", 
                "07", "08", "09", "10", "11", "12"
            };
        
        HashMap<String,BalanceSummary> hashMap = new HashMap();
        for(int i = -2; i < 2; ++i) {
        	for (int j = 0; j < numberMonths.length; ++ j) {
        		ArrayList<BalanceSummary> lstSummary = contributionModel.searchContributionBySSN(ssn, String.valueOf(year + i), numberMonths[j], employerId);
        		if(lstSummary == null || lstSummary.size() == 0 || lstSummary.get(0) == null) {
        			hashMap.put(year + i + numberMonths[j], null);
        		} else {
        			BalanceSummary balanceSummary = lstSummary.get(0);
        			hashMap.put(year + i + numberMonths[j], balanceSummary);
        		}
        	}
        }

        Object[][] dynamicData = new Object[12][5];
        
        
        for(int row = 0; row < months.length; ++row) {
        	dynamicData[row][0] = months[row]; // First column: Month name
        	int i = -2;
        	for(int column = 1; column < 5; ++column) {
        		
        		BalanceSummary balanceSummary = hashMap.get((year + i)+ numberMonths[row]);
        		if(balanceSummary == null) {
        			dynamicData[row][column] = "$0.00";
        		} else {
        			dynamicData[row][column] = "$" + balanceSummary.getShareAmount();
        			
        		}
        		
        		++i;
        	}
        }

        return dynamicData;
    }

    private static TitledBorder createColoredTitledBorder(String title, Color color) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(color);
        border.setTitleFont(new Font("Arial", Font.BOLD, 12));
        return border;
    }
    
    private static JPanel createContributionHistoryPanel() {
        JPanel contributionHistoryPanel = new JPanel(new BorderLayout());
        contributionHistoryPanel.setBorder(createColoredTitledBorder("Contribution History", new Color(255, 127, 80))); // Coral

        // Define months and years
        String[] months = {
            "January", "February", "March", "April", "May", "June", 
            "July", "August", "September", "October", "November", "December"
        };
        int year = Year.now().getValue();
        String[] columnHeaders = {"Month", String.valueOf(year - 2), String.valueOf(year - 1), String.valueOf(year), String.valueOf(year + 1)}; // Adjust years dynamically if needed

        // Placeholder data (12 months x 3 years)
        contributionData = new Object[12][5];
        for (int i = 0; i < months.length; i++) {
            contributionData[i][0] = months[i]; // First column: Month names
            for (int j = 1; j < columnHeaders.length; j++) {
            	contributionData[i][j] = ""; // Empty cells initially
            }
        }

        contributionTable = new JTable(contributionData, columnHeaders) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        JTableHeader tableHeader = contributionTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13)); // Bold font with size 14
        tableHeader.setReorderingAllowed(false); // Optional: Prevent column reordering
        // Center-align data in the table
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < contributionTable.getColumnCount(); i++) {
            contributionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Adjust table layout
        contributionTable.setRowHeight(25);
        contributionTable.getTableHeader().setReorderingAllowed(false);

        contributionHistoryPanel.add(new JScrollPane(contributionTable), BorderLayout.CENTER);
        return contributionHistoryPanel;
    }


    
	private static JPanel createEmployerManagementPanel() {
        EmployerTableModel model = new EmployerTableModel();
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(173, 216, 255));
                    } else {
                        c.setBackground(Color.WHITE); // White
                    }
                } else {
                    c.setBackground(new Color(173, 216, 230)); // Highlight color for selected row (Darker blue)
                }
                return c;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells non-editable
                return false;
            }
            
            @Override
            public String getToolTipText(java.awt.event.MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row >= 0) {
                    return "Click a row to view details";
                }
                return null;
            }
        };
        
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13)); // Bold font with size 14
        tableHeader.setReorderingAllowed(false); // Optional: Prevent column reordering
        // Set a larger row height (e.g., 30 pixels)
        table.setRowHeight(30);
        
        hideTableColumn(table, model.getColumnIndex("Created Date"));
        hideTableColumn(table, model.getColumnIndex("Created By"));
        hideTableColumn(table, model.getColumnIndex("Last Updated Date"));
        hideTableColumn(table, model.getColumnIndex("Last Updated By"));

        JPanel panel = new JPanel(new BorderLayout());

        // Add search bar and buttons panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        
        searchField.setToolTipText("Enter ID or Name to Search");
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(144, 238, 144)); // Light Green
        searchButton.setForeground(Color.BLACK);   
        
        JButton resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(173, 216, 255));
        resetButton.setForeground(Color.BLACK);           
     // Set placeholder text
        addPlaceholderText(searchField, "Enter ID or Name to Search");


        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);
        topPanel.add(searchPanel, BorderLayout.NORTH);

        // Bottom panel for Add, Update, Delete buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(244, 164, 96)); // Light Yellow (RGB: 255, 255, 153)
        addButton.setForeground(Color.BLACK); // Black text
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(147, 112, 219)); // Brown (RGB: 139, 69, 19)
        deleteButton.setForeground(Color.BLACK); // Black text
     
        JButton updateButton = new JButton("Update");


        addButton.addActionListener(e -> {
            // Display a form dialog to collect all necessary fields for a new employer
            JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10)); // Adjust rows as needed
            JTextField employerIdField = new JTextField();
            JTextField employerNameField = new JTextField();
            JTextField address1Field = new JTextField();
            JTextField address2Field = new JTextField();
            JTextField attentionLineField = new JTextField();
            JTextField phoneField = new JTextField();
            JTextField alternatePhoneField = new JTextField();
            JTextField notesField = new JTextField();            
         // Status ComboBox
            String[] statusOptions = {"Active", "Inactive"};
            JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);

            JTextField createdByField = new JTextField();
            createdByField.setText("Admin");
            createdByField.setEditable(false);
            JTextField lastUpdatedByField = new JTextField();
            lastUpdatedByField.setText("Admin");
            lastUpdatedByField.setEditable(false);
            
            // Use date pickers for contract date fields
            JXDatePicker contractEffectiveDatePicker = new JXDatePicker();
            contractEffectiveDatePicker.setFormats("MM-dd-yyyy"); // Ensure the format matches your database requirement
            JXDatePicker contractExpiryDatePicker = new JXDatePicker();
            contractExpiryDatePicker.setFormats("MM-dd-yyyy");
            JXDatePicker createdDatePicker = new JXDatePicker();
            createdDatePicker.setFormats("MM-dd-yyyy");
            createdDatePicker.setEditable(false);
            createdDatePicker.setDate(new java.util.Date());
            
            JXDatePicker lastUpdatedDatePicker = new JXDatePicker();
            lastUpdatedDatePicker.setFormats("MM-dd-yyyy");
            lastUpdatedDatePicker.setEditable(false);
            lastUpdatedDatePicker.setDate(new java.util.Date());
            
            formPanel.add(new JLabel("Employer ID:"));
            formPanel.add(employerIdField);
            formPanel.add(new JLabel("Employer Name:"));
            formPanel.add(employerNameField);
            formPanel.add(new JLabel("Address 1:"));
            formPanel.add(address1Field);
            formPanel.add(new JLabel("Address 2:"));
            formPanel.add(address2Field);
            formPanel.add(new JLabel("Attention Line:"));
            formPanel.add(attentionLineField);
            formPanel.add(new JLabel("Phone:"));
            formPanel.add(phoneField);
            formPanel.add(new JLabel("Alternate Phone:"));
            formPanel.add(alternatePhoneField);
            formPanel.add(new JLabel("Notes:"));
            formPanel.add(notesField);
            formPanel.add(new JLabel("Contract Effective Date:"));
            formPanel.add(contractEffectiveDatePicker); // Add Date Picker
            formPanel.add(new JLabel("Contract Expiry Date:"));
            formPanel.add(contractExpiryDatePicker); // Add Date Picker
            formPanel.add(new JLabel("Status:"));
            formPanel.add(statusComboBox);
            formPanel.add(new JLabel("Created Date:"));
            formPanel.add(createdDatePicker); // Add Date Picker
            

            formPanel.add(new JLabel("Created By:"));
            formPanel.add(createdByField);
            formPanel.add(new JLabel("Last Updated Date:"));
            formPanel.add(lastUpdatedDatePicker); // Add Date Picker
            
            

            formPanel.add(new JLabel("Last Updated By:"));
            formPanel.add(lastUpdatedByField);

            int result = JOptionPane.showConfirmDialog(
                    panel,
                    formPanel,
                    "Add New Employer",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                Object[] newRow = new Object[model.getColumnCount()];
                newRow[0] = employerIdField.getText();
                newRow[1] = employerNameField.getText();
                newRow[2] = address1Field.getText();
                newRow[3] = address2Field.getText();
                newRow[4] = attentionLineField.getText();
                newRow[5] = phoneField.getText();
                newRow[6] = alternatePhoneField.getText();
                newRow[7] = notesField.getText();

                // Format date picker values for database compatibility
                if (contractEffectiveDatePicker.getDate() != null) {
                    newRow[8] = new SimpleDateFormat("MM-dd-yyyy").format(contractEffectiveDatePicker.getDate());
                }
                if (contractExpiryDatePicker.getDate() != null) {
                    newRow[9] = new SimpleDateFormat("MM-dd-yyyy").format(contractExpiryDatePicker.getDate());
                }

                // Map display value to DB value
                String selectedStatus = (String) statusComboBox.getSelectedItem();
                newRow[10] = selectedStatus.equals("Active") ? "AA" : "IA";
                if (createdDatePicker.getDate() != null) {
                    newRow[11] = new SimpleDateFormat("MM-dd-yyyy").format(createdDatePicker.getDate());
                }
                newRow[12] = createdByField.getText();
                if (lastUpdatedDatePicker.getDate() != null) {
                    newRow[13] = new SimpleDateFormat("MM-dd-yyyy").format(lastUpdatedDatePicker.getDate());
                }
                newRow[14] = lastUpdatedByField.getText();
                try {
					model.addRow(newRow);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                JOptionPane.showMessageDialog(panel, "Employer added successfully.");
            }
        });


        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int confirmation = JOptionPane.showConfirmDialog(
                        panel,
                        "Are you sure you want to delete the selected employer?",
                        "Delete Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirmation == JOptionPane.YES_OPTION) {
                    model.deleteRow(selectedRow);
                    JOptionPane.showMessageDialog(panel, "Employer deleted successfully.");
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a row to delete.");
            }
        });

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int confirmation = JOptionPane.showConfirmDialog(
                        panel,
                        "Are you sure you want to update the selected employer?",
                        "Update Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirmation == JOptionPane.YES_OPTION) {
                    try {
						model.updateRow(selectedRow);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    JOptionPane.showMessageDialog(panel, "Employer updated successfully.");
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a row to update.");
            }
        });
        
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                model.search(query); // Implement search method in EmployerTableModel
            } else {
                JOptionPane.showMessageDialog(panel, "Please enter a search term.");
            }
        });

        resetButton.addActionListener(e -> {
            searchField.setText("");
            model.loadData(); // Reload full dataset
        });
        
     // Add double-click listener for row display
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double-click detected
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                    	displayEditableEmployerDialog(model, row);
                    }
                }
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }


    private static JPanel createEmployeeManagementPanel() {
        EmployeeTableModel model = new EmployeeTableModel();
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                    if (!isRowSelected(row)) {
                        if (row % 2 == 0) {
                            c.setBackground(new Color(173, 216, 255)); // Light Blue
                        } else {
                            c.setBackground(Color.WHITE); // White
                        }
                    } else {
                        c.setBackground(new Color(173, 216, 230)); // Highlight color for selected row
                    }
                    c.setForeground(Color.BLACK); // Black text
                //}
                return c;
            }

			@Override
            public String getToolTipText(java.awt.event.MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row >= 0) {
                    return "Click a row to view details";
                }
                return null;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells non-editable
                return false;
            }
        };
        
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13)); // Bold font with size 14
        tableHeader.setReorderingAllowed(false); // Optional: Prevent column reordering
        table.setRowHeight(30);
        hideTableColumn(table, model.getColumnIndex("Employee id"));
        hideTableColumn(table, model.getColumnIndex("Current Spouse"));
        hideTableColumn(table, model.getColumnIndex("Married Date"));
        hideTableColumn(table, model.getColumnIndex("Previous SSN"));
        hideTableColumn(table, model.getColumnIndex("Previous Employer Id"));
        hideTableColumn(table, model.getColumnIndex("Previous First Name"));
        hideTableColumn(table, model.getColumnIndex("Previous Last Name"));
        hideTableColumn(table, model.getColumnIndex("Previous Middle Name"));
        hideTableColumn(table, model.getColumnIndex("Previous Spouse"));
        hideTableColumn(table, model.getColumnIndex("Created Date"));
        hideTableColumn(table, model.getColumnIndex("Created By"));
        hideTableColumn(table, model.getColumnIndex("Last Updated Date"));
        hideTableColumn(table, model.getColumnIndex("Last Updated By"));
        
     // Add the context menu for copying SSN
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem copySSNItem = new JMenuItem("Copy SSN");
        contextMenu.add(copySSNItem);

        // Add mouse listener for showing the context menu
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) { // Right-click detected
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        table.setRowSelectionInterval(row, row); // Select the row
                        contextMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        // Add action listener for copying SSN
        copySSNItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String ssn = (String) table.getValueAt(selectedRow, model.getColumnIndex("SSN"));
                if (ssn != null) {
                    // Copy the SSN to clipboard
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(ssn), null);
                    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(table), "SSN copied to clipboard: " + ssn);
                } else {
                    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(table), "SSN is empty for the selected row.");
                }
            }
        });

        
        JPanel panel = new JPanel(new BorderLayout());

        // Add search bar and buttons panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(10);
        JComboBox<String> searchOptionComboBox = new JComboBox<>(new String[]{"By SSN", "By Employer ID", "By Name"});
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(144, 238, 144)); // Light Green
        searchButton.setForeground(Color.BLACK);   
        
        JButton resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(173, 216, 255));
        resetButton.setForeground(Color.BLACK); 

        searchPanel.add(searchLabel);
        searchPanel.add(searchOptionComboBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);
        topPanel.add(searchPanel, BorderLayout.NORTH);


        // Bottom panel for Add, Update, Delete buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(244, 164, 96)); // Light Yellow (RGB: 255, 255, 153)
        addButton.setForeground(Color.BLACK); // Black text
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(147, 112, 219)); // Brown (RGB: 139, 69, 19)
        deleteButton.setForeground(Color.BLACK); // Black text        

        // JComboBox for selecting employers
        
        JComboBox<String> employerComboBox = new JComboBox<>();
        populateEmployerComboBox(employerComboBox);

        addButton.addActionListener(e -> {
            // Display a form dialog to collect all necessary fields for a new employee
            JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10)); // Adjust rows as needed
            JTextField ssnField = new JTextField();
            
            JTextField firstNameField = new JTextField();
            JTextField lastNameField = new JTextField();
            JTextField middleNameField = new JTextField();

            // Use date pickers for date fields
            JXDatePicker dobPicker = new JXDatePicker();
            dobPicker.setFormats("MM-dd-yyyy"); // Ensure the format matches your database requirement
            JXDatePicker dateHiredPicker = new JXDatePicker();
            dateHiredPicker.setFormats("MM-dd-yyyy");
            JXDatePicker marriedDatePicker = new JXDatePicker();
            marriedDatePicker.setFormats("MM-dd-yyyy");
            JXDatePicker createdDatePicker = new JXDatePicker(new java.util.Date());
            createdDatePicker.setFormats("MM-dd-yyyy");
            createdDatePicker.setEditable(false);
            JXDatePicker lastUpdatedDatePicker = new JXDatePicker(new java.util.Date());
            lastUpdatedDatePicker.setFormats("MM-dd-yyyy");
            lastUpdatedDatePicker.setEditable(false);

            JTextField genderField = new JTextField();
            JTextField maritalStatusField = new JTextField();
            JTextField currentSpouseField = new JTextField();
            JTextField addressField = new JTextField();
            JTextField phoneField = new JTextField();
            JTextField cityField = new JTextField();
            JTextField stateField = new JTextField();
            JTextField zipField = new JTextField();
            JTextField previousSsnField = new JTextField();
            JTextField previousEmployerIdField = new JTextField();
            JTextField previousFirstNameField = new JTextField();
            JTextField previousLastNameField = new JTextField();
            JTextField previousMiddleNameField = new JTextField();
            JTextField previousSpouseField = new JTextField();
            JTextField createdByField = new JTextField("Admin");
            createdByField.setEditable(false);
            JTextField lastUpdatedByField = new JTextField("Admin");
            lastUpdatedByField.setEditable(false);
            //JTextField statusField = new JTextField();
            String[] statusOptions = {"AA", "PT", "IU", "SU"};
            JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
            JTextField balanceForwardField = new JTextField();
            JXDatePicker terminatedDatePicker = new JXDatePicker();
            terminatedDatePicker.setFormats("MM-dd-yyyy");

            formPanel.add(new JLabel("SSN:"));
            formPanel.add(ssnField);
            formPanel.add(new JLabel("Employer:"));
            formPanel.add(employerComboBox);
            formPanel.add(new JLabel("First Name:"));
            formPanel.add(firstNameField);
            formPanel.add(new JLabel("Last Name:"));
            formPanel.add(lastNameField);
            formPanel.add(new JLabel("Middle Name:"));
            formPanel.add(middleNameField);
            formPanel.add(new JLabel("Date of Birth:"));
            formPanel.add(dobPicker); // Add Date Picker
            formPanel.add(new JLabel("Date Hired:"));
            formPanel.add(dateHiredPicker); // Add Date Picker
            formPanel.add(new JLabel("Gender:"));
            formPanel.add(genderField);
            formPanel.add(new JLabel("Marital Status:"));
            formPanel.add(maritalStatusField);
            formPanel.add(new JLabel("Current Spouse:"));
            formPanel.add(currentSpouseField);
            formPanel.add(new JLabel("Married Date:"));
            formPanel.add(marriedDatePicker); // Add Date Picker
            formPanel.add(new JLabel("Address:"));
            formPanel.add(addressField);
            formPanel.add(new JLabel("Phone:"));
            formPanel.add(phoneField);
            formPanel.add(new JLabel("City:"));
            formPanel.add(cityField);
            formPanel.add(new JLabel("State:"));
            formPanel.add(stateField);
            formPanel.add(new JLabel("Zip:"));
            formPanel.add(zipField);
            formPanel.add(new JLabel("Previous SSN:"));
            formPanel.add(previousSsnField);
            formPanel.add(new JLabel("Previous Employer Id:"));
            formPanel.add(previousEmployerIdField);
            formPanel.add(new JLabel("Previous First Name:"));
            formPanel.add(previousFirstNameField);
            formPanel.add(new JLabel("Previous Last Name:"));
            formPanel.add(previousLastNameField);
            formPanel.add(new JLabel("Previous Middle Name:"));
            formPanel.add(previousMiddleNameField);
            formPanel.add(new JLabel("Previous Spouse:"));
            formPanel.add(previousSpouseField);

            formPanel.add(new JLabel("Created Date:"));
            formPanel.add(createdDatePicker); // Add Date Picker

            formPanel.add(new JLabel("Created By:"));
            formPanel.add(createdByField);

            formPanel.add(new JLabel("Last Updated Date:"));
            formPanel.add(lastUpdatedDatePicker); // Add Date Picker

            formPanel.add(new JLabel("Last Updated By:"));
            formPanel.add(lastUpdatedByField);
            formPanel.add(new JLabel("Status:"));
            formPanel.add(statusComboBox);
            formPanel.add(new JLabel("Balance Forward:"));
            formPanel.add(balanceForwardField);
            formPanel.add(new JLabel("Terminated Date:"));
            formPanel.add(terminatedDatePicker);            

            int result = JOptionPane.showConfirmDialog(
                    panel,
                    formPanel,
                    "Add New Employee",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                Object[] newRow = new Object[model.getColumnCount()];
                newRow[1] = ssnField.getText();
                newRow[2] = employerComboBox.getSelectedItem(); // Selected employer ID
                newRow[3] = firstNameField.getText();
                newRow[4] = lastNameField.getText();
                newRow[5] = middleNameField.getText();

                // Format date picker values for database compatibility
                if (dobPicker.getDate() != null) {
                    newRow[6] = new SimpleDateFormat("MM-dd-yyyy").format(dobPicker.getDate());
                }
                if (dateHiredPicker.getDate() != null) {
                    newRow[7] = new SimpleDateFormat("MM-dd-yyyy").format(dateHiredPicker.getDate());
                }

                newRow[8] = genderField.getText();
                newRow[9] = maritalStatusField.getText();
                newRow[10] = currentSpouseField.getText();
                if (marriedDatePicker.getDate() != null) {
                    newRow[11] = new SimpleDateFormat("MM-dd-yyyy").format(marriedDatePicker.getDate());
                }
                newRow[12] = addressField.getText();
                newRow[13] = phoneField.getText();
                newRow[14] = cityField.getText();
                newRow[15] = stateField.getText();
                newRow[16] = zipField.getText();
                newRow[17] = previousSsnField.getText();
                newRow[18] = previousEmployerIdField.getText();
                newRow[19] = previousFirstNameField.getText();
                newRow[20] = previousLastNameField.getText();
                newRow[21] = previousMiddleNameField.getText();
                newRow[22] = previousSpouseField.getText();
                if (createdDatePicker.getDate() != null) {
                    newRow[23] = new SimpleDateFormat("MM-dd-yyyy").format(createdDatePicker.getDate());
                }
                newRow[24] = createdByField.getText();
                if (lastUpdatedDatePicker.getDate() != null) {
                    newRow[25] = new SimpleDateFormat("MM-dd-yyyy").format(lastUpdatedDatePicker.getDate());
                }                
                newRow[26] = lastUpdatedByField.getText();
                newRow[27] = statusComboBox.getSelectedItem().toString();
                newRow[28] = balanceForwardField.getText();
                if (terminatedDatePicker.getDate() != null) {
                    newRow[29] = new SimpleDateFormat("MM-dd-yyyy").format(terminatedDatePicker.getDate());
                } 
                try {
					model.addRow(newRow);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                JOptionPane.showMessageDialog(panel, "Employee added successfully.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int confirmation = JOptionPane.showConfirmDialog(
                        panel,
                        "Are you sure you want to delete the selected employee?",
                        "Delete Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirmation == JOptionPane.YES_OPTION) {
                    model.deleteRow(selectedRow);
                    JOptionPane.showMessageDialog(panel, "Employee deleted successfully.");
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a row to delete.");
            }
        });

     // Add Search Logic
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            String searchOption = (String) searchOptionComboBox.getSelectedItem();

            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a search term.");
                return;
            }

            if ("By SSN".equals(searchOption)) {
                model.searchBySSN(query); // Implement searchBySSN in EmployeeTableModel
            } else if ("By Employer ID".equals(searchOption)) {
                model.searchByEmployerId(query); // Implement searchByEmployerId in EmployeeTableModel
            } else if ("By Name".equals(searchOption)) {
            	model.search(query);
            }
        });

        resetButton.addActionListener(e -> {
            searchField.setText("");
            model.loadData();
        });
        

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double-click detected
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        displayEditableEmployeeDialog(model, row); // Open editable dialog
                    }
                }
            }
        });

        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        //buttonPanel.add(updateButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
  
    private static boolean isRecordInWithdrawalEntry(String ssn) {
    	boolean withdrawalExists = false;
		WithdrawalEntryTableModel withdrawalModel = new WithdrawalEntryTableModel();
		WithdrawalSummary withdrawal = withdrawalModel.search(ssn);
		if(withdrawal == null) {
			withdrawalExists = false;
		} else {
			withdrawalExists = true;
		}
		return withdrawalExists;
	}
    // Utility to read CSV file
    private static List<String[]> readCsvFile(File csvFile) throws IOException {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line.split(",")); // Split by comma for CSV parsing
            }
        }
        return records;
    }

    private static JPanel createContributionSummaryPanel() {
        ContributionSummaryTableModel model = new ContributionSummaryTableModel();
        JTable table = new JTable(model) {
            @Override
            public String getToolTipText(java.awt.event.MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row >= 0) {
                    return "Double-click a row to view/edit contribution details";
                }
                return null;
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(173, 216, 255));
                    } else {
                        c.setBackground(Color.WHITE); // White
                    }
                } else {
                    c.setBackground(new Color(173, 216, 230)); // Highlight color for selected row (Darker blue)
                }
                return c;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells non-editable
                return false;
            }
        };
        
     // Center-align the column values
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Apply the renderer to all columns
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13)); // Bold font with size 14
        tableHeader.setReorderingAllowed(false); // Optional: Prevent column reordering
        // Set a larger row height (e.g., 30 pixels)
        table.setRowHeight(30);
        
        hideTableColumn(table, model.getColumnIndex("Created Date"));
        hideTableColumn(table, model.getColumnIndex("Created By"));
        hideTableColumn(table, model.getColumnIndex("Last Updated Date"));
        hideTableColumn(table, model.getColumnIndex("Last Updated By"));

        JPanel panel = new JPanel(new BorderLayout());
        
     // Search Panel
        //JPanel searchPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        //JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel employerIdLabel = new JLabel("Employer ID:");
        JComboBox<String> employerComboBox = new JComboBox<>();
        populateEmployerComboBox(employerComboBox);        
        //JTextField searchEmployerIdField = new JTextField(5);
        JLabel periodMonthLabel = new JLabel("Period Month:");
        JTextField searchPeriodMonthField = new JTextField(5);
        JLabel periodYearLabel = new JLabel("Period Year:");
        JTextField searchPeriodYearField = new JTextField(5);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(144, 238, 144)); // Light Green
        searchButton.setForeground(Color.BLACK);   
        
        JButton resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(173, 216, 255));
        resetButton.setForeground(Color.BLACK);  
        
        searchPanel.add(employerIdLabel);
        searchPanel.add(employerComboBox);
        //searchPanel.add(searchEmployerIdField);
        searchPanel.add(periodMonthLabel);
        searchPanel.add(searchPeriodMonthField);
        searchPanel.add(periodYearLabel);
        searchPanel.add(searchPeriodYearField);
        searchPanel.add(searchButton);

        searchPanel.add(resetButton);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(244, 164, 96)); // Light Yellow (RGB: 255, 255, 153)
        addButton.setForeground(Color.BLACK); // Black text
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(147, 112, 219)); // Brown (RGB: 139, 69, 19)
        deleteButton.setForeground(Color.BLACK); // Black text
        
        JButton uploadCsvButton = new JButton("Upload CSV"); 
        
        JLabel lblUploadCVSTemplate = new JLabel("<html><a src=''> Download CSV Template </a></html>");
        lblUploadCVSTemplate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblUploadCVSTemplate.setForeground(Color.BLUE);
        lblUploadCVSTemplate.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));

        lblUploadCVSTemplate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Replace with the path to your local file
            	String csvFile = DatabaseConnection.csvTemplateFile;
                File file = new File(csvFile);
                if (file.exists()) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "File does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
     // Search Logic
        searchButton.addActionListener(e -> {
            String employerId = (String) employerComboBox.getSelectedItem();
            String periodMonth = searchPeriodMonthField.getText().trim();
            String periodYear = searchPeriodYearField.getText().trim();

            // Validate inputs
            if (employerId.isEmpty() || periodMonth.isEmpty() || periodYear.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "All search fields are mandatory!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            model.searchContribution(employerId, periodMonth, periodYear);
            if(model.getRowCount() == 0) {
            	JOptionPane.showMessageDialog(panel,
           			 "The chosen contribution does not exist. Please click on Add to add a new contribution."
           			 , "No Records Found", JOptionPane.INFORMATION_MESSAGE);
            }       
            
        });

        // Add Contribution
        addButton.addActionListener(e -> {
            JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));

            JComboBox<String> employerComboBox1 = new JComboBox<>();
            populateEmployerComboBox(employerComboBox1);
            //JTextField employerIdField = new JTextField();
            JTextField periodMonthField = new JTextField();
            JTextField periodYearField = new JTextField();
            JTextField summaryAmountField = new JTextField();
            JTextField createdDateField = new JTextField(new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
            createdDateField.setEditable(false);
            JTextField createdByField = new JTextField("Admin");
            createdByField.setEditable(false);
            JTextField lastUpdatedDateField = new JTextField(new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
            lastUpdatedDateField.setEditable(false);
            JTextField lastUpdatedByField = new JTextField("Admin");
            lastUpdatedByField.setEditable(false);
            //JTextField cashReceivedDateField = new JTextField();
            JXDatePicker cashReceivedDateField = new JXDatePicker();
            cashReceivedDateField.setFormats("yyyy-MM-dd");

            formPanel.add(new JLabel("Employer ID:"));
            //formPanel.add(employerIdField);
            formPanel.add(employerComboBox1);
            formPanel.add(new JLabel("Period Month:"));
            formPanel.add(periodMonthField);
            formPanel.add(new JLabel("Period Year:"));
            formPanel.add(periodYearField);
            formPanel.add(new JLabel("Summary Amount:"));
            formPanel.add(summaryAmountField);
            formPanel.add(new JLabel("Cash Received Date:"));
            formPanel.add(cashReceivedDateField);
            formPanel.add(new JLabel("Created Date:"));
            formPanel.add(createdDateField);
            formPanel.add(new JLabel("Created By:"));
            formPanel.add(createdByField);
            formPanel.add(new JLabel("Last Updated Date:"));
            formPanel.add(lastUpdatedDateField);
            formPanel.add(new JLabel("Last Updated By:"));
            formPanel.add(lastUpdatedByField);

            int result = JOptionPane.showConfirmDialog(panel, formPanel, "Add Contribution", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
            	
                // Check if employer id is valid
                if(!isEmployerValid((String)employerComboBox1.getSelectedItem())) {
                    JOptionPane.showMessageDialog(panel, "Employer Code is invalid.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;                	
                }
                
                // Check if contribution entry already exists
                if(isContributionEntryPresent((String)employerComboBox1.getSelectedItem(), periodMonthField.getText(), periodYearField.getText())) {
                    JOptionPane.showMessageDialog(panel, "This contribution already exists. Please double click on the contribution to edit.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;                	
                }
                
                String regex = "\\d{4}-\\d{1,2}-\\d{1,2}";
                System.out.println("Cash received date: " + cashReceivedDateField.getDate());
                if(cashReceivedDateField.getDate() == null) {
	                JOptionPane.showMessageDialog(panel,
	                        "Invalid Cash Received Date. Please enter in the format yyyy-mm-dd",
	                        "Invalid Cash Received Date",
	                        JOptionPane.WARNING_MESSAGE
	                );     
	                return;               	
                }
                String cashReceivedDate = new SimpleDateFormat("yyyy-MM-dd").format(cashReceivedDateField.getDate());
                if(!cashReceivedDate.matches(regex)) {
	                JOptionPane.showMessageDialog(panel,
	                        "Invalid Cash Received Date. Please enter in the format yyyy-mm-dd",
	                        "Invalid Cash Received Date",
	                        JOptionPane.WARNING_MESSAGE
	                );     
	                return;
                }
                model.addRow(new Object[]{
                        null, // Auto-increment ID
                        Integer.parseInt((String)employerComboBox1.getSelectedItem()),
                        periodMonthField.getText(),
                        periodYearField.getText(),
                        summaryAmountField.getText(),
                        createdDateField.getText(),
                        createdByField.getText(),
                        lastUpdatedDateField.getText(),
                        lastUpdatedByField.getText(),
                        cashReceivedDate
                });
                JOptionPane.showMessageDialog(panel, "Contribution added successfully.");
                refreshHomeScreen();
            }
        });

        // Delete Contribution
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int confirmation = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this contribution?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    model.deleteRow(selectedRow);
                    JOptionPane.showMessageDialog(panel, "Contribution deleted successfully.");
                    refreshHomeScreen();
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a row to delete.");
            }
        });

        // Add double-click listener for row display
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double-click detected
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int contributionId = (int) model.getValueAt(row, 0); // Get contribution_id
                        String employerId = String.valueOf(model.getValueAt(row, 1)); // Get employer_id
                        displayContributionDetailsDialog(contributionId, employerId);
                    }
                }
            }
        });
        
        resetButton.addActionListener(e -> {
        	employerComboBox.setSelectedIndex(0);
        	searchPeriodMonthField.setText("");
        	searchPeriodYearField.setText("");
            model.loadData();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(uploadCsvButton);
        buttonPanel.add(lblUploadCVSTemplate);
        

        uploadCsvButton.setBackground(new Color(70, 130, 180)); // Steel Blue
        uploadCsvButton.setForeground(Color.WHITE);

        uploadCsvButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select a CSV file");
            int returnValue = fileChooser.showOpenDialog(panel);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String fileName = selectedFile.getName();

                // Validate file name format
                String regex = "\\d{4}-\\d{1,2}-\\d{4}-\\d{4}\\.\\d{1,2}\\.\\d{1,2}\\.csv";
                String[] fileNameSplit = fileName.split("-");
                String employerId = fileNameSplit[0];
                String periodMonth = fileNameSplit[1];
                String periodYear = fileNameSplit[2];
                String cashReceivedDate = fileNameSplit[3];
                
                // Check if employer id is valid
                if(!isEmployerValid(employerId)) {
                    JOptionPane.showMessageDialog(panel, "Employer Code is invalid. Please fix the file before uploading.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;                	
                }
                
                // Check if contribution entry already exists
                if(isContributionEntryPresent(employerId, periodMonth, periodYear)) {
                    JOptionPane.showMessageDialog(panel, "This contribution already exists. Please double click on the contribution to edit.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;                	
                }
                
                if (fileName.matches(regex)) {
                    try {
                        List<String[]> records = readCsvFile(selectedFile);

                        // Validation 1: Check if file is empty
                        if (records.isEmpty()) {
                            JOptionPane.showMessageDialog(panel, "The CSV file is empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        
                        ArrayList<String> duplicatesLst = validateNoDuplicateSSNsInFile(records);
                        if(duplicatesLst.size() > 0) {
                        	StringBuffer sbDuplicateMessage = new StringBuffer();
                        	sbDuplicateMessage.append("The following duplicate SSNs were found in the file. Please remove duplicates"
                        								+ " upload the file again. \n \n");
                        	for(int i = 0; i < duplicatesLst.size(); ++i) {
                        		sbDuplicateMessage.append(duplicatesLst.get(i) + " \n");
                        	}
                 
                        	JTextArea textArea = new JTextArea(sbDuplicateMessage.toString());
                        	textArea.setEditable(false); // Read-only
                        	textArea.setWrapStyleWord(true);
                        	textArea.setLineWrap(true);
                        	textArea.setCaretPosition(0); // Start at the top
                        	textArea.setFont(new Font("Arial", Font.PLAIN, 14));

                        	// Add scroll pane (in case of long messages)
                        	JScrollPane scrollPane = new JScrollPane(textArea);
                        	scrollPane.setPreferredSize(new Dimension(400, 200));
                        	
                        	JOptionPane.showMessageDialog(panel, scrollPane, "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Process records and validate
                        List<String[]> validRecords = new ArrayList<>();
                        List<String> missingSSNs = new ArrayList<>();
                        double totalAmount = 0;

                        for (String[] record : records) {
                            if (record.length < 3) {
                                JOptionPane.showMessageDialog(panel, "Invalid record format. Each record must have SSN, Share Amount, and Reserve Amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            String ssn = record[0].trim();
                            double shareAmount = parseAmount(record[1]);
                            double reserveAmount = parseAmount(record[2]);

                            // Validation 2: Check if SSN exists in the employee table
                            if (!isSSNValid(ssn)) {
                                missingSSNs.add(ssn);
                                continue;
                            }

                            // Validation 3: One of the amounts can be zero, but not both
                            if (shareAmount == 0 && reserveAmount == 0) {
                                JOptionPane.showMessageDialog(panel, "Each record must have either Share Amount or Reserve Amount, but not both as zero.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            // Validation 4: Only one amount can be non-zero
                            if (shareAmount != 0 && reserveAmount != 0) {
                                JOptionPane.showMessageDialog(panel, "Each record can have either Share Amount or Reserve Amount, but not both.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            validRecords.add(new String[]{ssn, String.valueOf(shareAmount), String.valueOf(reserveAmount)});
                            totalAmount += (shareAmount + reserveAmount);
                        }

//                        // Show missing SSNs
//                        if (!missingSSNs.isEmpty()) {
//                            JOptionPane.showMessageDialog(panel, "The following SSNs were not found. Please add them in Employee Maintenance Screen.\n" + String.join(", ", missingSSNs), "Missing SSNs", JOptionPane.WARNING_MESSAGE);
//                        }
                        
                        StringBuffer sbMissingSSNMessage = new StringBuffer();
                        sbMissingSSNMessage.append("The following SSNs were not found in Employee Master. Please add them"
                    								+ " \n \n");
                    	for(int i = 0; i < missingSSNs.size(); ++i) {
                    		sbMissingSSNMessage.append(missingSSNs.get(i) + " \n");
                    	}
             
                    	JTextArea textArea = new JTextArea(sbMissingSSNMessage.toString());
                    	textArea.setEditable(false); // Read-only
                    	textArea.setWrapStyleWord(true);
                    	textArea.setLineWrap(true);
                    	textArea.setCaretPosition(0); // Start at the top
                    	textArea.setFont(new Font("Arial", Font.PLAIN, 14));

                    	// Add scroll pane (in case of long messages)
                    	JScrollPane scrollPane = new JScrollPane(textArea);
                    	scrollPane.setPreferredSize(new Dimension(400, 200));
                    	
                    	JOptionPane.showMessageDialog(panel, scrollPane, "Missing SSNs", JOptionPane.WARNING_MESSAGE);

                        // Display records in an editable table
                        if (!validRecords.isEmpty()) {
                            displayEditableTablePopup(validRecords, totalAmount, employerId, periodMonth, periodYear, cashReceivedDate);
                        } else {
                            JOptionPane.showMessageDialog(panel, "No valid records to upload.", "Validation Result", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, "Error while uploading CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(panel,
                            "Invalid file name format. Expected format: <4-digit-employer-id>-month-year-cash-received-date.csv\n" +
                                    "Example: 3001-12-2024-2004.01.13.csv",
                            "Invalid File Name",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });
        panel.add(searchPanel, BorderLayout.NORTH);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    
    private static JPanel createWithdrawalEntriesPanel() {
        WithdrawalEntryTableModel model = new WithdrawalEntryTableModel();
        
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(173, 216, 255));
                    } else {
                        c.setBackground(Color.WHITE); // White
                    }
                } else {
                    c.setBackground(new Color(173, 216, 230)); // Highlight color for selected row (Darker blue)
                }
                return c;
            }

            @Override
            public String getToolTipText(java.awt.event.MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row >= 0) {
                    return "Double-click a row to view withdrawal details";
                }
                return null;
            }
        };
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13)); // Bold font with size 14
        tableHeader.setReorderingAllowed(false); // Optional: Prevent column reordering

        JPanel panel = new JPanel(new BorderLayout());

        // Top Panel: Search Section
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Search Withdrawal Entries"));

        JLabel ssnLabel = new JLabel("SSN:");
        JTextField ssnField = new JTextField(10);

        JLabel dateRangeLabel = new JLabel("Date Range:");
        JXDatePicker fromDatePicker = new JXDatePicker();
        fromDatePicker.setFormats("yyyy-MM-dd");
        JXDatePicker toDatePicker = new JXDatePicker();
        toDatePicker.setFormats("yyyy-MM-dd");
        
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(144, 238, 144)); // Light Green
        searchButton.setForeground(Color.BLACK);   
        
        JButton resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(173, 216, 255));
        resetButton.setForeground(Color.BLACK);

        searchPanel.add(ssnLabel);
        searchPanel.add(ssnField);
        searchPanel.add(dateRangeLabel);
        searchPanel.add(fromDatePicker);
        searchPanel.add(new JLabel("to"));
        searchPanel.add(toDatePicker);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        searchButton.addActionListener(e -> {
            String ssn = ssnField.getText().trim();
            String fromDate = fromDatePicker.getDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(fromDatePicker.getDate()) : null;
            String toDate = toDatePicker.getDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(toDatePicker.getDate()) : null;

            if (ssn.isEmpty() && (fromDate == null || toDate == null)) {
                JOptionPane.showMessageDialog(panel, "Please enter SSN or specify a date range for the search.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            model.search(ssn, fromDate, toDate); // Call the new method
        });


        // Reset button functionality
        resetButton.addActionListener(e -> {
            ssnField.setText("");
            fromDatePicker.setDate(null);
            toDatePicker.setDate(null);
            model.loadData(); // Reload all data
        });

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(244, 164, 96)); // Light Yellow
        addButton.setForeground(Color.BLACK);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(147, 112, 219)); // Brown
        deleteButton.setForeground(Color.BLACK);

        table.setRowHeight(30);
        hideTableColumn(table, model.getColumnIndex("Withdrawal Date (String)"));
        hideTableColumn(table, model.getColumnIndex("Created Date"));
        hideTableColumn(table, model.getColumnIndex("Created By"));
        hideTableColumn(table, model.getColumnIndex("Last Updated Date"));
        hideTableColumn(table, model.getColumnIndex("Last Updated By"));

        // Add Withdrawal Entry
        addButton.addActionListener(e -> {
            JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));

            //JTextField employerIdField = new JTextField();
            JComboBox<String> employerComboBox = new JComboBox<>();
            populateEmployerComboBox(employerComboBox);
            JTextField ssnFieldForm = new JTextField();
            //JTextField employeeIdField = new JTextField();
            JTextField withdrawalAmountField = new JTextField();
            JTextField addDividendField = new JTextField();
            JXDatePicker withdrawalDatePicker = new JXDatePicker();
            withdrawalDatePicker.setFormats("yyyy-MM-dd");
            JTextField createdByField = new JTextField("Admin");
            createdByField.setEditable(false);
            JTextField lastUpdatedByField = new JTextField("Admin");
            lastUpdatedByField.setEditable(false);

            formPanel.add(new JLabel("Employer ID:"));
            formPanel.add(employerComboBox);
            formPanel.add(new JLabel("SSN:"));
            formPanel.add(ssnFieldForm);
            //formPanel.add(new JLabel("Employee ID:"));
            //formPanel.add(employeeIdField);
            formPanel.add(new JLabel("Withdrawal Amount:"));
            formPanel.add(withdrawalAmountField);
            formPanel.add(new JLabel("Additional Dividend:"));
            formPanel.add(addDividendField);
            formPanel.add(new JLabel("Withdrawal Date:"));
            formPanel.add(withdrawalDatePicker);
            formPanel.add(new JLabel("Created By:"));
            formPanel.add(createdByField);
            formPanel.add(new JLabel("Last Updated By:"));
            formPanel.add(lastUpdatedByField);

            int result = JOptionPane.showConfirmDialog(panel, formPanel, "Add Withdrawal Entry", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
            	
            	// check if withdrawal already exists
            	WithdrawalSummary withdrawal = model.search(ssnFieldForm.getText());
            	if(withdrawal!=null) {
            		JOptionPane.showMessageDialog(panel, "A withdrawal was already made for this employee.");
            		return;
            	}
            	
//            	Check The employer code matches
            	EmployeeTableModel employeeModel = new EmployeeTableModel();
        	    HashMap<String, Employee> mapEmployees = employeeModel.getAllEmployees();
            	Employee employeeOriginal = mapEmployees.get(ssnFieldForm.getText());
            	String origEmployerId = employeeOriginal.getEmployerId();
            	if(!origEmployerId.equals(employerComboBox.getSelectedItem().toString())) {
            		JOptionPane.showMessageDialog(panel, "Employer ID entered incorrectly for this employee.");
            		
            		return;
            	}
            	
            	Employee employee = null;
				try {
					employee = employeeModel.searchForEligibility(ssnFieldForm.getText());
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date withdrawalDateUtil = null;
				java.sql.Date withdrawalDate = null;
        		try {
        			withdrawalDateUtil = formatter.parse(formatter.format(withdrawalDatePicker.getDate()));
        			withdrawalDate = new java.sql.Date(withdrawalDateUtil.getTime());
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				System.out.println("Date: " + new SimpleDateFormat("yyyy-MM-dd").format(withdrawalDatePicker.getDate()));
				
            	
                model.addRow(new Object[]{
                        null, // Auto-increment ID
                        employerComboBox.getSelectedItem(),
                        ssnFieldForm.getText(),
                        employee!=null ? employee.getEmployeeId() : "" ,
                        withdrawalAmountField.getText(),
                        addDividendField.getText(),
                        withdrawalDatePicker.getDate() != null ? withdrawalDate : null,
                        formatter.format(withdrawalDatePicker.getDate()),
                        new Timestamp(System.currentTimeMillis()), // Created Date
                        createdByField.getText(),
                        new Timestamp(System.currentTimeMillis()), // Last Updated Date
                        lastUpdatedByField.getText()
                });
                JOptionPane.showMessageDialog(panel, "Withdrawal Entry added successfully.");
                refreshHomeScreen();
            }
        });

        // Delete Withdrawal Entry
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int confirmation = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this withdrawal entry?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    model.deleteRow(selectedRow);
                    JOptionPane.showMessageDialog(panel, "Withdrawal Entry deleted successfully.");
                    refreshHomeScreen();
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a row to delete.");
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        panel.add(searchPanel, BorderLayout.NORTH); // Add search panel at the top
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
 
    private static JPanel createYearEndReportPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS)); // Arrange panels vertically
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the main panel
        

        // Panel 1: Generate Dividend Edit Report
        JPanel generateReportPanel = new JPanel();
        generateReportPanel.setLayout(new BoxLayout(generateReportPanel, BoxLayout.Y_AXIS));
        generateReportPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(135, 206, 250), 2), 
                "Generate Dividend Edit Report", 
                TitledBorder.CENTER, 
                TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                new Color(135, 206, 250))); // Blue border

        JLabel generateReportInstructions = new JLabel("<html><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
        		+ "Dividend Edit Report can be generated multiple times for a year.<br>"
    			+ "However once the balances are updated the report cannot be generated.<br><br>"
        		+ "Click the button below to generate the Dividend Edit Report.<br></html>");
        generateReportInstructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton generateReportButton = new JButton("Generate Dividend Edit Report");
        generateReportButton.setBackground(new Color(135, 206, 250)); // Blue background
        generateReportButton.setForeground(Color.WHITE); // White text
        generateReportButton.setFocusPainted(false);
        generateReportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateReportButton.setPreferredSize(new Dimension(200, 100));
        generateReportButton.addActionListener(e -> openDividendInputScreen());

        generateReportPanel.add(generateReportInstructions);
        generateReportPanel.add(Box.createVerticalStrut(10));
        generateReportPanel.add(generateReportButton);

        // Panel 2: Generate Statement of Accounts
        JPanel generateStatementPanel = new JPanel();
        generateStatementPanel.setLayout(new BoxLayout(generateStatementPanel, BoxLayout.Y_AXIS));
        generateStatementPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 179, 113), 2), 
                "Generate Statement of Accounts", 
                TitledBorder.CENTER, 
                TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                new Color(60, 179, 113))); // Green border

        JLabel generateStatementInstructions = new JLabel("<html><br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
        		+ "Click the button below to generate the Statement of Accounts.<br></html>");
        generateStatementInstructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton generateStatementButton = new JButton("Generate Statement of Accounts");
        generateStatementButton.setBackground(new Color(60, 179, 113)); // Green background
        generateStatementButton.setForeground(Color.WHITE); // White text
        generateStatementButton.setFocusPainted(false);
        generateStatementButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateStatementButton.setPreferredSize(new Dimension(200, 100));
        generateStatementButton.addActionListener(e -> openStatementOutputPopup());

        generateStatementPanel.add(generateStatementInstructions);
        generateStatementPanel.add(Box.createVerticalStrut(10));


        JButton generateByCompanyButton = new JButton("Generate Statements by Company");
        generateByCompanyButton.setBackground(new Color(255, 165, 0)); // Orange background
        generateByCompanyButton.setForeground(Color.WHITE); // White text
        generateByCompanyButton.setFocusPainted(false);
        generateByCompanyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateByCompanyButton.setPreferredSize(new Dimension(200, 100));
        generateByCompanyButton.addActionListener(e -> openCompanySelectionPopup());
        
        JLabel generateStatementByCompanyInstructions = new JLabel("<html><br><br><br>"
        		+ "Click the button below to generate the Statement of Accounts by company.<br></html>");
        generateStatementByCompanyInstructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        generateStatementPanel.add(generateStatementButton);
        generateStatementPanel.add(generateStatementByCompanyInstructions);
        generateStatementPanel.add(generateByCompanyButton);        
        
        
        // Panel 3: Update Balance
        JPanel updateBalancePanel = new JPanel();
        updateBalancePanel.setLayout(new BoxLayout(updateBalancePanel, BoxLayout.Y_AXIS));
        updateBalancePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 102, 102), 2), 
                "Update Balance", 
                TitledBorder.CENTER, 
                TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14), 
                new Color(255, 102, 102))); // Red border

        JLabel updateBalanceInstructions = new JLabel(
                "<html><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>"
                + "Click the button below to update the balance. <br><b>Warning:</b> This action cannot be undone.<br></html>");
        updateBalanceInstructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton updateBalanceButton = new JButton("Update Balance");
        updateBalanceButton.setBackground(new Color(255, 102, 102)); // Light red background
        updateBalanceButton.setForeground(Color.WHITE); // White text
        updateBalanceButton.setFocusPainted(false);
        updateBalanceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateBalanceButton.setPreferredSize(new Dimension(200, 100));
        updateBalanceButton.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(
                    mainPanel,
                    "Are you sure you want to update? This action cannot be undone.",
                    "Confirm Update",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                openUpdateBalanceOutputPopup();
                JOptionPane.showMessageDialog(mainPanel, "Balance updated successfully!");
            }
        });

        updateBalancePanel.add(updateBalanceInstructions);
        updateBalancePanel.add(Box.createVerticalStrut(10));
        updateBalancePanel.add(updateBalanceButton);

        // Add all panels to the main panel
        mainPanel.add(generateReportPanel);
        mainPanel.add(Box.createVerticalStrut(20)); // Add spacing between panels
        mainPanel.add(generateStatementPanel);
        mainPanel.add(Box.createVerticalStrut(20)); // Add spacing between panels
        mainPanel.add(updateBalancePanel);

        return mainPanel;
    }
    
    private static void openCompanySelectionPopup() {
        JDialog dialog = new JDialog((Frame) null, "Select Company", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Select a Company", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> companyList = new JList<>(listModel);
        companyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(companyList);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Populate company list
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT employer_id, employer_name FROM employer")) {

            while (resultSet.next()) {
                String company = resultSet.getString("employer_id") + " - " + resultSet.getString("employer_name");
                listModel.addElement(company);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Error loading companies: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(e -> {
            String selectedCompany = companyList.getSelectedValue();
            if (selectedCompany != null) {
                JOptionPane.showMessageDialog(dialog, "You selected: " + selectedCompany);
                dialog.dispose();

                // Perform further operations after selection
                String employerId = selectedCompany.split(" - ")[0]; // Get the employer ID
                openStatementByCompany(employerId);
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a company.");
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static void openStatementByCompany(String employerId) {
        JOptionPane.showMessageDialog(null, "Generating statements for employer ID: " + employerId);

        DatabaseConnection.loadProperties();
        String pdfDirectory = DatabaseConnection.statementDataFilePath + employerId + "\\";
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String outputFilePath = pdfDirectory + employerId + "-" + todayDate + ".pdf";

        File directory = new File(pdfDirectory);
        File[] pdfFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (pdfFiles == null || pdfFiles.length == 0) {
            JOptionPane.showMessageDialog(null, "No PDF files found in directory: " + pdfDirectory, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Create PDFMergerUtility to merge PDF files
            PDFMergerUtility mergerUtility = new PDFMergerUtility();
            mergerUtility.setDestinationFileName(outputFilePath);

            // Add PDF files to merge
            for (File pdfFile : pdfFiles) {
                mergerUtility.addSource(pdfFile);
            }

            // Merge the PDFs
            mergerUtility.mergeDocuments(null);
            JOptionPane.showMessageDialog(null, "PDFs merged successfully into: " + outputFilePath, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error merging PDF files: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private static void populateEmployerComboBox(JComboBox<String> comboBox) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT employer_id FROM employer")) {

            while (resultSet.next()) {
                comboBox.addItem(resultSet.getString("employer_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void displayEditableEmployeeDialog(EmployeeTableModel model, int rowIndex) {
        JDialog dialog = new JDialog((Frame) null, "Edit Employee Details", true);
        dialog.setSize(900, 600); // Adjust dialog size as needed
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        AtomicInteger rowCounter = new AtomicInteger(0); // Row tracker for layout

        SixConsumer<String, Component, String, Component, String, Component> addTripleLabeledField = (label1, component1, label2, component2, label3, component3) -> {
            int row = rowCounter.getAndIncrement();

            // First Column
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.15;
            formPanel.add(new JLabel(label1), gbc);
            gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.35;
            formPanel.add(component1, gbc);

            // Second Column
            gbc.gridx = 2; gbc.gridy = row; gbc.weightx = 0.15;
            formPanel.add(new JLabel(label2), gbc);
            gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.35;
            formPanel.add(component2, gbc);

            // Third Column
            gbc.gridx = 4; gbc.gridy = row; gbc.weightx = 0.15;
            formPanel.add(new JLabel(label3), gbc);
            gbc.gridx = 5; gbc.gridy = row; gbc.weightx = 0.35;
            formPanel.add(component3, gbc);
        };

        // Row 1: Employee ID, SSN, Employer ID
        JTextField employeeIdField = new JTextField(model.getValueAt(rowIndex, 0).toString());
        employeeIdField.setEditable(false);
        JTextField ssnField = new JTextField(model.getValueAt(rowIndex, 1).toString());
        JTextField employerIdField = new JTextField(model.getValueAt(rowIndex, 2).toString());
        addTripleLabeledField.accept("Employee ID:", employeeIdField, "SSN:", ssnField, "Employer ID:", employerIdField);

        // Row 2: First Name, Last Name, Middle Name
        JTextField firstNameField = new JTextField(model.getValueAt(rowIndex, 3).toString());
        JTextField lastNameField = new JTextField(model.getValueAt(rowIndex, 4).toString());
        JTextField middleNameField = new JTextField(model.getValueAt(rowIndex, 5).toString());
        addTripleLabeledField.accept("First Name:", firstNameField, "Last Name:", lastNameField, "Middle Name:", middleNameField);

        // Row 3: Date of Birth, Date Hired, Gender
        JXDatePicker dobPicker = new JXDatePicker();
        JXDatePicker dateHiredPicker = new JXDatePicker();
        dobPicker.setFormats("MM-dd-yyyy");
        dateHiredPicker.setFormats("MM-dd-yyyy");
        try {
            dobPicker.setDate(new SimpleDateFormat("MM-dd-yyyy").parse(model.getValueAt(rowIndex, 6).toString()));
            dateHiredPicker.setDate(new SimpleDateFormat("MM-dd-yyyy").parse(model.getValueAt(rowIndex, 7).toString()));
        } catch (Exception ignored) {}
        String[] genderOptions = {"Male", "Female"};
        JComboBox<String> genderComboBox = new JComboBox<>(genderOptions);
        genderComboBox.setSelectedItem(model.getValueAt(rowIndex, 8).toString().equals("M") ? "Male" : "Female");
        addTripleLabeledField.accept("Date of Birth:", dobPicker, "Date Hired:", dateHiredPicker, "Gender:", genderComboBox);

        // Row 4: Marital Status, Current Spouse, Married Date
        String[] maritalStatusOptions = {"Single", "Married", "Divorced", "Widowed"};
        JComboBox<String> maritalStatusComboBox = new JComboBox<>(maritalStatusOptions);
        String maritalStatusValue = model.getValueAt(rowIndex, 9).toString();
        switch (maritalStatusValue) {
            case "S" -> maritalStatusComboBox.setSelectedItem("Single");
            case "M" -> maritalStatusComboBox.setSelectedItem("Married");
            case "D" -> maritalStatusComboBox.setSelectedItem("Divorced");
            case "W" -> maritalStatusComboBox.setSelectedItem("Widowed");
        }
        JTextField currentSpouseField = new JTextField(model.getValueAt(rowIndex, 10).toString());
        JXDatePicker marriedDatePicker = new JXDatePicker();
        marriedDatePicker.setFormats("MM-dd-yyyy");
        try {
            marriedDatePicker.setDate(new SimpleDateFormat("MM-dd-yyyy").parse(model.getValueAt(rowIndex, 11).toString()));
        } catch (Exception ignored) {}
        addTripleLabeledField.accept("Marital Status:", maritalStatusComboBox, "Current Spouse:", currentSpouseField, "Married Date:", marriedDatePicker);

        // Row 5: Address, Phone, City
        JTextField addressField = new JTextField(model.getValueAt(rowIndex, 12).toString());
        JTextField phoneField = new JTextField(model.getValueAt(rowIndex, 13).toString());
        JTextField cityField = new JTextField(model.getValueAt(rowIndex, 14).toString());
        addTripleLabeledField.accept("Address:", addressField, "Phone:", phoneField, "City:", cityField);

        // Row 6: State, Zip, Status
        JTextField stateField = new JTextField(model.getValueAt(rowIndex, 15).toString());
        JTextField zipField = new JTextField(model.getValueAt(rowIndex, 16).toString());
        String[] statusOptions = {"AA", "PT", "IU", "SU"};
        JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setSelectedItem(model.getValueAt(rowIndex, 27).toString());
        addTripleLabeledField.accept("State:", stateField, "Zip:", zipField, "Status:", statusComboBox);

        // Row 7: Previous SSN, Previous Employer ID, Previous First Name
        JTextField previousSsnField = new JTextField(model.getValueAt(rowIndex, 17).toString());
        JTextField previousEmployerIdField = new JTextField(model.getValueAt(rowIndex, 18).toString());
        JTextField previousFirstNameField = new JTextField(model.getValueAt(rowIndex, 19).toString());
        addTripleLabeledField.accept("Previous SSN:", previousSsnField, "Previous Employer ID:", previousEmployerIdField, "Previous First Name:", previousFirstNameField);

        // Row 8: Previous Last Name, Previous Middle Name, Previous Spouse
        JTextField previousLastNameField = new JTextField(model.getValueAt(rowIndex, 20).toString());
        JTextField previousMiddleNameField = new JTextField(model.getValueAt(rowIndex, 21).toString());
        JTextField previousSpouseField = new JTextField(model.getValueAt(rowIndex, 22).toString());
        addTripleLabeledField.accept("Previous Last Name:", previousLastNameField, "Previous Middle Name:", previousMiddleNameField, "Previous Spouse:", previousSpouseField);

        // Row 9: Created Date, Created By, Last Updated Date
        JXDatePicker createdDatePicker = new JXDatePicker(new java.util.Date());
        JXDatePicker lastUpdatedDatePicker = new JXDatePicker(new java.util.Date());
        createdDatePicker.setFormats("MM-dd-yyyy");
        lastUpdatedDatePicker.setFormats("MM-dd-yyyy");
        createdDatePicker.setEnabled(false);
        lastUpdatedDatePicker.setEnabled(false);
        JTextField createdByField = new JTextField("Admin");
        JTextField lastUpdatedByField = new JTextField("Admin");
        createdByField.setEditable(false);
        lastUpdatedByField.setEditable(false);
        addTripleLabeledField.accept("Created Date:", createdDatePicker, "Created By:", createdByField, "Last Updated Date:", lastUpdatedDatePicker);

        // Row 10: Last Updated By, Balance Forward (Balance Forward completes the last row)
        JTextField balanceForwardField = new JTextField(model.getValueAt(rowIndex, 28).toString());
        balanceForwardField.setEditable(false);
        
        JXDatePicker terminatedDatePicker = new JXDatePicker();
        terminatedDatePicker.setFormats("MM-dd-yyyy");
        try {
        	terminatedDatePicker.setDate(new SimpleDateFormat("MM-dd-yyyy").parse(model.getValueAt(rowIndex, 29).toString()));
        } catch (Exception ignored) {}
        addTripleLabeledField.accept("Last Updated By:", lastUpdatedByField, "Balance Forward:", balanceForwardField, "Terminated Date:", terminatedDatePicker);
        
     // Set initial state of terminatedDatePicker based on the current status
        if (statusComboBox.getSelectedItem().toString().equals("PT")) {
            terminatedDatePicker.setEditable(true);
        } else {
            terminatedDatePicker.setEditable(false);
        }

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        
        JButton beneficiaryButton = new JButton("Manage Beneficiaries");
        beneficiaryButton.setBackground(Color.YELLOW); 
        beneficiaryButton.setForeground(Color.BLACK);
        
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        saveButton.setBackground(new Color(144, 238, 144)); // Light Green
        saveButton.setForeground(Color.BLACK);   
        
        cancelButton.setBackground(new Color(173, 216, 255));
        cancelButton.setForeground(Color.BLACK); 
        
        JButton printButton = new JButton("Print");
        printButton.setBackground(new Color(255, 215, 0)); // Gold
        printButton.setForeground(Color.BLACK);
        
        saveButton.addActionListener(e -> {
            try {
            	
            	if(statusComboBox.getSelectedItem().toString().equals("PT") && terminatedDatePicker.getDate() == null) {
            		JOptionPane.showMessageDialog(dialog, "Please enter a valid termination date", "Error", JOptionPane.ERROR_MESSAGE);
            		return;
            	}
                // Save logic (from previous implementation)
                model.setValueAt(ssnField.getText(), rowIndex, 1);
                model.setValueAt(employerIdField.getText(), rowIndex, 2);
                model.setValueAt(firstNameField.getText(), rowIndex, 3);
                model.setValueAt(lastNameField.getText(), rowIndex, 4);
                model.setValueAt(middleNameField.getText(), rowIndex, 5);
                if (dobPicker.getDate() != null) {
                    model.setValueAt(new SimpleDateFormat("MM-dd-yyyy").format(dobPicker.getDate()), rowIndex, 6);
                } else {
                	model.setValueAt("", rowIndex, 6);
                }
                if (dateHiredPicker.getDate() != null) {
                    model.setValueAt(new SimpleDateFormat("MM-dd-yyyy").format(dateHiredPicker.getDate()), rowIndex, 7);
                } else {
                	model.setValueAt("", rowIndex, 7);
                }
                model.setValueAt(genderComboBox.getSelectedItem().toString().equals("Male") ? "M" : "F", rowIndex, 8);
                model.setValueAt(maritalStatusComboBox.getSelectedItem().toString().charAt(0) + "", rowIndex, 9);
                model.setValueAt(currentSpouseField.getText(), rowIndex, 10);
                if (marriedDatePicker.getDate() != null) {
                    model.setValueAt(new SimpleDateFormat("MM-dd-yyyy").format(marriedDatePicker.getDate()), rowIndex, 11);
                } else {
                	model.setValueAt("", rowIndex, 11);
                }
                model.setValueAt(addressField.getText(), rowIndex, 12);
                model.setValueAt(phoneField.getText(), rowIndex, 13);
                model.setValueAt(cityField.getText(), rowIndex, 14);
                model.setValueAt(stateField.getText(), rowIndex, 15);
                model.setValueAt(zipField.getText(), rowIndex, 16);
                model.setValueAt(previousSsnField.getText(), rowIndex, 17);
                model.setValueAt(previousEmployerIdField.getText(), rowIndex, 18);
                model.setValueAt(previousFirstNameField.getText(), rowIndex, 19);
                model.setValueAt(previousLastNameField.getText(), rowIndex, 20);
                model.setValueAt(previousMiddleNameField.getText(), rowIndex, 21);
                model.setValueAt(previousSpouseField.getText(), rowIndex, 22);
                model.setValueAt(new SimpleDateFormat("MM-dd-yyyy").format(createdDatePicker.getDate()), rowIndex, 23);
                model.setValueAt("Admin", rowIndex, 24);
                model.setValueAt(new SimpleDateFormat("MM-dd-yyyy").format(lastUpdatedDatePicker.getDate()), rowIndex, 25);
                model.setValueAt("Admin", rowIndex, 26);
                model.setValueAt(statusComboBox.getSelectedItem().toString(), rowIndex, 27);
                model.setValueAt(balanceForwardField.getText(), rowIndex, 28);
                
                String terminatedDate = terminatedDatePicker.getDate() == null ? "" : new SimpleDateFormat("MM-dd-yyyy").format(terminatedDatePicker.getDate());
                model.setValueAt(terminatedDate, rowIndex, 29);
                model.updateRow(rowIndex); // Save changes to the database
                JOptionPane.showMessageDialog(dialog, "Changes saved successfully.");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving changes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        beneficiaryButton.addActionListener(e -> {
            String employeeId = employeeIdField.getText(); // Ensure this retrieves the correct employee ID

            if (employeeId == null || employeeId.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please save the employee first to manage beneficiaries.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Open the Beneficiary Panel in a Dialog
            JDialog beneficiaryDialog = new JDialog((Frame) null, "Manage Beneficiaries", true);
            beneficiaryDialog.setSize(600, 400);
            beneficiaryDialog.setLayout(new BorderLayout());

            // Add the Beneficiary Panel
            JPanel beneficiaryPanel = createBeneficiaryPanel(employeeId);
            beneficiaryDialog.add(beneficiaryPanel, BorderLayout.CENTER);

            // Close Button
            JButton closeButton = new JButton("Close");
            closeButton.setBackground(Color.YELLOW); // Light Yellow (RGB: 255, 255, 153)
            closeButton.setForeground(Color.BLACK); // Black text
            
            closeButton.addActionListener(closeEvent -> beneficiaryDialog.dispose());
            JPanel benButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            benButtonPanel.add(closeButton);

            beneficiaryDialog.add(benButtonPanel, BorderLayout.SOUTH);
            beneficiaryDialog.setLocationRelativeTo(null); // Center the dialog
            beneficiaryDialog.setVisible(true);        	
        });
        
        statusComboBox.addActionListener(e -> {
            String selectedStatus = statusComboBox.getSelectedItem().toString();
            if ("PT".equals(selectedStatus)) {
                terminatedDatePicker.setEditable(true);
            } else {
                terminatedDatePicker.setEditable(false);
                terminatedDatePicker.setDate(null); // Optionally clear the field when disabled
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(beneficiaryButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(printButton);
        
        // print employee information
        printButton.addActionListener(e -> {

 			PdfReader reader;
 			try {
 				reader = new PdfReader(DatabaseConnection.employeeDetailsTemplateFile);

 				PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(DatabaseConnection.employeeDetailsReportFile));
 				
 				AcroFields form = stamper.getAcroFields();
 				HashMap<String, Employer> mapEmployers = new EmployerTableModel().getAllEmployers();
 				Employer currentEmployer = mapEmployers.get(employerIdField.getText());
 				String currentEmployerName = currentEmployer.getEmployerName();
 				
 				String previousEmployerName = "";
 				Employer previousEmployer = null;
 				if(previousEmployerIdField.getText().equals("") || previousEmployerIdField.getText() == null) {
 					previousEmployerName = "";
 				} else {
 					previousEmployer = mapEmployers.get(employerIdField.getText());
 					previousEmployerName = previousEmployer.getEmployerName();
 				}
 				
 				form.setField("Employee Name", firstNameField.getText() + " " + middleNameField.getText() + "" 
 						+ lastNameField.getText());
 				form.setField("Current Employer Name", currentEmployerName);
 				form.setField("Address", addressField.getText());
 				form.setField("City", cityField.getText());
 				form.setField("State", stateField.getText());
 				form.setField("Zip", zipField.getText()); 
 				form.setField("Phone", phoneField.getText());
 				form.setField("Previous Employer Name", previousEmployerName);
 				form.setField("Date Hired", dateHiredPicker.getDate() == null ? "" : new SimpleDateFormat("MM-dd-yyyy").format(dateHiredPicker.getDate()));
 				
 				stamper.setFormFlattening(true);
 				stamper.close();
 				reader.close();

            	File reportFile = new File(DatabaseConnection.employeeDetailsReportFile);
                Desktop.getDesktop().open(reportFile);
                              
 			} catch (IOException | DocumentException e1) {
 				// TODO Auto-generated catch block
 				e1.printStackTrace();
 			}
         
         });

        

        // Add form and button panel to dialog
        dialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    
    private static JPanel createBeneficiaryPanel(String employeeId) {
        JPanel beneficiaryPanel = new JPanel(new BorderLayout());
        beneficiaryPanel.setBorder(BorderFactory.createTitledBorder("Employee Beneficiaries"));

        // Table for displaying beneficiaries
        DefaultTableModel beneficiaryTableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Relation", "Phone", "Address", "Order", "Type"}, 0);
        JTable beneficiaryTable = new JTable(beneficiaryTableModel) {
        	
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                    if (!isRowSelected(row)) {
                        if (row % 2 == 0) {
                            c.setBackground(new Color(173, 216, 255)); // Light Blue
                        } else {
                            c.setBackground(Color.WHITE); // White
                        }
                    } else {
                        c.setBackground(new Color(173, 216, 230)); // Highlight color for selected row
                    }
                    c.setForeground(Color.BLACK); // Black text
                //}
                return c;
            }
        	
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        beneficiaryTable.setRowHeight(25);
        beneficiaryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader tableHeader = beneficiaryTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13)); // Bold font with size 14
        tableHeader.setReorderingAllowed(false); // Optional: Prevent column reordering

        // Load beneficiaries from the database
        loadBeneficiaries(employeeId, beneficiaryTableModel);

        // Buttons for Add, Edit, Delete
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(244, 164, 96)); // Light Yellow (RGB: 255, 255, 153)
        addButton.setForeground(Color.BLACK); // Black text
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(147, 112, 219)); // Brown (RGB: 139, 69, 19)
        deleteButton.setForeground(Color.BLACK); // Black text  

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        // Add action listeners for buttons
        addButton.addActionListener(e -> openBeneficiaryDialog(null, employeeId, beneficiaryTableModel));
        
        beneficiaryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double-click detected
                    int row = beneficiaryTable.getSelectedRow();
                    if (row >= 0) {
                        String beneficiaryId = beneficiaryTableModel.getValueAt(row, 0).toString();
                        openBeneficiaryDialog(beneficiaryId, employeeId, beneficiaryTableModel);
                    }
                }
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = beneficiaryTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(
                        beneficiaryPanel,
                        "Are you sure you want to delete this beneficiary?",
                        "Delete Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    String beneficiaryId = beneficiaryTableModel.getValueAt(selectedRow, 0).toString();
                    deleteBeneficiary(beneficiaryId);
                    beneficiaryTableModel.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(beneficiaryPanel, "Please select a beneficiary to delete.");
            }
        });

        beneficiaryPanel.add(new JScrollPane(beneficiaryTable), BorderLayout.CENTER);
        beneficiaryPanel.add(buttonPanel, BorderLayout.SOUTH);

        return beneficiaryPanel;
    }

    private static void openBeneficiaryDialog(String beneficiaryId, String employeeId, DefaultTableModel tableModel) {
        JDialog dialog = new JDialog((Frame) null, beneficiaryId == null ? "Add Beneficiary" : "Edit Beneficiary", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));

        // Input fields
        JTextField nameField = new JTextField();
        JTextField relationField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField orderField = new JTextField();
        
        Map<String, String> typeMap = Map.of(
                "Primary", "1",
                "Secondary", "2"
        );
        Map<String, String> reverseTypeMap = Map.of(
                "1", "Primary",
                "2", "Secondary"
        );
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Primary", "Secondary"});

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Relation:"));
        dialog.add(relationField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Address:"));
        dialog.add(addressField);
        dialog.add(new JLabel("Order:"));
        dialog.add(orderField);
        dialog.add(new JLabel("Type:"));
        dialog.add(typeComboBox);

        String employee_beneficiary_id = "";
        // Pre-fill fields for editing
        if (beneficiaryId != null) {
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement ps = connection.prepareStatement(
                         "SELECT * FROM employee_beneficiary WHERE employee_beneficiary_id = ?")) {
                ps.setString(1, beneficiaryId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                	employee_beneficiary_id = rs.getString("employee_beneficiary_id");
                    nameField.setText(rs.getString("benificiary_name"));
                    relationField.setText(rs.getString("benificiary_relation"));
                    phoneField.setText(rs.getString("benificiary_phone"));
                    addressField.setText(rs.getString("benificiary_address"));
                    orderField.setText(rs.getString("benificiary_order"));
                    
                    String typeValue = rs.getString("benificiary_type");
                    typeComboBox.setSelectedItem(reverseTypeMap.getOrDefault(typeValue, "Primary"));
                
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        // Buttons
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        saveButton.setBackground(new Color(144, 238, 144)); // Light Green
        saveButton.setForeground(Color.BLACK);   
        
        cancelButton.setBackground(new Color(173, 216, 255));
        cancelButton.setForeground(Color.BLACK); 
        dialog.add(saveButton);
        dialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            try (Connection connection = DatabaseConnection.getConnection()) {
                PreparedStatement ps;
                if (beneficiaryId == null) { // Add
                    ps = connection.prepareStatement(
                            "INSERT INTO employee_beneficiary (benificiary_name, benificiary_relation, benificiary_phone, benificiary_address, benificiary_order, benificiary_type, employee_id) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?)");

                    ps.setString(7, employeeId);
                } else { // Edit
                    ps = connection.prepareStatement(
                            "UPDATE employee_beneficiary SET benificiary_name = ?, benificiary_relation = ?, benificiary_phone = ?, benificiary_address = ?, benificiary_order = ?, benificiary_type = ? " +
                                    "WHERE employee_beneficiary_id = ?");
                    ps.setString(7, beneficiaryId);
                }
                ps.setString(1, nameField.getText());
                ps.setString(2, relationField.getText());
                ps.setString(3, phoneField.getText());
                ps.setString(4, addressField.getText());
                ps.setString(5, orderField.getText());
                
                String selectedType = (String) typeComboBox.getSelectedItem();
                ps.setString(6, typeMap.get(selectedType));
                

                ps.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Beneficiary saved successfully!");

                // Reload table
                tableModel.setRowCount(0); // Clear existing rows
                loadBeneficiaries(employeeId, tableModel);
                dialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error saving beneficiary: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private static void loadBeneficiaries(String employeeId, DefaultTableModel tableModel) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM employee_beneficiary WHERE employee_id = ?")) {
            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("employee_beneficiary_id"),
                        rs.getString("benificiary_name"),
                        rs.getString("benificiary_relation"),
                        rs.getString("benificiary_phone"),
                        rs.getString("benificiary_address"),
                        rs.getInt("benificiary_order"),
                        rs.getString("benificiary_type")!= null && rs.getString("benificiary_type").equals("1") ? "Primary" : "Secondary"
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void deleteBeneficiary(String beneficiaryId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM employee_beneficiary WHERE employee_beneficiary_id = ?")) {
            ps.setString(1, beneficiaryId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    


    
    private static void displayEditableEmployerDialog(EmployerTableModel model, int rowIndex) {
        JDialog dialog = new JDialog((Frame) null, "Edit Employer Details", true);
        dialog.setSize(500, 700);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(12, 2, 10, 10));

        JTextField employerIdField = new JTextField(model.getValueAt(rowIndex, 0).toString());
        employerIdField.setEditable(false); // Employer ID should not be editable
        JTextField employerNameField = new JTextField(model.getValueAt(rowIndex, 1).toString());
        JTextField address1Field = new JTextField(model.getValueAt(rowIndex, 2).toString());
        JTextField address2Field = new JTextField(model.getValueAt(rowIndex, 3).toString());
        JTextField attentionLineField = new JTextField(model.getValueAt(rowIndex, 4).toString());
        JTextField phoneField = new JTextField(model.getValueAt(rowIndex, 5).toString());
        JTextField alternatePhoneField = new JTextField(model.getValueAt(rowIndex, 6).toString());
        JTextField notesField = new JTextField(model.getValueAt(rowIndex, 7).toString());

        // Date pickers for contract effective and expiry dates
        JXDatePicker contractEffectiveDatePicker = new JXDatePicker();
        JXDatePicker contractExpiryDatePicker = new JXDatePicker();

        // Set current values and date format
        contractEffectiveDatePicker.setFormats("MM-dd-yyyy");
        contractExpiryDatePicker.setFormats("MM-dd-yyyy");

        try {
            String effectiveDate = model.getValueAt(rowIndex, 8).toString();
            if (effectiveDate != null && !effectiveDate.isEmpty()) {
            	contractEffectiveDatePicker.setDate(new SimpleDateFormat("MM-dd-yyyy").parse(effectiveDate));
            }
            String expiryDate = model.getValueAt(rowIndex, 9).toString();
            if (expiryDate != null && !expiryDate.isEmpty()) {
            	contractExpiryDatePicker.setDate(new SimpleDateFormat("MM-dd-yyyy").parse(expiryDate));
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // Handle parsing errors if necessary
        }

        // Status ComboBox
        String[] statusOptions = {"Active", "In Active"};
        JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);

        // Map DB values ("AA", "IA") to display values ("Active", "In Active")
        String dbValue = model.getValueAt(rowIndex, 10).toString();
        if (dbValue.equals("AA")) {
            statusComboBox.setSelectedItem("Active");
        } else if (dbValue.equals("IA")) {
            statusComboBox.setSelectedItem("In Active");
        }

        formPanel.add(new JLabel("Employer ID:"));
        formPanel.add(employerIdField);
        formPanel.add(new JLabel("Employer Name:"));
        formPanel.add(employerNameField);
        formPanel.add(new JLabel("Address 1:"));
        formPanel.add(address1Field);
        formPanel.add(new JLabel("Address 2:"));
        formPanel.add(address2Field);
        formPanel.add(new JLabel("Attention Line:"));
        formPanel.add(attentionLineField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Alternate Phone:"));
        formPanel.add(alternatePhoneField);
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(notesField);
        formPanel.add(new JLabel("Contract Effective Date:"));
        formPanel.add(contractEffectiveDatePicker);
        formPanel.add(new JLabel("Contract Expiry Date:"));
        formPanel.add(contractExpiryDatePicker);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        
        JLabel lblContract = new JLabel("<html><a src=''> View Contract </a></html>");
        lblContract.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblContract.setForeground(Color.BLUE);

        lblContract.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Replace with the path to your local file
            	String contractFilePath = DatabaseConnection.contractFilePath;
            	String contractFileName = contractFilePath + employerIdField.getText() + ".pdf";
                File file = new File(contractFileName);
                if (file.exists()) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "File does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        formPanel.add(lblContract);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.setBackground(new Color(144, 238, 144)); // Light Green
        saveButton.setForeground(Color.BLACK);   
        
        cancelButton.setBackground(new Color(173, 216, 255));
        cancelButton.setForeground(Color.BLACK); 

        saveButton.addActionListener(e -> {
            model.setValueAt(employerNameField.getText(), rowIndex, 1);
            model.setValueAt(address1Field.getText(), rowIndex, 2);
            model.setValueAt(address2Field.getText(), rowIndex, 3);
            model.setValueAt(attentionLineField.getText(), rowIndex, 4);
            model.setValueAt(phoneField.getText(), rowIndex, 5);
            model.setValueAt(alternatePhoneField.getText(), rowIndex, 6);
            model.setValueAt(notesField.getText(), rowIndex, 7);

         // Format dates for database (yyyy-MM-dd)
            if (contractEffectiveDatePicker.getDate() != null) {
                model.setValueAt(new SimpleDateFormat("MM-dd-yyyy").format(contractEffectiveDatePicker.getDate()), rowIndex, 8);
            } else {
                model.setValueAt(null, rowIndex, 8);
            }

            if (contractExpiryDatePicker.getDate() != null) {
                model.setValueAt(new SimpleDateFormat("MM-dd-yyyy").format(contractExpiryDatePicker.getDate()), rowIndex, 9);
            } else {
                model.setValueAt(null, rowIndex, 9);
            }

            // Map display value to DB value
            String selectedStatus = (String) statusComboBox.getSelectedItem();
            String dbStatusValue = selectedStatus.equals("Active") ? "AA" : "IA";
            model.setValueAt(dbStatusValue, rowIndex, 10);
            model.setValueAt(new SimpleDateFormat("MM-dd-yyyy").format(new Date()), rowIndex, 11);
            model.setValueAt("Admin", rowIndex, 12);
            model.setValueAt(new SimpleDateFormat("MM-dd-yyyy").format(new Date()), rowIndex, 13);
            model.setValueAt("Admin", rowIndex, 14);
            
            try {
				model.updateRow(rowIndex);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // Save changes to the database
            JOptionPane.showMessageDialog(dialog, "Changes saved successfully.");
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

/*
    private static void displayContributionDetailsDialog(int contributionId, String employerId) {
        JDialog dialog = new JDialog((Frame) null, "Contribution Details", true);
        dialog.setSize(800, 500);
        dialog.setLayout(new BorderLayout());

        ContributionEntryDetailTableModel detailModel = new ContributionEntryDetailTableModel(contributionId);
        //JTable detailTable = new JTable(detailModel);
        
        
        JTable detailTable = new JTable(detailModel) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(173, 216, 255));
                    } else {
                        c.setBackground(Color.WHITE); // White
                    }
                } else {
                    c.setBackground(new Color(173, 216, 230)); // Highlight color for selected row (Darker blue)
                }
                return c;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow only "Share Amount" and "Reserve Amount" columns to be editable
                String columnName = getColumnName(column);
                return columnName.equalsIgnoreCase("Share Amount") || columnName.equalsIgnoreCase("Reserve Amount");
            }
            
        };
        
        JTableHeader tableHeader = detailTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13)); // Bold font with size 14
        tableHeader.setReorderingAllowed(false); // Optional: Prevent column reordering
        
     // Center-align the column values
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Apply the renderer to all columns
        for (int i = 0; i < detailTable.getColumnModel().getColumnCount(); i++) {
        	detailTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Set a larger row height (e.g., 30 pixels)
        detailTable.setRowHeight(20);
        
        hideTableColumn(detailTable, detailModel.getColumnIndex("Created Date"));
        hideTableColumn(detailTable, detailModel.getColumnIndex("Created By"));
        hideTableColumn(detailTable, detailModel.getColumnIndex("Last Updated Date"));
        hideTableColumn(detailTable, detailModel.getColumnIndex("Last Updated By"));

        
        
        
        

        JLabel summaryLabel = new JLabel("Total: Share Amount + Reserve Amount = 0.00");

        // Compute and update the summary whenever the data changes
        detailModel.addTableModelListener(e -> updateSummaryLabel(detailModel, summaryLabel));

        // Initial computation of summary
        updateSummaryLabel(detailModel, summaryLabel);

        JPanel buttonPanel = new JPanel();
        JButton bulkChangeButton = new JButton("Bulk Amount Change");
        JButton saveButton = new JButton("Save Changes");
        JButton closeButton = new JButton("Close");
        JButton addEmployeeButton = new JButton("Add Employee"); // Add Employee Button

        bulkChangeButton.setBackground(new Color(144, 238, 144)); // Light Green
        bulkChangeButton.setForeground(Color.BLACK);   
        
        saveButton.setBackground(new Color(173, 216, 255));
        saveButton.setForeground(Color.BLACK); 
        
        closeButton.setBackground(new Color(244, 164, 96)); // Light Yellow (RGB: 255, 255, 153)
        closeButton.setForeground(Color.BLACK); // Black text
        
        addEmployeeButton.setBackground(new Color(147, 112, 219)); // Brown (RGB: 139, 69, 19)
        addEmployeeButton.setForeground(Color.BLACK); // Black text        
        

        // Save changes and update summary in contribution table
        saveButton.addActionListener(e -> {
            if (detailTable.isEditing()) {
                detailTable.getCellEditor().stopCellEditing(); // Commit pending edits
            }
            detailModel.saveChanges();
            JOptionPane.showMessageDialog(dialog, "Changes saved successfully.");
            dialog.dispose(); // Close dialog after saving
        });

        // Close dialog
        closeButton.addActionListener(e -> dialog.dispose());

        // Add Employee Button functionality
        addEmployeeButton.addActionListener(e -> showAddEmployeePopup(detailModel, employerId, contributionId));
        bulkChangeButton.addActionListener(e -> showBulkAmountChangePopup(detailModel));

        buttonPanel.add(bulkChangeButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(addEmployeeButton); // Add the new button
        buttonPanel.add(closeButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(summaryLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        dialog.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

*/
    
    private static void displayContributionDetailsDialog(int contributionId, String employerId) {
        JDialog dialog = new JDialog((Frame) null, "Contribution Details", true);
        dialog.setSize(800, 500);
        dialog.setLayout(new BorderLayout());

        // --- Employer Name at the top ---
        Employer employer = edMapEmployers.get(employerId);
        String employerName = employer.getEmployerName();
        JLabel employerLabel = new JLabel("Employer: " + employerName);
        employerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        employerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(employerLabel, BorderLayout.NORTH);

        ContributionEntryDetailTableModel detailModel = new ContributionEntryDetailTableModel(contributionId);

        JTable detailTable = new JTable(detailModel) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(173, 216, 255));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(new Color(173, 216, 230));
                }
                return c;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                String columnName = getColumnName(column);
                return columnName.equalsIgnoreCase("Share Amount") || columnName.equalsIgnoreCase("Reserve Amount");
            }
        };

        JTableHeader tableHeader = detailTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13));
        tableHeader.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < detailTable.getColumnModel().getColumnCount(); i++) {
            detailTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        detailTable.setRowHeight(20);

        hideTableColumn(detailTable, detailModel.getColumnIndex("Created Date"));
        hideTableColumn(detailTable, detailModel.getColumnIndex("Created By"));
        hideTableColumn(detailTable, detailModel.getColumnIndex("Last Updated Date"));
        hideTableColumn(detailTable, detailModel.getColumnIndex("Last Updated By"));
        hideTableColumn(detailTable, detailModel.getColumnIndex("Detail ID"));
        hideTableColumn(detailTable, detailModel.getColumnIndex("Employer ID"));
        hideTableColumn(detailTable, detailModel.getColumnIndex("Employee ID"));
        hideTableColumn(detailTable, detailModel.getColumnIndex("Employer Name"));
        

        JLabel summaryLabel = new JLabel("Total: Share Amount + Reserve Amount = 0.00");
        detailModel.addTableModelListener(e -> updateSummaryLabel(detailModel, summaryLabel));
        updateSummaryLabel(detailModel, summaryLabel);

        JPanel buttonPanel = new JPanel();

        JButton bulkChangeButton = new JButton("Bulk Amount Change");
        JButton saveButton = new JButton("Save Changes");
        JButton closeButton = new JButton("Close");
        JButton addEmployeeButton = new JButton("Add Employee");
        JButton printButton = new JButton("Print");

        bulkChangeButton.setBackground(new Color(144, 238, 144));
        bulkChangeButton.setForeground(Color.BLACK);

        saveButton.setBackground(new Color(173, 216, 255));
        saveButton.setForeground(Color.BLACK);

        closeButton.setBackground(new Color(244, 164, 96));
        closeButton.setForeground(Color.BLACK);

        addEmployeeButton.setBackground(new Color(147, 112, 219));
        addEmployeeButton.setForeground(Color.BLACK);

        printButton.setBackground(new Color(255, 222, 173)); // NavajoWhite
        printButton.setForeground(Color.BLACK);

        saveButton.addActionListener(e -> {
            if (detailTable.isEditing()) {
                detailTable.getCellEditor().stopCellEditing();
            }
            detailModel.saveChanges();
            JOptionPane.showMessageDialog(dialog, "Changes saved successfully.");
            dialog.dispose();
        });

        closeButton.addActionListener(e -> dialog.dispose());

        addEmployeeButton.addActionListener(e -> showAddEmployeePopup(detailModel, employerId, contributionId));
        bulkChangeButton.addActionListener(e -> showBulkAmountChangePopup(detailModel));

        printButton.addActionListener(e -> {
            try {
                detailTable.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(dialog, "Printing failed: " + ex.getMessage());
            }
        });

        buttonPanel.add(bulkChangeButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(addEmployeeButton);
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(summaryLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        dialog.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }


    private static void updateSummaryLabel(ContributionEntryDetailTableModel model, JLabel summaryLabel) {
        double total = 0.0;
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                double shareAmount = Double.parseDouble(model.getValueAt(i, 6).toString()); // Share Amount
                double reserveAmount = Double.parseDouble(model.getValueAt(i, 7).toString()); // Reserve Amount
                total += shareAmount + reserveAmount;
            } catch (NumberFormatException e) {
                // Skip rows with invalid numbers
            }
        }
        summaryLabel.setText(String.format("Total: Share Amount + Reserve Amount = %.2f", total));
    }

    private static void showAddEmployeePopup(ContributionEntryDetailTableModel detailModel, String employerId, int contributionId) {
        JDialog popup = new JDialog((Frame) null, "Add Employee", true);
        popup.setSize(600, 400);
        popup.setLayout(new BorderLayout());

        List<Object[]> availableEmployees = getAvailableEmployees(employerId, contributionId);

        
        for(int i = 0; i < detailModel.getRowCount(); ++i) {
        	String contributionDetailsSSN = (String) detailModel.getValueAt(i, 3);
        	Iterator iterator = availableEmployees.iterator();
        	while (iterator.hasNext()) {
        		Object[] obj = (Object[]) iterator.next();
        		String addEmployeesSSN = (String) obj[1];
        		if(addEmployeesSSN.equalsIgnoreCase(contributionDetailsSSN)) {
        			iterator.remove();
        		}
        	}
        }
        
        String[] columnNames = {"Employee ID", "SSN", "First Name", "Last Name"};
        Object[][] employeeData = availableEmployees.toArray(new Object[0][]);

        JTable employeeTable = new JTable(employeeData, columnNames);
        employeeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JButton addButton = new JButton("Add Selected Employees");
        addButton.addActionListener(e -> {
            int[] selectedRows = employeeTable.getSelectedRows();
            for (int selectedRow : selectedRows) {
                Object[] row = availableEmployees.get(selectedRow);
                // Add the selected employee to the contribution details model
                detailModel.addEmployee(row);
            }
            popup.dispose(); // Close popup after adding
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> popup.dispose());
        
        addButton.setBackground(new Color(144, 238, 144)); // Light Green
        addButton.setForeground(Color.BLACK);   
        
        cancelButton.setBackground(new Color(173, 216, 255));
        cancelButton.setForeground(Color.BLACK); 

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        popup.add(new JScrollPane(employeeTable), BorderLayout.CENTER);
        popup.add(buttonPanel, BorderLayout.SOUTH);
        popup.setLocationRelativeTo(null);
        popup.setVisible(true);
    }

    private static List<Object[]> getAvailableEmployees(String employerId, int contributionId) {
        List<Object[]> employees = new ArrayList<>();
        String query = """
            SELECT e.employee_id, e.ssn, e.first_name, e.last_name 
            FROM employee e
            WHERE e.employer_id = ?
            AND e.employee_id NOT IN (
                SELECT d.employee_id FROM contribution_entry_detail d WHERE d.contribution_entry_id = ?
            ) ORDER BY e.first_name
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, employerId);
            preparedStatement.setInt(2, contributionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    employees.add(new Object[]{
                            resultSet.getInt("employee_id"),
                            resultSet.getString("ssn"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }
    
    private static void openDividendInputScreen() {
        JDialog dialog = new JDialog((Frame) null, "Dividend Input", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel yearLabel = new JLabel("Dividend Year:");
        JComboBox<String> yearComboBox = new JComboBox<>();

        // Populate years from 2023 to 2100
        for (int year = 2023; year <= 2100; year++) {
            yearComboBox.addItem(String.valueOf(year));
        }

        JLabel rateLabel = new JLabel("Dividend Rate:");
        JTextField rateField = new JTextField();


        JButton submitButton = new JButton("Submit");
        JButton closeButton = new JButton("Close"); // Add the Close button

        formPanel.add(yearLabel);
        formPanel.add(yearComboBox);
        formPanel.add(rateLabel);
        formPanel.add(rateField);

        dialog.add(formPanel, BorderLayout.NORTH);

        // TextArea to display the output
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        dialog.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(closeButton); // Add the Close button to the panel

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Submit Button Action
        submitButton.addActionListener(e -> {
            String dividendYear = (String) yearComboBox.getSelectedItem();
            String dividendRate = rateField.getText().trim();

            if (dividendYear.isEmpty() || dividendRate.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Both fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Clear the output area
            outputArea.setText("");

            // Execute the Java program and capture its output
            new Thread(() -> {
                try {
                    executeDividendReportProgram(dividendYear, dividendRate, outputArea);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> outputArea.append("Error occurred: " + ex.getMessage() + "\n"));
                }
            }).start();
        });
        
     // Close Button Action
        closeButton.addActionListener(e -> dialog.dispose()); // Close the dialog on click

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private static void executeDividendReportProgram(String dividendYear, String dividendRate, JTextArea outputArea) {
        // Append messages to the JTextArea
        SwingUtilities.invokeLater(() -> outputArea.append("Starting Dividend Report generation...\n"));

        // Run the report generation logic in a new thread to avoid blocking the UI
        new Thread(() -> {
            try {
                // Redirect System.out to the JTextArea for logging
                PrintStream printStream = new PrintStream(new OutputStream() {
                    @Override
                    public void write(int b) {
                        SwingUtilities.invokeLater(() -> outputArea.append(String.valueOf((char) b)));
                    }
                });
                System.setOut(printStream);
                System.setErr(printStream);

                // Call the target class directly
                com.util.GenerateDividendSummaryReportCSV.generateReport(Integer.parseInt(dividendYear), Double.parseDouble(dividendRate));

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> outputArea.append("Error occurred: " + ex.getMessage() + "\n"));
            } finally {
                // Restore System.out and System.err to their original streams if necessary
                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
                System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
            }
        }).start();
    }
    
    private static void openStatementOutputPopup() {
        JDialog dialog = new JDialog((Frame) null, "Statement of Accounts", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        dialog.add(scrollPane, BorderLayout.CENTER);

        // Close Button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Execute the Java class in a separate thread to capture output
        new Thread(() -> {
            try {
                // Redirect System.out to the JTextArea
                PrintStream printStream = new PrintStream(new OutputStream() {
                    @Override
                    public void write(int b) {
                        SwingUtilities.invokeLater(() -> outputArea.append(String.valueOf((char) b)));
                    }
                });
                System.setOut(printStream);
                System.setErr(printStream);

                // Call the Generate Statement of Accounts Java class
                GenerateStatementOfAccounts.generate();

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> outputArea.append("Error occurred: " + ex.getMessage() + "\n"));
            } finally {
                // Restore System.out and System.err to their original streams
                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
                System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
            }
        }).start();

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private static void openUpdateBalanceOutputPopup() {
        JDialog dialog = new JDialog((Frame) null, "Update Balances", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        dialog.add(scrollPane, BorderLayout.CENTER);

        // Close Button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Execute the Java class in a separate thread to capture output
        new Thread(() -> {
            try {
                // Redirect System.out to the JTextArea
                PrintStream printStream = new PrintStream(new OutputStream() {
                    @Override
                    public void write(int b) {
                        SwingUtilities.invokeLater(() -> outputArea.append(String.valueOf((char) b)));
                    }
                });
                System.setOut(printStream);
                System.setErr(printStream);

                // Call the Generate Statement of Accounts Java class
                UpdateFinalBalances.updateBalances();

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> outputArea.append("Error occurred: " + ex.getMessage() + "\n"));
            } finally {
                // Restore System.out and System.err to their original streams
                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
                System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
            }
        }).start();

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    
    private static void hideTableColumn(JTable table, int columnIndex) {
        if (columnIndex >= 0) {
            table.getColumnModel().getColumn(columnIndex).setMinWidth(0);
            table.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
            table.getColumnModel().getColumn(columnIndex).setPreferredWidth(0);
        }
    }
    
    private static void addPlaceholderText(JTextField textField, String placeholder) {
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });

        // Set the initial placeholder text
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
    }
    private static void showBulkAmountChangePopup(ContributionEntryDetailTableModel detailModel) {
        JDialog bulkChangeDialog = new JDialog((Frame) null, "Bulk Amount Change", true);
        bulkChangeDialog.setSize(400, 250);
        bulkChangeDialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        // Input field for amount
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel amountLabel = new JLabel("Enter Amount:");
        JTextField amountField = new JTextField(15);
        amountPanel.add(amountLabel);
        amountPanel.add(amountField);

        // Radio buttons for selecting Share/Reserve Amount
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JRadioButton shareAmountButton = new JRadioButton("Apply for Share Amount");
        JRadioButton reserveAmountButton = new JRadioButton("Apply for Reserve Amount");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(shareAmountButton);
        buttonGroup.add(reserveAmountButton);
        radioPanel.add(shareAmountButton);
        radioPanel.add(reserveAmountButton);

        // Add components to form panel
        formPanel.add(amountPanel);
        formPanel.add(radioPanel);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton okButton = new JButton("OK");
        JButton closeButton = new JButton("Close");

        okButton.addActionListener(e -> {
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty() || (!shareAmountButton.isSelected() && !reserveAmountButton.isSelected())) {
                JOptionPane.showMessageDialog(bulkChangeDialog, "Please enter an amount and select an option.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (shareAmountButton.isSelected()) {
                    // Apply amount to Share Amount column
                    for (int i = 0; i < detailModel.getRowCount(); i++) {
                        detailModel.setValueAt(amountText, i, detailModel.getColumnIndex("Share Amount"));
                    }
                } else if (reserveAmountButton.isSelected()) {
                    // Apply amount to Reserve Amount column
                    for (int i = 0; i < detailModel.getRowCount(); i++) {
                        detailModel.setValueAt(amountText, i, detailModel.getColumnIndex("Reserve Amount"));
                    }
                }
                JOptionPane.showMessageDialog(bulkChangeDialog, "Bulk amount change applied successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                bulkChangeDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(bulkChangeDialog, "Please enter a valid numeric amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        closeButton.addActionListener(e -> bulkChangeDialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(closeButton);

        bulkChangeDialog.add(formPanel, BorderLayout.CENTER);
        bulkChangeDialog.add(buttonPanel, BorderLayout.SOUTH);
        bulkChangeDialog.setLocationRelativeTo(null);
        bulkChangeDialog.setVisible(true);
    }
    
    private static boolean isEmployerValid(String employerId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM employer WHERE employer_id = ?")) {
            statement.setString(1, employerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // True if employer exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Utility to check if contribution entry already exists
    private static boolean isContributionEntryPresent(String employerId, String periodMonth, String periodYear) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM contribution_entry WHERE employer_id = ? AND period_month = ? AND period_year = ?")) {
            statement.setString(1, employerId);
            statement.setString(2, periodMonth);
            statement.setString(3, periodYear);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // True if entry exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
 // Helper methods for validations and popup
    private static double parseAmount(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static boolean isSSNValid(String ssn) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM employee WHERE ssn = ?")) {
            statement.setString(1, ssn);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void displayEditableTablePopup(List<String[]> records, double totalAmount, String employerId, String periodMonth,
    		String periodYear, String cashReceivedDate) {
        JDialog dialog = new JDialog((Frame) null, "Review and Confirm Records", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        // Create table model
        String[] columnNames = {"SSN", "Share Amount", "Reserve Amount"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Make SSN non-editable
            }
        };

        for (String[] record : records) {
            tableModel.addRow(record);
        }

        JTable table = new JTable(tableModel);
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13)); // Bold font with size 14
        tableHeader.setReorderingAllowed(false); // Optional: Prevent column reordering
        JScrollPane scrollPane = new JScrollPane(table);

        // Bottom panel for total amount and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel totalLabel = new JLabel(String.format("Total Amount: $%.2f", totalAmount));
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        // Add TableModelListener to recalculate total when amounts change
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                recalculateTotal(tableModel, totalLabel);
            }
        });

        confirmButton.addActionListener(e -> {
            try {
				saveRecordsToDatabase(tableModel, employerId, periodMonth, periodYear, cashReceivedDate);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            JOptionPane.showMessageDialog(dialog, "Records saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        confirmButton.setBackground(new Color(144, 238, 144)); // Light Green
        confirmButton.setForeground(Color.BLACK);   
        
        cancelButton.setBackground(new Color(173, 216, 255));
        cancelButton.setForeground(Color.BLACK); 

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // Helper Method: Recalculate Total Amount
    private static void recalculateTotal(DefaultTableModel tableModel, JLabel totalLabel) {
        double total = 0.0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                double shareAmount = Double.parseDouble(tableModel.getValueAt(i, 1).toString());
                double reserveAmount = Double.parseDouble(tableModel.getValueAt(i, 2).toString());
                total += (shareAmount + reserveAmount);
            } catch (NumberFormatException ex) {
                // Ignore invalid amounts
            }
        }

        totalLabel.setText(String.format("Total Amount: $%.2f", total));
    }

 // Helper Method: Validate for Duplicate SSNs in File
    private static ArrayList<String> validateNoDuplicateSSNsInFile(List<String[]> records) {
        Set<String> ssnSet = new HashSet<>();
        ArrayList<String> duplicatesList = new ArrayList<String>();
        for (String[] record : records) {
            String ssn = record[0].trim();
            if (ssnSet.contains(ssn)) {
            	duplicatesList.add(ssn); // Duplicate SSN found
            }
            ssnSet.add(ssn);
        }
        return duplicatesList;
    }

    private static void saveRecordsToDatabase(DefaultTableModel tableModel, String employerId, String periodMonth,
    		String periodYear, String cashReceivedDate) throws SQLException {
    	ContributionSummaryTableModel contributionSummaryModel = new ContributionSummaryTableModel();
    	
    	
    	double summaryAmount = 0.0;
    	for (int i = 0; i < tableModel.getRowCount(); i++) {
    		summaryAmount += Double.parseDouble(tableModel.getValueAt(i, 1).toString()) + Double.parseDouble(tableModel.getValueAt(i, 2).toString());
    	}
    	
    	int nextContributionId =  contributionSummaryModel.getNextContributionId();
    	contributionSummaryModel.addRowManual(new Object[]{
    			nextContributionId, 
                Integer.parseInt(employerId),
                periodMonth,
                periodYear,
                String.valueOf(summaryAmount),
                new SimpleDateFormat("MM-dd-yyyy").format(new Date()),
                "Admin",
                new SimpleDateFormat("MM-dd-yyyy").format(new Date()),
                "Admin",
                cashReceivedDate.replace(".", "-").replace("-csv", "")
        });


    	ContributionEntryDetailTableModel contributionDetailModel = new ContributionEntryDetailTableModel(nextContributionId);
        int nextContributionDetailId = contributionDetailModel.getNextContributionEntryDetailId();
        
    	List<Object[]> lstContributionDetailEntries = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String ssn = tableModel.getValueAt(i, 0).toString();
            String shareAmount = tableModel.getValueAt(i, 1).toString();
            String reserveAmount = tableModel.getValueAt(i, 2).toString();
            System.out.println("Saving record: SSN=" + ssn + ", Share Amount=" + shareAmount + ", Reserve Amount=" + reserveAmount);
 
            // get EmployeeID
            EmployeeTableModel employeeModel = new EmployeeTableModel();
            Employee employee = employeeModel.searchForEligibility(ssn);
            String employeeId = employee.getEmployeeId();
            
            Object[] newRow = new Object[13];
            newRow[0] = nextContributionDetailId++;
            newRow[1] = employerId;
            newRow[2] = employeeId; // Employee ID
            newRow[3] = ssn; // SSN
            newRow[4] = periodMonth;    // Use existing periodMonth from current contribution
            newRow[5] = periodYear;     // Use existing periodYear from current contribution
            newRow[6] = shareAmount; 
            newRow[7] = reserveAmount; 
            newRow[8] = nextContributionId; // Contribution Entry ID
            newRow[9] = new Timestamp(System.currentTimeMillis()); // Created Date
            newRow[10] = "Admin";       // Created By (adjust as necessary)
            newRow[11] = new Timestamp(System.currentTimeMillis()); // Last Updated Date
            newRow[12] = "Admin";       // Last Updated By (adjust as necessary)
            
            lstContributionDetailEntries.add(newRow);
            
        }
        
        contributionDetailModel.saveDetailEntry(lstContributionDetailEntries);
        
    }



}

