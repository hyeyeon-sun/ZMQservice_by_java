package REQ_RES;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class lec_05_prg_02_req_rep_basic_client {
    public static void main(String args[]){
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.REQ);
        socket.connect("tcp://localhost:5555");

        for (int request = 0; request < 10; request++){
            System.out.println("Sending request " + request + " ...");

            String req = "Hello";

            socket.send(req.getBytes(ZMQ.CHARSET), 0);
            byte[] message = socket.recv();
            String s_message = new String(message);
            System.out.println("Received replay " + request + " [ " + s_message + " ]");
        }
    }
}
