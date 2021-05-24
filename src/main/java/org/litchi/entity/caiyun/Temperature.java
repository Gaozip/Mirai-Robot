package org.litchi.entity.caiyun;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: gaozp
 * @date: 2021-04-27 10:18
 * @desc: 彩云天气温度实体
 */

@Data
public class Temperature implements Serializable {

    private String datetime;
    private Double value;

    public String getDatetime() {
        return datetime.substring(11,16);
    }

    @Override
    public String toString() {
        return "Temperature{" +
                "datetime=" + datetime +
                ", value=" + value +
                '}';
    }
}
