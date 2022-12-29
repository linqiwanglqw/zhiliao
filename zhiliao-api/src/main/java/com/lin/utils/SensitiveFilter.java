package com.lin.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 过滤词
 */
@Component
public class SensitiveFilter {


    //替换符
    private final static String SUBSTITUTOR = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();


    //前缀树
    private class TrieNode {

        //关键词结束标识
        private boolean isKeyWordEnd = false;


        //子节点
        //下级字符：key 为单个字符 （Character只需要对单个字符操作）
        //下级节点：value 对应节点
        private final Map<Character, TrieNode> childNode = new HashMap<>();

        //添加子节点
        public void addChildNode(Character character, TrieNode trieNode) {
            childNode.put(character, trieNode);
        }

        //获取子节点
        public TrieNode getChildNode(Character character) {
            return childNode.get(character);
        }

        //get
        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        //set
        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }
    }

    //加载sensitiveWords.txt中的过滤词
    @PostConstruct
    public void init() {
        try {
            //字节流
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("sensitiveWords.txt");
            //字符流
            InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
            //字符缓冲流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String keyWord;
            while ((keyWord = bufferedReader.readLine()) != null) {
                //添加到前缀树中
                this.addKeyWord(keyWord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //添加到前缀树
    private void addKeyWord(String keyWord) {
        //初始化一个首节点
        TrieNode trieNode = rootNode;
        for (int i = 0; i < keyWord.length(); i++) {
            //当前节点
            char c = keyWord.charAt(i);
            //先判断当前节点的子节点是否存在需要添加的字符,map如果不存在返回null，存在返回value节点
            TrieNode childNode = trieNode.getChildNode(c);
            if (childNode == null) {
                //不存在则添加新节点
                childNode = new TrieNode();
                trieNode.addChildNode(c, childNode);
            }
            //指向子节点，也就是进下一层   childNode可能是原来的节点，可以可能是新增的节点
            trieNode = childNode;
            //设置结束标识
            if (i == (keyWord.length() - 1)) {
                trieNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     *
     * @param text 需要过滤的文本
     * @return 过滤完成的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        //树指针
        TrieNode trieNode = rootNode;
        //文本起始指针
        int begin = 0;
        //文本偏移量指针
        int position = 0;
        //返回的结果
        StringBuilder resString = new StringBuilder();

        //终止条件是begin指针到最后一个字符串的位置
        while (position < text.length()) {
            char c = text.charAt(position);

            //特殊符号
            if (isSymbol(c)) {
                //如果树指针在根节点，则直接添加，并让文本起始指针加1
                if (trieNode == rootNode) {
                    resString.append(c);
                    begin++;
                }
                //无论如何position都会加1
                position++;
                continue;
            }

            //检查是否有该字符串的子节点
            // 树节点向下遍历
            trieNode = trieNode.getChildNode(c);
            //如果为null，说明没有begin开头的违禁词
            if (trieNode == null) {
                resString.append(text.charAt(begin));
                //begin向下遍历进入下一个字符 树节点也归位
                position = ++begin;
                trieNode = rootNode;
            } else if (trieNode.isKeyWordEnd()) {
                //childNode.isKeyWordEnd为true则代表是违禁词的最后一个字符
                resString.append(SUBSTITUTOR);
                //position向下遍历进入下一个字符 树节点也归位
                begin = ++position;
                trieNode = rootNode;
            } else {
                position++;
            }

            //position超过最尾节点，需要保证position和begin同时到达最尾节点，避免剩余节点没有被检测
            if (position == text.length() && begin !=position){
                //并且说明当前begin位置没有敏感词
                resString.append(text.charAt(begin));
                //从begin开始遍历
                position = ++begin;
                trieNode = rootNode;
            }
        }

        return resString.toString();
    }

    /**
     * 判断是否为符号
     *
     * @return 符号返回true，不是符号返回false
     */
    private Boolean isSymbol(Character character) {
        //0x2E80~0x9FFF 是东亚文字范围   ! isAsciiAlphanumeric特殊符号返回true
        return !CharUtils.isAsciiAlphanumeric(character) && (character < 0x2E80 || character > 0x9FFF);
    }


}
