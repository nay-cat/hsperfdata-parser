package org.nay;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class HsPerfDataParser {
    static {
        try {
            InputStream dllStream = HsPerfDataParser.class.getResourceAsStream("/x64/hsperfdata_parser.dll");

            if (dllStream == null) {
                throw new RuntimeException("native library not found");
            }

            File tempDll = File.createTempFile("hsperfdata_parser", ".dll");
            tempDll.deleteOnExit();

            Files.copy(dllStream, tempDll.toPath(), StandardCopyOption.REPLACE_EXISTING);
            dllStream.close();

            System.load(tempDll.getAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException("error loading native" + e.getMessage(), e);
        }
    }

    public native Map<String, Object> parseHsPerfDataFromFile(String filePath);
}