package P2P;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class lec_05_prg_12_p2p_dechat {

    public static String search_nameserver(String ip_mask, String local_ip_addr, int port_nameserver) {
        ZContext context = new ZContext();
        ZMQ.Socket req = context.createSocket(SocketType.SUB);
        for (int last = 1; last < 255; last++) {
            String target_ip_addr = "tcp://" + ip_mask + "."+last+ ":" + port_nameserver;
            if (target_ip_addr != local_ip_addr || target_ip_addr == local_ip_addr) {
                req.connect(target_ip_addr);
            }
            req.setReceiveTimeOut(2000);
            req.subscribe("NAMESERVER".getBytes());
        }

        try {
            String res = req.recvStr();
            String[] res_list = res.split(":");
            if (res_list[0] == "NAMESERVER") {
                return res_list[1];
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static void beacon_nameserver(String local_ip_addr, String port_nameserver) {
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.PUB);
        socket.bind("tcp://" + local_ip_addr + ":" + port_nameserver);
        System.out.println("local p2p name server bind to tcp://" + local_ip_addr + ":" + port_nameserver + ".");
        while (true) {
            try {
                Thread.sleep(1000);
                String msg = "NAMESERVER:" + local_ip_addr;
                socket.send(msg);
            } catch (Exception e) {
                break;
            }
        }
    }

    public static void user_manager_nameserver(String local_ip_addr, String port_subscribe) {
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

    public static void relay_server_nameserver(String local_ip_addr, String port_chat_publisher, String port_chat_collector) {
        ZContext context = new ZContext();
        ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
        publisher.bind("tcp://" + local_ip_addr + ":" + port_chat_publisher);
        ZMQ.Socket collector = context.createSocket(SocketType.PULL);
        collector.bind("tcp://"+local_ip_addr+":"+port_chat_collector);
        System.out.println("local p2p relay server activated at tcp://" + local_ip_addr + ":" + port_chat_publisher + " & " + port_chat_collector + ".");

        while (true) {
            try {
                String message = collector.recvStr();
                System.out.println("p2p-relay:<==>" + message);
                publisher.send("RELAY:" + message);
            } catch (Exception e) {
                break;
            }
        }
    }

    public static String get_local_ip() {
        InetAddress local;
        try {
            local = InetAddress.getLocalHost();
            String ip = local.getHostAddress();
            return ip;
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
            return "127.0.0.1";
        }
    }
}
