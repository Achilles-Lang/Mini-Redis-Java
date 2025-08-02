package cn.tedu.Achilles;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * @author Achilles
 * 构建基础TCP服务器
 */
public class Main {
    public static void main(String[] args) {
        //1.初始化ServerSocket，让它监听Redis的默认窗口6379
        try(ServerSocket serverSocket = new ServerSocket(6379)) {
            System.out.println("Mini-Redis server started on port: 6379" );
            //2.循环监听，持续等待客户端连接
            while (true) {
                Socket clientSocket = serverSocket.accept();

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: "+e.getMessage());
        }
        //3.接受连接
        //4.处理连接
    }
}