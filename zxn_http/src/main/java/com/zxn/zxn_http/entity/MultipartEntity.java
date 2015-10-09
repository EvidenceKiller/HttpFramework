package com.zxn.zxn_http.entity;

import com.zxn.zxn_http.base.Request;
import com.zxn.zxn_http.base.Response;
import com.zxn.zxn_http.httpstacks.HttpStack;

import java.io.ByteArrayOutputStream;
import java.util.Random;

/**
 * User : ZXN
 * Date : 2015-10-09
 * Time : 21:21
 */
public class MultipartEntity implements HttpStack {

    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    // 换行符
    private final String NEW_LINE_STR = "\r\n";

    private final String CONTENT_TYPE = "Content-Type: ";
    private final String CONTENT_DISPOSITION = "Content-Disposition: ";

    // 文本参数和字符集
    private final String TYPE_TEXT_CHARSET = "text/plain; charset=UTF-8";

    // 字节流参数
    private final String TYPE_OCTET_STREAM = "application/octet-stream";

    // 二进制参数
    private final byte[] BINARY_ENCODING = "Content-Transfer-Encoding: binary\r\n\r\n".getBytes();

    // 文本参数
    private final byte[] BIT_ENCODING = "Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes();

    // 分隔符
    private String boundary = null;

    // 输出流
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public MultipartEntity() {
        this.boundary = generateBoundary();
    }

    /**
     * 产生随机分隔符
     *
     * @return
     */
    private String generateBoundary() {
        final StringBuffer buf = new StringBuffer();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buf.toString();
    }


    public Response performRequest(Request<?> request) {
        return null;
    }
}
