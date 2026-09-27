package network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.URI;
import java.net.URISyntaxException;

public class HostAndUriInspector {
	public static void main(String[] args) {
        // Xử lý thiếu tham số
        if (args.length < 2) {
            System.out.println("Usage: java network.HostAndUriInspector <hostname> <URI>");
            return;
        }

        String hostname = args[0];
        String uriString = args[1];

        System.out.println("=== PHÂN TÍCH HOST ===");
        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            System.out.println("Host: " + hostname);
            for (InetAddress address : addresses) {
                System.out.println("- IP: " + address.getHostAddress());
                if (address instanceof Inet4Address) {
                    System.out.println("  Type: IPv4");
                } else if (address instanceof Inet6Address) {
                    System.out.println("  Type: IPv6");
                }
                System.out.println("  Canonical: " + address.getCanonicalHostName());
                System.out.println("  Loopback: " + address.isLoopbackAddress());
                System.out.println("  Site local: " + address.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            System.err.println("Lỗi: Không phân giải được host -> " + hostname);
        }

        System.out.println("\n=== PHÂN TÍCH URI ===");
        try {
            URI uri = new URI(uriString);
            System.out.println("URI Input: " + uriString);
            System.out.println("- Scheme: " + uri.getScheme());
            System.out.println("- Host: " + uri.getHost());
            System.out.println("- Port: " + (uri.getPort() == -1 ? "Không xác định (mặc định theo scheme)" : uri.getPort()));
            System.out.println("- Path: " + uri.getPath());
            System.out.println("- Query: " + uri.getQuery());
            System.out.println("- Fragment: " + uri.getFragment());
        } catch (URISyntaxException e) {
            System.err.println("Lỗi: Cú pháp URI không hợp lệ -> " + e.getMessage());
        }
    }
}
