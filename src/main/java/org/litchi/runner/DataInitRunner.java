package org.litchi.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.litchi.constant.CommonConstant;
import org.litchi.entity.AdCityInfo;
import org.litchi.entity.ColorImage;
import org.litchi.entity.YellowImage;
import org.litchi.entity.YellowNovel;
import org.litchi.mapper.AdCodeMapper;
import org.litchi.mapper.ColorImageMapper;
import org.litchi.mapper.YellowImageMapper;
import org.litchi.mapper.YellowNovelMapper;
import org.litchi.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author: gaozp
 * @date: 2021-04-18 22:00
 * @desc:
 */

@Slf4j
@Component
public class DataInitRunner {

    private AdCodeMapper adCodeMapper;

    @Autowired
    public void setAdCodeMapper(AdCodeMapper adCodeMapper) {
        this.adCodeMapper = adCodeMapper;
    }

    private RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private ColorImageMapper colorImageMapper;

    @Autowired
    public void setColorImageMapper(ColorImageMapper colorImageMapper) {
        this.colorImageMapper = colorImageMapper;
    }

    private RedisService redisService;

    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    private YellowImageMapper yellowImageMapper;

    @Autowired
    public void setYellowImageMapper(YellowImageMapper yellowImageMapper) {
        this.yellowImageMapper = yellowImageMapper;
    }

    private YellowNovelMapper yellowNovelMapper;

    @Autowired
    public void setYellowNovelMapper(YellowNovelMapper yellowNovelMapper) {
        this.yellowNovelMapper = yellowNovelMapper;
    }

    @PostConstruct
    private void initRedis() {
        this.initCityCode();
        this.initColorImage();
        this.initYellowImage();
        this.initYellowNovel();
    }

    /**
     * ????????????????????????????????????
     */
    private void initCityCode() {

        List<AdCityInfo> adCityInfos = adCodeMapper.selectList(null);

        Boolean resetFlag = false;
        if (redisService.hasKey(CommonConstant.REDIS_CITY_KEY)) {
            List<AdCityInfo> redisInfos = JSONArray.parseArray(redisService.get(CommonConstant.REDIS_CITY_KEY).toString(), AdCityInfo.class);
            log.info("?????????mysql??????????????????{}??????redis??????????????????{}???",adCityInfos.size(),redisInfos.size());

            resetFlag = adCityInfos.size() != redisInfos.size();
        }

        if(!redisService.hasKey(CommonConstant.REDIS_CITY_KEY)){
            log.info("?????????redis??????????????????????????????");
            resetFlag = true;
        }

        if (resetFlag) {
            log.info("??????????????????????????????!");
            redisTemplate.opsForValue().set(CommonConstant.REDIS_CITY_KEY, JSON.toJSONString(adCityInfos));
            log.info("?????????????????????????????????");
        }
    }

    /**
     * ????????????url
     */
    private void initColorImage() {

        List<ColorImage> colorImages = colorImageMapper.selectList(null);

        Boolean resetFlag = false;
        if(redisService.hasKey(CommonConstant.REDIS_COLOR_IMAGE_KEY)){
            Long redisSize = redisService.lGetListSize(CommonConstant.REDIS_COLOR_IMAGE_KEY);
            log.info("?????????mysql????????????{}??????redis????????????{}???",colorImages.size(),redisSize);
            resetFlag = !Long.valueOf(colorImages.size()).equals(redisSize);
        }

        if(!redisService.hasKey(CommonConstant.REDIS_COLOR_IMAGE_KEY)){
            log.info("?????????redis????????????????????????");
            resetFlag = true;
        }

        if (resetFlag) {
            log.info("????????????????????????!");
            colorImages.forEach(item -> {
                redisService.lSet(CommonConstant.REDIS_COLOR_IMAGE_KEY, item.getUrl());
                log.info("??????[" + item.getId() + "] url = " + item.getUrl());
            });
            log.info("????????????????????????!");
        }
    }

    private void initYellowImage() {

        List<YellowImage> yellowImages = yellowImageMapper.selectList(null);

        Boolean resetFlag = false;
        if(redisService.hasKey(CommonConstant.REDIS_YELLOW_IMAGE_KEY)){
            Long redisSize = redisService.lGetListSize(CommonConstant.REDIS_YELLOW_IMAGE_KEY);
            log.info("?????????mysql yellow????????????{}??????redis yellow?????????{}???",yellowImages.size(),redisSize);
            resetFlag = !Long.valueOf(yellowImages.size()).equals(redisSize);
        }

        if(!redisService.hasKey(CommonConstant.REDIS_YELLOW_IMAGE_KEY)){
            log.info("?????????redis?????????yellow???????????????");
            resetFlag = true;
        }

        if (resetFlag) {
            log.info("????????????yellow image???");
            yellowImages.forEach(image -> {
                redisService.lSet(CommonConstant.REDIS_YELLOW_IMAGE_KEY, JSON.toJSONString(image));
                log.info("??????[" + image.getId() + "]" + image.getTitle());
            });
            log.info("yellow image???????????????");
        }
    }

    private void initYellowNovel() {

        List<YellowNovel> yellowNovels = yellowNovelMapper.selectList(null);

        Boolean resetFlag = false;
        if(redisService.hasKey(CommonConstant.REDIS_YELLOW_NOVEL_KEY)){
            Long redisSize = redisService.lGetListSize(CommonConstant.REDIS_YELLOW_NOVEL_KEY);
            log.info("?????????mysql yellow????????????{}??????redis yellow????????????{}???",yellowNovels.size(),redisSize);
            resetFlag = !Long.valueOf(yellowNovels.size()).equals(redisSize);
        }

        if(!redisService.hasKey(CommonConstant.REDIS_YELLOW_NOVEL_KEY)){
            log.info("?????????redis?????????yellow????????????");
            resetFlag = true;
        }

        if (resetFlag) {
            redisTemplate.opsForList().trim(CommonConstant.REDIS_YELLOW_NOVEL_KEY, 1L, 0L);
            log.info("????????????yellow novel???");
            yellowNovels.forEach(image -> {
                redisService.lSet(CommonConstant.REDIS_YELLOW_NOVEL_KEY, JSON.toJSONString(image));
                log.info("??????[" + image.getId() + "]" + image.getTitle());
            });
            log.info("yellow novel???????????????");
        }
    }
}
