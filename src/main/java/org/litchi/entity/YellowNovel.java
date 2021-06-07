package org.litchi.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: gaozp
 * @date: 2021-06-06 14:19
 * @desc:
 */
@Data
public class YellowNovel implements Serializable {

    private Integer id;
    private String title;
    private String content;
}
