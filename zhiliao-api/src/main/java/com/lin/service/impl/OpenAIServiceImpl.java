package com.lin.service.impl;


import com.alibaba.fastjson.JSON;
import com.lin.dao.mapper.OpenaiAnswersMapper;
import com.lin.dao.pojo.OpenaiAnswers;
import com.lin.service.OpenAIService;
import com.lin.vo.ai.AIAnswer;
import com.lin.vo.ai.Choices;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
@Async("taskExecutor")
public class OpenAIServiceImpl implements OpenAIService {


    @Resource
    OpenaiAnswersMapper openaiAnswersMapper;

    private Logger logger = LoggerFactory.getLogger(OpenAIServiceImpl.class);

//    @Value("${chatbot-api.openAiKey}")
//    private String openAiKey;

    @Override
    public String doChatGPT(String question) throws IOException {
        String openAiKey="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";

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpPost post = new HttpPost("https://api.openai.com/v1/completions");
        post.addHeader("Content-Type", "application/json");
        post.addHeader("Authorization", "Bearer " + openAiKey);

        String paramJson = "{\"model\": \"text-davinci-003\", \"prompt\": \"" + question + "\", \"temperature\": 0, \"max_tokens\": 1024}";

        StringEntity stringEntity = new StringEntity(paramJson, ContentType.create("text/json", "UTF-8"));
        post.setEntity(stringEntity);

        CloseableHttpResponse response = httpClient.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String jsonStr = EntityUtils.toString(response.getEntity());
            AIAnswer aiAnswer = JSON.parseObject(jsonStr, AIAnswer.class);
            StringBuilder answers = new StringBuilder();
            List<Choices> choices = aiAnswer.getChoices();
            for (Choices choice : choices) {
                answers.append(choice.getText());
            }

            return answers.toString();
        } else {
            logger.error("api.openai.com Err Code is " + response.getStatusLine().getStatusCode());
            return null;
        }
    }

    /**
     * 保存ChatGPT数据
     * @param content 文本
     * @param id 文章id
     * @param isEdit 是否修改
     * @throws IOException
     */

    @Override
    public void generateAnswers(String content, Long id, Boolean isEdit) throws IOException {
        OpenaiAnswers openaiAnswers = new OpenaiAnswers();
        //更新
        if(isEdit){
            OpenaiAnswers openaiAnswersRest = openaiAnswersMapper.selectById(id);
            openaiAnswers.setId(openaiAnswersRest.getId());
        }
        //新增
        openaiAnswers.setArticleId(String.valueOf(id));
        String s = this.doChatGPT(content);
        if(s!=null){
            openaiAnswers.setAnswer(s);
            openaiAnswersMapper.insert(openaiAnswers);
        }
    }



}
