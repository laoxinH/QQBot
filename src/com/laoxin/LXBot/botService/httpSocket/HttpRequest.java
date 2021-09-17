package com.laoxin.LXBot.botService.httpSocket;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class HttpRequest extends Socket {
    private Map header;
    private String method;
    private String path;
    private String httpVersion;
    private String data;

    public HttpRequest(Socket socket, String documentRootDirectory,String indexHtml){

        try {
            //System.out.println("开始封装");
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is,"UTF-8");

            BufferedReader br = new BufferedReader(isr);
            String len;
            List<String> reqList = new ArrayList();
            while ((len=br.readLine())!= "") {
                reqList.add(len);
                /**
                 * 读取完数据
                 */
                if (!br.ready()){
                    break;
                }
            }
            //System.out.println("循环完成" + reqList.toString());
            String str = reqList.remove(0);
            StringTokenizer tokenizer = new StringTokenizer(str);
            this.method = tokenizer.nextToken();
            this.path = tokenizer.nextToken();
            if (!documentRootDirectory.endsWith("/")) {
                documentRootDirectory+="/";
            }
            if (this.path.equalsIgnoreCase("/")) {
                this.path = documentRootDirectory + indexHtml;
            } else{
                this.path = documentRootDirectory + this.path.substring(1);
            }
            // data 封装
            String data = reqList.remove(reqList.size() - 1);
            this.data = data;
            // requestHeader 封装
            this.header = new HashMap<String,String>();
            for (String headerStr : reqList) {
                //System.out.println(headerStr);
                if (!headerStr.isEmpty()) {
                    String[] split = headerStr.split(": ");
                    this.header.put(split[0].trim(),split[1].trim());
                }
            }
            if (tokenizer.hasMoreTokens()) {
                this.httpVersion = tokenizer.nextToken();
            } else {
                this.httpVersion = "";
            }
            //System.out.println("封装完成");
        } catch (Exception e) {
            System.out.println("请求封装失败,原因:" + e.getClass());
            e.printStackTrace();
        }
    }

    public Map getHeader() {
        return header;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getDataStr() {
        return data;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "header=" + header +
                ", method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}

