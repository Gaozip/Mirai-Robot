package org.litchi.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: gaozp
 * @date: 2021-05-13 14:38
 * @desc:
 */

@Data
@Accessors(chain = true)
@TableName("sys_city_code")
public class AdCityInfo implements Serializable {


    private Long id;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 城市编码
     */
    private String adCode;
    /**
     * 经度
     */
    private Double lng;
    /**
     * 纬度
     */
    private Double lat;
}
