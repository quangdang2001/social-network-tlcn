package com.tlcn.demo.service.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tlcn.demo.util.Convert;
import com.tlcn.demo.util.contant.FolderName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Configuration
public class CloudinaryUpload {
    @Bean
    public Cloudinary cloudinary(){
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "quangdangcloud",
                "api_key", "738222114899535",
                "api_secret", "V37UHtOmQ62U1VH-kXyd7kRLgf4",
                "secure", true));
        return cloudinary;
    }

    public Map upload(MultipartFile file, FolderName folderName) throws IOException {
        Map params = ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", folderName.getName()
        );
        Map map = cloudinary().uploader().upload(Convert.convertMultiPartToFile(file),params);
        return map;
    }

    public String getPublicId(String urlImage){
        int temp1 = urlImage.lastIndexOf(".");
        int temp2 = urlImage.lastIndexOf("/");
        return urlImage.substring(temp2+1,temp1);
    }

}
