package org.example.kit;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileKit {

    public static String getUserRootPath(){
        return String.class.getResource("/").getPath();
    }

    public static byte[] getAllBytes(File file) throws IOException{
        long len = file.length();
        if(len > Integer.MAX_VALUE){
            throw new IOException("File is too big!");
        }
        InputStream in = new FileInputStream(file);
        byte[] bytes = new byte[(int)len];
        int offset = 0, n = 0;
        while(offset < bytes.length && (n = in.read(bytes, offset, bytes.length - offset)) >= 0){
            offset += n;
        }
        if(offset != bytes.length){
            throw new IOException("Failed to read the whole file!" + file.getName());
        }
        in.close();
        return bytes;
    }

    public static List<String> getAllLines(String fileName) throws IOException{
        return Files.readAllLines(Paths.get(fileName));
    }

    public static List<String> getAllLines(String fileName, String charset) throws IOException{
        return Files.readAllLines(Paths.get(fileName), Charset.forName(charset));
    }

    public static void writeAllLines(List<String> list, String fileName) throws IOException{
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdir()) {
                System.out.println("创建目标文件目录失败");
                return ;
            }
        }
        file.createNewFile();
        PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
        for(String s : list){
            pw.println(s);
        }
        pw.close();
    }

    public static void writeAllLines(List<String> list, String fileName, String charset) throws IOException{
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), charset));
        for(String s : list){
            pw.println(s);
        }
        pw.close();
    }
}
