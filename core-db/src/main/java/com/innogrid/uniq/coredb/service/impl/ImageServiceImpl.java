package com.innogrid.uniq.coredb.service.impl;

import com.innogrid.uniq.core.model.ImageDetailInfo;
import com.innogrid.uniq.core.model.ImageInfo;
import com.innogrid.uniq.coredb.dao.ImageDao;
import com.innogrid.uniq.coredb.service.ImageService;
import fi.evident.dalesbred.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {
    private final static Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Autowired
    private ImageDao imageDao;

    @Override
    public List<ImageInfo> getImages(Map<String, Object> params) {
        return imageDao.getImages(params);
    }

    @Override
    public List<ImageDetailInfo> getImageDetails(String type, String location) {

        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        if(location != null) {
            params.put("location", location);
        }

        List<ImageDetailInfo> list = imageDao.getImageDetails(params);

        if(list == null) return new ArrayList<>();
        return list;
    }

    @Override
    public ImageDetailInfo getImageDetail(String id) {
        ImageDetailInfo info = imageDao.getImageDetail(id);

        return info;
    }

    @Override
    public int getImageDetailIdCount(String id) {
        int result = imageDao.getImageDetailIdCount(id);

        return result;
    }

    @Override
    public int createImageDetail(ImageDetailInfo info) {
        int result = imageDao.createImageDetail(info);

        return result;
    }

    @Override
    public int updateImageDetail(ImageDetailInfo info) {
        int result = imageDao.updateImageDetail(info);

        return result;
    }

    @Override
    public int deleteImageDetail(ImageDetailInfo info) {
        int result = imageDao.deleteImageDetail(info);

        return result;
    }
}
