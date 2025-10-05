public class CheatStrategy implements Strategy {
    @Override
    public String getMove(String playerMove) {
        String computerMove;
        switch (playerMove) {
            case "R":
                computerMove = "P";
                break;
            case "P":
                computerMove = "S";
                break;
            case "S":
                computerMove = "R";
                break;
            default:
                computerMove = "R";
        }
        return computerMove;
    }
}