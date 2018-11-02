package site.binghai.biz.tasks;

import com.alibaba.fastjson.JSONObject;
import site.binghai.lib.service.AccessTokenService;
import site.binghai.lib.utils.IoUtils;

public class SendTemplateMsg extends PostSendBase
    implements Runnable {
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

    @Override
    public void run() {
        String sendResut = send();
        IoUtils.WriteCH("/data/wwwroot/notify/WxTplMsgSendLog.log",String.format("%s send result : %s\n", new Object[] {this.data, sendResut}));
    }
}
