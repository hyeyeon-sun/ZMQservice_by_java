package P2P;

import java.util.StringTokenizer;

public class Test {
    public static void main(String args[]){
        String ip_addr_p2p_server = "";
        int port_nameserver = 9001;
        int port_chat_publisher = 9002;
        int port_chat_collector = 9003;
        int port_subscribe = 9004;

        String user_name = args[0];
        String ip_addr = lec_05_prg_12_p2p_dechat.get_local_ip();
        String ip_mask = ip_addr.substring(0,ip_addr.lastIndexOf("."));

        System.out.println("searching for p2p server.");

        String name_server_ip_addr = lec_05_prg_12_p2p_dechat.search_nameserver(ip_mask, ip_addr, port_nameserver);
        if (name_server_ip_addr == null){
            ip_addr_p2p_server = ip_addr;
            System.out.println(ip_addr);
            System.out.println("p2p server is not found, and p2p server mode is activated.");
            beacon_nameserver beacon_runnable = new beacon_nameserver(ip_addr, Integer.toString(port_nameserver));
            Thread beacon_thread = new Thread(beacon_runnable);
            beacon_thread.start();
            System.out.println("p2p beacon server is activated.");
            user_manager_nameserver db_runnable = new user_manager_nameserver(ip_addr, Integer.toString(port_subscribe));
            Thread db_thread = new Thread(db_runnable);
            db_thread.start();
            System.out.println("p2p subsciber database server is activated.");
            relay_server_nameserver relay_runnable = new relay_server_nameserver(ip_addr, Integer.toString(port_chat_publisher), Integer.toString(port_chat_collector));
            Thread relay_thread = new Thread(relay_runnable);
            relay_thread.start();
            System.out.println("p2p message relay server is activated.");
        }
    }
}
