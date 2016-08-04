package com.feed.service;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.feed.FeedConfig;
import com.feed.KryoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2016/8/1.
 */
@Service
public class RMQService {
    private static final Logger log = LoggerFactory.getLogger(RMQService.class);
    private static DefaultMQProducer producer = new DefaultMQProducer("LZ_QUEUE");
    static {
        try {
            producer.setNamesrvAddr(FeedConfig.RMQ_NAME_SERVER);
            producer.start();
        } catch (MQClientException e) {
           log.error("MQ错误",e);
        }
    }
    /**
     * 不保证顺序发送
     */
    public boolean sendCurrentlyMsg(String topic, byte[] msgBytes){
        try{
            Message rocketMsg = new Message(topic, msgBytes);
            SendResult sendResult ;
            //重发三次后就不再重发
            for (int j = 0; j < 3; j++) {
                sendResult = producer.send(rocketMsg);
                if (sendResult.getSendStatus() == SendStatus.SEND_OK){
                    return true;
                }
            }
            return false;
        }catch (Exception e){
            log.error(String.format("send rocketmq msg error!, topic:%s", topic), e);
            return false;
        }
    }

    public static void main(String[] args) {
        RMQService rmqService =new RMQService();
        rmqService.sendCurrentlyMsg("lz",KryoUtils.writeKryoObject("lizhao"));


        producer.shutdown();
    }
}
