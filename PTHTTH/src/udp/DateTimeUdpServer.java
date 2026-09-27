package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUdpServer {
	private static final int PORT = 5004;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP DateTime Server listening on port " + PORT);
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                String cmd = new String(request.getData(), request.getOffset(), request.getLength(), StandardCharsets.UTF_8).trim();
                
                String responseText = process(cmd);
                byte[] responseData = responseText.getBytes(StandardCharsets.UTF_8);
                DatagramPacket response = new DatagramPacket(responseData, responseData.length, request.getAddress(), request.getPort());
                socket.send(response);
            }
        } catch (IOException e) {
            System.err.println("Lỗi UDP Server: " + e.getMessage());
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
