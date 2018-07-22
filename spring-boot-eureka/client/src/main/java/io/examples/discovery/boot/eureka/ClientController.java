package io.examples.discovery.boot.eureka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @Author Gary Cheng
 */

@RestController
@Slf4j
public class ClientController {
    private String SERVICE_URL = "http://myService/api/";

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/myData/")
    Mono<MyData> getMyData() {
        log.info("Received myData request");
        Mono<MyData> data = this.rxBuildData();
        log.info("Return myData response");
        return data;
    }

    private Mono<MyData> rxBuildData() {
        MyData myData = new MyData();
        myData.setMyDataId(UUID.randomUUID().toString());
        myData.setDataContent("My Data Content");
        log.info("Sending service request");
        return Mono.create(emitter ->
                webClientBuilder.build().get().uri(SERVICE_URL).exchange()
                        .flatMap(res -> res.bodyToMono(ServiceData.class))
                        .doOnError(ex -> {
                            log.error(ex.getLocalizedMessage());
                            myData.setServiceData(new ServiceData("ERROR", ex.getLocalizedMessage()));
                            emitter.success(myData);
                        })
                        .subscribe(serviceData -> {
                            log.info("Received service data:{}", serviceData.toString());
                            myData.setServiceData(serviceData);
                            emitter.success(myData);
                        })

        );
    }
}
