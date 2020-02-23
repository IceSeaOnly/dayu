package site.binghai.biz.tasks;

import com.alibaba.fastjson.JSONObject;
import site.binghai.lib.service.AccessTokenService;

public class SendTemplateMsg extends PostSendBase {
    private String data;
    private String token;

    public SendTemplateMsg(JSONObject content) {
        this.data = content.toString();
    }

    public String send() {
        this.token = AccessTokenService.getServiceHolder().get();
        if (this.token == null) {
            return null;
        }
        setPostUrl("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + this.token);
        setContent(this.data);
        return postSend();
    }
}
