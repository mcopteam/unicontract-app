package com.uniledger.contract.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by wxcsdb88 on 2017/5/26 0:14.
 */
@EnableWebMvc
@EnableSwagger2
@Configuration
@ComponentScan(basePackages = "com.uniledger.contract.controller")
public class RestApiConfig extends WebMvcConfigurationSupport {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.uniledger.contract.controller"))
                .paths(PathSelectors.any())
                .build().useDefaultResponseMessages(false);

//                .forCodeGeneration(true)
//                .select().apis(RequestHandlerSelectors.any())//过滤生成链接
//                .paths(PathSelectors.regex("/swagger/.*")).build().apiInfo(apiInfo());
//                .apis(RequestHandlerSelectors.basePackage("com.uniledger.contract.swagger"))
//                .paths(PathSelectors.any())
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("wxcsdb88", "http://www.futurever.com", "wx_cs_db_88@163.com");
        ApiInfo apiInfo = new ApiInfoBuilder().license("Apache License Version 2.0").title("ContractOriginal-app Restful API").description("接口文档").contact(contact).version("1.0").build();
        return apiInfo;
//        return new ApiInfoBuilder()
//                .title("ContractOriginal-app Restful API")
//                .termsOfServiceUrl("https://github.com/wxcsdb88")
//                .contact(new Contact("wxcsdb88","https://www.futurever.com","wx_cs_db_88@163.com"))
//                .version("1.0.0")
//                .build();
    }
}
