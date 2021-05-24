package org.litchi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.litchi.entity.AdCityInfo;
import org.springframework.stereotype.Component;

/**
 * @author: gaozp
 * @date: 2021-05-19 12:11
 * @desc:
 */
@Mapper
public interface AdCodeMapper extends BaseMapper<AdCityInfo> {
}
