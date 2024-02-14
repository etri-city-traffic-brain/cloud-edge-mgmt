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
@RequestMapping("/infra/cloudServices/VM2")
public class EtriVM2Controller {
    private static Logger logger = LoggerFactory.getLogger(EtriVM2Controller.class);

    @Autowired
    private AES256Util aes256Util;

    String openstackVm2Url = "http://101.79.1.113:8086/api/v2/query?orgID=fecf3660a510e8c2&bucket=innogrid_vm2";

    // cpu 사용량 => 현재 usage_idle 로 설정되어있어 보완 필요
    @RequestMapping(value = {"/monitoring/cpu_usage"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getCpuUsageMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = openstackVm2Url;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm2\") |> range(start: - "+ Time_range +") |> filter(fn: (r) => " +
                    "r[\"_measurement\"] == \"cpu\") |> filter(fn: (r) => r[\"cpu\"] == \"cpu-total\") " +
                    "|> filter(fn: (r) => r[\"_field\"] == \"usage_system\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET VM2 = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("innogrid-test2").length; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 1]);
            data.put("host", "innogrid-test2");
            data.put("start", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 7]);
            data.put("end", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 6]);
            data.put("now", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 5]);
            data.put("value", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 4]);
            array.add(data);
        }
        logger.error("array : {} ", array);
        return array;
    }

    @RequestMapping(value = {"/monitoring/system_up_time"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getSystemTimeMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = openstackVm2Url;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm2\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => " +
                    "r[\"_measurement\"] == \"system\") |> filter(fn: (r) => r[\"_field\"] == \"uptime\") " +
                    "|> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("system_up_time GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("innogrid-test2").length-1; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 1]);
            data.put("host", "innogrid-test2");
            data.put("start", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 6]);
            data.put("end", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 5]);
            data.put("now", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 3]);
            array.add(data);
        }
        return array;
    }

    // memory 사용량
    @RequestMapping(value = {"/monitoring/mem_usage"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getMemUsageMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = openstackVm2Url;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm2\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"mem\") |> filter(fn: (r) => r[\"_field\"] == \"used_percent\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET Mem Usage= " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("innogrid-test2").length; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "");
            data.put("host", "innogrid-test2");
            data.put("start", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 6]);
            data.put("end", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 5]);
            data.put("now", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 3]);
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

        String url = openstackVm2Url;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");

        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm2\") |> range(start: -" + Time_range + ")|> filter(fn: (r) => r[\"_measurement\"] == \"mem\") |> filter(fn: (r) => r[\"_field\"] == \"total\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET Mem Total= " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("innogrid-test2").length; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "");
            data.put("host", "innogrid-test2");
            data.put("start", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 6]);
            data.put("end", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 5]);
            data.put("now", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 3]);
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

        String url = openstackVm2Url;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm2\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"disk\") |> filter(fn: (r) => r[\"_field\"] == \"used_percent\") |> filter(fn: (r) => r[\"device\"] == \"vda1\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET Disk Usgae= " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("/").length; i++) {
            JSONObject data = new JSONObject();
            data.put("host", "innogrid-test2");
            data.put("device", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 4]);
            data.put("start", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 10]);
            data.put("end", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 9]);
            data.put("now", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 8]);
            data.put("value", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 7]);

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

        String url = openstackVm2Url;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm2\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"disk\") |> filter(fn: (r) => r[\"_field\"] == \"total\") |> filter(fn: (r) => r[\"device\"] == \"vda1\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET Disk Total = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("/").length; i++) {
            JSONObject data = new JSONObject();
            data.put("host", "innogrid-test2");
            data.put("device", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 4]);
            data.put("start", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 10]);
            data.put("end", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 9]);
            data.put("now", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 8]);
            data.put("value", "" + (result.split("/")[i]).split(",")[(result.split("/")[i]).split(",").length - 7]);

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

        String url = openstackVm2Url;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm2\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"diskio\") |> filter(fn: (r) => r[\"_field\"] == \"write_bytes\") |> filter(fn: (r) => r[\"host\"] == \"innogrid-test2\") |> filter(fn: (r) => r[\"name\"] == \"vda\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("vda").length; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "vda");
            data.put("host", "" + (result.split("vda")[i]).split(",")[(result.split("vda")[i]).split(",").length - 1]);
            data.put("start", "" + (result.split("vda")[i]).split(",")[(result.split("vda")[i]).split(",").length - 7]);
            data.put("end", "" + (result.split("vda")[i]).split(",")[(result.split("vda")[i]).split(",").length - 6]);
            data.put("now", "" + (result.split("vda")[i]).split(",")[(result.split("vda")[i]).split(",").length - 5]);
            data.put("value", "" + (result.split("vda")[i]).split(",")[(result.split("vda")[i]).split(",").length - 4]);

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

        String url = openstackVm2Url;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid\") |> range(start: " + Time_range + ") |> filter(fn: (r) => r[\"_measurement\"] == \"diskio\") |> filter(fn: (r) => r[\"_field\"] == \"read_bytes\") |> filter(fn: (r) => r[\"host\"] == \"innogrid-test2\") |> filter(fn: (r) => r[\"name\"] == \"sda\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("sda").length; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "sda");
            data.put("host", "" + (result.split("sda")[i]).split(",")[(result.split("sda")[i]).split(",").length - 1]);
            data.put("start", "" + (result.split("sda")[i]).split(",")[(result.split("sda")[i]).split(",").length - 7]);
            data.put("end", "" + (result.split("sda")[i]).split(",")[(result.split("sda")[i]).split(",").length - 6]);
            data.put("now", "" + (result.split("sda")[i]).split(",")[(result.split("sda")[i]).split(",").length - 5]);
            data.put("value", "" + (result.split("sda")[i]).split(",")[(result.split("sda")[i]).split(",").length - 4]);

            array.add(data);
        }
        return array;
    }

    @RequestMapping(value = {"/monitoring/cpu_core"}, method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getCpuCoreMonitoringData(HttpServletRequest request, HttpServletResponse response, Principal principal, HttpSession session, Model model) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        HttpUtils htppUtils = new HttpUtils();

        String url = openstackVm2Url;
        String method = "POST";
        String result = "";
        String Time_range = request.getParameter("range") == null ? "5m" : request.getParameter("range");
        HttpURLConnection conn = null;

        //HttpURLConnection 객체 생성
        conn = htppUtils.getHttpURLConnection(url, method);
//        BufferedOutputStream dataOutputStream = new BufferedOutputStream(conn.getOutputStream());
        conn.setDoOutput(true);
        try (DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());){
            String str = "from(bucket: \"innogrid_vm2\") |> range(start: -" + Time_range + ") |> filter(fn: (r) => " +
                    "r[\"_measurement\"] == \"system\") |> filter(fn: (r) => r[\"_field\"] == \"n_cpus\") " +
                    "|> filter(fn: (r) => r[\"host\"] == \"innogrid-test2\") |> yield(name: \"mean\")";

            dataOutputStream.write(str.getBytes());
            dataOutputStream.flush();

            result = htppUtils.getHttpRespons(conn);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET = " + result);

        JSONArray array = new JSONArray();

        for(int i=0; i< result.split("innogrid-test2").length; i++) {
            JSONObject data = new JSONObject();
            data.put("name", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 1]);
            data.put("host", "innogrid-test2");
            data.put("start", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 6]);
            data.put("end", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 5]);
            data.put("now", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 4]);
            data.put("value", "" + (result.split("innogrid-test2")[i]).split(",")[(result.split("innogrid-test2")[i]).split(",").length - 3]);
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