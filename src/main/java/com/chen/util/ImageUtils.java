package com.chen.util;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
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
    public static String[] IMAGE_FILE_EXTD = new String[] { ".png", ".bmp", ".jpg", ".jpeg",".pdf" };

    // 判断是否是图片文件 (这里仅判断后缀)
    public boolean isImageAllowed(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String filetype = filename.substring(filename.lastIndexOf("."));
        System.out.println("fileType==>"+filetype);
        for (String ext : IMAGE_FILE_EXTD) {
            if(ext.equalsIgnoreCase(filetype)){
                return true;
            }
        }
        return false;
    }

    // base64转字节数组
    public byte[] imageToBytes(String image) {
        String base64 = image.split(",")[1].replaceAll("\n","").trim();
        return Base64.getDecoder().decode(base64);
    }

    // 根据字节数组上传
    public String uploadImage(byte[] bytes,String filename) {
        try {
            //2、创建日期目录分隔
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String datePath = dateFormat.format(new Date());

            //3、获取文件名
            String suffix = filename.substring(filename.lastIndexOf("."));
            String finalFilename = datePath+"/"+ UUID.randomUUID().toString().replace("-", "")+ suffix;

            //4.构造一个带指定 Region 对象的
            Configuration cfg = new Configuration(Region.huanan());
            UploadManager uploadManager = new UploadManager(cfg);

            //5.获取七牛云提供的 token
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            uploadManager.put(bytes,finalFilename,upToken);

            return domain+"/"+finalFilename;
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
    }
    // 根据MultipartFile上传
    public Map<String, String> uploadImage(MultipartFile multipartFile) {
        try {
            //1、获取文件上传的流
            byte[] fileBytes = multipartFile.getBytes();
            String filename = multipartFile.getOriginalFilename();
            Map<String,String> map = new HashMap();
            map.put("imageUrl",uploadImage(fileBytes,filename));
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> uploadImages(MultipartFile[] multipartFiles) {
        List<String> urlList = new ArrayList<>();
        try {
            for (int i = 0; i < multipartFiles.length; i++) {
                byte[] fileBytes = multipartFiles[i].getBytes();
                String filename = multipartFiles[i].getOriginalFilename();
                String url = uploadImage(fileBytes, filename);
                urlList.add(url);
            }
            return urlList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}