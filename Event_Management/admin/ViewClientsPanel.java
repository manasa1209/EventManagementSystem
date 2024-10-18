package com.example.Event_Management.admin;// ViewClientsPanel.java
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewClientsPanel extends JFrame {
    public ViewClientsPanel() {
        super("View Clients");

        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(250, 230, 100));
        JLabel titleLabel = new JLabel("View Clients");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea clientsTextArea = new JTextArea();
        clientsTextArea.setEditable(false);
        panel.add(new JScrollPane(clientsTextArea), BorderLayout.CENTER);

        setContentPane(panel);

        fetchAndDisplayClients(clientsTextArea);
    }

    private void fetchAndDisplayClients(JTextArea clientsTextArea) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EventManage", "root", "1234");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT rc.name, rc.age, rc.gender, rc.contact, e.event_name " +
                    "FROM registered_clients rc " +
                    "INNER JOIN events e ON rc.event_id = e.event_id " +
                    "INNER JOIN admin_cred a ON rc.admin_id = a.admin_id " +
                    "INNER JOIN user_cred uc ON rc.client_id = uc.client_id");

            StringBuilder clientsInfo = new StringBuilder();
            while (rs.next()) {
                clientsInfo.append("Name: ").append(rs.getString("name")).append("\n");
                clientsInfo.append("Age: ").append(rs.getString("age")).append("\n");
                clientsInfo.append("Gender: ").append(rs.getString("gender")).append("\n");
                clientsInfo.append("Contact Number: ").append(rs.getString("contact")).append("\n");
                clientsInfo.append("Event: ").append(rs.getString("event_name")).append("\n");
                clientsInfo.append("-----------------------------------\n");
            }
            clientsTextArea.setText(clientsInfo.toString());

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
