package io.examples.discovery.boot.eureka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @Author Gary Cheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceData {
    private String id;
    private String content;
}
