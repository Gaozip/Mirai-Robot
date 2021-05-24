package org.litchi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: gaozp
 * @date: 2021-05-21 14:24
 * @desc:
 */

@Data
@TableName("sensitive_word")
public class SensitiveWord implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String word;
    private String type;

}
