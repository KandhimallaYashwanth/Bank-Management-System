import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    /**
     * Add a new transaction to the database
     * @param accountNumber the account number
     * @param type the transaction type (Deposit, Withdraw, Transfer)
     * @param amount the transaction amount
     * @param description the transaction description
     * @return true if insert successful, false otherwise
     */
    public static boolean addTransaction(String accountNumber, String type, double amount, String description) {
        String query = "INSERT INTO transactions (account_number, type, amount, description, transaction_date) "
                     + "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, description);
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all transactions for a specific account
     * @param accountNumber the account number
     * @return List of Transaction objects
     */
    public static List<Transaction> getTransactionsByAccount(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                LocalDateTime dateTime = rs.getTimestamp("transaction_date").toLocalDateTime();
                String description = rs.getString("description");
                
                Transaction transaction = new Transaction(type, amount, dateTime, description);
                transactions.add(transaction);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching transactions: " + e.getMessage());
        }
        
        return transactions;
    }

    /**
     * Get total number of transactions for an account
     * @param accountNumber the account number
     * @return the count of transactions
     */
    public static int getTransactionCount(String accountNumber) {
        String query = "SELECT COUNT(*) FROM transactions WHERE account_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting transaction count: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Delete all transactions for an account (use with caution)
     * @param accountNumber the account number
     * @return true if successful, false otherwise
     */
    public static boolean deleteTransactionsByAccount(String accountNumber) {
        String query = "DELETE FROM transactions WHERE account_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error deleting transactions: " + e.getMessage());
            return false;
        }
    }
}
