package org.nay;


import java.util.Map;

public class HsPerfDataParser {
    static {
        String libraryPath = HsPerfDataParser.class.getResource("/x64/hsperfdata_parser.dll").getPath();
        System.load(libraryPath);
    }
    public native Map<String, Object> parseHsPerfDataFromFile(String filePath);

}