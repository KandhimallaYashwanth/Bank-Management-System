import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account {
    // Encapsulation: fields are private and accessed through methods.
    private final String accountNumber;
    private final String name;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, String name, double balance) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String deposit(double amount) {
        if (amount <= 0) {
            return "Invalid deposit amount.";
        }
        balance += amount;
        addTransaction(new Transaction("Deposit", amount, LocalDateTime.now(), "Amount deposited"));
        return String.format("Deposit successful. New Balance: %.2f", balance);
    }

    // Abstraction: subclasses must provide their own withdrawal rule.
    public abstract String withdraw(double amount);

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public String transfer(Account receiver, double amount) {
        if (receiver == null) {
            return "Receiver account not found.";
        }
        if (receiver == this) {
            return "Cannot transfer to the same account.";
        }
        if (amount <= 0) {
            return "Invalid transfer amount.";
        }

        String withdrawResult = withdraw(amount);
        if (!withdrawResult.startsWith("Withdrawal successful")) {
            return "Transfer failed: " + withdrawResult;
        }

        String depositResult = receiver.deposit(amount);
        if (!depositResult.startsWith("Deposit successful")) {
            setBalance(getBalance() + amount);
            return "Transfer failed: " + depositResult;
        }

        // Replace generic withdraw/deposit entries with transfer-specific entries.
        removeLastIfType("Withdraw");
        receiver.removeLastIfType("Deposit");

        LocalDateTime now = LocalDateTime.now();
        addTransaction(new Transaction("Transfer", amount, now,
                "Transfer Sent to A/C " + receiver.getAccountNumber()));
        receiver.addTransaction(new Transaction("Transfer", amount, now,
                "Transfer Received from A/C " + getAccountNumber()));

        return String.format("Transfer successful. New Balance: %.2f", getBalance());
    }

    private void removeLastIfType(String type) {
        if (transactions.isEmpty()) {
            return;
        }
        Transaction last = transactions.get(transactions.size() - 1);
        if (type.equals(last.getType())) {
            transactions.remove(transactions.size() - 1);
        }
    }

    public String display() {
        return String.format(
                "Account Number: %s\nName: %s\nBalance: %.2f",
                accountNumber,
                name,
                balance
        );
    }
}