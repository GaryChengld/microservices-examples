package io.examples.rest.boot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gary Cheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Todo {
    private Integer id;
    private String title;
    private Boolean completed;
    private Integer order;
}
