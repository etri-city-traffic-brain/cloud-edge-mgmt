package com.innogrid.uniq.coredb.service;

import com.innogrid.uniq.core.model.ImageDetailInfo;
import com.innogrid.uniq.core.model.ImageInfo;

import java.util.List;
import java.util.Map;

public interface ImageService {

    /**
     * @author khk
     * @date 2019.5.31
     * @brief Image 목록 조회
     */
    public List<ImageInfo> getImages(Map<String, Object> params);

    /**
     * @author khk
     * @date 2019.6.03
     * @brief Image 상세 정보 조회
     */
    public List<ImageDetailInfo> getImageDetails(String type, String location);

    /**
     * @author khk
     * @date 2019.5.31
     * @brief Image 상세 정보 조회
     */
    public ImageDetailInfo getImageDetail(String id);

    /**
     * @author khk
     * @date 2019.5.31
     * @brief Image 상세 정보 ID 갯수 정보 조회
     */
    public int getImageDetailIdCount(String id);

    /**
     * @author khk
     * @date 2019.5.31
     * @brief Image 상세 정보 등록
     */
    public int createImageDetail(ImageDetailInfo info);

    /**
     * @author khk
     * @date 2019.5.31
     * @brief Image 상세 정보 수정
     */
    public int updateImageDetail(ImageDetailInfo info);

    /**
     * @author khk
     * @date 2019.6.20
     * @brief Image 상세 정보 삭제
     */
    public int deleteImageDetail(ImageDetailInfo info);
}
