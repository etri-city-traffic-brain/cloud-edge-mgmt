package com.innogrid.uniq.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author khk
 * @date 2019.5.31
 * @brief 클라우드 별 이미지 ID 정보를 담는 클래스
 */
@Data
public class ImageInfo implements Serializable {
    private static final long serialVersionUID = -4732500528249202654L;
    private String id;
    private String type;
}
