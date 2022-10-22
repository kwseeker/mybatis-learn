package top.kwseeker.mybatis.tk.biz;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class CommandLineInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        //String path1 = "classpath:top/kwseeker/mybatis/tk/*/service/*.class";
        String path1 = "classpath:res/resourceFile.yml";
        //String path1 = "classpath:mapper/primary/*.xml";
        //String path2 = "classpath*:top/kwseeker/mybatis/tk/*/service/*.class";
        String path2 = "classpath*:res/resourceFile.yml";
        //String path2 = "classpath*:mapper/primary/*.xml";

        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] resources1 = resourceResolver.getResources(path1);
        org.springframework.core.io.Resource[] resources2 = resourceResolver.getResources(path2);
        System.out.println("classpath >>>>>>>");
        for (org.springframework.core.io.Resource resource : resources1) {
            System.out.println(resource.toString());
        }
        System.out.println("classpath* >>>>>>>");
        for (org.springframework.core.io.Resource resource : resources2) {
            System.out.println(resource.toString());
        }
    }
}
