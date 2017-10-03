import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

class Client {
    public static void main(String args[]) throws Exception
    {
        String serverAddress = (String) args[0];
        int n_port = Integer.parseInt(args[1]);
        int req_code = Integer.parseInt(args[2]);
        String msg = (String) args[3];
        String reservedSentence;
        String ack_msg;

        //create UDP socket
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(serverAddress);
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

        //send request code
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(req_code);
        sendData = b.array();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, n_port);
        clientSocket.send(sendPacket);

        //receive r_port
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        int r_port = ByteBuffer.wrap(receivePacket.getData()).getInt();
        System.out.println("FROM SERVER r_port: " + r_port + '\n');

        //send r_port
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(r_port);
        sendData = bb.array();
        DatagramPacket sendPacket_2 = new DatagramPacket(sendData, sendData.length, IPAddress, n_port);
        clientSocket.send(sendPacket_2);
        System.out.println("Send SERVER r_port: " + r_port + '\n');

        //receive r_port acknowledge
        DatagramPacket receivePacket_2 = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket_2);
        ack_msg = new String(receivePacket_2.getData()).substring(0, 2);
        System.out.println("SERVER_ACK_MSG=" + ack_msg + '\n');
        if(ack_msg.equals( "ok")) {
            //close UDP socket
            clientSocket.close();
            System.out.println("UDP close" + '\n');

            //create TCP socket
            Socket clientSocket_2 = new Socket(IPAddress, r_port);
            DataOutputStream outToServer = new DataOutputStream(clientSocket_2.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket_2.getInputStream()));

            System.out.println("CLIENT_MSG=" + msg + '\n');
            outToServer.writeBytes(msg + '\n');

            reservedSentence = inFromServer.readLine();
            System.out.println("SERVER_RCV_MSG=" + reservedSentence + '\n');
            clientSocket.close();
        } else {
            //close UDP socket
            clientSocket.close();
            System.out.println("UDP close " + '\n');
        }
    }
}