package org.litchi.entity;

import java.io.Serializable;
import java.util.Vector;

/**
 * @author: gaozp
 * @date: 2021-04-27 9:43
 * @desc: 折线图数据dto
 */
public class Serie implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 名字
     */
    private String name;
    /**
     * 数据值ֵ
     */
    private Vector<Object> data;

    public Serie() {

    }

    /**
     * @param name 名称（线条名称）
     * @param data 数据（线条上的所有数据值）
     */
    public Serie(String name, Vector<Object> data) {

        this.name = name;
        this.data = data;
    }

    /**
     * @param name  名称（线条名称）
     * @param array 数据（线条上的所有数据值）
     */
    public Serie(String name, Object[] array) {
        this.name = name;
        if (array != null) {
            data = new Vector<Object>(array.length);
            for (int i = 0; i < array.length; i++) {
                data.add(array[i]);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector<Object> getData() {
        return data;
    }

    public void setData(Vector<Object> data) {
        this.data = data;
    }
}
