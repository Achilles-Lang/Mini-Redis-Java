package cn.tedu.Achilles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Achilles
 * 处理核心命令
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;


    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream writer = clientSocket.getOutputStream();
        ){
            List<String> command = RESP.parseRequest(reader);
            String commandName = command.get(0).toUpperCase();

            switch (commandName){
                case "PING":
                    writer.write(RESP.encodeSimpleString("PONG"));
                    break;
                case "ECHO":
                    writer.write(RESP.encodeBulkString(command.get(1)));
                    break;
                case "SET":
                    String key = command.get(1);
                    String valueToSet = command.get(2);
                    long expiry = -1; // 默认永不过期

                    if (command.size() > 3 && "PX".equalsIgnoreCase(command.get(3))) {
                        long duration = Long.parseLong(command.get(4));
                        expiry = System.currentTimeMillis() + duration;
                    }

                    Store.DATA.put(key, new Store.ValueWithExpiry(valueToSet, expiry));
                    writer.write(RESP.encodeSimpleString("OK"));
                    break;
                case "GET":
                    String keyToGet = command.get(1);
                    Store.ValueWithExpiry valueWithExpiry = Store.DATA.get(keyToGet);
                    if(valueWithExpiry == null || valueWithExpiry.isExpired()){
                        if(valueWithExpiry!=null){
                            Store.DATA.remove(keyToGet);
                        }
                        writer.write(RESP.encodeBulkString(null));
                    }else {
                        writer.write(RESP.encodeSimpleString(valueWithExpiry.value));
                    }
                    break;
                    default:
                    writer.write(("-ERR unknown command '" + commandName + "'\r\n").getBytes());
            }
            writer.flush();

        } catch (IOException e) {
            System.out.println("Error handing client: " + e.getMessage());
        }finally {
            try {
                clientSocket.close();
            }catch (IOException e){
                //ignore
            }

        }
    }
}
