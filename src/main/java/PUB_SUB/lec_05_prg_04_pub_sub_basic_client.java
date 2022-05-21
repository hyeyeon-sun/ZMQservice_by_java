package PUB_SUB;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class lec_05_prg_04_pub_sub_basic_client {
    public static void main(String Args[]){
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.SUB);

        System.out.println("Collecting updates from seather server...");
        socket.connect("tcp://localhost:5555");

        String zip_filter;

        if (Args.length>0){
            //접두어를 기준으로 필터링 하므로, 다섯자리 미만의 숫자도 제대로 받아오도록 공백을 붙임
            // ex) 50을 입력했을 때 505, 50232 등을 필터링 하지 않도록 함
            zip_filter = Args[0] + " ";
        }else{
            zip_filter = "10001";
        }
        //subscribe socket에 필터를 거는 부분
        socket.subscribe(zip_filter.getBytes());

        int total_temp = 0;
        for (int update_nbr = 0; update_nbr<20; update_nbr++){
            String string = socket.recvStr();
            String[] info = string.split(" ");
            String zipcode = info[0];
            String temperature = info[1];
            String relhumidity = info[2];
            total_temp += Integer.parseInt(temperature);

            System.out.println("Receive temperature for zipcode " + zip_filter + " was " + zipcode);
        }

        System.out.println("Average temperature for zipcode " + zip_filter + " was " + total_temp/20);

    }
}
