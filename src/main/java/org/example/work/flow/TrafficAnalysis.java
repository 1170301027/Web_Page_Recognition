package org.example.work.flow;

import org.example.kit.ByteBuffer;
import org.example.kit.entity.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname TrafficAnalysis
 * @Description 流量解析
 * @Date 2021/3/17 20:05
 * @Created by shuaif
 */
public class TrafficAnalysis {
    private static final int DATA_FRAME_HEAD_LENGTH = 40 + 14;
    private static final int IP_PACKET_HEAD_LENGTH = 20;
    private static final int TCP_HEAD_LENGTH = 20; // 暂定无扩展
    public static void responseAnalysis(byte[] responsePacket) {

    }

    public static void clientHelloAnalysis(byte[] packet) {
        System.out.println("正在解析数据包，，，");
        // 跳过链路层，网络层，以及传输层报文头部
        ByteArray buffer = new ByteArray(packet,DATA_FRAME_HEAD_LENGTH+IP_PACKET_HEAD_LENGTH+TCP_HEAD_LENGTH,packet.length + 1);
//        ByteArray buffer = new ByteArray(packet);
        int index = 0;
        byte type = buffer.get(index++);
        if (type != 0x16) {
            System.out.println("非handshake类型报文。");
            return ;
        }
        ssl_handshake handshake = new ssl_handshake((char) type,getShort(buffer.subBytes(index,index + 2)),
                getShort(buffer.subBytes(index + 2, index + 4)),buffer.subBytes(index + 4));
        if (handshake.isIs_client_hello()) {
            System.out.println("是 client hello 请求报文");
            if (handshake.isIs_get_server_name()) {
                System.out.println("服务器域名可获得");
                for (ssl_extension extension : handshake.getSsl_hello().getExtensions()) {
                    if (extension.getType() == 0) {
                        ByteArray byteArray = new ByteArray(extension.getData());
                        System.out.println("host : " + new String(byteArray.subBytes(5)));
                        break;
                    }
                }
            }
        }
    }

    public static short getShort(byte[] bytes) {
        if (bytes == null || bytes.length!=2) {
            System.out.println("数据格式错误");
            return 0;
        }
        byte argB1 = bytes[0];
        byte argB2 = bytes[1];
        return (short) ((argB1 & 0xFF)| (argB2 << 8));
    }
}
// SSL头部 TVL 格式
class ssl_handshake {
    private char type; // handshake = 0x16
    private short version; // TLS 1.0 = 0x0301  1.1 0x0302 类推。
    private short length;
    private byte[] content;
    private ssl_hello ssl_hello;
    private boolean is_client_hello = true;
    private boolean is_get_server_name;

    public ssl_handshake(char type, short version, short length, byte[] content) {
        this.type = type;
        this.version = version;
        this.length = length;
        this.content = content;
        parseSSLHello();
    }

    private void parseSSLHello() {
        System.out.println("parse ssl hello..");
        ByteArray buffer = new ByteArray(content);
        int index = 0; // skip nothing
        byte handshake_type = buffer.get(index++);
        if (handshake_type != 0x1) { // 非client hello
            is_client_hello = false;
            System.out.println("非 client hello ： " + handshake_type);
            return;
        }
        index++; // skip type
        byte[] length = buffer.subBytes(index, index + 3);
        index += 3; // skip length
        short version = getShort(buffer.subBytes(index, index + 2));
        index += 2; // skip version
        ssl_random random = new ssl_random(buffer.subBytes(index,index+4),buffer.subBytes(index+4,index+32));
        index += 31; // skip random
        char session_id_length = (char) buffer.get(index);
//        System.out.println(((int) session_id_length));
        index ++;
        byte[] session_id = buffer.subBytes(index,index + (int)session_id_length);
        index += session_id_length; // skip session id
        short cipher_suites_length = getShort(buffer.subBytes(index, index + 2));
//        System.out.println(cipher_suites_length);
        index += 2;
        byte[] cipher_suites = buffer.subBytes(index,index + cipher_suites_length);
        index += cipher_suites_length; // skip cipher_suites
        char compression_methods_length = (char) buffer.get(index);
        index ++;
        byte[] compression_methods = buffer.subBytes(index,index + (int) compression_methods_length);
        index += compression_methods_length; // skip compression_methods
//        System.out.println(((int) compression_methods_length));
        short extension_length = getShort(buffer.subBytes(index, index + 2));
        index += 2;
//        System.out.println(extension_length);
        List<ssl_extension> extensions = parseExtensions(buffer.subBytes(index));

        this.ssl_hello = new ssl_hello((char) handshake_type,length,version,random,session_id_length
                ,session_id,cipher_suites_length,cipher_suites,compression_methods_length,compression_methods,extension_length,extensions);
    }

    private List<ssl_extension> parseExtensions(byte[] content) {
        List<ssl_extension> result = new ArrayList<>();
        ByteArray buffer = new ByteArray(content);
        int index = 0;
        while (index < content.length - 1) {
            short type = getShort(buffer.subBytes(index, index + 2));
            if (type == 0) this.is_get_server_name = true;
            index += 2;
            short length = getShort(buffer.subBytes(index, index + 2));
            index += 2;
            byte[] data = buffer.subBytes(index, index + length);
            index += length;
            result.add(new ssl_extension(type,length,data));
//            System.out.println("type : " + type);
        }
        return result;
    }

    public short getShort(byte[] bytes) {
        if (bytes == null || bytes.length!=2) {
            System.out.println("数据格式错误");
            return 0;
        }
        byte argB1 = bytes[0];
        byte argB2 = bytes[1];
        return (short) ((argB1 << 8)| (argB2 & 0xFF));
    }

    public boolean isIs_client_hello() {
        return is_client_hello;
    }

    public boolean isIs_get_server_name() {
        return  is_get_server_name;
    }

    public char getType() {
        return type;
    }

    public short getVersion() {
        return version;
    }

    public short getLength() {
        return length;
    }

    public byte[] getContent() {
        return content;
    }

    public ssl_hello getSsl_hello() {
        return ssl_hello;
    }
}

class ssl_hello {
    private char handshake_type; // client = 1
    private byte[] length = new byte[3];
    private short version; // TLS 1.0 = 0x0301  1.1 0x0302 类推。
    private ssl_random random ;   // 跳过32个字节。
    private char session_id_length; // session 长度， 新建立的链接此字段可能为0
    private byte[] session_id;
    private short cipher_suites_length; // 加密套件长度，
    private byte[] cipher_suites; // 加密套件。（跳过）
    private char compression_methods_length; // 压缩算法长度（我这辈子没见过用的）
    private byte[] compression_method;
    private short extensions_length; // 扩展字段，server_name（1） 字段提供了客户端访问服务器时服务器的域名。
    private List<ssl_extension> extensions;

    public void setHandshake_type(char handshake_type) {
        this.handshake_type = handshake_type;
    }

    public void setLength(byte[] length) {
        this.length = length;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public void setRandom(ssl_random random) {
        this.random = random;
    }

    public void setSession_id_length(char session_id_length) {
        this.session_id_length = session_id_length;
    }

    public void setSession_id(byte[] session_id) {
        this.session_id = session_id;
    }

    public void setCipher_suites_length(short cipher_suites_length) {
        this.cipher_suites_length = cipher_suites_length;
    }

    public void setCipher_suites(byte[] cipher_suites) {
        this.cipher_suites = cipher_suites;
    }

    public void setCompression_methods_length(char compression_methods_length) {
        this.compression_methods_length = compression_methods_length;
    }

    public void setCompression_method(byte[] compression_method) {
        this.compression_method = compression_method;
    }

    public void setExtensions_length(short extensions_length) {
        this.extensions_length = extensions_length;
    }

    public void setExtensions(List<ssl_extension> extensions) {
        this.extensions = extensions;
    }

    public List<ssl_extension> getExtensions() {
        return extensions;
    }

    public ssl_hello(char handshake_type, byte[] length, short version
            , ssl_random random, char session_id_length, byte[] session_id, short cipher_suites_length, byte[] cipher_suites
            , char compression_methods_length, byte[] compression_method, short extensions_length, List<ssl_extension> extensions) {
        this.handshake_type = handshake_type;
        this.length = length;
        this.version = version;
        this.random = random;
        this.session_id_length = session_id_length;
        this.session_id = session_id;
        this.cipher_suites_length = cipher_suites_length;
        this.cipher_suites = cipher_suites;
        this.compression_methods_length = compression_methods_length;
        this.compression_method = compression_method;
        this.extensions_length = extensions_length;
        this.extensions = extensions;
    }
}

// 32bytes
class ssl_random {
    private int timestamp;
    private byte[] random = new byte[28];

    public ssl_random(byte[] timestamp,byte[] random) {
        this.random = random;
    }
}

// server name
class server_name {
    private short list_length;
    private char type; // 0 by host_name
    private short length;
    private byte[] server_name;
    public server_name(byte[] bytes) {
        ByteArray buffer = new ByteArray(bytes);

    }

    public byte[] getServer_name() {
        return server_name;
    }
}

// TLS 扩展字段
class ssl_extension {
    private short type;
    private short length;
    private byte[] data;

    public ssl_extension(short type, short length, byte[] data) {
        this.type = type;
        this.length = length;
        this.data = data;
    }

    public short getType() {
        return type;
    }

    public short getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }
}


