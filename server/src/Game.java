import java.io.IOException;
import java.util.Random;

public class Game {
    private Player playerA, playerB;
    private Player [] players; // this array exists only for convenience

    private static int A = 0, B = 1, NONE = -1;
    private int first, second, lastWinner;

    private Random random;

    public Game(Player playerA, Player playerB) throws IllegalArgumentException {
        if (playerA == null || playerB == null) {
            throw new IllegalArgumentException("One of the Player objects was null.");
        }
        this.playerA = playerA;
        this.playerB = playerB;
        players = new Player[]{playerA, playerB};

        first = second = NONE;
        lastWinner = NONE;

        this.random = new Random();
    }

    public void start() throws IOException, ClassNotFoundException {
        // get decks from both players
        playerA.sendMessage(Messages.GIMME_DECK);
        playerA.readDeck();
        playerA.sendMessage(Messages.WAIT);

        playerB.sendMessage(Messages.GIMME_DECK);
        playerB.readDeck();
        playerB.sendMessage(Messages.WAIT);

        // shuffle them
        playerA.shuffleDeck();

        playerB.shuffleDeck();

        // put some cards from the top of each deck into players' hands
        playerA.initHand();

        playerB.initHand();

        // send hands to players
        playerA.sendMessage(Messages.RECV_INIT_HAND);
        playerA.sendHand();
        playerA.sendMessage(Messages.SHOW_INIT_HAND);

        playerB.sendMessage(Messages.RECV_INIT_HAND);
        playerB.sendHand();
        playerB.sendMessage(Messages.SHOW_INIT_HAND);

        // ask for their approval
        playerA.sendMessage(Messages.GIMME_INIT_HAND_APPROVAL);

        playerB.sendMessage(Messages.GIMME_INIT_HAND_APPROVAL);

        // wait for their approval
        if (!(playerA.waitForInitHandApproval() && playerB.waitForInitHandApproval())) {
            throw new IOException("Players didn't give approval for their initial hands");
        }

        // while nobody has lost more than once
        while (playerA.getLives() != 0 && playerB.getLives() != 0) {
            // find out who goes first
            findFirst();

            // tell the players their order
            players[first].sendMessage(Messages.YOU_FIRST);
            players[second].sendMessage(Messages.YOU_SECOND);

            Card current;
            int temp;
            // while this round can continue (both players can play)
            while (playerA.canPlay() && playerB.canPlay()) {
                players[first].sendMessage(Messages.GIMME_CARD);

                current = players[first].playCard(players[first].readCardIndex());

                playCard(current);

                temp = first;
                first = second;
                second = temp;
            }

            // find out who won and subtract the life accordingly
            findRoundWinnerSubLife();

            // put the cards from each player's boards into their respective discard piles
            playerA.discardBoard();

            playerB.discardBoard();
        }
    }

    private void playCard(Card c) {

    }

    private void findRoundWinnerSubLife() {
        int aTotal = playerA.getTotal(), bTotal = playerB.getTotal();
        if (aTotal > bTotal) {
            lastWinner = A;
            playerB.subLife();
        }
        else if (aTotal < bTotal) {
            lastWinner = B;
            playerA.subLife();
        }
        else {
            lastWinner = NONE;
            playerA.subLife();
            playerB.subLife();
        }
    }

    private void findFirst() {
        if (lastWinner == A) {
            first = A;
            second = B;
        }
        else if (lastWinner == B) {
            first = B;
            second = A;
        }
        else { // if nobody won last round or this is the first round
            first = random.nextInt(2);
            second = (first == A ? B : A);
        }
    }
}
