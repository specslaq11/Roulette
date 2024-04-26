import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RouletteGUI {
    private Game game;
    private JFrame frame;
    private JPanel panel;
    private JTextField betAmountField, betTypeField;
    private JTextArea resultArea;
    private JButton placeBetButton, resetButton, settingsButton;
    private JLabel totalBetsLabel, totalWinningsLabel, totalSpinsLabel, numberFreqLabel, colorFreqLabel;
    private JLabel balanceLabel;
    private User currentPlayer;
    private JCheckBox darkThemeCheckbox;
    private RouletteWheelPanel rouletteWheelPanel;

    public RouletteGUI() {
        game = new Game();
        currentPlayer = new User("Player 1", 50);
        game.addPlayer(currentPlayer);
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Roulette Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel setup
        panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Balance label
        balanceLabel = new JLabel("Balance: " + currentPlayer.getBalance());
        balanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(balanceLabel, BorderLayout.NORTH);

        totalBetsLabel = new JLabel("Total Bets Placed: ");
        totalWinningsLabel = new JLabel("Total Winnings: ");
        totalSpinsLabel = new JLabel("Total Spins: ");
        numberFreqLabel = new JLabel("Number Frequency: ");
        colorFreqLabel = new JLabel("Color Frequency: ");

        panel.add(totalBetsLabel);
        panel.add(totalWinningsLabel);
        panel.add(totalSpinsLabel);
        panel.add(numberFreqLabel);
        panel.add(colorFreqLabel);

        // Bet controls panel
        JPanel betControlsPanel = new JPanel(new GridLayout(3, 2, 5, 5)); // Adjusted layout to accommodate the fields
        betAmountField = new JTextField(10);
        betControlsPanel.add(new JLabel("Bet Amount:"));
        betControlsPanel.add(betAmountField);
        betTypeField = new JTextField(10);
        betControlsPanel.add(new JLabel("Bet Type (e.g., 2, even, odd, black, red):"));
        betControlsPanel.add(betTypeField);
        // Add an empty label to maintain the layout
        betControlsPanel.add(new JLabel());
        panel.add(betControlsPanel, BorderLayout.NORTH); // Changed the position to NORTH

        // Roulette Panel
        rouletteWheelPanel = new RouletteWheelPanel();
        panel.add(rouletteWheelPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        placeBetButton = new JButton("Place Bet");
        resetButton = new JButton("Reset Game");
        settingsButton = new JButton("Settings");

        buttonPanel.add(placeBetButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(settingsButton);

        JButton statisticsButton = new JButton("Statistics");
        statisticsButton.addActionListener(e -> showStatisticsDialog());
        buttonPanel.add(statisticsButton);

        // Result area panel
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        // Add both buttonPanel and resultPanel to a new panel
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.add(buttonPanel);
        bottomPanel.add(resultPanel);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        placeBetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeBet();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSettingsDialog();
            }
        });
        betAmountField.addActionListener(e -> placeBet());
        betTypeField.addActionListener(e -> placeBet());
    }


    private void showStatisticsDialog() {
        JFrame statisticsFrame = new JFrame("Statistics");
        statisticsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statisticsFrame.setSize(400, 300);
        statisticsFrame.setLocationRelativeTo(frame);

        JPanel statisticsPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        statisticsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        totalBetsLabel = new JLabel("Total Bets Placed: " + game.getTotalBetsPlaced());
        totalWinningsLabel = new JLabel("Total Winnings: " + game.getTotalWinnings());
        totalSpinsLabel = new JLabel("Total Spins: " + game.getTotalSpins());

        StringBuilder numberFreqText = new StringBuilder("Number Frequency: ");
        for (Map.Entry<String, Integer> entry : game.getNumberFrequency().entrySet()) {
            numberFreqText.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }
        numberFreqLabel = new JLabel(numberFreqText.toString());

        StringBuilder colorFreqText = new StringBuilder("Color Frequency: ");
        for (Map.Entry<String, Integer> entry : game.getColorFrequency().entrySet()) {
            colorFreqText.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }
        colorFreqLabel = new JLabel(colorFreqText.toString());

        statisticsPanel.add(totalBetsLabel);
        statisticsPanel.add(totalWinningsLabel);
        statisticsPanel.add(totalSpinsLabel);
        statisticsPanel.add(numberFreqLabel);
        statisticsPanel.add(colorFreqLabel);

        statisticsFrame.add(statisticsPanel);
        statisticsFrame.setVisible(true);
    }

    private void placeBet() {
        try {
            double amount = Double.parseDouble(betAmountField.getText());
            String betInput = betTypeField.getText().toLowerCase();

            String type;
            String target = "";

            // Parse bet input
            if (betInput.matches("[0-9]+")) {
                type = "number";
            } else if (betInput.equalsIgnoreCase("color")) {
                type = "color";
            } else if (betInput.equalsIgnoreCase("odd")) {
                type = "odd";
            } else if (betInput.equalsIgnoreCase("even")) {
                type = "even";
            } else {
                throw new IllegalArgumentException("Invalid bet input: " + betInput);
            }

            game.placeBet(currentPlayer, amount, type); // Call the updated placeBet method without target parameter

            resultArea.append("Bet placed: " + amount + " on " + type + "\n");

            // Start the roulette spin animation
            spinRouletteAnimation();

        } catch (NumberFormatException e) {
            resultArea.append("Invalid amount entered. Please enter a valid number.\n");
        } catch (Exception e) {
            resultArea.append("Error: " + e.getMessage() + "\n");
        }
    }


    private void spinRouletteAnimation() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int count = 0;
            final String[] spinResults = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", // Add more results if needed
                    "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                    "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                    "31", "32", "33", "34", "35", "36"};

            @Override
            public void run() {
                if (count < spinResults.length) {
                    // Shuffle the array to display numbers randomly
                    shuffleArray(spinResults);
                    String currentResult = spinResults[count];
                    rouletteWheelPanel.setCurrentResult(currentResult);
                    count++;
                } else {
                    this.cancel();
                    // Spin completed, get the actual result
                    String result = game.spinRoulette();
                    rouletteWheelPanel.setCurrentResult(result);

                    // Check the outcome of the bet
                    Bet newBet = currentPlayer.getCurrentBets().get(0); // Assuming only one bet for simplicity
                    boolean won = newBet.checkOutcome(result);
                    if (won) {
                        double winAmount = newBet.calculateWin(Double.parseDouble(betAmountField.getText()), true);
                        resultArea.append("Congratulations! You won " + winAmount + "\n");
                    } else {
                        resultArea.append("Sorry! You lost " + Double.parseDouble(betAmountField.getText()) + "\n");
                    }
                    updateDisplayAfterSpin(result);
                }
            }
        }, 0, 100); // Change the delay (in milliseconds) to control the animation speed
    }

    // Method to shuffle the array
    private void shuffleArray(String[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            // Swap the elements
            String temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }


    private void showSettingsDialog() {
        JFrame settingsFrame = new JFrame("Settings");
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        settingsFrame.setSize(300, 150);
        settingsFrame.setLocationRelativeTo(frame);

        JPanel settingsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        settingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        darkThemeCheckbox = new JCheckBox("Dark Theme");
        settingsPanel.add(darkThemeCheckbox);

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applySettings();
                settingsFrame.dispose();
            }
        });
        settingsPanel.add(applyButton);

        settingsFrame.add(settingsPanel);
        settingsFrame.setVisible(true);
    }

    private void applySettings() {
        if (darkThemeCheckbox.isSelected()) {
            // Apply dark theme
            Color darkGray = new Color(45, 45, 45);
            Color lighterGray = new Color(70, 70, 70);
            Color lightGray = new Color(100, 100, 100);

            // Apply gradient to panel

            balanceLabel.setForeground(Color.WHITE);
            resultArea.setBackground(darkGray);
            resultArea.setForeground(Color.WHITE);

            // Apply gradient to buttons
            GradientButton(placeBetButton, lightGray, darkGray);
            GradientButton(resetButton, lightGray, darkGray);
            GradientButton(settingsButton, lightGray, darkGray);
        } else {
            // Apply default theme
            panel.setBackground(null);
            balanceLabel.setForeground(null);
            resultArea.setBackground(null);
            resultArea.setForeground(null);

            // Reset button UI to default
            placeBetButton.setUI(new BasicButtonUI());
            resetButton.setUI(new BasicButtonUI());
            settingsButton.setUI(new BasicButtonUI());

            // Apply default colors to buttons
            placeBetButton.setForeground(null);
            placeBetButton.setContentAreaFilled(true);
            placeBetButton.setBorderPainted(true);
            resetButton.setForeground(null);
            resetButton.setContentAreaFilled(true);
            resetButton.setBorderPainted(true);
            settingsButton.setForeground(null);
            settingsButton.setContentAreaFilled(true);
            settingsButton.setBorderPainted(true);
        }
    }


    private void GradientButton(JButton button, Color startColor, Color endColor) {
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add your button action code here
            }
        });

        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                int w = c.getWidth();
                int h = c.getHeight();
                GradientPaint gp = new GradientPaint(0, 0, startColor, 0, h, endColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                super.paint(g, c);
            }
        });
    }






    private void updateDisplayAfterSpin(String result) {
        // Assuming this method is triggered after the roulette spin result is processed
        game.resolveBets(result);
        updateStatistics();  // Refresh the statistics display in GUI
        showBalance();
    }

    public void updateStatistics() {
        totalBetsLabel.setText("Total Bets Placed: " + game.getTotalBetsPlaced());
        totalWinningsLabel.setText("Total Winnings: " + game.getTotalWinnings());
        totalSpinsLabel.setText("Total Spins: " + game.getTotalSpins());
        // Update number and color frequency labels
        StringBuilder numberFreqText = new StringBuilder("Number Frequency: ");
        for (Map.Entry<String, Integer> entry : game.getNumberFrequency().entrySet()) {
            numberFreqText.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }
        numberFreqLabel.setText(numberFreqText.toString());

    }


    private void showBalance() {
        resultArea.append("Your current balance is: " + currentPlayer.getBalance() + "\n");
        balanceLabel.setText("Balance: " + currentPlayer.getBalance()); // Update balance label
    }

    private void resetGame() {
        game.resetRound();
        resultArea.append("Game reset.\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RouletteGUI::new);
    }

    public JTextField getBetAmountField() {
        return betAmountField;
    }

    public JTextField getBetTypeField() {
        return betTypeField;
    }
}

class RouletteWheelPanel extends JPanel {
    private String currentResult;

    public RouletteWheelPanel() {
        setPreferredSize(new Dimension(300, 300));
        setBackground(Color.BLACK);
        setOpaque(true);
    }

    public void setCurrentResult(String currentResult) {
        this.currentResult = currentResult;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        if (currentResult != null) {
            String text = "Spinning: " + currentResult;
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(text, x, y);
        } else {
            String text = "Roulette Wheel";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(text, x, y);
        }
        g2d.dispose();
    }
}