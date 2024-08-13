package ihp;

import ihp.logging.IHPLogger;
import ihp.treading.ThreadContext;

import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static final String VERSION = "alpha-1";

    public static void main(String[] args) {
        IHPLogger.info("Starting iH-Proxy v" + VERSION);
        try (ServerSocket serverSocket = new ServerSocket(25567)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Socket upstreamSocket = new Socket("127.0.0.1", 25565);
                new ThreadContext(clientSocket, upstreamSocket);
            }
        } catch (Throwable t) {
            IHPLogger.exception("An error occurred in the main loop :/", t);
        }
    }
}
