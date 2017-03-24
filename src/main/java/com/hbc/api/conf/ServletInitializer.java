package com.hbc.api.conf;

/**
 * Created by cheng on 16/8/8.
 *
 * 该类是为了把项目打包war所需要,如果项目是jar请删除该类
 */
import com.hbc.api.Application;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
}
