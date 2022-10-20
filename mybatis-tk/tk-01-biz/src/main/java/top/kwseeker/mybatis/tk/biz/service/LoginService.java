package top.kwseeker.mybatis.tk.biz.service;

import org.springframework.stereotype.Service;
import top.kwseeker.mybatis.tk.base.persistence.entity.primary.UserEntity;
import top.kwseeker.mybatis.tk.base.service.UserService;

import javax.annotation.Resource;

@Service
public class LoginService {

    @Resource
    private UserService userService;

    public void login() {
        long spaceId = 112;
        long userId = 222;
        UserEntity userEntity = userService.getUserFromDb(spaceId, userId);
        UserEntity userEntity2 = userService.getUserByPairId(spaceId, userId);
        System.out.println(userEntity.toString());
        System.out.println(userEntity2.toString());
    }
}
