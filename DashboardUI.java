import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardUI extends JFrame {
    private final User currentUser;
    private final LoginUI loginUI;
    private final JLabel lblWelcome;
    private final JLabel lblAccountNumber;
    private final JLabel lblBalance;
    private final JTextArea outputArea;

    public DashboardUI(User currentUser, LoginUI loginUI) {
        this.currentUser = currentUser;
        this.loginUI = loginUI;

        setTitle("Dashboard - Bank Management System");
        setSize(860, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        Color backgroundColor = new Color(244, 246, 247);
        Color primaryColor = new Color(44, 62, 80);
        Color cardBorderColor = new Color(224, 224, 224);
        Color accentBalance = new Color(26, 188, 156);

        JPanel rootPanel = new JPanel(new BorderLayout(0, 15));
        rootPanel.setBackground(backgroundColor);
        rootPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 6, 6));
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cardBorderColor, 1),
                new EmptyBorder(24, 24, 24, 24)
        ));

        lblWelcome = new JLabel();
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));

        lblAccountNumber = new JLabel();
        lblAccountNumber.setForeground(Color.WHITE);
        lblAccountNumber.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        lblBalance = new JLabel();
        lblBalance.setForeground(accentBalance);
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 18));

        headerPanel.add(lblWelcome);
        headerPanel.add(lblAccountNumber);
        headerPanel.add(lblBalance);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(backgroundColor);

        JPanel outputCard = new JPanel(new BorderLayout(0, 10));
        outputCard.setBackground(Color.WHITE);
        outputCard.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        outputCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cardBorderColor, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel outputTitle = new JLabel("Activity / Transactions");
        outputTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        outputTitle.setForeground(primaryColor);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        outputArea.setBackground(new Color(250, 250, 250));
        outputArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(100, 260));
        scrollPane.setBorder(BorderFactory.createLineBorder(cardBorderColor, 1));

        outputCard.add(outputTitle, BorderLayout.NORTH);
        outputCard.add(scrollPane, BorderLayout.CENTER);

        JButton btnDeposit = new JButton("Deposit");
        JButton btnWithdraw = new JButton("Withdraw");
        JButton btnTransfer = new JButton("Transfer");
        JButton btnDetails = new JButton("View Account Details");
        JButton btnHistory = new JButton("View Transactions");
        JButton btnLogout = new JButton("Logout");

        styleButton(btnDeposit, new Color(41, 128, 185));
        styleButton(btnWithdraw, new Color(230, 126, 34));
        styleButton(btnTransfer, new Color(142, 68, 173));
        styleButton(btnDetails, new Color(22, 160, 133));
        styleButton(btnHistory, new Color(52, 73, 94));
        styleButton(btnLogout, new Color(231, 76, 60));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 15, 15));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        buttonPanel.add(btnDeposit);
        buttonPanel.add(btnWithdraw);
        buttonPanel.add(btnTransfer);
        buttonPanel.add(btnDetails);
        buttonPanel.add(btnHistory);
        buttonPanel.add(btnLogout);

        centerPanel.add(outputCard);

        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(centerPanel, BorderLayout.CENTER);
        rootPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(rootPanel);

        revalidate();
        repaint();

        refreshHeader();

        btnDeposit.addActionListener(e -> deposit());
        btnWithdraw.addActionListener(e -> withdraw());
        btnTransfer.addActionListener(e -> transfer());
        btnDetails.addActionListener(e -> viewDetails());
        btnHistory.addActionListener(e -> showTransactionHistory());
        btnLogout.addActionListener(e -> logout());
    }

    private void refreshHeader() {
        // Fetch latest balance from database
        String accountNumber = currentUser.getAccount().getAccountNumber();
        User updatedUser = UserDAO.getUserByAccountNumber(accountNumber);
        
        if (updatedUser != null) {
            // Update the current user's account balance
            currentUser.getAccount().setBalance(updatedUser.getAccount().getBalance());
        }
        
        lblWelcome.setText("Welcome, " + currentUser.getUsername());
        lblAccountNumber.setText("Account Number: " + currentUser.getAccount().getAccountNumber());
        lblBalance.setText(String.format("Balance: %.2f", currentUser.getAccount().getBalance()));
    }

    private void deposit() {
        String amountText = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
        if (amountText == null) {
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (NumberFormatException ex) {
            showMessage("Invalid amount.");
            return;
        }

        if (amount <= 0) {
            showMessage("Invalid deposit amount.");
            return;
        }

        String accountNumber = currentUser.getAccount().getAccountNumber();
        double currentBalance = currentUser.getAccount().getBalance();
        double newBalance = currentBalance + amount;

        // Update balance in database
        if (UserDAO.updateBalance(accountNumber, newBalance)) {
            // Add transaction record
            TransactionDAO.addTransaction(accountNumber, "Deposit", amount, "Amount deposited");
            currentUser.getAccount().setBalance(newBalance);
            showMessage(String.format("Deposit successful. New Balance: %.2f", newBalance));
            refreshHeader();
        } else {
            showMessage("Deposit failed. Please try again.");
        }
    }

    private void withdraw() {
        String amountText = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
        if (amountText == null) {
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (NumberFormatException ex) {
            showMessage("Invalid amount.");
            return;
        }

        String accountNumber = currentUser.getAccount().getAccountNumber();
        double currentBalance = currentUser.getAccount().getBalance();

        if (amount <= 0) {
            showMessage("Invalid withdrawal amount.");
            return;
        }

        if (amount > currentBalance) {
            showMessage("Insufficient balance.");
            return;
        }

        double newBalance = currentBalance - amount;

        // Update balance in database
        if (UserDAO.updateBalance(accountNumber, newBalance)) {
            // Add transaction record
            TransactionDAO.addTransaction(accountNumber, "Withdraw", amount, "Amount withdrawn");
            currentUser.getAccount().setBalance(newBalance);
            showMessage(String.format("Withdrawal successful. New Balance: %.2f", newBalance));
            refreshHeader();
        } else {
            showMessage("Withdrawal failed. Please try again.");
        }
    }

    private void transfer() {
        String targetUsername = JOptionPane.showInputDialog(this, "Enter target username:");
        if (targetUsername == null || targetUsername.trim().isEmpty()) {
            return;
        }

        String amountText = JOptionPane.showInputDialog(this, "Enter transfer amount:");
        if (amountText == null) {
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (NumberFormatException ex) {
            showMessage("Invalid amount.");
            return;
        }

        if (amount <= 0) {
            showMessage("Invalid transfer amount.");
            return;
        }

        if (targetUsername.equalsIgnoreCase(currentUser.getUsername())) {
            showMessage("Cannot transfer to the same account.");
            return;
        }

        String senderAccountNumber = currentUser.getAccount().getAccountNumber();
        double senderBalance = currentUser.getAccount().getBalance();

        if (amount > senderBalance) {
            showMessage("Insufficient balance for transfer.");
            return;
        }

        // Find target user by username
        // Since we're transferring by username, we need to query - for now, show message that we need account number
        User targetUser = findUserByUsername(targetUsername.trim());
        if (targetUser == null) {
            showMessage("Target user not found.");
            return;
        }

        String receiverAccountNumber = targetUser.getAccount().getAccountNumber();
        double receiverBalance = targetUser.getAccount().getBalance();

        // Deduct from sender
        double newSenderBalance = senderBalance - amount;
        if (!UserDAO.updateBalance(senderAccountNumber, newSenderBalance)) {
            showMessage("Transfer failed. Please try again.");
            return;
        }

        // Add to receiver
        double newReceiverBalance = receiverBalance + amount;
        if (!UserDAO.updateBalance(receiverAccountNumber, newReceiverBalance)) {
            // Rollback sender's balance if receiver update fails
            UserDAO.updateBalance(senderAccountNumber, senderBalance);
            showMessage("Transfer failed. Please try again.");
            return;
        }

        // Add transaction records
        TransactionDAO.addTransaction(senderAccountNumber, "Transfer", amount,
                "Transfer Sent to " + targetUsername);
        TransactionDAO.addTransaction(receiverAccountNumber, "Transfer", amount,
                "Transfer Received from " + currentUser.getUsername());

        currentUser.getAccount().setBalance(newSenderBalance);
        showMessage(String.format("Transfer successful to %s.\nAmount: %.2f\nNew Balance: %.2f",
                targetUsername, amount, newSenderBalance));
        refreshHeader();
    }

    private void viewDetails() {
        // Fetch latest data from database
        String accountNumber = currentUser.getAccount().getAccountNumber();
        User updatedUser = UserDAO.getUserByAccountNumber(accountNumber);

        if (updatedUser == null) {
            showMessage("Error fetching account details.");
            return;
        }

        Account account = updatedUser.getAccount();
        String accountType = account instanceof SavingsAccount ? "Savings" : "Current";

        String details = "Username: " + currentUser.getUsername() + "\n"
                + "Account Type: " + accountType + "\n"
                + "Account Number: " + account.getAccountNumber() + "\n"
                + "Name: " + account.getName() + "\n"
                + String.format("Balance: %.2f", account.getBalance());
        showMessage(details);
    }

    private void showTransactionHistory() {
        outputArea.setText("");
        outputArea.append("=== Transaction History ===\n\n");

        String accountNumber = currentUser.getAccount().getAccountNumber();
        List<Transaction> transactions = TransactionDAO.getTransactionsByAccount(accountNumber);

        if (transactions.isEmpty()) {
            outputArea.append("No transactions yet.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        for (Transaction transaction : transactions) {
            outputArea.append(String.format(
                    "[%s] - %s - Rs.%.2f - %s\n",
                    transaction.getDateTime().format(formatter),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getDescription()
            ));
        }
    }

    private void logout() {
        dispose();
        loginUI.setVisible(true);
    }

    /**
     * Find user by username (helper method for transfer)
     * Uses database query via UserDAO
     */
    private User findUserByUsername(String username) {
        return UserDAO.getUserByUsername(username);
    }

    private void showMessage(String message) {
        outputArea.setText(message);
    }

    private void styleButton(JButton button, Color color) {
        Color hoverColor = darken(color, 18);
        Color pressedColor = darken(color, 30);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 48));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(pressedColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(185, 185, 185), 1),
                        new EmptyBorder(11, 14, 9, 14)
                ));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.contains(e.getPoint())) {
                    button.setBackground(hoverColor);
                } else {
                    button.setBackground(color);
                }
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        new EmptyBorder(10, 14, 10, 14)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        new EmptyBorder(10, 14, 10, 14)
                ));
            }
        });
    }

    private Color darken(Color color, int delta) {
        int r = Math.max(0, color.getRed() - delta);
        int g = Math.max(0, color.getGreen() - delta);
        int b = Math.max(0, color.getBlue() - delta);
        return new Color(r, g, b);
    }
}
