package top.kwseeker.mybatis.tk.base.persistence.dao.primary;

import org.apache.ibatis.annotations.Param;
import top.kwseeker.mybatis.tk.base.mybatis.CommonMapper;
import top.kwseeker.mybatis.tk.base.persistence.entity.primary.UserEntity;

public interface UserEntityMapper extends CommonMapper<UserEntity> {

    void updateUserInfo(String tableName, UserEntity userEntity);

    UserEntity selectByPairId(@Param("spaceId") Long spaceId, @Param("userId") Long userId);

    int updateNickName(@Param("spaceId") Long spaceId, @Param("userId") Long userId, @Param("nickname") String nickname);
}
