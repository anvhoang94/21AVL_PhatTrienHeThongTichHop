package tcp;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeTcpServer {
	private static final int PORT = 5003;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("TCP DateTime Server listening on port " + PORT);
            while (true) {
                try (Socket socket = server.accept()) {
                    serve(socket);
                } catch (IOException e) {
                    System.err.println("Lỗi client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi server: " + e.getMessage());
        }
    }

    static void serve(Socket socket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String request;
            while ((request = in.readLine()) != null) {
                String trimmed = request.trim();
                if (trimmed.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }
                out.println(process(trimmed));
            }
        }
    }

    static String process(String cmd) {
        LocalDateTime now = LocalDateTime.now();
        if (cmd.equalsIgnoreCase("DATE")) return "OK " + now.format(DATE_FMT);
        if (cmd.equalsIgnoreCase("TIME")) return "OK " + now.format(TIME_FMT);
        if (cmd.equalsIgnoreCase("DATETIME")) return "OK " + now.format(DATETIME_FMT);
        return "ERR UNKNOWN_COMMAND";
    }
}
