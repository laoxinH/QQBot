package com.laoxin.LXBot.botService;

import com.laoxin.LXBot.bean.CqGoBean;
import com.laoxin.LXBot.botService.httpSocket.HttpRequest;
import com.laoxin.LXBot.botService.httpSocket.HttpResponse;
import com.laoxin.LXBot.botService.httpSocket.HttpSocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laoxin.LXBot.CQHandle.CQHandler;
import java.net.Socket;
import java.util.List;
import java.util.LinkedList;


public class RequestProcessor implements Runnable {
    private static List<Socket> pool = new LinkedList<>();
    private String documentRootDirectory;
    private String indexFileName = "index.html";

    public RequestProcessor(String documentRootDirectory, String indexFileName) {
        if (indexFileName != null) {
            this.indexFileName = indexFileName;
        }
        this.documentRootDirectory = documentRootDirectory;
    }

    public static void processRequest(Socket httpSocket) {
        synchronized (pool) {
            pool.add(pool.size(), httpSocket);
            pool.notifyAll();
        }
    }

    @Override
    public void run() {
        while (true) {
            Socket socket;
            synchronized (pool) {
                while (pool.isEmpty()) {
                    try {
                        pool.wait();
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
                socket = pool.remove(0);
            }

            try {
                HttpSocket httpSocket = new HttpSocket(socket, documentRootDirectory, indexFileName);
                HttpRequest request = httpSocket.getRequest();
                HttpResponse response = httpSocket.getResponse();
                /**
                 * 简单的请求分发get
                 */
                if (request.getMethod().equalsIgnoreCase("get")) {
                    try {
                        response.send("我是你爸爸");
                        //response.send(new File(request.getPath()));
                    } catch (Exception e) {
                        System.out.println("发送失败！ " + request.getPath());
                        e.printStackTrace();
                    }
                    /**
                     * post请求(默认处理cq-goHttp上报数据)
                     */
                } else if (request.getMethod().equalsIgnoreCase("post")) {
                    if (request.getHeader().get("User-Agent").toString().contains("CQHttp/4.15.0")){
                        // 封装数据
                        ObjectMapper objectMapper = new ObjectMapper();

                        try {
                            CqGoBean cqGo = objectMapper.readValue(request.getDataStr(), CqGoBean.class);
                            cqGo.setResponse(response);
                            CQHandler.handleMsg(cqGo);
                            //System.out.println(cqGo);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                // TODO :
            }

        }
    }
}