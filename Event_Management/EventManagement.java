// EventManagement.java
package com.example.Event_Management;

import com.example.Event_Management.admin.AdminGUI;
import com.example.Event_Management.client.ClientGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

interface User {
    boolean login(String name, String password);
    boolean signup(String username, String password);
}

class UserSql {
    Connection conn;

    UserSql() {
        try {
            String url = "jdbc:mysql://localhost:3306/EventManage";
            String username = "root";
            String password = "1234";
            conn = DriverManager.getConnection(url, username, password);
            createTablesIfNotExist(); // Call method to create tables if they don't exist
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void createTablesIfNotExist() {
        try (Statement statement = conn.createStatement()) {
            String createUserTableSQL = "CREATE TABLE IF NOT EXISTS user_cred ("
                    + "client_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(50) NOT NULL UNIQUE,"
                    + "password VARCHAR(50) NOT NULL)";

            // Create admin_cred table
            String createAdminTableSQL = "CREATE TABLE IF NOT EXISTS admin_cred ("
                    + "admin_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(50) NOT NULL UNIQUE,"
                    + "password VARCHAR(50) NOT NULL)";

            // Create events table
            String createEventsTableSQL = "CREATE TABLE IF NOT EXISTS events ("
                    + "event_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "admin_id INT NOT NULL,"
                    + "status ENUM('open', 'closed') DEFAULT 'open',"
                    + "event_name VARCHAR(100) NOT NULL,"
                    + "event_date DATE NOT NULL,"
                    + "event_location VARCHAR(100) NOT NULL,"
                    + "description TEXT)";

            // Create registered_clients table
            String createRegisteredClientsTableSQL = "CREATE TABLE IF NOT EXISTS registered_clients ("
                    + "event_id INT NOT NULL,"
                    + "admin_id INT NOT NULL,"
                    + "client_id INT NOT NULL,"
                    + "name VARCHAR(100) NOT NULL,"
                    + "age INT,"
                    + "gender ENUM('Male', 'Female', 'Other'),"
                    + "contact VARCHAR(20),"
                    + "registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "PRIMARY KEY (event_id, admin_id, client_id),"
                    + "FOREIGN KEY (event_id) REFERENCES events(event_id),"
                    + "FOREIGN KEY (admin_id) REFERENCES admin_cred(admin_id),"
                    + "FOREIGN KEY (client_id) REFERENCES user_cred(client_id))";


            statement.executeUpdate(createUserTableSQL);
            statement.executeUpdate(createAdminTableSQL);
            statement.executeUpdate(createEventsTableSQL);
            statement.executeUpdate(createRegisteredClientsTableSQL);

            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}


class Admin extends UserSql implements User {
    public int getAdminIdByUsername(String username) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT admin_id FROM admin_cred WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("admin_id");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return -1; // Return -1 if username not found or error occurred
    }
    @Override
    public boolean login(String username, String password) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM admin_cred WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // If there's at least one row, credentials are valid
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }
    @Override
    public boolean signup(String username, String password) {
        try {
            PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM admin_cred WHERE username = ?");
            checkStmt.setString(1, username);
            ResultSet checkRs = checkStmt.executeQuery();
            if (checkRs.next()) {
                return false;
            }

            PreparedStatement lastIdStmt = conn.prepareStatement("SELECT admin_id FROM admin_cred ORDER BY admin_id DESC LIMIT 1");
            ResultSet rs = lastIdStmt.executeQuery();
            int adminId = 1000; // Default admin ID if no data exists in the table
            if (rs.next()) {
                adminId = rs.getInt("admin_id");
            }
            adminId++; // Increment the admin ID for the new user

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO admin_cred (admin_id, username, password) VALUES (?, ?, ?)");
            stmt.setInt(1, adminId);
            stmt.setString(2, username);
            stmt.setString(3, password);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }
}

class Client extends UserSql implements User {
    public int getClientIdByUsername(String username) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT client_id FROM user_cred WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("client_id");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return -1; // Return -1 if username not found or error occurred
    }
    @Override
    public boolean login(String username, String password) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_cred WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    @Override
    public boolean signup(String username, String password) {
        try {
            PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM user_cred WHERE username = ?");
            checkStmt.setString(1, username);
            ResultSet checkRs = checkStmt.executeQuery();
            if (checkRs.next()) {
                return false;
            }

            PreparedStatement lastIdStmt = conn.prepareStatement("SELECT client_id FROM user_cred ORDER BY client_id DESC LIMIT 1");
            ResultSet rs = lastIdStmt.executeQuery();
            int clientId = 1000; // Default client ID if no data exists in the table
            if (rs.next()) {
                clientId = rs.getInt("client_id");
            }
            clientId++; // Increment the client ID for the new user

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO user_cred (client_id, username, password) VALUES (?, ?, ?)");
            stmt.setInt(1, clientId);
            stmt.setString(2, username);
            stmt.setString(3, password);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }
}

public class EventManagement extends JFrame {
    private JLabel username;
    private JLabel pass;
    private JLabel hed;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JRadioButton adminRadioButton;
    private JRadioButton clientRadioButton;

    private Admin admin;
    private Client client;

    private void openAdminGUI() {
        AdminGUI adminGUI = new AdminGUI();
        adminGUI.setVisible(true);
        dispose();
    }

    private void openClientGUI() {
        ClientGUI clientGUI = new ClientGUI();
        clientGUI.setVisible(true);
        dispose();
    }

    private static int clientId = -1;
    private static int adminId = -1;

    public static int getCurrentClientId() {
        return clientId;
    }
    public static int getCurrentAdminId() {
        return adminId;
    }
    public EventManagement() {
        super("Event Management System");
        // Define the font for the title



        admin = new Admin();
        client = new Client();

        JPanel panel = new JPanel(new GridBagLayout());

        JLabel titleLabel = new JLabel("Event Management System");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBackground(new Color(250, 150, 150));
        Font titleFont = new Font("Arial", Font.BOLD, 24); // Create a new font instance
        titleLabel.setFont(titleFont); // Set the font for the title label
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        ButtonGroup group = new ButtonGroup();
        gbc.gridy++;
        gbc.gridy++;
        gbc.gridy++;
        adminRadioButton = new JRadioButton("Admin");
        clientRadioButton = new JRadioButton("Client");
        clientRadioButton.setBackground(new Color(250, 150, 150));
        adminRadioButton.setBackground(new Color(250, 150, 150));
        adminRadioButton.setHorizontalAlignment(JTextField.CENTER);
        clientRadioButton.setHorizontalAlignment(JTextField.CENTER);
        group.add(adminRadioButton);
        group.add(clientRadioButton);
        hed = new JLabel("Join as:");
        Font font = new Font("Arial", Font.ITALIC,20); // 16 is the font size
        hed.setFont(font);
        panel.add(hed, gbc);

        gbc.gridy++;
        panel.add(adminRadioButton, gbc);

        gbc.gridy++;
        panel.add(clientRadioButton, gbc);

        gbc.gridy++;
        username = new JLabel("Username: ");
        username.setHorizontalAlignment(JTextField.CENTER);
        panel.add(username, gbc);

        gbc.gridy++;
        usernameField = new JTextField(20);
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        panel.add(usernameField, gbc);

        gbc.gridy++;
        pass = new JLabel("Password: ");
        pass.setHorizontalAlignment(JTextField.CENTER);
        panel.add(pass, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField(20);
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        panel.add(passwordField, gbc);

//        gbc.gridx++;
        gbc.gridy++;
        loginButton = new JButton("Login");
//        panel.add(loginButton);


//        gbc.gridx++;
        gbc.gridy++;
        signupButton = new JButton("Signup");
        panel.add(loginButton, gbc);
        gbc.gridx++;
        panel.add(signupButton, gbc);
//        panel.add(signupButton);


        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Perform database operation in a background thread
                new Thread(() -> {
                    System.out.println(Thread.currentThread().getName());
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());

                    if (adminRadioButton.isSelected()) {
                        if (admin.login(username, password)) {
                            adminId = admin.getAdminIdByUsername(username); // Retrieve admin ID
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(EventManagement.this, "Admin login successful! Admin ID: " + adminId);
                                openAdminGUI();
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(EventManagement.this, "Admin login failed. Please try again.");
                            });
                        }
                    } else if (clientRadioButton.isSelected()) {
                        if (client.login(username, password)) {
                            clientId = client.getClientIdByUsername(username); // Retrieve client ID
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(EventManagement.this, "Client login successful! Client ID: " + clientId);
                                openClientGUI();
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(EventManagement.this, "Client login failed. Please try again.");
                            });
                        }
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(EventManagement.this, "Please select an option (Admin/Client).");
                        });
                    }
                }).start();
            }
        });

        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Perform signup operation in a background thread
                new Thread(() -> {
                    System.out.println(Thread.currentThread().getName());
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());

                    if (adminRadioButton.isSelected()) {
                        if (admin.signup(username, password)) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(EventManagement.this, "Admin signup successful!");
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(EventManagement.this, "Username already exists. Admin signup failed.");
                            });
                        }
                    } else if (clientRadioButton.isSelected()) {
                        if (client.signup(username, password)) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(EventManagement.this, "Client signup successful! Please log in.");
                                // Prompt the user to log in again after successful signup
                                usernameField.setText("");
                                passwordField.setText("");
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(EventManagement.this, "Username already exists. Client signup failed.");
                            });
                        }
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(EventManagement.this, "Please select an option (Admin/Client).");
                        });
                    }
                }).start();
            }
        });




        add(panel);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EventManagement();
            }
        });
    }
}

