package top.kwseeker.mybatis.analysis.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
//@Alias("BlogAlias")
public class Blog {

    private Integer id;
    private String title;
    private String nickname;
}
