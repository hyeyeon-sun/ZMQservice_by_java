package DEALEAR_ROUTER;

public class server_test {
    public static void main (String args[]){
        ServerTask server = new ServerTask(Integer.parseInt(args[0]));
        server.start();
        //server.join();자꾸 이 줄이 있으면 에러가 발생함
    }
}
