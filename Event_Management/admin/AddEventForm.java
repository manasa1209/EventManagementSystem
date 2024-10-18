package com.example.Event_Management.admin;

import com.example.Event_Management.EventManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

public class AddEventForm extends JFrame {
    private JTextField eventNameField;
    private JSpinner eventDateSpinner; // Added JSpinner for event date selection
    private JTextField eventLocationField;
    private JTextField descriptionField;

    public AddEventForm() {
        super("Add Event Form");

        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.setBackground(new Color(250, 230, 100));
        JLabel eventNameLabel = new JLabel("Event Name:");
        eventNameField = new JTextField();
        JLabel eventDateLabel = new JLabel("Event Date(yyyy-mm-dd):");
        SpinnerDateModel spinnerDateModel = new SpinnerDateModel();
        spinnerDateModel.setCalendarField(Calendar.DAY_OF_MONTH);
        eventDateSpinner = new JSpinner(spinnerDateModel); // Initialize the eventDateSpinner

        JLabel eventLocationLabel = new JLabel("Event Location:");
        eventLocationField = new JTextField();
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionField = new JTextField();

        panel.add(eventNameLabel);
        panel.add(eventNameField);
        panel.add(eventDateLabel);
        panel.add(eventDateSpinner);
        panel.add(eventLocationLabel);
        panel.add(eventLocationField);
        panel.add(descriptionLabel);
        panel.add(descriptionField);

        JButton addButton = new JButton("Add Event");
        panel.add(addButton);

        setContentPane(panel);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eventName = eventNameField.getText();
                java.util.Date eventDate = (java.util.Date) eventDateSpinner.getValue();
                String eventLocation = eventLocationField.getText();
                String description = descriptionField.getText();

                if (eventName.isEmpty() || eventLocation.isEmpty()) {
                    JOptionPane.showMessageDialog(AddEventForm.this, "Please fill in all required fields.");
                    return;
                }

                // Save event information to database
                boolean success = saveEventInfo(eventName, eventDate, eventLocation, description);
                if (success) {
                    JOptionPane.showMessageDialog(AddEventForm.this, "Event added successfully!");
                    // Close add event form after successful addition
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(AddEventForm.this, "Failed to add event. Please try again.");
                }
            }
        });
    }

    private boolean saveEventInfo(String eventName, java.util.Date eventDate, String eventLocation, String description) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventManage", "root", "1234");
            String query = "INSERT INTO events (admin_id, event_name, event_date, event_location, description) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, EventManagement.getCurrentAdminId()); // Assuming this method returns the admin_id of the currently logged-in admin
            preparedStatement.setString(2, eventName);
            preparedStatement.setDate(3, new java.sql.Date(eventDate.getTime())); // Use setDate method
            preparedStatement.setString(4, eventLocation);
            preparedStatement.setString(5, description);
            int rowsAffected = preparedStatement.executeUpdate();
            conn.close();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AddEventForm().setVisible(true);
            }
        });
    }
}
