package com.qiguliuxing.dts.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket initDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(initApiInfo())
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.qiguliuxing.dts.admin"))
                .paths(PathSelectors.regex("^(?!auth).*$"))
                .build();

    }

    private ApiInfo initApiInfo(){
        return new ApiInfoBuilder()
                .title("构建 swagger2")
                .description("接口测试")
                .termsOfServiceUrl("http://www.baidu.com")
                .version("v1.0")
                .build();
    }

    public List<ApiKey> securitySchemes(){
        List<ApiKey> apiKeys=new ArrayList<>();
        apiKeys.add(new ApiKey("oauth2 认证","Authorization","header"));

        return apiKeys;
    }

    public List<SecurityContext> securityContexts(){
        List<SecurityContext> securityContexts=new ArrayList<>();
        securityContexts.add(SecurityContext.builder()
                .securityReferences(securityReferences())
                .forPaths(PathSelectors.any()).build());

        return securityContexts;
    }

    public List<SecurityReference> securityReferences(){
        AuthorizationScope[] authorizationScopes=new AuthorizationScope[1];
        authorizationScopes[0]=new AuthorizationScope("global","accessEverything");

        List<SecurityReference> securityReferences=new ArrayList<>();
        securityReferences.add(new SecurityReference("Authorization",authorizationScopes));

        return securityReferences;
    }
}