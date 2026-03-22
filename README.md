# 🏦 Bank Management System (Java + MySQL)

A desktop-based **Bank Management System** built using **Java Swing, OOP principles, and MySQL (JDBC)**.
This application simulates real-world banking operations like **user authentication, account management, and transaction processing** with persistent storage.

---

## 🚀 Features

* 🔐 User Registration & Login (Authentication)
* 💰 Deposit Money
* 💸 Withdraw Money (with balance validation)
* 🔄 Transfer Funds between accounts
* 📊 View Account Details
* 📜 Transaction History (stored in database)
* 🧾 Auto-generated Account Numbers
* 🛡️ SQL Injection Protection using PreparedStatement

---

## 🏗️ Tech Stack

| Layer        | Technology  |
| ------------ | ----------- |
| Frontend UI  | Java Swing  |
| Backend      | Java (OOP)  |
| Database     | MySQL       |
| Connectivity | JDBC        |
| Architecture | DAO Pattern |

---

## 🧠 OOP Concepts Used

### 1. Encapsulation

* Private fields in classes like `User`, `Account`
* Access controlled using getters/setters

### 2. Abstraction

* Abstract class `Account`
* Defines common methods like `deposit()`, `withdraw()`

### 3. Inheritance

* `SavingsAccount` and `CurrentAccount` extend `Account`
* Enables code reuse and specialization

### 4. Polymorphism

* Overridden methods (e.g., withdraw rules differ by account type)
* Runtime behavior based on object type



---

## 🔄 Architecture

```
UI Layer (Swing)
   ↓
DAO Layer (UserDAO, TransactionDAO)
   ↓
JDBC (DBConnection)
   ↓
MySQL Database
```

---

## 🔌 JDBC Connection

```java
Connection conn = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/bank_db",
    "root",
    ""
);
```

---

## ⚙️ How to Run

### 1️⃣ Compile

```bash
javac *.java
```

### 2️⃣ Run (IMPORTANT - include JDBC driver)

```bash
java -cp ".;lib/mysql-connector-j-9.6.0.jar" LoginUI
```

## 🔐 Security Features

* PreparedStatement → prevents SQL injection
* Input validation → prevents invalid transactions
* Balance checks → avoids overdraft

---


## 👨‍💻 Author

**Kandhimalla Yashwanth**


---


