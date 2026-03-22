import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginUI extends JFrame {
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;

    public LoginUI() {
        setTitle("Login - Bank Management System");
        setSize(560, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        Color backgroundColor = new Color(244, 246, 247);
        Color headerColor = new Color(44, 62, 80);
        Color cardBorderColor = new Color(220, 220, 220);

        JPanel rootPanel = new JPanel(new BorderLayout(0, 15));
        rootPanel.setBackground(backgroundColor);
        rootPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(headerColor);
        headerPanel.setBorder(new EmptyBorder(16, 10, 16, 10));

        JLabel lblTitle = new JLabel("Welcome to Bank System", JLabel.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(backgroundColor);

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cardBorderColor, 1),
            new EmptyBorder(22, 22, 22, 22)
        ));

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername = new JTextField();
        styleInput(txtUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword = new JPasswordField();
        styleInput(txtPassword);

        JPanel buttonPanel = new JPanel(new BorderLayout(12, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnLogin = new JButton("Login");
        JButton btnCreateAccount = new JButton("Create New Account");

        styleButton(btnLogin, new Color(52, 152, 219));      // #3498DB
        styleButton(btnCreateAccount, new Color(39, 174, 96)); // #27AE60

        buttonPanel.add(btnLogin, BorderLayout.WEST);
        buttonPanel.add(btnCreateAccount, BorderLayout.EAST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 0);

        gbc.gridy = 0;
        cardPanel.add(lblUsername, gbc);

        gbc.gridy = 1;
        cardPanel.add(txtUsername, gbc);

        gbc.gridy = 2;
        cardPanel.add(lblPassword, gbc);

        gbc.gridy = 3;
        cardPanel.add(txtPassword, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(6, 0, 0, 0);
        cardPanel.add(buttonPanel, gbc);

        centerPanel.add(cardPanel);

        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(centerPanel, BorderLayout.CENTER);
        add(rootPanel);

        revalidate();
        repaint();

        btnLogin.addActionListener(e -> doLogin());
        btnCreateAccount.addActionListener(e -> openRegister());
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        // Use UserDAO to validate credentials from database
        User user = UserDAO.loginUser(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
            return;
        }

        DashboardUI dashboardUI = new DashboardUI(user, this);
        dashboardUI.setVisible(true);
        setVisible(false);
        clearInputs();
    }

    private void openRegister() {
        RegisterUI registerUI = new RegisterUI(this);
        registerUI.setVisible(true);
        setVisible(false);
        clearInputs();
    }

    private void clearInputs() {
        txtUsername.setText("");
        txtPassword.setText("");
    }

    private void styleInput(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 34));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1, true),
                new EmptyBorder(7, 10, 7, 10)
        ));
    }

    private void styleButton(JButton button, Color color) {
        Color hoverColor = darken(color, 18);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 40));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    private Color darken(Color color, int delta) {
        int r = Math.max(0, color.getRed() - delta);
        int g = Math.max(0, color.getGreen() - delta);
        int b = Math.max(0, color.getBlue() - delta);
        return new Color(r, g, b);
    }

    /**
     * Generate next account number from database
     * @return the next account number as string
     */
    public static String generateAccountNumber() {
        int latestAccountNumber = UserDAO.getLatestAccountNumber();
        return String.valueOf(latestAccountNumber + 1);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Keep default look if system look is unavailable.
        }

        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}