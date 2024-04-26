import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private double balance = 50;
    private List<Bet> currentBets;
    private List<String> betHistory;

    public User(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.currentBets = new ArrayList<>();
        this.betHistory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public List<Bet> getCurrentBets() {
        return currentBets;
    }

    public List<String> getBetHistory() {
        return betHistory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addBet(Bet bet) {
        if (balance >= bet.getAmount()) {
            currentBets.add(bet);
            balance -= bet.getAmount(); // Deduct the bet amount from the balance when the bet is placed
            System.out.println("Bet placed: " + bet.getAmount() + ", New balance: " + balance);
        } else {
            throw new IllegalArgumentException("Insufficient balance to place bet");
        }
    }

    public void win(double amount) {
        balance += amount;  // Accurately add the winnings to the user's balance
        System.out.println("Balance after win for " + name + ": " + balance);
    }

    public void updateWinHistory(String record){
        betHistory.add(record);
    }

    public void resetBets() {
        currentBets.clear();
    }

}
