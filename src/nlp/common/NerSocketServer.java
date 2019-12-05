package nlp.common;

/**
 * Created by znt on 2019/8/23.
 */
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class NerSocketServer{

    public static void main(String[] args) throws IOException {
        Segment.init();
        NerRecognition.init();
        ServerSocket server = null;
        try {
            server = new ServerSocket(5000);
            System.out.println("Server start!");
            int count = 0;
            while (true) {
                //处理socket请求
                Socket socket = server.accept();
                NerSocketServerThread nerSocketServerThread = new NerSocketServerThread(socket);
                System.out.println("Client host address is: " + socket.getInetAddress().getHostAddress());
                nerSocketServerThread.start();
                count++;
                System.out.println("now client count is " + count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(server != null);
                server.close();
        }
    }

}
