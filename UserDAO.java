import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Register a new user in the database
     * @param username the username
     * @param password the password
     * @param name the full name
     * @param accountType the account type (Savings or Current)
     * @param accountNumber the generated account number
     * @return the account number if successful, null if failed
     */
    public static String registerUser(String username, String password, String name, 
                                     String accountType, String accountNumber) {
        String query = "INSERT INTO users (username, password, name, account_type, account_number, balance) "
                     + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Set initial balance based on account type
            double initialBalance = "Savings".equals(accountType) ? 500.0 : 0.0;
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, accountType);
            pstmt.setString(5, accountNumber);
            pstmt.setDouble(6, initialBalance);
            
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0 ? accountNumber : null;
            
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return null;
        }
    }

    /**
     * Authenticate user by username and password
     * @param username the username
     * @param password the password
     * @return User object if credentials are valid, null otherwise
     */
    public static User loginUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Create appropriate account object based on type
                Account account = createAccount(rs);
                User user = new User(username, password, account);
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error logging in user: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get user by account number
     * @param accountNumber the account number
     * @return User object if found, null otherwise
     */
    public static User getUserByAccountNumber(String accountNumber) {
        String query = "SELECT * FROM users WHERE account_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Account account = createAccount(rs);
                User user = new User(rs.getString("username"), rs.getString("password"), account);
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching user by account number: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Find user by username
     * @param username the username to search for
     * @return User object if found, null otherwise
     */
    public static User getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Account account = createAccount(rs);
                User user = new User(username, rs.getString("password"), account);
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching user by username: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Update user balance in database
     * @param accountNumber the account number
     * @param newBalance the new balance
     * @return true if update successful, false otherwise
     */
    public static boolean updateBalance(String accountNumber, double newBalance) {
        String query = "UPDATE users SET balance = ? WHERE account_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);
            
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if username already exists
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    public static boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Get latest account number for generating next one
     * @return the latest account number as integer
     */
    public static int getLatestAccountNumber() {
        String query = "SELECT MAX(CAST(account_number AS UNSIGNED)) FROM users";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int result = rs.getInt(1);
                return result > 0 ? result : 1000;  // Start from 1000 if no users exist
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting latest account number: " + e.getMessage());
        }
        
        return 1000;
    }

    /**
     * Helper method to create Account object from ResultSet
     * @param rs the ResultSet containing user data
     * @return Account object (SavingsAccount or CurrentAccount)
     */
    private static Account createAccount(ResultSet rs) throws SQLException {
        String accountNumber = rs.getString("account_number");
        String name = rs.getString("name");
        double balance = rs.getDouble("balance");
        String accountType = rs.getString("account_type");
        
        if ("Savings".equals(accountType)) {
            return new SavingsAccount(accountNumber, name, balance);
        } else {
            return new CurrentAccount(accountNumber, name, balance);
        }
    }
}
