package org.litchi.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.util.Strings;

import java.io.Serializable;

/**
 * @author: gaozp
 * @date: 2021-05-24 0:34
 * @desc:
 */

@Data
@Accessors(chain = true)
public class WeiBoTop implements Serializable {

    private String no;
    private String title;
    private String url;
    private String hot;
    private String type;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();



        sb.append("[" + (Strings.isBlank(no) ? "置顶" : no) + "] " + title);
        if(Strings.isNotBlank(hot)){
            sb.append("_" + hot);
        }
        if(Strings.isNotBlank(type)){
            sb.append("(" + type + ")");
        }
        return sb.toString();
    }
}
