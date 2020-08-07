package fun.littlecc.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author luckyjiet
 */
@MapperScan(basePackages = "fun.littlecc.repository.mapper")
@SpringBootApplication(scanBasePackages = "fun.*")
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}
