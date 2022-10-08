package top.kwseeker.mybatis.analysis.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Data
//@Alias("BlogAlias")
public class Blog implements Serializable {

    private static final long serialVersionUID = 5792288367353051120L;

    private Integer id;
    private String title;
    private String nickname;
}
