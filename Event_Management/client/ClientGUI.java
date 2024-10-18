package com.example.Event_Management.client;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class ClientGUI extends JFrame {
    private final JComboBox<String> eventComboBox;
    private boolean comboBoxVisible = false;

    public ClientGUI() {
        super("Client Panel");

        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(180, 200, 230));
        JLabel titleLabel = new JLabel("Client Panel");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        Font titleFont = new Font("Arial", Font.BOLD, 24); // Create a new font instance
        titleLabel.setFont(titleFont); // Set the font for the title label
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Adjust the height of the space as needed
        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Adjust the height of the space as needed


        JButton viewEventsButton = new JButton("View Events");
        viewEventsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(viewEventsButton);

        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Adjust the height of the space as needed


        JButton registerButton = new JButton("Register for Event");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(registerButton);

        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Adjust the height of the space as needed
        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Adjust the height of the space as needed


        eventComboBox = new JComboBox<>();
        eventComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        eventComboBox.setVisible(false); // Initially hide the combo box
        Dimension comboBoxSize = new Dimension(200, 30); // Adjust the width and height as needed
        eventComboBox.setPreferredSize(comboBoxSize);
        // eventComboBox.setMaximumSize(comboBoxSize); // Optionally set maximum size
        panel.add(eventComboBox);

        setContentPane(panel);

        viewEventsButton.addActionListener(e -> {
            toggleComboBoxVisibility(); // Call toggleComboBoxVisibility() method
        });

        registerButton.addActionListener(e -> {
            openRegistrationForm();
        });
    }


    private void toggleComboBoxVisibility() {
        comboBoxVisible = !comboBoxVisible; // Toggle the visibility flag
        eventComboBox.setVisible(comboBoxVisible); // Set the visibility of the combo box
        displayEvents();
    }

    private void displayEvents() {
        ArrayList<String> events = getEventsFromDatabase();
        eventComboBox.removeAllItems();
        for (String event : events) {
            eventComboBox.addItem(event);
        }
    }

    private ArrayList<String> getEventsFromDatabase() {
        ArrayList<String> events = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventManage", "root", "1234");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT event_name FROM events");
            while (rs.next()) {
                events.add(rs.getString("event_name"));
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    private void openRegistrationForm() {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setVisible(true);
    }
}