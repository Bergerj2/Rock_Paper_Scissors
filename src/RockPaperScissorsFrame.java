import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RockPaperScissorsFrame extends JFrame {

    private int playerWins = 0;
    private int computerWins = 0;
    private int ties = 0;
    private final Map<String, Integer> playerMoveCounts = new HashMap<>();
    private String lastPlayerMove = null;
    private final Random random = new Random();

    private final JTextField playerWinsField = new JTextField("0", 5);
    private final JTextField computerWinsField = new JTextField("0", 5);
    private final JTextField tiesField = new JTextField("0", 5);
    private final JTextArea gameHistoryArea = new JTextArea(15, 30);

    private final CheatStrategy cheatStrategy = new CheatStrategy();
    private final RandomStrategy randomStrategy = new RandomStrategy();

    private final LeastUsedStrategy leastUsedStrategy = new LeastUsedStrategy();
    private final MostUsedStrategy mostUsedStrategy = new MostUsedStrategy();
    private final LastUsedStrategy lastUsedStrategy = new LastUsedStrategy();

    public RockPaperScissorsFrame() {
        super("Rock Paper Scissors Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        playerMoveCounts.put("R", 0);
        playerMoveCounts.put("P", 0);
        playerMoveCounts.put("S", 0);

        setLayout(new BorderLayout(10, 10));

        add(createControlsPanel(), BorderLayout.NORTH);
        add(createStatsPanel(), BorderLayout.SOUTH);
        add(createHistoryPanel(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBorder(BorderFactory.createTitledBorder("Make Your Move"));

        GameButtonListener listener = new GameButtonListener();

        JButton rockButton = new JButton("Rock", createIcon("rock.png"));
        rockButton.setActionCommand("R");
        rockButton.addActionListener(listener);

        JButton paperButton = new JButton("Paper", createIcon("paper.png"));
        paperButton.setActionCommand("P");
        paperButton.addActionListener(listener);

        JButton scissorsButton = new JButton("Scissors", createIcon("scissors.png"));
        scissorsButton.setActionCommand("S");
        scissorsButton.addActionListener(listener);

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));

        panel.add(rockButton);
        panel.add(paperButton);
        panel.add(scissorsButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(quitButton);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 5));
        TitledBorder border = BorderFactory.createTitledBorder("Game Statistics");
        panel.setBorder(border);

        playerWinsField.setEditable(false);
        computerWinsField.setEditable(false);
        tiesField.setEditable(false);

        panel.add(createStatGroup("Player Wins:", playerWinsField));
        panel.add(createStatGroup("Computer Wins:", computerWinsField));
        panel.add(createStatGroup("Ties:", tiesField));

        return panel;
    }

    private JPanel createStatGroup(String labelText, JTextField textField) {
        JPanel group = new JPanel(new FlowLayout(FlowLayout.CENTER));
        group.add(new JLabel(labelText));
        group.add(textField);
        return group;
    }

    private JScrollPane createHistoryPanel() {
        gameHistoryArea.setEditable(false);
        gameHistoryArea.setLineWrap(true);
        gameHistoryArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(gameHistoryArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Game History"));
        return scrollPane;
    }

    private ImageIcon createIcon(String filename) {
        java.net.URL imgURL = getClass().getResource(filename);
        if (imgURL != null) {
            ImageIcon originalIcon = new ImageIcon(imgURL);
            Image originalImage = originalIcon.getImage();
            final int ICON_SIZE = 50;

            Image scaledImage = originalImage.getScaledInstance(
                    ICON_SIZE,
                    ICON_SIZE,
                    Image.SCALE_SMOOTH
            );
            return new ImageIcon(scaledImage);
        }
        return null;
    }

    private class GameButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String playerMove = e.getActionCommand();
            Strategy currentStrategy = selectStrategy();
            String strategyName = getStrategyName(currentStrategy);
            String computerMove = currentStrategy.getMove(playerMove);
            String resultText = determineWinner(playerMove, computerMove);

            updateGameCounts(playerMove);
            updateGUI(resultText, strategyName);
        }
    }

    private Strategy selectStrategy() {
        int roll = random.nextInt(100) + 1;

        if (roll <= 10) {
            return cheatStrategy;
        } else if (roll <= 30) {
            return leastUsedStrategy;
        } else if (roll <= 50) {
            return mostUsedStrategy;
        } else if (roll <= 70) {
            return lastUsedStrategy;
        } else {
            return randomStrategy;
        }
    }

    private String getStrategyName(Strategy strategy) {
        if (strategy instanceof CheatStrategy) return "Cheat";
        if (strategy instanceof RandomStrategy) return "Random";
        if (strategy instanceof LeastUsedStrategy) return "Least Used";
        if (strategy instanceof MostUsedStrategy) return "Most Used";
        if (strategy instanceof LastUsedStrategy) return "Last Used";
        return "Unknown";
    }

    private String determineWinner(String pMove, String cMove) {
        String result;
        String relation;

        if (pMove.equals(cMove)) {
            ties++;
            relation = "Both chose the same symbol.";
            result = "It's a tie.";
        } else if (
                (pMove.equals("R") && cMove.equals("S")) ||
                        (pMove.equals("P") && cMove.equals("R")) ||
                        (pMove.equals("S") && cMove.equals("P"))
        ) {
            playerWins++;
            relation = getRelation(pMove, cMove);
            result = "Player wins!";
        } else {
            computerWins++;
            relation = getRelation(cMove, pMove);
            result = "Computer Wins!";
        }

        return String.format("%s vs %s: %s (%s)",
                getMoveName(pMove), getMoveName(cMove), relation, result);
    }

    private void updateGameCounts(String playerMove) {
        playerMoveCounts.put(playerMove, playerMoveCounts.get(playerMove) + 1);
        lastPlayerMove = playerMove;
    }

    private void updateGUI(String resultText, String strategyName) {
        playerWinsField.setText(String.valueOf(playerWins));
        computerWinsField.setText(String.valueOf(computerWins));
        tiesField.setText(String.valueOf(ties));

        String historyLine = String.format("%s (Computer: %s)\n", resultText, strategyName);
        gameHistoryArea.append(historyLine);
        gameHistoryArea.setCaretPosition(gameHistoryArea.getDocument().getLength());
    }

    private String getMoveName(String move) {
        return switch (move) {
            case "R" -> "Rock";
            case "P" -> "Paper";
            case "S" -> "Scissors";
            default -> "Unknown";
        };
    }

    private String getRelation(String winner, String loser) {
        if (winner.equals("R") && loser.equals("S")) return "Rock breaks Scissors.";
        if (winner.equals("P") && loser.equals("R")) return "Paper covers Rock.";
        if (winner.equals("S") && loser.equals("P")) return "Scissors cuts Paper.";
        return "";
    }

    private class LeastUsedStrategy implements Strategy {
        @Override
        public String getMove(String playerMove) {
            String leastUsedMove = "R";
            int minCount = Integer.MAX_VALUE;

            for (Map.Entry<String, Integer> entry : playerMoveCounts.entrySet()) {
                if (entry.getValue() < minCount) {
                    minCount = entry.getValue();
                    leastUsedMove = entry.getKey();
                }
            }
            return getWinningMove(leastUsedMove);
        }
    }

    private class MostUsedStrategy implements Strategy {
        @Override
        public String getMove(String playerMove) {
            String mostUsedMove = "R";
            int maxCount = -1;

            for (Map.Entry<String, Integer> entry : playerMoveCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostUsedMove = entry.getKey();
                }
            }
            return getWinningMove(mostUsedMove);
        }
    }

    private class LastUsedStrategy implements Strategy {
        @Override
        public String getMove(String playerMove) {
            if (lastPlayerMove == null) {
                return randomStrategy.getMove(playerMove);
            }
            return getWinningMove(lastPlayerMove);
        }
    }

    private String getWinningMove(String move) {
        return switch (move) {
            case "R" -> "P";
            case "P" -> "S";
            case "S" -> "R";
            default -> "R";
        };
    }
}