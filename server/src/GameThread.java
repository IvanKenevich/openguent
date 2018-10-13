import java.io.IOException;
import java.net.Socket;

public class GameThread extends Thread{
    private Socket playerOne, playerTwo;

    public GameThread(Socket playerOne, Socket playerTwo) {
        super("Game Session Thread");
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    public void run() {
        try (
                Player playerA = new Player(playerOne);
                Player playerB = new Player(playerTwo)
        ) {
            Game game = new Game(playerA, playerB);
            game.start();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}