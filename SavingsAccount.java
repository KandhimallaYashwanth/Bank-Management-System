import java.time.LocalDateTime;

// Inheritance: SavingsAccount extends Account and reuses common account behavior.
public class SavingsAccount extends Account {
    private static final double MIN_BALANCE = 500.0;

    public SavingsAccount(String accountNumber, String name, double balance) {
        super(accountNumber, name, balance);
    }

    @Override
    public String withdraw(double amount) {
        // Polymorphism: this implementation is chosen at runtime for savings accounts.
        if (amount <= 0) {
            return "Invalid withdrawal amount.";
        }

        double newBalance = getBalance() - amount;
        if (newBalance < MIN_BALANCE) {
            return String.format(
                    "Insufficient balance. Savings account must keep minimum %.2f.",
                    MIN_BALANCE
            );
        }

        setBalance(newBalance);
        addTransaction(new Transaction("Withdraw", amount, LocalDateTime.now(), "Amount withdrawn"));
        return String.format("Withdrawal successful. New Balance: %.2f", getBalance());
    }
}