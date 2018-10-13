import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server implements AutoCloseable {
    private int port;
    private ServerSocket socket;
    private Stack<Socket> connections;

    public Server (int port) {
        this.port = port;


        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        connections = new Stack<>();
    }

    public void listen() {
        while (true) {
            try {
                connections.push(socket.accept());
                System.out.println("Accepted connection: " + connections.peek());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            if (connections.size() == 2) new GameThread(connections.pop(), connections.pop()).start();
        }
    }


    @Override
    public void close() throws IOException {
        socket.close();
    }
}