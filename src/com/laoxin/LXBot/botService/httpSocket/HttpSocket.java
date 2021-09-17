package com.laoxin.LXBot.botService.httpSocket;

import java.net.Socket;

public class HttpSocket extends Socket {
    private HttpRequest request;
    private HttpResponse response;

    public HttpSocket(Socket socket, String documentRootDirectory,String indexHtml){
        this.request = new HttpRequest(socket,documentRootDirectory,indexHtml);
        this.response = new HttpResponse(socket,this.request);
    }

    public HttpRequest getRequest() {
        return request;
    }

    public HttpResponse getResponse() {
        return response;
    }
}





