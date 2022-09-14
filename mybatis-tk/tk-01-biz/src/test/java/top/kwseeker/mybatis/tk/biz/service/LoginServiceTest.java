package top.kwseeker.mybatis.tk.biz.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LoginServiceTest {

    @Resource
    private LoginService loginService;

    @Test
    public void login() {
        loginService.login();
    }
}