package com.java.xval.val.mq;


public class MqConstant {

    /**
     * top
     */
    public static class Top {

        public static final String USER_ORDER_TOPIC = "ORDER";
        public static final String PAY_TOPIC = "PAY_TOPIC";
    }


    /**
     * consumeGroup 消费者
     */
    public static class ConsumeGroup {
        public static final String USER_ORDER_GROUP = "ORDER_CONSUMER_GROUP";
    }


}
