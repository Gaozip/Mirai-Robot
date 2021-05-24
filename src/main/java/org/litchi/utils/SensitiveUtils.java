package org.litchi.utils;

import com.alibaba.druid.sql.visitor.functions.Char;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.litchi.entity.SensitiveWord;
import org.litchi.mapper.SensitiveWordsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author Zero
 */
@Slf4j
@Component
public class SensitiveUtils {

    private SensitiveWordsMapper sensitiveWordsMapper;

    @Autowired
    public void setSensitiveWordRepository(SensitiveWordsMapper sensitiveWordsMapper) {
        this.sensitiveWordsMapper = sensitiveWordsMapper;
    }

    /**
     * 根节点
     */
    private TrieNode rootNode = new TrieNode();

    /**
     * 容器实例化Bean构造器,服务初始化
     */
    @PostConstruct
    public void init() {
        try {

            rootNode = new TrieNode();
            List<SensitiveWord> wordList = sensitiveWordsMapper.selectList(null);
            if (wordList.size() <= 0) {
                log.info("从数据库加载敏感词失败");
                return;
            }
            for (SensitiveWord sensitiveWord : wordList) {
                // 添加到前缀树
                this.addKeyword(sensitiveWord.getWord());
            }
            log.info("敏感词库加载完成,当前词条数为[{}]", size);
        } catch (Exception e) {
            log.info("敏感词库加载异常: [{}]", e.getMessage());
        }
    }



    /**
     * trie树中拥有多少分枝（多少个敏感词）
     */
    private static int size;

    /**
     * 前缀树
     */
    private static class TrieNode {

        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 子节点(key是下级字符,value是下级节点)
        private final Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        /**
         * 添加子节点
         */
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        /**
         * 获取子节点
         */
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }

    /**
     * 判断是否为符号
     */
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 将敏感词添加到前缀树中
     *
     * @param keyword 敏感词
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            // 指向子节点,进入下一轮循环
            tempNode = subNode;
            // 设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
        size++;
    }

    /**
     * 判断是否为敏感词
     *
     * @param text 待检查文本
     * @return 是否为敏感词
     */
//    public Boolean contains(String text) {
//
//        String hitText = "";
//
//        if (text.isEmpty()) {
//            return null;
//        }
//        // 指针1
//        TrieNode tempNode = rootNode;
//        // 指针2,指针2
//        int begin = 0, position = 0;
//
//        while (position < text.length()) {
//            char c = text.charAt(position);
//            // 跳过符号
//            if (isSymbol(c)) {
//                hitText += c;
//                // 若指针1处于根节点,将此符号计入结果,让指针2向下走一步
//                if (tempNode == rootNode) {
//                    begin++;
//                }
//                // 无论符号在开头或中间,指针3都向下走一步
//                position++;
//                continue;
//            }
//            // 检查下级节点
//            tempNode = tempNode.getSubNode(c);
//            if (tempNode == null) {
//
//                hitText = "";
//
//                // 进入下一个位置
//                position = ++begin;
//                // 重新指向根节点
//                tempNode = rootNode;
//            } else if (tempNode.isKeywordEnd()) {
//                hitText += c;
//                System.out.println(hitText);
//                // 发现敏感词，直接返回true
//                return true;
//            } else {
//
//                hitText += c;
//                position++;
//            }
//        }
//        return false;
//    }

    public String hitSensitive(String text){


        //用户所发
        String hitSensitiveText = "";

        if (text.isEmpty()) {
            return hitSensitiveText;
        }
        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2,指针2
        int begin = 0, position = 0;

        while (position < text.length()) {
            char c = text.charAt(position);
            // 跳过符号
            if (isSymbol(c)) {
                hitSensitiveText += c;
                // 若指针1处于根节点,将此符号计入结果,让指针2向下走一步
                if (tempNode == rootNode) {
                    begin++;
                }
                // 无论符号在开头或中间,指针3都向下走一步
                position++;
                continue;
            }
            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {

                hitSensitiveText = "";

                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                hitSensitiveText += c;
                // 发现敏感词，直接返回true

                return hitSensitiveText;
            } else {
                hitSensitiveText += c;
                position++;
            }
        }
        return "";
    }
}
