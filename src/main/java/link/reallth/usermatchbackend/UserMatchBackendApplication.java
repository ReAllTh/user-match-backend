package link.reallth.usermatchbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("link.reallth.usermatchbackend.mapper")
public class UserMatchBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserMatchBackendApplication.class, args);
    }

}
