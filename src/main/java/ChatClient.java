import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ChatClient {

    //The inputs are like so:
    //[IP address of STUN server] [server port] [username] [peer username] [client port]

    public static void main(String[] args) throws IOException {

        String stunServerIp = args[0];
        int stunServerPort = Integer.parseInt(args[1]);
        String username = args[2];
        String peerUsername = args[3];
        int clientPort = Integer.parseInt(args[4]);

        DatagramSocket socket = new DatagramSocket(clientPort);
        InetAddress address = InetAddress.getByName(stunServerIp);

        byte[] buf;

        String msg = username + ":" + peerUsername;

        buf = msg.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, stunServerPort);

        System.out.println("Sending packet to port " + stunServerPort + " to address: " + address.getHostAddress());
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

        Scanner sc = new Scanner(System.in);

        Thread send = new Thread(new Runnable() {
            String msg;
            @Override
            public void run() {
                boolean end = false;
                while (!end) {
                    msg = sc.nextLine();
                    if (msg.startsWith("end")) {
                        end = true;
                    }
                    byte[] sendMsgBytes = msg.getBytes();
                    DatagramPacket sendPacket;
                    sendPacket = new DatagramPacket(sendMsgBytes, sendMsgBytes.length,  peerAddress, peerPort);
                    try {
                        socket.send(sendPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                socket.close();
            }
        });

        Thread receive = new Thread(new Runnable() {
            String msg;
            @Override
            public void run() {
                do {
                    byte[] receiveMsgBytes = new byte[256];
                    DatagramPacket receivePacket = new DatagramPacket(receiveMsgBytes, receiveMsgBytes.length);
                    try {
                        socket.receive(receivePacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println(msg);
                } while (!msg.startsWith("end"));
            }
        });

        send.start();
        receive.start();
    }
}
