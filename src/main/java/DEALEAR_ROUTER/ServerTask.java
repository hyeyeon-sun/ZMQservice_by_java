package DEALEAR_ROUTER;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;


public class ServerTask extends Thread {
    int num_server;

    public ServerTask(int num_server){
        super();
        this.num_server = num_server;
    }

    @Override
    public void run() {
        ZContext context = new ZContext();
        ZMQ.Socket frontend = context.createSocket(SocketType.ROUTER);
        frontend.bind("tcp://localhost:5557");

        ZMQ.Socket backend = context.createSocket(SocketType.DEALER);
        backend.bind("inproc://backend");

        ArrayList<ServerWorker> workers = new ArrayList<ServerWorker>();
        for(int i = 0; i<this.num_server; i++){
            ServerWorker worker = new ServerWorker(context, i);
            worker.start();
            workers.add(worker);
        }
        ZMQ.proxy(frontend,backend, null);
        frontend.close();
        backend.close();
        context.destroy();
    }

    public static void main(String args[]){
        ServerTask server = new ServerTask(Integer.parseInt(args[0]));
        server.start();
        //server.join();자꾸 이 줄이 있으면 에러가 발생함
    }
}
