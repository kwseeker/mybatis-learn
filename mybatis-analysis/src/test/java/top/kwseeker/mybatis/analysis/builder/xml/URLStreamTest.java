package top.kwseeker.mybatis.analysis.builder.xml;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class URLStreamTest {

    @Test
    public void testGetUrlAsStream() throws IOException {
        String urlStr = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";
        URL url = new URL(urlStr);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();

        FileOutputStream fos = new FileOutputStream("url-content.txt");
        byte[] b = new byte[1024];
        int length;
        while ((length = is.read(b)) > 0) {
            fos.write(b, 0, length);
        }
        is.close();
        fos.close();
    }
}
