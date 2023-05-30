package com.innogrid.uniq.apiopenstack.controller;

import com.innogrid.uniq.core.util.AES256Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;

import static javax.ws.rs.HttpMethod.POST;

/**
 * Created by ksh on 23. 5. 25.
 */
@Controller
@RequestMapping("/infra/cloudServices/Rexgen")
public class RexgenController {
    private static Logger logger = LoggerFactory.getLogger(RexgenController.class);

    @Autowired
    private AES256Util aes256Util;

    String rexgenServerUrl = "http://101.79.1.113:8086/api/v2/query?orgID=fecf3660a510e8c2&bucket=rexgen";

    // cpu 사용량 => 현재 usage_idle 로 설정되어있어 보완 필요
    @RequestMapping(value = {"/monitoring/cpu_usage"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getCpuUsageMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = rexgenServerUrl;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"rexgen\") |> range(start: - "+ Time_range +") |> filter(fn: (r) => " +
                    "r[\"_measurement\"] == \"win_cpu\") |> filter(fn: (r) => r[\"host\"] == \"DC637Q53\") |> filter(fn: (r) => r[\"instance\"] == \"_Total\")" +
                    "|> filter(fn: (r) => r[\"_field\"] == \"Percent_User_Time\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);
        System.out.println("length1 = " + result.split("DC637Q53").length);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("DC637Q53").length-1; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 1]);
            data.put("host", "DC637Q53");
            data.put("start", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 6]);
            data.put("end", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 5]);
            data.put("now", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 3]);
            array.add(data);
        }
        logger.error("array : {} ", array);
        return array;
    }

    // memory 사용량
    @RequestMapping(value = {"/monitoring/mem_usage"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getMemUsageMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = rexgenServerUrl;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"rexgen\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"win_mem\") |> " +
                    "filter(fn: (r) => r[\"_field\"] == \"Standby_Cache_Normal_Priority_Bytes\") |> filter(fn: (r) => r[\"host\"] == \"DC637Q53\") |> " +
                    "filter(fn: (r) => r[\"objectname\"] == \"Memory\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("DC637Q53").length-1; i++) {
            JSONObject data = new JSONObject();
            
            data.put("name", "");
            data.put("host", "DC637Q53");
            data.put("start", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 6]);
            data.put("end", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 5]);
            data.put("now", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 3]);
            array.add(data);
        }
        return array;
    }

    // memory 총량
    @RequestMapping(value = {"/monitoring/mem_total"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getMemTotalMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = rexgenServerUrl;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");

        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"rexgen\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"win_mem\") |> " +
                    "filter(fn: (r) => r[\"_field\"] == \"Available_Bytes\") |> filter(fn: (r) => r[\"host\"] == \"DC637Q53\") |> " +
                    "filter(fn: (r) => r[\"objectname\"] == \"Memory\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("mem_total GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("DC637Q53").length-1; i++) {
            JSONObject data = new JSONObject();

            data.put("name", "");
            data.put("host", "DC637Q53");
            data.put("start", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 6]);
            data.put("end", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 5]);
            data.put("now", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 3]);
            array.add(data);
        }
        return array;
    }

    // disk 사용량
    @RequestMapping(value = {"/monitoring/disk_usage"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getDiskUsageMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = rexgenServerUrl;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"rexgen\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"win_disk\") |> " +
                    "filter(fn: (r) => r[\"_field\"] == \"Percent_Disk_Time\") |> filter(fn: (r) => r[\"host\"] == \"DC637Q53\") |> " +
                    "filter(fn: (r) => r[\"instance\"] == \"C:\") |> " +
                    "filter(fn: (r) => r[\"objectname\"] == \"LogicalDisk\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("DISK GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("DC637Q53").length-1; i++) {
            JSONObject data = new JSONObject();

            data.put("host", "DC637Q53");
            data.put("device", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 10]);
            data.put("start", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 5]);
            data.put("end", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 6]);
            data.put("now", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 3]);

            array.add(data);
        }
        return array;
    }

    // disk 총량
    @RequestMapping(value = {"/monitoring/disk_total"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getDiskTotalMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = rexgenServerUrl;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"rexgen\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"win_disk\") |> " +
                    "filter(fn: (r) => r[\"_field\"] == \"Percent_Free_Space\") |> filter(fn: (r) => r[\"host\"] == \"DC637Q53\") |> " +
                    "filter(fn: (r) => r[\"instance\"] == \"C:\") |> " +
                    "filter(fn: (r) => r[\"objectname\"] == \"LogicalDisk\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("disk_total GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("DC637Q53").length-1; i++) {
            JSONObject data = new JSONObject();

            data.put("host", "DC637Q53");
            data.put("device", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 10]);
            data.put("start", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 5]);
            data.put("end", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 6]);
            data.put("now", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 3]);

            array.add(data);
        }
        return array;
    }

    // diskio writes_bytes 정보
    @RequestMapping(value = {"/monitoring/diskio_wb"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getDiskIoWriteMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");

        HttpUtils htppUtils = new HttpUtils();

        String url = rexgenServerUrl;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"rexgen\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"win_disk\") |> " +
                    "filter(fn: (r) => r[\"_field\"] == \"Percent_Disk_Write_Time\") |> filter(fn: (r) => r[\"host\"] == \"DC637Q53\") |> " +
                    "filter(fn: (r) => r[\"instance\"] == \"C:\") |> " +
                    "filter(fn: (r) => r[\"objectname\"] == \"LogicalDisk\") |> yield(name: \"mean\")";
            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("DC637Q53").length-1; i++) {
            JSONObject data = new JSONObject();

            data.put("host", "DC637Q53");
            data.put("device", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 4]);
            data.put("start", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 10]);
            data.put("end", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 9]);
            data.put("now", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 8]);
            data.put("value", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 7]);

            array.add(data);
        }
        return array;
    }

    @RequestMapping(value = {"/monitoring/diskio_rb"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getDiskIoReadMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model, CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
        response.setHeader("Access-Control-Allow-Origin", "*");

        HttpUtils htppUtils = new HttpUtils();

        String url = rexgenServerUrl;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"rexgen\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"win_disk\") |> " +
                    "filter(fn: (r) => r[\"_field\"] == \"Percent_Disk_Read_Time\") |> filter(fn: (r) => r[\"host\"] == \"DC637Q53\") |> " +
                    "filter(fn: (r) => r[\"instance\"] == \"C:\") |> " +
                    "filter(fn: (r) => r[\"objectname\"] == \"LogicalDisk\") |> yield(name: \"mean\")";
            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("DC637Q53").length-1; i++) {
            JSONObject data = new JSONObject();
            data.put("host", "DC637Q53");
            data.put("device", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 4]);
            data.put("start", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 10]);
            data.put("end", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 9]);
            data.put("now", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 8]);
            data.put("value", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 7]);

            array.add(data);
        }
        return array;
    }

    @RequestMapping(value = {"/monitoring/system_up_time"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getCpuCoreMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = rexgenServerUrl;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"rexgen\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => " +
                    "r[\"_measurement\"] == \"win_system\") |> filter(fn: (r) => r[\"_field\"] == \"System_Up_Time\") " +
                    "|> filter(fn: (r) => r[\"host\"] == \"DC637Q53\") |> filter(fn: (r) => r[\"objectname\"] == \"System\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("system_up_time GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("DC637Q53").length-1; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 1]);
            data.put("host", "DC637Q53");
            data.put("start", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 6]);
            data.put("end", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 5]);
            data.put("now", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("DC637Q53")[i]).split(",")[(result.split("DC637Q53")[i]).split(",").length - 3]);
            array.add(data);
        }
        return array;
    }

    class HttpUtils {
        public HttpURLConnection getHttpURLConnection(String strUrl, String method) {
            URL url;
            HttpURLConnection conn = null;
            try {
                url = new URL(strUrl);

                conn = (HttpURLConnection) url.openConnection(); //HttpURLConnection 객체 생성
                conn.setRequestMethod(POST); //Method 방식 설정. GET/POST/DELETE/PUT/HEAD/OPTIONS/TRACE
                conn.setConnectTimeout(5000); //연결제한 시간 설정. 5초 간 연결시도
                conn.setRequestProperty("Content-Type", "application/vnd.flux");
                conn.setRequestProperty("Authorization", "Token q7yGkB-dz4MkDY-AMKSnOL2blfuDssrk7WRPwRZB0igyqSVQmwGSRvT8J_ueX9sYXSzo5qYsiYNF5sVIJNK9CA==");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }

            return conn;

        }

        public String getHttpRespons(HttpURLConnection conn) {
            StringBuilder sb = null;

            try {
                if(conn.getResponseCode() == 200) {
                    // 정상적으로 데이터를 받았을 경우
                    //데이터 가져오기
                    System.out.println(conn.getResponseCode());
                    sb = readResopnseData(conn.getInputStream());
                    System.out.println(sb);
                }else{
                    // 정상적으로 데이터를 받지 못했을 경우

                    //오류코드, 오류 메시지 표출
                    System.out.println(conn.getResponseCode());
                    System.out.println(conn.getResponseMessage());
                    //오류정보 가져오기
                    sb = readResopnseData(conn.getErrorStream());
                    System.out.println("error : " + sb.toString());
                    return null;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally {
                conn.disconnect(); //연결 해제
            };
            if(sb == null) return null;

            return sb.toString();
        }

        public StringBuilder readResopnseData(InputStream in) {
            if(in == null ) return null;

            StringBuilder sb = new StringBuilder();
            String line = "";

            try (InputStreamReader ir = new InputStreamReader(in);
                 BufferedReader br = new BufferedReader(ir)){
                while( (line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return sb;
        }
    }
}