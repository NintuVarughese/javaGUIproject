import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EventManagementSystemGUI2 extends JFrame implements ActionListener {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/event?characterEncoding=utf8&useSSL=false&useUnicode=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private JTextField usernameField, passwordField, eventNameField, eventLocationField, ticketsField;
    private JButton registerButton, loginButton, bookEventButton;
    private Connection connection;
    private String loggedInUser; // Store the username of the logged-in user

    public EventManagementSystemGUI2() {
                setTitle("Event Management System");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        JPanel eventPanel = new JPanel(new GridLayout(4, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel eventNameLabel = new JLabel("Event Name:");
        eventNameField = new JTextField();
        JLabel eventLocationLabel = new JLabel("Event Location:");
        eventLocationField = new JTextField();
        JLabel ticketsLabel = new JLabel("Number of Tickets:");
        ticketsField = new JTextField();

        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        bookEventButton = new JButton("Book Event");
        bookEventButton.addActionListener(this);
        bookEventButton.setEnabled(false);

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(registerButton);
        loginPanel.add(loginButton);

        eventPanel.add(eventNameLabel);
        eventPanel.add(eventNameField);
        eventPanel.add(eventLocationLabel);
        eventPanel.add(eventLocationField);
        eventPanel.add(ticketsLabel);
        eventPanel.add(ticketsField);
        eventPanel.add(bookEventButton);

        add(loginPanel, BorderLayout.NORTH);
        add(eventPanel, BorderLayout.CENTER);


        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Establish the database connection
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Connected to the database.");

            // Create tables if they do not exist
            createTablesIfNotExist();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createTablesIfNotExist() throws SQLException {
        String createUserTableQuery = "CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) UNIQUE, password VARCHAR(50))";
        String createEventTableQuery = "CREATE TABLE IF NOT EXISTS events (id INT AUTO_INCREMENT PRIMARY KEY, eventName VARCHAR(100), eventLocation VARCHAR(100), eventCapacity INT, eventBooked INT)";

        Statement statement = connection.createStatement();
        statement.executeUpdate(createUserTableQuery);
        statement.executeUpdate(createEventTableQuery);
        System.out.println("Tables created (if not exist) successfully.");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            registerUser();
        } else if (e.getSource() == loginButton) {
            loginUser();
        } else if (e.getSource() == bookEventButton) {
            bookEvent();
        }
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            registerUserInDatabase(username, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loginUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            boolean loginStatus = loginUserInDatabase(username, password);
            if (loginStatus) {
                loggedInUser = username; // Store the username of the logged-in user
                bookEventButton.setEnabled(true);
                registerButton.setVisible(false); // Hide the Register button after login
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void bookEvent() {
        String eventName = eventNameField.getText();
        String eventLocation = eventLocationField.getText();
        int tickets = Integer.parseInt(ticketsField.getText());
        try {
            bookEventInDatabase(eventName, eventLocation, tickets);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void registerUserInDatabase(String username, String password) throws SQLException {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.executeUpdate();
        JOptionPane.showMessageDialog(this, "User registered successfully.");
    }

    private boolean loginUserInDatabase(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    private void bookEventInDatabase(String eventName, String eventLocation, int tickets) throws SQLException {
        String query = "INSERT INTO events (eventName, eventLocation, eventCapacity, eventBooked) " +
                "VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, eventName);
        preparedStatement.setString(2, eventLocation);
        preparedStatement.setInt(3, tickets);
        preparedStatement.setInt(4, 0); // Initially no tickets are booked
        preparedStatement.executeUpdate();
        JOptionPane.showMessageDialog(this, "Event booked successfully.");
    }

    public static void main(String[] args) {
        EventManagementSystemGUI app = new EventManagementSystemGUI();
        app.setVisible(true);
    }
}
