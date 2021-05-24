package org.litchi.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: gaozp
 * @date: 2021-05-13 14:42
 * @desc:
 */

@Data
@Accessors( chain = true)
@TableName("sys_color_img")
public class ColorImage implements Serializable {

    private Long id;
    private String author;
    private Integer height;
    private Integer width;
    private String title;
    private String url;

}
