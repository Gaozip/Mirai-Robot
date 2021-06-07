package org.litchi.runner;

import com.alibaba.fastjson.JSON;
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
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: gaozp
 * @date: 2021-04-18 22:00
 * @desc:
 */

@Slf4j
@Component
public class RedisStartRunner implements ApplicationRunner {

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

    @Override
    public void run(ApplicationArguments args) {
        this.initRedis();
    }

    private void initRedis(){

        this.initCityCode();
        this.initColorImage();
        this.initYellowImage();
        this.initYellowNovel();
    }

    /**
     * 缓存天气预报城市编码数据
     */
    private void initCityCode(){

        if(redisTemplate.hasKey(CommonConstant.REDIS_CITY_KEY)){
            redisTemplate.delete(CommonConstant.REDIS_CITY_KEY);
        }
        log.info("开始缓存城市编码数据!");

        if(!redisTemplate.hasKey(CommonConstant.REDIS_CITY_KEY)){
            List<AdCityInfo> adCityInfos = adCodeMapper.selectList(null);
            redisTemplate.opsForValue().set(CommonConstant.REDIS_CITY_KEY,JSON.toJSONString(adCityInfos));
            log.info("城市编码数据缓存完成！");
        }
    }

    /**
     * 缓存涩图url
     */
    private void initColorImage(){

        List<ColorImage> colorImages = colorImageMapper.selectList(null);

        Boolean hasKey = redisService.hasKey(CommonConstant.REDIS_COLOR_IMAGE_KEY);
        Long redisImageKeySize = null;
        if(hasKey){
            redisImageKeySize = redisService.lGetListSize(CommonConstant.REDIS_COLOR_IMAGE_KEY);
            if(!redisImageKeySize.equals(Long.valueOf(colorImages.size()))){
                redisService.lRemove(CommonConstant.REDIS_COLOR_IMAGE_KEY,redisImageKeySize,null);
            }
        }


        Boolean hasResetKey = redisImageKeySize != null && !redisImageKeySize.equals(Long.valueOf(colorImages.size()));

        if(!hasKey || hasResetKey){
            log.info("开始缓存涩图!");
            colorImages.forEach(item ->{
                redisService.lSet(CommonConstant.REDIS_COLOR_IMAGE_KEY,item.getUrl());
                log.info("开始缓存=" + item.getUrl());
            });
            log.info("涩图填充完成!");
        }
    }

    private void initYellowImage(){

        List<YellowImage> yellowImages = yellowImageMapper.selectList(null);

        Boolean hasKey = redisService.hasKey(CommonConstant.REDIS_YELLOW_IMAGE_KEY);
        Long redisImageKeySize = null;
        if(hasKey){
            redisImageKeySize = redisService.lGetListSize(CommonConstant.REDIS_YELLOW_IMAGE_KEY);
            if(!redisImageKeySize.equals(Long.valueOf(yellowImages.size()))){
                redisService.lRemove(CommonConstant.REDIS_YELLOW_IMAGE_KEY,redisImageKeySize,null);
            }
        }

        Boolean hasResetKey = redisImageKeySize != null && !redisImageKeySize.equals(Long.valueOf(yellowImages.size()));

        if(!hasKey || hasResetKey) {
            log.info("开始缓存yellow image！");
            yellowImages.forEach(image -> {
                redisService.lSet(CommonConstant.REDIS_YELLOW_IMAGE_KEY, JSON.toJSONString(image));
                log.info("缓存[" + image.getId() + "]" + image.getTitle());
            });
            log.info("yellow image缓存完成！");
        }
    }

    private void initYellowNovel(){
        List<YellowNovel> yellowImages = yellowNovelMapper.selectList(null);

        Boolean hasKey = redisService.hasKey(CommonConstant.REDIS_YELLOW_NOVEL_KEY);
        Long redisImageKeySize = null;
        if(hasKey){
            redisImageKeySize = redisService.lGetListSize(CommonConstant.REDIS_YELLOW_NOVEL_KEY);
            if(!redisImageKeySize.equals(Long.valueOf(yellowImages.size()))){
                redisService.lRemove(CommonConstant.REDIS_YELLOW_NOVEL_KEY,redisImageKeySize,null);
            }
        }

        Boolean hasResetKey = redisImageKeySize != null && !redisImageKeySize.equals(Long.valueOf(yellowImages.size()));

        if(!hasKey || hasResetKey) {
            log.info("开始缓存yellow novel！");
            yellowImages.forEach(image -> {
                redisService.lSet(CommonConstant.REDIS_YELLOW_NOVEL_KEY, JSON.toJSONString(image));
                log.info("缓存[" + image.getId() + "]" + image.getTitle());
            });
            log.info("yellow novel！");
        }
    }
}
