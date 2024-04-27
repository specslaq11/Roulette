import javax.swing.*;
import java.util.*;

public class Game {
    private int totalBetsPlaced = 0;
    private double totalWinnings = 0;
    private int totalSpins = 0;
    private Map<String, Integer> numberFrequency = new HashMap<>();
    private Map<String, Integer> colorFrequency = new HashMap<>();
    private List<User> players;
    private List<Bet> currentBets;

    public Game() {
        this.players = new ArrayList<>();
        this.currentBets = new ArrayList<>();
    }

    public void addPlayer(User user) {
        this.players.add(user);
    }

    public boolean placeBet(User player, double amount, String type) {
        if (player.getBalance() < amount) {
            JOptionPane.showMessageDialog(null, "Insufficient balance to place bet. Please add funds.");
            return false; // Exit the method if balance is insufficient
        }

        if (!isValidType(type)) {
            return false;
        }

        Bet bet;
        String target = "";

        if (type.equals("number")) {
            // In case of number bet, we need to prompt the user for the target number
            target = JOptionPane.showInputDialog(null, "Enter the number you want to bet on (0-36):");
            if (target == null || target.isEmpty() || !target.matches("\\d+") || Integer.parseInt(target) < 0 || Integer.parseInt(target) > 36) {
                JOptionPane.showMessageDialog(null, "Invalid number entered. Please enter a number between 0 and 36.");
                return false;
            }
            bet = new Bet(amount, type, target);
        } else if (type.equals("color")) {
            // In case of color bet, we'll prompt the user for red or black
            String[] options = {"Red", "Black"};
            int choice = JOptionPane.showOptionDialog(null, "Choose a color to bet on:", "Color Bet", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (choice == 0) {
                target = "red";
            } else {
                target = "black";
            }
            bet = new Bet(amount, type, target.toLowerCase()); // Convert target to lowercase
        } else {
            // For even, odd, or other bets, we don't need a target
            bet = new Bet(amount, type, target);
        }

        currentBets.add(bet);
        player.addBet(bet);
        totalBetsPlaced++;

        if (!target.isEmpty()) {
            if (type.equals("number")) {
                numberFrequency.put(target, numberFrequency.getOrDefault(target, 0) + 1);
            }
        }
        return true;
    }




    private boolean isValidType(String type) {
        switch (type) {
            case "number":
            case "color":
            case "even":
            case "odd":
                return true;
            default:
                return false;
        }
    }



    public String spinRoulette() {
        int result = new Random().nextInt(37);
        return Integer.toString(result);
    }

    public void resolveBets(String spinResult) {
        for (User player : players) {
            for (Bet bet : player.getCurrentBets()) {
                boolean outcome = bet.checkOutcome(spinResult);
                if (outcome) {
                    double winAmount = bet.calculateWin(bet.getAmount(), true);
                    totalWinnings += winAmount;
                    player.win(winAmount);  // Properly add winnings
                    System.out.println("Player " + player.getName() + " wins: " + winAmount);
                } else {
                    System.out.println("Player " + player.getName() + " does not win on bet type: " + bet.getType());
                }
            }
        }

        totalSpins++;
        updateFrequencies(spinResult); // Update frequencies after each spin
        resetRound();
    }

    private void updateFrequencies(String spinResult) {
        // Update number frequency
        numberFrequency.put(spinResult, numberFrequency.getOrDefault(spinResult, 0) + 1);

        // Determine the color based on the spin result
        boolean isRed;
        try {
            int resultNumber = Integer.parseInt(spinResult.trim());
            isRed = Arrays.asList(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36).contains(resultNumber);
        } catch (NumberFormatException e) {
            // Handle invalid spin result format
            System.err.println("Invalid spin result format: " + spinResult);
            return;
        }

        // Update color frequency based on the determined color
        String color = isRed ? "red" : "black";
        colorFrequency.put(color, colorFrequency.getOrDefault(color, 0) + 1);
    }







    private void resetMultiplier() {
        for (Bet bet : currentBets) {
            bet.resetMultiplier();
        }
    }

    public void resetRound() {
        currentBets.clear();
        for (User player : players) {
            player.resetBets();
        }
        System.out.println("Resetting round...");
    }

    private void concludeGame() {
        System.out.println("Game over!");
        displayStatus();
    }

    public void displayStatus() {
        for (User player : players) {
            System.out.println(player.getName() + "'s balance: " + player.getBalance());
        }
    }

    public void runGame(JTextField betAmountField, JTextField betTypeField) {
        initializeGame();
        registerPlayers();

        boolean continuePlaying = true;
        while (continuePlaying) {
            double amount = Double.parseDouble(betAmountField.getText());
            String type = betTypeField.getText().toLowerCase();

            for (User player : players) {
                placeBet(player, amount, type);
            }

            String result = spinRoulette();
            System.out.println("Roulette wheel stopped on: " + result);
            resolveBets(result);

            continuePlaying = continuePlaying();
            resetRound();
        }
        concludeGame();
    }

    private boolean continuePlaying() {
        int choice = JOptionPane.showConfirmDialog(null, "Continue playing?", "Continue", JOptionPane.YES_NO_OPTION);
        return choice == JOptionPane.YES_OPTION;
    }

    private void initializeGame() {
        System.out.println("Welcome to the Roulette Game");
        System.out.println("======================================");
        System.out.println("How to Play:");
        System.out.println("1. Each player registers and enters their starting balance.");
        System.out.println("2. Players place their bets on numbers, colors, or other valid bet types.");
        System.out.println("3. The wheel is spun to determine the winning number or color.");
        System.out.println("4. Winnings are calculated based on the bets placed.");
        System.out.println("5. Rounds continue until players decide to end the game.");
        System.out.println("Good luck and have fun!");
        System.out.println("======================================");
    }

    private void registerPlayers() {
        System.out.println("Registering players...");
        players.add(new User("Player 1", 50));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RouletteGUI gui = new RouletteGUI();
            Game game = new Game();
            game.runGame(gui.getBetAmountField(), gui.getBetTypeField());
        });
    }

    public List<User> getPlayers() {
        return players;
    }

    public int getTotalBetsPlaced() {
        return totalBetsPlaced;
    }

    public double getTotalWinnings() {
       return totalWinnings;
    }

    public int getTotalSpins() {
        return totalSpins;
    }

    public Map<String, Integer> getNumberFrequency() {
        return numberFrequency;
    }

    public Map<String, Integer> getColorFrequency() {
        return colorFrequency;
    }
}
