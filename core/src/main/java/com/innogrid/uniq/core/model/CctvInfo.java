package com.innogrid.uniq.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innogrid.uniq.core.util.JsonDateDeserializer;
import com.innogrid.uniq.core.util.JsonDateSerializer;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class CctvInfo implements Serializable {
    private static final long serialVersionUID = 4705455919358933691L;
    private String cctv_id;
    private String cctv_parent_id;
    private String cctv_cam_nm;
    private String cctv_ip;
    private String cctv_login_id;
    private String cctv_login_pw;
    private String cctv_rtsp_url;
    private int cctv_rtsp_port;
    private int cctv_http_port;
    private String crsrd_id;
    private int lght_use_yn;
    private int connect_svr;
}
