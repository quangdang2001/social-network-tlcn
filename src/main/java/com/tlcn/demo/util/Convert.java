package com.tlcn.demo.util;


import com.tlcn.demo.model.Users;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Convert {
    private static String pathImg = "Images";
    public static File convertMultiPartToFile(MultipartFile file) throws IOException {
        if (!Files.isDirectory(Path.of(pathImg))){
            File newDir = new File(pathImg);
            newDir.mkdir();
        }
        File convFile = new File(pathImg+"/" +file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
    public static String formatName(String name){
        name = name.trim();
        name = name.replaceAll("  "," ");
        return name;
    }

}
