import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;


class Server {
    public static void main(String args[]) throws Exception
    {
        int req_code = Integer.parseInt(args[0]);
        int n_port = 9787;
        DatagramSocket serverSocket = new DatagramSocket(n_port);
        System.out.println("SERVER_PORT=" + n_port + '\n');
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        String msg;
        String clientSentence;
        String reversedSentence;

        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            receiveData = receivePacket.getData();
            int req_code_client = ByteBuffer.wrap(receiveData).getInt();
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            //verify <req_code>
            if (req_code == req_code_client) {
                //creates TCP socket with <r_port>
                int r_port = 9899;
                ServerSocket welcomeSocket = new ServerSocket(9899);
                System.out.println("SERVER_TCP_PORT=" + r_port + '\n');

                //reply back <r_port>, use UDP
                ByteBuffer b = ByteBuffer.allocate(4);
                b.putInt(r_port);
                sendData = b.array();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);

                //acknowledge client recieve <r_port>
                DatagramPacket receivePacket_2 = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket_2);
                int r_port_client = ByteBuffer.wrap(receivePacket_2.getData()).getInt();
                InetAddress IPAddress_2 = receivePacket_2.getAddress();
                if(r_port == r_port_client){
                    msg = "ok";
                }else{
                    msg = "no";
                }
                sendData = msg.getBytes();
                DatagramPacket sendPacket_2 = new DatagramPacket(sendData, sendData.length, IPAddress_2, port);
                serverSocket.send(sendPacket_2);

                //TCP recieving
                while(true) {
                    Socket connectionSocket = welcomeSocket.accept();
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    clientSentence = inFromClient.readLine();

                    System.out.println("CLIENT_MSG=" + clientSentence + '\n');
                    //reverse the sentence
                    reversedSentence = new StringBuilder(clientSentence).reverse().toString();
                    System.out.println("SERVER_RCV_MSG=" + reversedSentence + '\n');
                    outToClient.writeBytes(reversedSentence + '\n');
                }

            }
        }
    }
}

