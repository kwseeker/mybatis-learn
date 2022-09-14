package top.kwseeker.mybatis.tk.base.persistence.dao.primary;


import top.kwseeker.mybatis.tk.base.mybatis.CommonMapper;
import top.kwseeker.mybatis.tk.base.persistence.entity.primary.UserEntity;

public interface UserEntityMapper extends CommonMapper<UserEntity> {

    void updateUserInfo(String tableName, UserEntity userEntity);
}