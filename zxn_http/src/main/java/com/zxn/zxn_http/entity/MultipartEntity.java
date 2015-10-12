package com.zxn.zxn_http.entity;

import android.text.TextUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * User : ZXN
 * Date : 2015-10-09
 * Time : 21:21
 */
public class MultipartEntity implements HttpEntity {

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

    /**
     * 参数开头的分隔符
     *
     * @throws IOException
     */
    private void writeFirstBoundary() throws IOException {
        byteArrayOutputStream.write(("--" + boundary + "\r\n").getBytes());
    }

    /**
     * 添加文本参数
     *
     * @param paramName
     * @param value
     */
    public void addStringPart(final String paramName, final String value) {
        writeToOutputStream(paramName, value.getBytes(), TYPE_TEXT_CHARSET, BIT_ENCODING, "");
    }

    /**
     * 将数据写入输出流
     *
     * @param paramName
     * @param rawData
     * @param type
     * @param encodingBytes
     * @param fileName
     */
    private void writeToOutputStream(String paramName, byte[] rawData, String type, byte[] encodingBytes, String fileName) {
        try {
            writeFirstBoundary();
            byteArrayOutputStream.write((CONTENT_TYPE + type + NEW_LINE_STR).getBytes());
            byteArrayOutputStream.write(getContentDispositionBytes(paramName, fileName));
            byteArrayOutputStream.write(encodingBytes);
            byteArrayOutputStream.write(rawData);
            byteArrayOutputStream.write(NEW_LINE_STR.getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加二进制参数, 例如bitmap的字节流参数
     *
     * @param paramName
     * @param rawData
     */
    public void addBinaryPart(String paramName, final byte[] rawData) {
        writeToOutputStream(paramName, rawData, TYPE_OCTET_STREAM, BINARY_ENCODING, "no-file");
    }

    /**
     * 添加文件头，实现文件上传功能
     *
     * @param key
     * @param file
     */
    public void addFilePart(final String key, final File file) {
        InputStream fin = null;
        try {
            fin = new FileInputStream(file);
            writeFirstBoundary();
            final String type = CONTENT_TYPE + TYPE_OCTET_STREAM + NEW_LINE_STR;
            byteArrayOutputStream.write(getContentDispositionBytes(key, file.getName()));
            byteArrayOutputStream.write(type.getBytes());
            byteArrayOutputStream.write(BINARY_ENCODING);

            final byte[] tmp = new byte[4096];
            int len = 0;
            while ((len = fin.read(tmp)) != -1) {
                byteArrayOutputStream.write(tmp, 0, len);
            }
            byteArrayOutputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(fin);
        }
    }

    /**
     * 关闭输出流
     *
     * @param closeable
     */
    private void closeSilently(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param paramName
     * @param fileName
     * @return
     */
    private byte[] getContentDispositionBytes(String paramName, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CONTENT_DISPOSITION + "form-data; name=\"" + paramName + "\"");
        // 文本参数没有fileName参数， 设置为空即可
        if (!TextUtils.isEmpty(fileName)) {
            stringBuilder.append("; filename=\"" + fileName + "\"");
        }
        return stringBuilder.append(NEW_LINE_STR).toString().getBytes();
    }


    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public long getContentLength() {
        return byteArrayOutputStream.toByteArray().length;
    }

    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", "Multipart/form-data; boundary=" + boundary);
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        // 参数最末尾的结束符
        final String endString = "--" + boundary + "--\r\n";
        // 写入结束符
        byteArrayOutputStream.write(endString.getBytes());
        outputStream.write(byteArrayOutputStream.toByteArray());
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void consumeContent() throws IOException, UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
        }
    }
}
