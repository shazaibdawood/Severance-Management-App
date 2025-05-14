package com.ui;

import javax.swing.*;

import com.model.EmployeeTableModel;

import java.awt.*;
import java.text.ParseException;

public class EmployeeManagementApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Employee Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            EmployeeTableModel model = new EmployeeTableModel();
            JTable table = new JTable(model);

            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add");
            JButton deleteButton = new JButton("Delete");
            JButton updateButton = new JButton("Update");

            addButton.addActionListener(e -> {
                Object[] newRow = new Object[model.getColumnCount()];
                newRow[1] = JOptionPane.showInputDialog(frame, "Enter SSN:");
                newRow[2] = JOptionPane.showInputDialog(frame, "Enter Employer ID:");
                newRow[3] = JOptionPane.showInputDialog(frame, "Enter First Name:");
                newRow[4] = JOptionPane.showInputDialog(frame, "Enter Last Name:");
                newRow[5] = JOptionPane.showInputDialog(frame, "Enter Middle Name:");
                newRow[6] = JOptionPane.showInputDialog(frame, "Enter Date of Birth:");
                // Continue for remaining columns as needed.
                try {
					model.addRow(newRow);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                JOptionPane.showMessageDialog(frame, "Employee added successfully.");
            });

            deleteButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int confirmation = JOptionPane.showConfirmDialog(
                            frame,
                            "Are you sure you want to delete the selected employee?",
                            "Delete Confirmation",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirmation == JOptionPane.YES_OPTION) {
                        model.deleteRow(selectedRow);
                        JOptionPane.showMessageDialog(frame, "Employee deleted successfully.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a row to delete.");
                }
            });

            updateButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int confirmation = JOptionPane.showConfirmDialog(
                            frame,
                            "Are you sure you want to update the selected employee?",
                            "Update Confirmation",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirmation == JOptionPane.YES_OPTION) {
                        try {
							model.updateRow(selectedRow);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                        JOptionPane.showMessageDialog(frame, "Employee updated successfully.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a row to update.");
                }
            });

            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(updateButton);

            frame.add(new JScrollPane(table), BorderLayout.CENTER);
            frame.add(buttonPanel, BorderLayout.SOUTH);

            frame.setSize(1000, 600);
            frame.setVisible(true);
        });
    }
}

