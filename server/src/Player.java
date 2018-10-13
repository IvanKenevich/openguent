import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Stack;

public class Player implements AutoCloseable {
    // connection to the actual player
    private Socket client;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private ArrayList<Card> hand;
    private Stack<Card> deck, discard;

    private int n_lives, score;

    private boolean passed;

    ArrayList<Card> cc, ranged, siege;

    /**
     * @param client the socket to send and receive information from
     * @throws IOException if the socket connection fails
     */
    public Player(Socket client) throws IOException, ClassNotFoundException {
        this.client = client;
        in = new ObjectInputStream(client.getInputStream());
        out = new ObjectOutputStream(client.getOutputStream());

        hand = new ArrayList<>(10);

        n_lives = 2;
        score = 0;

        passed = false;
    }

    public void readDeck() throws IOException, ClassNotFoundException {
        // total number of cards in the deck
        int size = in.readInt();

        for (int i = 0; i < size; i++) {
            deck.push((Card) in.readObject());
        }
    }

    public void shuffleDeck() {
        java.util.Collections.shuffle(deck);
    }

    public void initHand() {
        for (int i = 0; i < GameConstants.INITIAL_HAND_SIZE; i++) {
            hand.add(deck.pop());
        }
    }

    public void sendHand() throws IOException {
        /*
        int hand_size = hand.size();
        for (int i = 0; i < hand_size; i++) {
            out.writeObject(hand.get(i));
        }
        */
        for (Card card : hand) {
            out.writeObject(card);
        }
    }

    public void sendMessage(Messages msg) throws IOException {
        out.writeObject(msg);
    }

    public boolean waitForInitHandApproval() throws IOException {
        return in.readBoolean();
    }

    public boolean canPlay() {
        if (passed) return false;
        else if (hand.isEmpty()) {
            passed = true;
            return false;
        }
        else return true;
    }

    public int getLives() {
        return n_lives;
    }

    public void subLife() {
        --n_lives;
    }

    public int getTotal() {
        return 0;
    }

    public int readCardIndex() throws IOException {
        return in.readInt();
    }

    public Card playCard(int index) {
        return hand.remove(index);
    }

    public void discardBoard() {
        int i;
        for (i = 0; i < cc.size(); i++) {
            discard.push(cc.remove(i));
        }
        for (i = 0; i < ranged.size(); i++) {
            discard.push(cc.remove(i));
        }
        for (i = 0; i < siege.size(); i++) {
            discard.push(cc.remove(i));
        }
    }

    public void close() throws IOException {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            throw new IOException("Failed to close the input and/or output stream for a Player.\nException: " + e.getMessage());
        }
    }

    public String toString() {
        return client.toString();
    }
}