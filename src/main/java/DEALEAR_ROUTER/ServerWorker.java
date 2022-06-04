package DEALEAR_ROUTER;

import org.zeromq.*;

import java.util.ArrayList;

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
            //receive-multipart 버전
            ZMsg receivedmsg = ZMsg.recvMsg(worker);
            ArrayList<String> info = new ArrayList<String>();

            ZFrame f_ident = receivedmsg.pop();
            ZFrame f_msg = receivedmsg.pop();
            assert (f_msg != null);
            receivedmsg.destroy();

            String ident = new String(f_ident.getData());
            String msg = new String(f_msg.getData());

            System.out.println("Worker#"+this.id+" received "+msg+" from "+ident);
            f_ident.send(worker, ZFrame.REUSE + ZFrame.MORE);
            f_msg.send(worker, ZFrame.REUSE);


            /* 기본 send 버전
            byte[] message = worker.recv();
            String s_message = new String(message);
            System.out.println(s_message);
            //jeromq 에는 multipart를 보내는 메서드가 없기에 split으로 구현
            String[] messages = s_message.split(" ");
            String ident = messages[0];
            String msg = messages[1];
            System.out.println(s_message);
            worker.send(ident + " " + message);
             */
        }
    }
}
