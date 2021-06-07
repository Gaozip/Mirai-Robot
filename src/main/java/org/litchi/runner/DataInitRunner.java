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
     * 缓存天气预报城市编码数据
     */
    private void initCityCode() {

        List<AdCityInfo> adCityInfos = adCodeMapper.selectList(null);

        Boolean resetFlag = false;
        if (redisService.hasKey(CommonConstant.REDIS_CITY_KEY)) {
            List<AdCityInfo> redisInfos = JSONArray.parseArray(redisService.get(CommonConstant.REDIS_CITY_KEY).toString(), AdCityInfo.class);
            log.info("检测到mysql城市编码数据{}条，redis城市编码数据{}条",adCityInfos.size(),redisInfos.size());

            resetFlag = adCityInfos.size() != redisInfos.size();
        }

        if(!redisService.hasKey(CommonConstant.REDIS_CITY_KEY)){
            log.info("检测到redis中暂无城市编码数据！");
            resetFlag = true;
        }

        if (resetFlag) {
            log.info("开始缓存城市编码数据!");
            redisTemplate.opsForValue().set(CommonConstant.REDIS_CITY_KEY, JSON.toJSONString(adCityInfos));
            log.info("城市编码数据缓存完成！");
        }
    }

    /**
     * 缓存涩图url
     */
    private void initColorImage() {

        List<ColorImage> colorImages = colorImageMapper.selectList(null);

        Boolean resetFlag = false;
        if(redisService.hasKey(CommonConstant.REDIS_COLOR_IMAGE_KEY)){
            Long redisSize = redisService.lGetListSize(CommonConstant.REDIS_COLOR_IMAGE_KEY);
            log.info("检测到mysql涩图数据{}条，redis涩图数据{}条",colorImages.size(),redisSize);
            resetFlag = !Long.valueOf(colorImages.size()).equals(redisSize);
        }

        if(!redisService.hasKey(CommonConstant.REDIS_COLOR_IMAGE_KEY)){
            log.info("检测到redis中暂无涩图数据！");
            resetFlag = true;
        }

        if (resetFlag) {
            log.info("开始缓存涩图数据!");
            colorImages.forEach(item -> {
                redisService.lSet(CommonConstant.REDIS_COLOR_IMAGE_KEY, item.getUrl());
                log.info("缓存[" + item.getId() + "] url = " + item.getUrl());
            });
            log.info("涩图数据缓存完成!");
        }
    }

    private void initYellowImage() {

        List<YellowImage> yellowImages = yellowImageMapper.selectList(null);

        Boolean resetFlag = false;
        if(redisService.hasKey(CommonConstant.REDIS_YELLOW_IMAGE_KEY)){
            Long redisSize = redisService.lGetListSize(CommonConstant.REDIS_YELLOW_IMAGE_KEY);
            log.info("检测到mysql yellow图片数据{}条，redis yellow图数据{}条",yellowImages.size(),redisSize);
            resetFlag = !Long.valueOf(yellowImages.size()).equals(redisSize);
        }

        if(!redisService.hasKey(CommonConstant.REDIS_YELLOW_IMAGE_KEY)){
            log.info("检测到redis中暂无yellow图片数据！");
            resetFlag = true;
        }

        if (resetFlag) {
            log.info("开始缓存yellow image！");
            yellowImages.forEach(image -> {
                redisService.lSet(CommonConstant.REDIS_YELLOW_IMAGE_KEY, JSON.toJSONString(image));
                log.info("缓存[" + image.getId() + "]" + image.getTitle());
            });
            log.info("yellow image缓存完成！");
        }
    }

    private void initYellowNovel() {

        List<YellowNovel> yellowNovels = yellowNovelMapper.selectList(null);

        Boolean resetFlag = false;
        if(redisService.hasKey(CommonConstant.REDIS_YELLOW_NOVEL_KEY)){
            Long redisSize = redisService.lGetListSize(CommonConstant.REDIS_YELLOW_NOVEL_KEY);
            log.info("检测到mysql yellow小说数量{}条，redis yellow小说数量{}条",yellowNovels.size(),redisSize);
            resetFlag = !Long.valueOf(yellowNovels.size()).equals(redisSize);
        }

        if(!redisService.hasKey(CommonConstant.REDIS_YELLOW_NOVEL_KEY)){
            log.info("检测到redis中暂无yellow图数据！");
            resetFlag = true;
        }

        if (resetFlag) {
            redisTemplate.opsForList().trim(CommonConstant.REDIS_YELLOW_NOVEL_KEY, 1L, 0L);
            log.info("开始缓存yellow novel！");
            yellowNovels.forEach(image -> {
                redisService.lSet(CommonConstant.REDIS_YELLOW_NOVEL_KEY, JSON.toJSONString(image));
                log.info("缓存[" + image.getId() + "]" + image.getTitle());
            });
            log.info("yellow novel缓存完成！");
        }
    }
}
