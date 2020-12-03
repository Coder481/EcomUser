package com.example.ecomuser.fcmsender;

public class MsgFormatter {

    private static String sampleMsgFormat = "{" +
            "  \"to\": \"/topics/%s\"," +
            "  \"data\": {" +
            "       \"title\":\"%s\"," +
            //"       \"for\":\"%s\"," +
            "       \"body\":\"%s\"" +
            "   }" +
            "}";

    public static String getSampleMessage(String topic , String title , String body){
        return String.format(sampleMsgFormat,topic,title,body);
    }
}
