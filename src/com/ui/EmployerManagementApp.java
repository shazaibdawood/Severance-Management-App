package com.ui;

import javax.swing.*;

import com.model.EmployerTableModel;

import java.awt.*;
import java.text.ParseException;

public class EmployerManagementApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Employer Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            EmployerTableModel model = new EmployerTableModel();
            JTable table = new JTable(model);

            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add");
            JButton deleteButton = new JButton("Delete");
            JButton updateButton = new JButton("Update");

			/*
			 * addButton.addActionListener(e -> { Object[] newRow = new
			 * Object[model.getColumnCount()]; // Example values for new row. Modify as
			 * needed. newRow[0] = "E123"; newRow[1] = "New Employer"; model.addRow(newRow);
			 * });
			 */
            
            addButton.addActionListener(e -> {
                // Collect data for a new row
                Object[] newRow = new Object[model.getColumnCount()];

                newRow[0] = JOptionPane.showInputDialog(frame, "Enter Employer ID:");
                if (newRow[0] == null || ((String) newRow[0]).trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Employer ID cannot be empty.");
                    return; // Stop if no ID is entered
                }
                newRow[1] = JOptionPane.showInputDialog(frame, "Enter Employer Name:");
                newRow[2] = JOptionPane.showInputDialog(frame, "Enter Address 1:");
                newRow[3] = JOptionPane.showInputDialog(frame, "Enter Address 2:");
                newRow[4] = JOptionPane.showInputDialog(frame, "Enter Attention Line:");
                newRow[5] = JOptionPane.showInputDialog(frame, "Enter Phone:");
                newRow[6] = JOptionPane.showInputDialog(frame, "Enter Alternate Phone:");
                newRow[7] = JOptionPane.showInputDialog(frame, "Enter Notes:");
                newRow[8] = JOptionPane.showInputDialog(frame, "Enter Contract Effective Date (yyyy-MM-dd HH:mm:ss):");
                newRow[9] = JOptionPane.showInputDialog(frame, "Enter Contract Expiry Date (yyyy-MM-dd HH:mm:ss):");


                // Add the new row to the model
                try {
					model.addRow(newRow);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                JOptionPane.showMessageDialog(frame, "Employer added successfully.");
            });


            deleteButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int confirmation = JOptionPane.showConfirmDialog(
                            frame,
                            "Are you sure you want to delete the selected employer?",
                            "Delete Confirmation",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirmation == JOptionPane.YES_OPTION) {
                        model.deleteRow(selectedRow);
                        JOptionPane.showMessageDialog(frame, "Employer deleted successfully.");
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
                        JOptionPane.showMessageDialog(frame, "Employer updated successfully.");
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

            frame.setSize(800, 600);
            frame.setVisible(true);
        });
    }
}
