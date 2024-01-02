package com.snapmocklib.example.integration.config;

import com.snapmocklib.example.classes.MyApi;
import com.snapmocklib.example.classes.MyApiService;
import com.snapmocklib.example.classes.MyFirstOutputImpl;
import com.snapmocklib.example.classes.MyFirstOutputSpi;
import com.snapmocklib.example.classes.MySecondOutputImpl;
import com.snapmocklib.example.classes.MySecondOutputSpi;
import com.snapmocklib.example.classes.MyStepImpl;
import com.snapmocklib.example.classes.MyStepSpi;
import com.snapmocklib.example.classes.MyThirdOutputImpl;
import com.snapmocklib.example.classes.MyThirdOutputSpi;
import com.snapmocklib.example.classes.NoSerializeTypesApi;
import com.snapmocklib.example.classes.NoSerializeTypesIntegrationApi;
import com.snapmocklib.example.classes.NoSerializeTypesIntegrationService;
import com.snapmocklib.example.classes.NoSerializeTypesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationTestConfig {

    @Bean
    public MyApi myApi(MyFirstOutputSpi myFirstOutputSpi,
                       MySecondOutputSpi mySecondOutputSpi,
                       MyThirdOutputSpi myThirdOutputSpi,
                       MyStepSpi myStepSpi) {
        return new MyApiService(myFirstOutputSpi, mySecondOutputSpi, myThirdOutputSpi, myStepSpi);
    }

    @Bean
    public MyFirstOutputSpi myFirstOutputSpi() {
        return new MyFirstOutputImpl();
    }

    @Bean
    public MySecondOutputSpi mySecondOutputSpi() {
        return new MySecondOutputImpl();
    }

    @Bean
    public MyThirdOutputSpi myThirdOutputSpi() {
        return new MyThirdOutputImpl();
    }

    @Bean
    public MyStepSpi myStepSpi() {
        return new MyStepImpl();
    }

    @Bean
    public NoSerializeTypesApi noSerializeTypesApi() {
        return new NoSerializeTypesService();
    }

    @Bean
    public NoSerializeTypesIntegrationApi noSerializeTypesIntegrationApi(NoSerializeTypesApi noSerializeTypesApi) {
        return new NoSerializeTypesIntegrationService(noSerializeTypesApi);
    }
}
