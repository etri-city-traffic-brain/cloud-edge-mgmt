package com.innogrid.uniq.coredb.dao;

import com.innogrid.uniq.core.model.ImageDetailInfo;
import com.innogrid.uniq.core.model.ImageInfo;

import java.util.List;
import java.util.Map;

public interface ImageDao {

    public List<ImageInfo> getImages(Map<String, Object> params);

    public List<ImageDetailInfo> getImageDetails(Map<String, Object> params);

    public ImageDetailInfo getImageDetail(String id);

    public int getImageDetailIdCount(String id);

    public int createImageDetail(ImageDetailInfo info);

    public int updateImageDetail(ImageDetailInfo info);

    public int deleteImageDetail(ImageDetailInfo info);
}
