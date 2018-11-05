package site.binghai.biz.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.biz.service.jdy.WxTplMsgLogService;
import site.binghai.biz.tasks.SendTemplateMsg;
import site.binghai.lib.utils.BaseBean;

@Service
public class WxTplMessageService extends BaseBean {

    @Autowired
    private WxTplMsgLogService wxTplMsgLogService;


    public String send(JSONObject content){
        String ret =  new SendTemplateMsg(content).send();
        wxTplMsgLogService.save(content,ret);
        return ret;
    }

}
