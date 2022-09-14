package top.kwseeker.mybatis.tk.base.persistence.entity.primary;

import lombok.Data;
import top.kwseeker.mybatis.tk.base.mybatis.DefineShareField;
import top.kwseeker.mybatis.tk.base.mybatis.DynamicTableNameEntity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "user")
public class UserEntity extends DynamicTableNameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @DefineShareField(value = 20)
    private Long loveSpaceId;

    private String nickName;

    private Integer gender;

    private String header;

    private Date registerTime;

    private Date loginTime;
}
