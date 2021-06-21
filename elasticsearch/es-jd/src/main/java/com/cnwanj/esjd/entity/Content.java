package com.cnwanj.esjd.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author: cnwanj
 * @date: 2021-06-14 17:36:30
 * @version: 1.0
 * @desc:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Content {

    private String title;

    private String img;

    private String price;
}
