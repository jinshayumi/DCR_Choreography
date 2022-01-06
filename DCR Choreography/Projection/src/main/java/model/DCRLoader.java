package model;

import com.alibaba.fastjson.JSON;
import model.entities.JSONDCR;
import org.apache.commons.io.FileUtils;

import java.io.*;


public class DCRLoader {
    public static JSONDCR LoadFromJsonFile(final String filePath) throws IOException {
        String absolutePath = System.getProperty("user.dir") + filePath;
        File file = new File(absolutePath);
        String jsonString = FileUtils.readFileToString(file);

        JSONDCR jsonObject = JSON.parseObject(jsonString, JSONDCR.class);
        return jsonObject;
    }
}
