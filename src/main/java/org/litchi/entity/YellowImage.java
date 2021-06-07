package org.litchi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: gaozp
 * @date: 2021-06-06 13:40
 * @desc:
 */
@Data
public class YellowImage implements Serializable {

    private Integer id;
    private String title;
    private String url;
    @TableField(value = "imageNum")
    private Integer imageNum;
}
