package com.laoxin.LXBot.botService;

import com.laoxin.LXBot.utils.BotConfigUtils;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class LXBot extends Thread {

    private String documentRootDirectoryPath;
    private String indexFileName="index.html";
    private ServerSocket server;
    private int numThreads=5;

    public LXBot(String documentRootDirectoryPath, int port , String indexFileName, int numThreads)throws IOException {
        File file = new File(documentRootDirectoryPath);
        // 目录检测
        if (file.isDirectory() && file.exists()) {
            this.documentRootDirectoryPath =documentRootDirectoryPath;
            this.indexFileName=indexFileName;
            this.server=new ServerSocket(port);
            this.numThreads = numThreads;
        } else {
            throw new IOException(documentRootDirectoryPath+" 原因: LXBot启动失败, 文件目录不存在！");
        }
    }

    private LXBot(String documentRootDirectory, int port)throws IOException {
        this(documentRootDirectory, port, "index.html",50);
    }

    public void run(){
        for (int i = 0; i < numThreads; i++) {
            Thread t=new Thread(new RequestProcessor(documentRootDirectoryPath, indexFileName));
            t.start();
        }
        System.out.println("当前端口: "
                +server.getLocalPort() +" 当前地址: "+ server.getInetAddress());
        System.out.println("静态文件目录: "+ documentRootDirectoryPath);
        while (true) {
            try {
                Socket request=server.accept();
                RequestProcessor.processRequest(request);
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: handle exception
            }
        }
    }


    /**
     * @param args
     */

    public static void main(String[] args) {

        System.out.println("LXBOT 开始初始化");
        BotConfigUtils config = BotConfigUtils.getBotConfig();

        if (config.getCqgo().getHost() == null || config.getCqgo().getPort()== null) {
            System.out.println("cgqo 未正确配置，请打开BotConfig.yaml按要求填写cqgo配置");
        }


        int port = config.getPort();
            if (port<0||port>65535) {
                port=8080;
            }
        try {
            LXBot webserver=new LXBot(config.getStaticPath(), port, config.getIndexFileName(),config.getMaxConcurrent());
            System.out.println("LXBOT 开始启动");
            webserver.start();
            System.out.println("LXBOT 启动成功");
        } catch (IOException e) {
            System.out.println("Server could not start because of an "+e.getClass());
            System.out.println(e);
        }
    }
}

