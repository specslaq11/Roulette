import java.util.Arrays;

public class Bet {
    private double bet;
    private String type;
    private boolean outcome;
    private int multiplier;
    private String target;

    public Bet(double bet, String type, String target) {
        this.bet = bet;
        this.type = type;
        this.target = target;
    }

    public double getAmount() {
        return bet;
    }

    public String getType() {
        return type;
    }

    public boolean getOutcome() {
        return outcome;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public double calculateWin(double amount, boolean isWinner) {
        if (!isWinner) {
            return 0; // No winnings
        }
        switch (type) {
            case "number":
                multiplier = 35; // Win 35 times the bet
                break;
            case "color":
            case "even":
            case "odd":
                multiplier = 2; // Win double the bet
                break;
            default:
                multiplier = 0; // No winnings if an unsupported bet type
                return 0;
        }
        return amount * multiplier;
    }

    public boolean checkOutcome(String spinResult) {
        try {
            int resultNumber = Integer.parseInt(spinResult.trim());
            switch (type) {
                case "number" -> {
                    int targetNumber = Integer.parseInt(target);
                    return resultNumber == targetNumber;
                }
                case "color" -> {
                    boolean isRed = Arrays.asList(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36).contains(resultNumber);
                    return (target.equals("red") && isRed) || (target.equals("black") && !isRed);
                }
                case "even" -> {
                    return resultNumber % 2 == 0;
                }
                case "odd" -> {
                    return resultNumber % 2 != 0;
                }
                default -> throw new IllegalArgumentException("Unsupported bet type: " + type);
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid spin result format: " + spinResult);
            return false;
        }
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public String getTarget() {
        return target;
    }

    public void resetMultiplier() {
        multiplier = 0;
    }
}
