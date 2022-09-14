package top.kwseeker.mybatis.tk.base.service;

import org.springframework.stereotype.Service;
import top.kwseeker.mybatis.tk.base.persistence.dao.primary.UserEntityMapper;
import top.kwseeker.mybatis.tk.base.persistence.entity.primary.UserEntity;

import javax.annotation.Resource;

@Service
public class UserService {

    @Resource
    private UserEntityMapper userEntityMapper;

    public UserEntity getUserFromDb(Long loveSpaceId, Long userId) {
        UserEntity entity = new UserEntity();
        entity.setUserId(userId);
        entity.setLoveSpaceId(loveSpaceId);
        entity = userEntityMapper.selectOne(entity);
        return entity;
    }
}

