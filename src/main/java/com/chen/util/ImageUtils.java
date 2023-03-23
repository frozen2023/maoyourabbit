package com.chen.util;

import com.chen.MaoyouRabbitApplication;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Struct;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ImageUtils {

    //使用时只能使用注入,不能使用new(),不然会导致配置文件读取不到
    @Value("${qiniu.kodo.accessKey}")
    String accessKey;
    @Value("${qiniu.kodo.secretKey}")
    String secretKey;
    @Value("${qiniu.kodo.bucket}")
    String bucket;
    @Value("${qiniu.kodo.domain}")
    String domain;

  /**
     * 处理多文件
     * @param multipartFiles
     * @return
     */
    public Map<String, List<String>> uploadImages(List<MultipartFile> multipartFiles){
        Map<String,List<String>> map = new HashMap<>();
        List<String> imageUrls = new ArrayList<>();
        for(MultipartFile file : multipartFiles){
            imageUrls.add(uploadImageQiniu(file));
         }
        map.put("imageUrl",imageUrls);
        return map;
    }
    public Map<String, String> uploadImage(MultipartFile multipartFiles){
        Map<String,String> map = new HashMap<>();
        map.put("imageUrl",uploadImageQiniu(multipartFiles));
        return map;
    }
    /**
     * 上传图片到七牛云
     * @param multipartFile
     * @return
     */
    private String uploadImageQiniu(MultipartFile multipartFile){
        try {
            //1、获取文件上传的流
            byte[] fileBytes = multipartFile.getBytes();
            //2、创建日期目录分隔
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String datePath = dateFormat.format(new Date());

            //3、获取文件名
            String originalFilename = multipartFile.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = datePath+"/"+ UUID.randomUUID().toString().replace("-", "")+ suffix;

            //4.构造一个带指定 Region 对象的配置类
            Configuration cfg = new Configuration(Region.huanan());
            UploadManager uploadManager = new UploadManager(cfg);

            //5.获取七牛云提供的 token
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            uploadManager.put(fileBytes,filename,upToken);

            return domain+"/"+filename;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}