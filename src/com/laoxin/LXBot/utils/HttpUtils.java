package com.laoxin.LXBot.utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laoxin.LXBot.bean.HttpRespBean;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class HttpUtils {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    private HttpUtils(){}
    /**
     * http请求工具
     * @param opts 包含请求地址,请求方法,请求头,请求体的对象
     * @return HttpRespBean对象，包含响应头，状态码，响应数据
     */
    public static HttpRespBean httpConn(HttpOpts opts) {



        // 记录信息
        StringBuffer buffer = new StringBuffer();
        HttpURLConnection conn = null;
        HttpRespBean httpResp = null;
        PrintWriter out = null;
        try {
            URL url = new URL(opts.getRequestURL());
            conn = (HttpURLConnection) url.openConnection();
            // 设置请求的属性
            conn.setDoOutput(true); // 是否可以输出
            conn.setRequestMethod(opts.getMethod()); // 请求方式, 只包含"GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"六种
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(60000); // 最高超时时间
            conn.setReadTimeout(60000); // 最高读取时间
            conn.setConnectTimeout(60000); // 最高连接时间

            // 设置请求头
            Map<String, String> requestHeader = opts.getRequestHeader();
            if (requestHeader != null) {
                if (opts.getMethod().equalsIgnoreCase("get")) {
                    requestHeader.remove("Content-Type");
                    requestHeader.remove("Content-Length");

                }
                if (requestHeader.get("Content-Type") == null && !opts.getMethod().equalsIgnoreCase("get")) {
                    conn.setRequestProperty("Content-Type", "application/application/x-www-form-urlencoded");
                }
                for (Map.Entry<String, String> stringObjectEntry : requestHeader.entrySet()) {
                    conn.setRequestProperty(stringObjectEntry.getKey(), stringObjectEntry.getValue());
                }
            }


            // 设置请求体
            Map<String, Object> requestParams = opts.getRequestParams();
            String requestData = opts.getRequestData();
            //System.out.println(objectMapper.writeValueAsString(requestParams));
            if (requestParams != null) {
                out = new PrintWriter(conn.getOutputStream());
                // 发送请求参数
                // 解决服务端接受数据问题
                out.println(objectMapper.writeValueAsString(requestParams));
                // flush输出流的缓冲
                out.flush();
                out.close();
            } else if (requestData != null){
                out = new PrintWriter(conn.getOutputStream());
                out.println(requestData);
                out.flush();
                out.close();
            }
            if (conn != null) {
                httpResp = new HttpRespBean();
                httpResp.setResponseHeader(conn.getHeaderFields()); // 封装响应头
                httpResp.setResponseCode(conn.getResponseCode()); // 封装响应代码
                InputStream inputStream = null;
                //根据responseCode来获取输入流，此处错误响应码的响应体内容也要获取（看服务端的返回结果形式决定）
                try {
                    inputStream = conn.getInputStream();
                }catch (IOException e){
                    inputStream = conn.getErrorStream();

                }
/*                try {
                    inputStream = new GZIPInputStream(inputStream);
                } catch (IOException e){}*/
                //inputStream = conn.getInputStream();

                //System.out.println(inputStream);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                    buffer.append("\r\n");
                }
                br.close();
            }
            conn.disconnect();
            httpResp.setData(buffer.toString());

        } catch (Exception e) {
            System.out.println("HTTP 请求错误: ");
            e.printStackTrace();
        }
        return httpResp;
    }
}