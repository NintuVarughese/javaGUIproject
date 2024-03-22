import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class GymManagementSystemGUI extends JFrame implements ActionListener {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/gym?characterEncoding=utf8&useSSL=false&useUnicode=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private JTextField usernameField, passwordField, nameField, ageField, weightField, heightField, placeField;
    private JButton registerButton, loginButton, bookGymButton;
    private Connection connection;
    private String loggedInUser; // Store the username of the logged-in user

    public GymManagementSystemGUI() {
        setTitle("Gym Management System");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        JPanel gymPanel = new JPanel(new GridLayout(6, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        JLabel ageLabel = new JLabel("Age:");
        ageField = new JTextField();
        JLabel weightLabel = new JLabel("Weight:");
        weightField = new JTextField();
        JLabel heightLabel = new JLabel("Height:");
        heightField = new JTextField();
        JLabel placeLabel = new JLabel("Place:");
        placeField = new JTextField();

        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        bookGymButton = new JButton("Book Gym");
        bookGymButton.addActionListener(this);
        bookGymButton.setEnabled(false);

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(registerButton);
        loginPanel.add(loginButton);

        gymPanel.add(nameLabel);
        gymPanel.add(nameField);
        gymPanel.add(ageLabel);
        gymPanel.add(ageField);
        gymPanel.add(weightLabel);
        gymPanel.add(weightField);
        gymPanel.add(heightLabel);
        gymPanel.add(heightField);
        gymPanel.add(placeLabel);
        gymPanel.add(placeField);
        gymPanel.add(bookGymButton);

        add(loginPanel, BorderLayout.NORTH);
        add(gymPanel, BorderLayout.CENTER);


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
        String createDetailsTableQuery = "CREATE TABLE IF NOT EXISTS user_details (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) UNIQUE, name VARCHAR(100), age INT, weight DOUBLE, height DOUBLE, place VARCHAR(100))";
        Statement statement = connection.createStatement();
        statement.executeUpdate(createUserTableQuery);
        statement.executeUpdate(createDetailsTableQuery);
        System.out.println("Tables created (if not exist) successfully.");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            registerUser();
        } else if (e.getSource() == loginButton) {
            loginUser();
        } else if (e.getSource() == bookGymButton) {
            bookGym();
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
                bookGymButton.setEnabled(true);
                registerButton.setVisible(false); // Hide the Register button after login
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void bookGym() {
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        double weight = Double.parseDouble(weightField.getText());
        double height = Double.parseDouble(heightField.getText());
        String place = placeField.getText();
        try {
            bookGymInDatabase(loggedInUser, name, age, weight, height, place);
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

    private void bookGymInDatabase(String username, String name, int age, double weight, double height, String place) throws SQLException {
        String query = "INSERT INTO user_details (username, name, age, weight, height, place) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, name);
        preparedStatement.setInt(3, age);
        preparedStatement.setDouble(4, weight);
        preparedStatement.setDouble(5, height);
        preparedStatement.setString(6, place);
        preparedStatement.executeUpdate();
        JOptionPane.showMessageDialog(this, "Gym booked successfully.");
    }

    public static void main(String[] args) {
        GymManagementSystemGUI app = new GymManagementSystemGUI();
        app.setVisible(true);
    }
}
