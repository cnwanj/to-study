package com.cnwanj.es.esapi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author: cnwanj
 * @date: 2021-05-04 21:06:03
 * @version: 1.0
 * @desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class User {

    private String name;

    private int age;
}
