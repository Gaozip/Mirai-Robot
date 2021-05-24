package org.litchi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author: gaozp
 * @date: 2021-05-22 18:00
 * @desc:
 */

@Service
public class TextToImageService {

    /**
     * 文字转图片url
     */
    private final String TEXT_TO_IMAGE_URL = "http://www.akuziti.com/";

    private RestTemplate restTemplate;
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void test(){



    }
}
