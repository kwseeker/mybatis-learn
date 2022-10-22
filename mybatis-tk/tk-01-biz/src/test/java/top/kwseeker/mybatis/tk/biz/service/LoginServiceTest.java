package top.kwseeker.mybatis.tk.biz.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LoginServiceTest {

    @Resource
    private LoginService loginService;

    @Test
    public void login() {
        loginService.login();
    }

    @Test
    public void loadResource() throws IOException {
        Enumeration<?> resourceUrls = this.getClass().getClassLoader().getResources("mapper/primary/");
        while(resourceUrls.hasMoreElements()) {
            URL url = (URL)resourceUrls.nextElement();
            System.out.println(url.getPath());
        }
    }
}