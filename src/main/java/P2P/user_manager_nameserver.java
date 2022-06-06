package P2P;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;

public class user_manager_nameserver implements Runnable{

    String local_ip_addr;
    String port_subscribe;

    public user_manager_nameserver(String local_ip_addr, String port_subscribe) {
        this.local_ip_addr = local_ip_addr;
        this.port_subscribe = port_subscribe;
    }

    @Override
    public void run() {
        ArrayList<String> user_db = new ArrayList<String>();
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.REP);
        socket.bind("tcp://" + local_ip_addr + ":" + port_subscribe);
        System.out.println("local p2p db server activated at tcp://" + local_ip_addr + ":" + port_subscribe + ".");
        while (true) {
            try {
                String[] user_req = socket.recvStr().split(":");
                for (String req : user_req) {
                    user_db.add(req);
                }
                System.out.println("user registration '" + user_req[1] + "' from '" + user_req[0] + "'.");
                socket.send("ok");
            } catch (Exception e) {
                break;
            }
        }
    }
}
