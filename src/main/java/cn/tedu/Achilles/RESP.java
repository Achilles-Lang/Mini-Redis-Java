package cn.tedu.Achilles;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Achilles
 * 实现RESP协议
 */
public class RESP {
    //解析来自客户端的命令
    public static List<String> parseRequest(BufferedReader reader) throws IOException {
        //读取数组的长度，例如*2
        String line=reader.readLine();
        if(line==null || !line.startsWith("*")){
            throw new IOException("无效的 RESP 请求：非预期数组");
        }
        int arrayLength=Integer.parseInt(line.substring(1));
        List<String> commandParts=new ArrayList<>();

        for(int i=0;i<arrayLength;i++){
            //读取每个Bulk String的长度，例如$4
            line=reader.readLine();
            if(line==null || !line.startsWith("$")){
                throw new IOException("Invalid RESP request: expected bulk string length");
            }
            int bulkStringLength=Integer.parseInt(line.substring(1));
            //读取Bulk String的内容
            char[] buffer=new char[bulkStringLength];
            reader.read(buffer,0,bulkStringLength);
            commandParts.add(new String(String.valueOf(buffer)));

            //读取结尾的CRLF
            reader.readLine();
        }
        return commandParts;
    }

    //将Simple String编码为RESP格式
    public static byte[] encodeSimpleString(String str){
        return ("+"+str+"\r\n").getBytes();
    }

    // 将Bulk String编码为RESP格式
    public static byte[] encodeBulkString(String str) {
        if (str == null) {
            // Null Bulk String
            return "$-1\r\n".getBytes();
        }
        return ("$" + str.length() + "\r\n" + str + "\r\n").getBytes();
    }
}
