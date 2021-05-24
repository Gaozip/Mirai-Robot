package org.litchi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.litchi.entity.SensitiveWord;

import java.util.Optional;

/**
 * @author: gaozp
 * @date: 2021-05-21 14:29
 * @desc:
 */
@Mapper
public interface SensitiveWordsMapper extends BaseMapper<SensitiveWord> {

    /**
     * 查询关键词是否存在
     * @param word
     * @return
     */
    @Select(value = "SELECT * FROM sensitive_word WHERE word = #{word}")
    Optional<SensitiveWord> findWord(@Param("word") String word);

    /**
     * 根据敏感词移除
     * @param word
     */
    @Delete(value = "delete from sensitive_word where trim(word) = #{word}")
    void deleteByWord(String word);
}
