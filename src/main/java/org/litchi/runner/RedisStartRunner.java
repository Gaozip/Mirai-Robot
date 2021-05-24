package org.litchi.runner;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.litchi.constant.CommonConstant;
import org.litchi.entity.AdCityInfo;
import org.litchi.entity.ColorImage;
import org.litchi.mapper.AdCodeMapper;
import org.litchi.mapper.ColorImageMapper;
import org.litchi.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: gaozp
 * @date: 2021-04-18 22:00
 * @desc:
 */

@Slf4j
@Component
public class RedisStartRunner implements ApplicationRunner {


    @Resource
    private RedisTemplate redisTemplate;
    
    @Resource
    private AdCodeMapper adCodeMapper;

    @Override
    public void run(ApplicationArguments args) {
        this.initRedis();
    }

    private void initRedis(){

        this.initCityCode();
        this.initColorImage();
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


    @Resource
    private ColorImageMapper colorImageMapper;

    @Autowired
    private RedisService redisService;

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
}
