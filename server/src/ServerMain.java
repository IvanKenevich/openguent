public class ServerMain {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java server.ServerMain <port number>");
            System.exit(1);
        }

        Server server = new Server(Integer.parseInt(args[0]));
        server.listen();
    }
}
