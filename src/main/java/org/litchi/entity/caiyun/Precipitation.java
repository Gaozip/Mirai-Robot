package org.litchi.entity.caiyun;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: gaozp
 * @date: 2021-04-27 10:28
 * @desc: 彩云天气降雨量实体
 */

@Data
public class Precipitation implements Serializable {

    private String datetime;
    private Double value;

    public String getDatetime() {
        return datetime.substring(11,16);
    }

    @Override
    public String toString() {
        return "Precipitation{" +
                "datetime=" + datetime +
                ", value=" + value +
                '}';
    }
}
