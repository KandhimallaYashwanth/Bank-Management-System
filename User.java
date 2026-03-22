public class User {
    // Encapsulation: keep fields private and expose only needed getters.
    private final String username;
    private final String password;
    private final Account account;

    public User(String username, String password, Account account) {
        this.username = username;
        this.password = password;
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Account getAccount() {
        return account;
    }
}