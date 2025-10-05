import java.util.Random;

public class RandomStrategy implements Strategy {
    private static final String[] MOVES = {"R", "P", "S"};
    private final Random random = new Random();

    @Override
    public String getMove(String playerMove) {
        return MOVES[random.nextInt(MOVES.length)];
    }
}