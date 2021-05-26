package org.litchi.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litchi.constant.CommonConstant;
import org.litchi.constant.Strings;
import org.litchi.entity.WeiBoTop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: gaozp
 * @date: 2021-05-22 21:16
 * @desc:
 */

@Service
public class WeiBoTopService {

    private final String urlPrefix = "https://s.weibo.com";

    private final String TOP_MAIN_ID = "pl_top_realtimehot";

    private final String TR_NO_CLASS = "td-01 ranktop";

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<WeiBoTop> opsForWeiBoTopNews() {
        String html = restTemplate.getForObject(CommonConstant.WEI_BO_TOP_URL, String.class);

        List<WeiBoTop> weiBoTopList = new ArrayList<>(16);

        Document doc = Jsoup.parse(html);

        Elements allElements = doc.getElementById(TOP_MAIN_ID).select("tr");
        allElements.remove(0);

        //清洗置顶
        cleanTopNew(allElements, weiBoTopList);

        //清洗头条
        cleanNews(allElements, weiBoTopList);

        return weiBoTopList;
    }


    /**
     * 清洗置顶头条
     *
     * @param allElements
     * @param weiBoTopList
     */
    private void cleanTopNew(Elements allElements, List<WeiBoTop> weiBoTopList) {

        //置顶头条
        Elements aElements = allElements.get(0).select("a");
        Element aElement = aElements.get(0);
        String href = aElement.attr("href");
        String title = aElement.text();

        WeiBoTop topNew = new WeiBoTop().setUrl(urlPrefix + href)
                .setTitle(title)
                .setType("热");
        weiBoTopList.add(topNew);

        allElements.remove(0);
    }

    /**
     * 清洗剩余头条
     *
     * @param allElements
     * @param weiBoTopList
     */
    private void cleanNews(Elements allElements, List<WeiBoTop> weiBoTopList) {

        allElements.stream().filter(item -> !item.getElementsByClass(TR_NO_CLASS).get(0).text().equals(Strings.CN_POINT) ).forEach(element -> {

            WeiBoTop weiBoTop = new WeiBoTop();

            //Type
            Elements iElements = element.select("i");
            if (iElements != null && iElements.size() > 0) {
                String type = iElements.get(0).text();
                weiBoTop.setType(type);
            }

            //No
            Elements noElements = element.getElementsByClass(TR_NO_CLASS);
            if (noElements != null && noElements.size() > 0) {
                weiBoTop.setNo("No." + noElements.get(0).text());

            }

            //Href
            //Title
            Elements aElements = element.select("a");
            if (aElements != null && aElements.size() > 0) {
                Element aElement = aElements.get(0);
                weiBoTop.setTitle(aElement.text());
                weiBoTop.setUrl(urlPrefix + aElement.attr("href"));
            }

            //Hot
            Elements spanElements = element.select("span");
            if (spanElements != null && spanElements.size() > 0) {
                weiBoTop.setHot(spanElements.get(0).text());
            }
            weiBoTopList.add(weiBoTop);
        });
    }
}
