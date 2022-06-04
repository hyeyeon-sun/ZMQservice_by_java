package DEALEAR_ROUTER;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZPoller;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ClientTask extends Thread{
    int id;

    public ClientTask(int id){
        super();
        this.id = id;
    }

    @Override
    public void run() {
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.DEALER);
        String identity = Integer.toString(this.id);
        socket.setIdentity(identity.getBytes(StandardCharsets.US_ASCII));
        socket.connect("tcp://localhost:5570");
        System.out.println("Client " + identity + " started");
        ZPoller poll = new ZPoller(context);
        poll.register(socket, ZMQ.Poller.POLLIN);
        int reqs = 0;
        while (true) {
            reqs += 1;
            System.out.println("Req #" + Integer.toString(reqs) + " sent..");
            socket.send("request #"+Integer.toString(reqs));
            try{
                Thread.sleep(1000);
            }catch(Exception e){
            }
            int socket_num = poll.poll(1000);
            if(poll.isReadable(socket)){
                byte[] msg = socket.recv();
                String s_message = new String(msg);
                System.out.println(identity + "received: "+s_message);
            }
        }
    }
}
