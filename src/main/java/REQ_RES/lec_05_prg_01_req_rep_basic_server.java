package REQ_RES;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class lec_05_prg_01_req_rep_basic_server {
    public static void main (String args[]){
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.REP);
        socket.bind("tcp://localhost:5555");

        while(true){

            byte[] message = socket.recv();
            String s_message = new String(message);
            System.out.println("Received request: " + s_message);
            try {
                Thread.sleep(1000);
            } catch(Exception e){

            }
            String response = "World";
            socket.send(response.getBytes(ZMQ.CHARSET), 0);
        }

    }
}
