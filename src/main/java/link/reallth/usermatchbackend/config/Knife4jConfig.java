package link.reallth.usermatchbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j config
 *
 * @author ReAllTh
 */
@Configuration
public class Knife4jConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info();
        info.setTitle("用户匹配系统");
        info.setDescription("用户匹配系统后端接口文档");
        info.setVersion("0.0.1");
        info.setTermsOfService("TODO");
        License mit = new License().name("MIT").url("https://choosealicense.com/licenses/mit/");
        info.setLicense(mit);

        return new OpenAPI().info(info);
    }
}