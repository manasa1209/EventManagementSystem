// AdminGUI.java
package com.example.Event_Management.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminGUI extends JFrame {
    public AdminGUI() {
        super("Admin Panel");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


        panel.setBackground(new Color(250, 230, 100)); // Custom color with RGB values (red=100, green=200, blue=50)


        JLabel titleLabel = new JLabel("Admin Panel");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        Font titleFont = new Font("Arial", Font.BOLD, 24); // Create a new font instance
        titleLabel.setFont(titleFont); // Set the font for the title label
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);


        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Adjust the height of the space as needed
        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Adjust the height of the space as needed
        JButton addEventButton = new JButton("Add Event");
        addEventButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(addEventButton);

        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Adjust the height of the space as needed

        JButton manageEventsButton = new JButton("Manage Events");
        manageEventsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(manageEventsButton);

        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Adjust the height of the space as needed

        JButton viewClientsButton = new JButton("View Clients");
        viewClientsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(viewClientsButton);

        setContentPane(panel);

        // Add action listeners for the buttons
        addEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddEventForm();
            }
        });

        manageEventsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openManageEventsPanel();
            }
        });

        viewClientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openViewClientsPanel();
            }
        });
    }

    private void openAddEventForm() {
        AddEventForm addEventForm = new AddEventForm();
        addEventForm.setVisible(true);
    }

    private void openManageEventsPanel() {
        ManageEventsPanel manageEventsPanel = new ManageEventsPanel();
        manageEventsPanel.setVisible(true);
    }

    private void openViewClientsPanel() {
        ViewClientsPanel viewClientsPanel = new ViewClientsPanel();
        viewClientsPanel.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AdminGUI().setVisible(true);
            }
        });
    }
}
