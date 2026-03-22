import java.time.LocalDateTime;

// Inheritance: CurrentAccount extends Account with its own withdrawal logic.
public class CurrentAccount extends Account {
    public CurrentAccount(String accountNumber, String name, double balance) {
        super(accountNumber, name, balance);
    }

    @Override
    public String withdraw(double amount) {
        // Polymorphism: runtime method dispatch calls this for current accounts.
        if (amount <= 0) {
            return "Invalid withdrawal amount.";
        }

        if (amount > getBalance()) {
            return "Insufficient balance.";
        }

        setBalance(getBalance() - amount);
        addTransaction(new Transaction("Withdraw", amount, LocalDateTime.now(), "Amount withdrawn"));
        return String.format("Withdrawal successful. New Balance: %.2f", getBalance());
    }
}