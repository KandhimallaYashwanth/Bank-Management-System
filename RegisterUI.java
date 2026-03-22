import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
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

public class RegisterUI extends JFrame {
    private final LoginUI loginUI;
    private final JTextField txtName;
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JComboBox<String> cmbAccountType;

    public RegisterUI(LoginUI loginUI) {
        this.loginUI = loginUI;

        setTitle("Register - Bank Management System");
        setSize(620, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        JLabel lblTitle = new JLabel("Create New Account", JLabel.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(backgroundColor);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cardBorderColor, 1),
            new EmptyBorder(22, 22, 22, 22)
        ));

        JLabel lblName = new JLabel("Name");
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtName = new JTextField();
        styleInput(txtName);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername = new JTextField();
        styleInput(txtUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword = new JPasswordField();
        styleInput(txtPassword);

        JLabel lblType = new JLabel("Account Type");
        lblType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbAccountType = new JComboBox<>(new String[]{"Savings", "Current"});
        cmbAccountType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbAccountType.setPreferredSize(new Dimension(320, 34));
        cmbAccountType.setBackground(Color.WHITE);
        cmbAccountType.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230), 1));

        JButton btnRegister = new JButton("Register");
        styleButton(btnRegister, new Color(39, 174, 96)); // #27AE60

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 0);

        gbc.gridy = 0;
        formPanel.add(lblName, gbc);
        gbc.gridy = 1;
        formPanel.add(txtName, gbc);

        gbc.gridy = 2;
        formPanel.add(lblUsername, gbc);
        gbc.gridy = 3;
        formPanel.add(txtUsername, gbc);

        gbc.gridy = 4;
        formPanel.add(lblPassword, gbc);
        gbc.gridy = 5;
        formPanel.add(txtPassword, gbc);

        gbc.gridy = 6;
        formPanel.add(lblType, gbc);
        gbc.gridy = 7;
        formPanel.add(cmbAccountType, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(6, 0, 0, 0);
        formPanel.add(btnRegister, gbc);

        centerPanel.add(formPanel);

        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(centerPanel, BorderLayout.CENTER);
        add(rootPanel);

        revalidate();
        repaint();

        btnRegister.addActionListener(e -> registerUser());
    }

    private void registerUser() {
        String name = txtName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String accountType = (String) cmbAccountType.getSelectedItem();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        // Validate password length
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.");
            return;
        }

        // Check if username already exists in database
        if (UserDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose another.");
            return;
        }

        String accountNumber = LoginUI.generateAccountNumber();

        // Register user in database with initial balance
        String result = UserDAO.registerUser(username, password, name, accountType, accountNumber);

        if (result != null) {
            String initialBalance = "Savings".equals(accountType) ? "500.00" : "0.00";
            JOptionPane.showMessageDialog(
                    this,
                    "Registration successful.\nAccount Number: " + accountNumber + "\nInitial Balance: " + initialBalance
            );
            dispose();
            loginUI.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.");
        }
    }

    private void styleInput(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(320, 34));
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
}