
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client1 {

    public static void main(String[] args) throws IOException, InterruptedException {

        String myIp = InetAddress.getLocalHost().getHostAddress();

        System.out.println("Trying set source port to 9000");

        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName("stun-server");

        byte[] buf;

        String msg = "BOB:ALICE";

        buf = msg.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 9000);

        System.out.println("Sending packet to port 9000 to address: " + address.getHostAddress());
        socket.send(packet);

        byte[] receiveBuf = new byte[256];

        packet = new DatagramPacket(receiveBuf, receiveBuf.length);

        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());

        System.out.println(received);

        InetAddress peerAddress = InetAddress.getByName(received.split(":")[0]);
        int peerPort = Integer.parseInt(received.split(":")[1]);

        System.out.println("peer address: " + peerAddress.getHostAddress());
        System.out.println("peer port: " + peerPort);

        Thread send = new Client("send", "localhost", peerAddress.getHostAddress(), 6000, peerPort);
        send.start();
        Thread listen = new Client("listen", "localhost", peerAddress.getHostAddress(), 6000, peerPort);
        listen.start();

        listen.join();
        send.join();
    }
}
