// RegistrationForm.java
package com.example.Event_Management.client;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import com.example.Event_Management.EventManagement;
public class RegistrationForm extends JFrame {
    private JTextField nameField;
    private JTextField ageField;
    private JTextField genderField;
    private JTextField contactField;
    private JComboBox<String> eventComboBox;

    public RegistrationForm() {
        super("Registration Form");

        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.setBackground(new Color(180, 200, 230));
        JLabel titleLabel = new JLabel("Registration Form");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Name: "), gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Age: "), gbc);

        ageField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(ageField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Gender: "), gbc);

        genderField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(genderField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Contact: "), gbc);

        contactField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(contactField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Select Event: "), gbc);

        eventComboBox = new JComboBox<>();
        gbc.gridx = 1;
        panel.add(eventComboBox, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton registerButton = new JButton("Register");
        panel.add(registerButton, gbc);

        setContentPane(panel);

        fetchEvents();

        registerButton.addActionListener(e -> {
            register();
        });
    }

    private void fetchEvents() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventManage", "root", "1234");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT event_name FROM events");
            while (rs.next()) {
                eventComboBox.addItem(rs.getString("event_name"));
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void register() {
        String name = nameField.getText();
        String age = ageField.getText();
        String gender = genderField.getText();
        String contact = contactField.getText();
        String selectedEvent = (String) eventComboBox.getSelectedItem();
        int clientId = EventManagement.getCurrentClientId(); // Assuming this method returns the client_id of the currently logged-in client

        if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || contact.isEmpty() || selectedEvent == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventManage", "root", "1234");

            // Query to retrieve event_id and admin_id based on selectedEvent
            String eventQuery = "SELECT event_id, admin_id FROM events WHERE event_name = ?";
            PreparedStatement eventStmt = conn.prepareStatement(eventQuery);
            eventStmt.setString(1, selectedEvent);
            ResultSet eventResult = eventStmt.executeQuery();

            int eventId = -1; // Default value if event is not found
            int organizerId = -1; // Default value if organizer is not found

            // Retrieve event_id and admin_id
            if (eventResult.next()) {
                eventId = eventResult.getInt("event_id");
                organizerId = eventResult.getInt("admin_id");
            } else {
                JOptionPane.showMessageDialog(this, "Selected event not found.");
                conn.close();
                return;
            }

            // Insert into registered_clients table
            String query = "INSERT INTO registered_clients (event_id, admin_id, client_id, name, age, gender, contact, registration_date) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, eventId);
            preparedStatement.setInt(2, organizerId);
            preparedStatement.setInt(3, clientId); // Set the client_id
            preparedStatement.setString(4, name);
            preparedStatement.setString(5, age);
            preparedStatement.setString(6, gender);
            preparedStatement.setString(7, contact);

            int rowsAffected = preparedStatement.executeUpdate();

            conn.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred. Please try again.");
        }
    }

}
