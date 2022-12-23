package com.lin.component;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * webSocketService
 */
@ServerEndpoint(value = "/imserver/{username}")
@Service
@RocketMQMessageListener(
        topic = "api-im-send-message-topic",
        selectorExpression = "SEND_IM_MSG",
        // 广播模式
        messageModel = MessageModel.BROADCASTING,
        consumerGroup = "api-im-group"
)
public class WebSocketServer implements RocketMQListener<String> {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 记录当前在线连接数
     */
    public static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();


    private static RocketMQTemplate rocketMQTemplate;

    //解决websocket中属性无法注入的问题
    @Autowired
    public void  setRocketMQTemplate(RocketMQTemplate rocketMQTemplate){
        WebSocketServer.rocketMQTemplate=rocketMQTemplate;
    }


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessionMap.put(username, session);
        log.info("有新用户加入，username={}, 当前在线人数为：{}", username, sessionMap.size());
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        result.set("users", array);
        for (Object key : sessionMap.keySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("username", key);
            // {"username", "zhang", "username": "admin"}
            array.add(jsonObject);
        }
//        {"users": [{"username": "zhang"},{ "username": "admin"}]}
        sendAllMessage(JSONUtil.toJsonStr(result));  // 后台发送消息给所有的客户端
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessionMap.remove(username);
        log.info("有一连接关闭，移除username={}的用户session, 当前在线人数为：{}", username, sessionMap.size());
    }

    /**
     * 收到客户端消息后调用的方法
     * 后台收到客户端发送过来的消息
     * onMessage 是一个消息的中转站
     * 接受 浏览器端 socket.send 发送过来的 json数据
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("username") String username) {
        log.info("服务端收到用户username={}的消息:{}", username, message);
        JSONObject obj = JSONUtil.parseObj(message);
        // to表示发送给哪个用户，比如 admin
        String toUsername = obj.getStr("to");
        String text = obj.getStr("text"); // 发送的消息文本  hello
        // {"to": "admin", "text": "聊天文本"}
        Session toSession = sessionMap.get(toUsername); // 根据 to用户名来获取 session，再通过session发送消息文本

        // 服务器端 再把消息组装一下，组装后的消息包含发送人和发送的文本内容
        // {"from": "zhang", "text": "hello"}
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("from", username);  // from 是 zhang
        jsonObject.set("text", text);  // text 同上面的text
        jsonObject.set("to",toUsername); //to

        //判断用户是否在线
        if (toSession != null && toSession.isOpen()) {

            this.sendMessage(jsonObject.toString(), toSession);
            log.info("发送给用户username={}，消息：{}", toUsername, jsonObject.toString());
        } else {
            //当前集群节点不存在，可能在其他节点
            rocketMQTemplate.convertAndSend("api-im-send-message-topic:SEND_IM_MSG",jsonObject);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
    private void sendMessage(String message, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }

    /**
     * 服务端发送消息给所有客户端
     */
    private void sendAllMessage(String message) {
        try {
            for (Session session : sessionMap.values()) {
                log.info("服务端给客户端[{}]发送消息{}", session.getId(), message);
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }

    /**
     * mq消费逻辑
     * @param message
     */
    @Override
    public void onMessage(String message) {
        System.out.println(message);

        JSONObject obj = JSONUtil.parseObj(message);
        String toUsername = obj.getStr("to"); // to表示发送给哪个用户，比如 admin
        Session toSession = sessionMap.get(toUsername);
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("from", obj.getStr("from"));  // from 是 zhang
        jsonObject.set("text", obj.getStr("text"));  // text 同上面的text
        jsonObject.set("to",toUsername); //to
        if (toSession != null && toSession.isOpen()) {
            this.sendMessage(jsonObject.toString(), toSession);
            log.info("发送给用户username={}，消息：{}", toUsername, jsonObject.toString());
        } else {
            log.info("发送失败，未找到用户username={}的session", toUsername);
        }
    }
}
