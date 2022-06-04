package DEALEAR_ROUTER;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ServerWorker extends Thread {
    ZContext context;
    int id;

    public ServerWorker(ZContext context, int id) {
        super();
        this.context = context;
        this.id = id;
    }

    @Override
    public void run() {
        ZMQ.Socket worker = context.createSocket(SocketType.DEALER);
        worker.connect("inproc://backend");
        System.out.println("Worker#" + Integer.toString(this.id) + " started");
        while(true){
            byte[] message = worker.recv();
            String s_message = new String(message);
            System.out.println(s_message);
            //jeromq 에는 multipart를 보내는 메서드가 없기에 split으로 구현
            String[] messages = s_message.split(" ");
            String ident = messages[0];
            String msg = messages[1];
            System.out.println(s_message);
            worker.send(ident + " " + message);
        }
    }
}
