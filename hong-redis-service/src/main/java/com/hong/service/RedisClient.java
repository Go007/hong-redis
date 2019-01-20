package com.hong.service;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by John on 2019/1/20.
 * 模拟Redis客户端与服务端交互的原理
 */
public class RedisClient {

    Socket connection;

    public RedisClient() throws IOException {
        connection = new Socket("127.0.0.1",6379);
    }

    /**
     * 模拟客户端 set key value操作
     * @param key
     * @param value
     */
    public void set(String key,String value) throws IOException {
        /**
         * 协议约定如下：
         * *参数个数 \r\n
         * $第一个参数长度 \r\n
         * 第一个参数值 \r\n
         * $第N个参数长度 \r\n
         * 第N个参数值 \r\n
         *
         * 补充说明：很多高性能的框架都是自定义协议的，为什么不采用通用的HTTP协议呢？
         * 效率。HTTP协议本身并不是高效的，它包含请求头 请求行 cookie等一大堆数据，
         * 而Redis为了追求高效的读写，采取自定义的简单协议。
         */
        // 1.协议组装,注意一定要getBytes()，否则传输中文会报错，因为网络传输时以字节为单位的
        StringBuilder sb = new StringBuilder();
        sb.append("*3").append("\r\n")
          .append("$3").append("\r\n")
          .append("SET\r\n")
          .append("$").append(key.getBytes().length).append("\r\n")
          .append(key).append("\r\n")
          .append("$").append(value.getBytes().length).append("\r\n")
          .append(value).append("\r\n");

        /**
         *3
         $3
         SET
         $5
         hello
         $5
         redis

         sb的输出将会被记录在aof文件中
         */
        System.out.println(sb);

       //2.将按协议组装好的数据发送给redis server
        connection.getOutputStream().write(sb.toString().getBytes());

        //3.读取redis server的响应
        byte[] response = new byte[1024];
        connection.getInputStream().read(response);
        System.out.println(new String(response));//+OK
    }

    public static void main(String[] args) throws IOException {
        // 推导法--用已知推理未知
        // 网络交互 -socket-BIO/NIO
       /* Socket socket = new Socket("127.0.0.1",6379);
        // 要想与redis server通信，就要遵循redis通信协议RESP
        socket.getOutputStream().write("hello redis\r\n".getBytes());

        byte[] response = new byte[1024];
        socket.getInputStream().read(response);
        System.out.println(new String(response));*/

        RedisClient redisClient = new RedisClient();
        redisClient.set("hello","redis");
    }
}
