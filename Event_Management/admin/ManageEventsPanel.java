// ManageEvents.java
package com.example.Event_Management.admin;

import com.example.Event_Management.EventManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Calendar;

public class ManageEventsPanel extends JFrame {
    private JTable eventsTable;
    private DefaultTableModel tableModel;

    public ManageEventsPanel() {
        super("Manage Events");

        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 230, 100));
        // Table to display events
        eventsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        JButton updateStatusButton = new JButton("Update Status");
        updateStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEventStatus();
            }
        });

        JButton modifyDetailsButton = new JButton("Modify Details");
        modifyDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyEventDetails();
            }
        });

        JButton deleteEventButton = new JButton("Delete Event");
        deleteEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEvent();
            }
        });

        JButton viewRegisteredClientsButton = new JButton("View Registered Clients");
        viewRegisteredClientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewRegisteredClients();
            }
        });

        buttonsPanel.add(updateStatusButton);
        buttonsPanel.add(modifyDetailsButton);
        buttonsPanel.add(deleteEventButton);
        buttonsPanel.add(viewRegisteredClientsButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        setContentPane(panel);

        // Populate events table
        populateEventsTable();
    }

    private void populateEventsTable() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventManage", "root", "1234");
            String query = "SELECT * FROM events WHERE admin_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, getCurrentAdminId());
            ResultSet resultSet = preparedStatement.executeQuery();

            // Create table model with column names
            tableModel = new DefaultTableModel();
            tableModel.addColumn("Event ID");
            tableModel.addColumn("Organizer ID");
            tableModel.addColumn("Status");
            tableModel.addColumn("Event Name");
            tableModel.addColumn("Event Date");
            tableModel.addColumn("Event Location");
            tableModel.addColumn("Description");

            // Add rows to the table model
            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getInt("event_id"),
                        resultSet.getInt("admin_id"),
                        resultSet.getString("status"),
                        resultSet.getString("event_name"),
                        resultSet.getDate("event_date"),
                        resultSet.getString("event_location"),
                        resultSet.getString("description")
                };
                tableModel.addRow(rowData);
            }

            eventsTable.setModel(tableModel);
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateEventStatus() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event.");
            return;
        }
        int eventId = (int) eventsTable.getValueAt(selectedRow, 0);
        String eventName = (String) eventsTable.getValueAt(selectedRow, 3);
        String currentStatus = (String) eventsTable.getValueAt(selectedRow, 2);
        String newStatus = currentStatus.equals("open") ? "closed" : "open";

        int choice = JOptionPane.showConfirmDialog(this, "Do you want to change the status of event: " + eventName + " (ID: " + eventId + ") to " + newStatus + "?", "Update Event Status", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventManage", "root", "1234");
                String query = "UPDATE events SET status = ? WHERE event_id = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, newStatus.toLowerCase()); // Convert status to lowercase
                preparedStatement.setInt(2, eventId);
                int rowsAffected = preparedStatement.executeUpdate();
                conn.close();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Status updated for event: " + eventName + " (ID: " + eventId + ") to " + newStatus);
                    populateEventsTable(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update event status.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while updating event status.");
            }
        }
    }




    private void modifyEventDetails() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event.");
            return;
        }
        int eventId = (int) eventsTable.getValueAt(selectedRow, 0);

        // Fetch the existing event details from the database
        String existingEventName = (String) eventsTable.getValueAt(selectedRow, 3);
        java.util.Date existingEventDateUtil = (java.util.Date) eventsTable.getValueAt(selectedRow, 4);
        String existingEventLocation = (String) eventsTable.getValueAt(selectedRow, 5);
        String existingDescription = (String) eventsTable.getValueAt(selectedRow, 6);

        // Prompt the user to enter the modified event details
        JTextField eventNameField = new JTextField(existingEventName);
        JSpinner eventDateSpinner = new JSpinner(new SpinnerDateModel(existingEventDateUtil, null, null, Calendar.HOUR_OF_DAY));
        JTextField eventLocationField = new JTextField(existingEventLocation);
        JTextArea descriptionArea = new JTextArea(existingDescription);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Event Name:"));
        panel.add(eventNameField);
        panel.add(new JLabel("Event Date:"));
        panel.add(eventDateSpinner);
        panel.add(new JLabel("Event Location:"));
        panel.add(eventLocationField);
        panel.add(new JLabel("Description:"));
        panel.add(scrollPane);

        int result = JOptionPane.showConfirmDialog(null, panel, "Modify Event Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String modifiedEventName = eventNameField.getText();
            java.util.Date modifiedEventDateUtil = (java.util.Date) eventDateSpinner.getValue();
            String modifiedEventLocation = eventLocationField.getText();
            String modifiedDescription = descriptionArea.getText();

            // Update only the modified event details in the database
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventManage", "root", "1234");
                String updateQuery = "UPDATE events SET event_name = ?, event_date = ?, event_location = ?, description = ? WHERE event_id = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
                preparedStatement.setString(1, modifiedEventName);
                preparedStatement.setObject(2, new java.sql.Date(modifiedEventDateUtil.getTime()));
                preparedStatement.setString(3, modifiedEventLocation);
                preparedStatement.setString(4, modifiedDescription);
                preparedStatement.setInt(5, eventId);
                int rowsAffected = preparedStatement.executeUpdate();
                conn.close();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Event details updated successfully!");
                    populateEventsTable(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update event details. Please try again.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while updating event details. Please try again.");
            }
        }
    }



    // Method to refresh the events table after modifications
    private void refreshEventsTable() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventManage", "root", "1234");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM events");

            DefaultTableModel model = (DefaultTableModel) eventsTable.getModel();
            model.setRowCount(0); // Clear the existing table data

            while (rs.next()) {
                // Retrieve data from the result set
                int eventId = rs.getInt("event_id");
                String eventName = rs.getString("event_name"); // Retrieve the event name as a String
                // Add the data to the table model
                model.addRow(new Object[]{eventId, eventName});
            }

            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while refreshing events table. Please try again.");
        }
    }


    private boolean isAdminAuthorized(int eventId) {
        int currentAdminId = getCurrentAdminId();
        int eventOrganizerId = getEventOrganizerId(eventId);
        return currentAdminId == eventOrganizerId;
    }

    private int getCurrentAdminId() {
        // Implement a method to retrieve the ID of the currently logged-in admin
        // For example, if you have a method in your EventManagement class that returns the current admin's ID:
        return EventManagement.getCurrentAdminId();
    }

    private int getEventOrganizerId(int eventId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventManage", "root", "1234");
            String query = "SELECT admin_id FROM events WHERE event_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, eventId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("admin_id");
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Return -1 if organizer ID is not found or an error occurs
    }

    private void deleteEvent() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event.");
            return;
        }
        int eventId = (int) eventsTable.getValueAt(selectedRow, 0);

        if (!isAdminAuthorized(eventId)) {
            JOptionPane.showMessageDialog(this, "You are not authorized to delete this event.");
            return;
        }

        String eventName = (String) eventsTable.getValueAt(selectedRow, 3);

        int choice = JOptionPane.showConfirmDialog(this, "Do you want to delete event: " + eventName + " (ID: " + eventId + ")?", "Delete Event", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventManage", "root", "1234");
                String query = "DELETE FROM events WHERE event_id = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setInt(1, eventId);
                int rowsAffected = preparedStatement.executeUpdate();
                conn.close();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Event deleted successfully: " + eventName + " (ID: " + eventId + ")");
                    populateEventsTable(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete event.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while deleting event.");
            }
        }
    }

    private void viewRegisteredClients() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event.");
            return;
        }
        int eventId = (int) eventsTable.getValueAt(selectedRow, 0);
        int organizerId = (int) eventsTable.getValueAt(selectedRow, 1); // Retrieve the organizer ID as an Integer
        String eventName = (String) eventsTable.getValueAt(selectedRow, 3);

        // Implement logic to view registered clients for the selected event
        // For example, you can query the database to retrieve registered clients for the event
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventManage", "root", "1234");
            String query = "SELECT * FROM registered_clients WHERE event_id = ? AND admin_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, eventId);
            preparedStatement.setInt(2, organizerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            StringBuilder message = new StringBuilder();
            message.append("Registered clients for event: ").append(eventName).append(" (ID: ").append(eventId).append(")\n\n");

            while (resultSet.next()) {
                String clientName = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                String contact = resultSet.getString("contact");
                message.append("Client Name: ").append(clientName).append("\n");
                message.append("Age: ").append(age).append("\n");
                message.append("Gender: ").append(gender).append("\n");
                message.append("Contact: ").append(contact).append("\n\n");
            }

            JOptionPane.showMessageDialog(this, message.toString());

            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while fetching registered clients.");
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ManageEventsPanel().setVisible(true);
            }
        });
    }
}


