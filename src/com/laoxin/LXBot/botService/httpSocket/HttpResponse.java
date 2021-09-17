package com.laoxin.LXBot.botService.httpSocket;

import java.io.*;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HttpResponse extends Socket {

    private Socket socket;
    private HttpRequest request;
    private List<String> respHeader = new ArrayList();
    private OutputStream out;

    public HttpResponse(Socket socket, HttpRequest request) {
        try {
            this.out = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("response 封装失败,原因: " + e.getClass());
            e.printStackTrace();
        }
        this.respHeader.add("HTTP/1.1 200 OK");
        this.respHeader.add("Server: com.laoxin.LXBot.botService.LXBot 1.0");
        this.request = request;
        this.socket = socket;
    }

    /**
     * 发送文本信息
     *
     * @param msg
     */
    public void send(String msg) {
        msg += "\r\n";
        // 防止出现中文解析问题
/*        String str = "";
        for (int i = 0; i < msg.length() * 2; i++) {
            str += " ";
        }*/
        //TODO： json字符串中包含中文时会解析错误（可通过在json字符串中添加空格解决），原因未知
       // msg += str;
        setResponseHeard(request.getHttpVersion(), msg.getBytes().length, "json");
        //System.out.println(msg);
        try {
            out.write(msg.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送html
     *
     * @param file
     * @throws FileNotFoundException
     */
    public void send(File file) throws IOException {
        if (file.exists() && file.isFile()) {
            setResponseHeard(request.getHttpVersion(), file.length(), file.getName());
            //System.out.println(respHeader);
            FileInputStream fs = new FileInputStream(file);
            byte[] cbuf = new byte[1024 * 100];
            while (fs.read(cbuf) != -1) {
                out.write(cbuf);
            }
            out.flush();
            out.close();
            socket.close();
            //System.out.println("send 完成");
        } else {
            setStatue(404, "file not found");
            setResponseHeard(request.getHttpVersion(), 0, "");
            out.flush();
            out.close();
            socket.close();
            throw new FileNotFoundException("找不到该文件");
        }
    }

    /**
     * 设置响应状态码
     *
     * @param statue
     */
    public void setStatue(int statue, String msg) {
        respHeader.remove(0);
        respHeader.add(0, "HTTP/1.1 " + statue + " " + msg);
    }


    /**
     * 设置响应头
     *
     * @param version
     * @param len
     * @throws IOException
     */
    private void setResponseHeard(String version, long len, String type) {
        if (version.contains("HTTP")) {
            Date now = new Date();
            String contentType;
            contentType = guessContentTypeFromName(type);
            if (request.getMethod().equalsIgnoreCase("post")) {
                contentType = guessContentTypeFromName("json");

            }
            respHeader.add("Content-length: " + len);
            respHeader.add("Content-Type: " + contentType);
            respHeader.add("Date: " + now);
        }
        try {
            for (String header : respHeader) {
                out.write((header + "\r\n").getBytes("UTF-8"));
            }
            out.write("\r\n".getBytes("utf-8"));

        }catch (IOException e){
            // TODO 处理异常
        }

    }

    /**
     * 生成Content-Type
     *
     * @param type
     * @return
     */

    private String guessContentTypeFromName(String type) {
        //System.out.println(name);
        if (type.endsWith(".html") || type.endsWith(".htm")) {
            return "text/html;charset=utf-8";
        } else if (type.endsWith(".txt") || type.endsWith(".java")) {
            return "text/plain;charset=utf-8";
        } else if (type.endsWith(".gif")) {
            return "image/gif";
        } else if (type.endsWith(".class")) {
            return "application/octet-stream";
        } else if (type.endsWith(".jpg") || type.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (type.equalsIgnoreCase("json")) {
            return "application/json; charset=utf-8";
        } else {
            return "text/plain;charset=utf-8";
        }
    }
}
