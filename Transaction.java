import java.time.LocalDateTime;

public class Transaction {
    private final String type;
    private final double amount;
    private final LocalDateTime dateTime;
    private final String description;

    public Transaction(String type, double amount, LocalDateTime dateTime, String description) {
        this.type = type;
        this.amount = amount;
        this.dateTime = dateTime;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }
}