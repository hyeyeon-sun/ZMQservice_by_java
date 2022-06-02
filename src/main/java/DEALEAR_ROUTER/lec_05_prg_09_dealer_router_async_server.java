package DEALEAR_ROUTER;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class lec_05_prg_09_dealer_router_async_server extends Thread {

    @Override
    public void run() {
        ZContext context = new ZContext;
        ZMQ.Socket frontend = context.createSocket(SocketType.ROUTER);
        frontend.bind("tcp://localhost:5557");
        ZMQ.Socket backend = context.createSocket(SocketType.DEALER);
        backend.bind("inproc://backend");

        //worker type
        for(int i = 0; i<num_server; i++){

        }
    }
}

class ServerWorker extends Thread {
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
            //recv multipart가 어떤건지 알아보기
        }
    }
}

