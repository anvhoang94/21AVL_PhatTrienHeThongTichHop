package LAB4.Bai9;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class MulticastSender {
    private static final String MULTICAST_IP = "239.255.0.1";
    private static final int PORT = 5002;

    public static void main(String[] args) {
        System.out.println("=== MULTICAST SENDER ===");
        System.out.println("Địa chỉ Multicast: " + MULTICAST_IP + " | Port: " + PORT);
        System.out.println("Nhập nội dung thông báo để phát cho tất cả Receiver.");
        System.out.println("Gõ 'SHUTDOWN' để thông báo tất cả Receiver dừng lại.");
        System.out.println("Gõ 'QUIT' để thoát Sender.\n");

        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            String input;

            while ((input = console.readLine()) != null) {
                String trimmed = input.trim();
                if (trimmed.isEmpty()) continue;

                if (trimmed.equalsIgnoreCase("QUIT")) {
                    System.out.println("Thoát Sender.");
                    break;
                }

                byte[] data = trimmed.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(data, data.length, group, PORT);
                socket.send(packet);

                System.out.println("-> Đã phát thông báo: " + trimmed);

                if (trimmed.equalsIgnoreCase("SHUTDOWN")) {
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi Sender: " + e.getMessage());
        }
    }
}
