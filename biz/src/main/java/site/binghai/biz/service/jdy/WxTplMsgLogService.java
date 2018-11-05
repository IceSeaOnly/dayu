package site.binghai.biz.service.jdy;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.jdy.WxTplMsg;
import site.binghai.lib.service.BaseService;

import javax.transaction.Transactional;

@Service
public class WxTplMsgLogService extends BaseService<WxTplMsg> {

    @Transactional
    public void save(JSONObject jsonObject, String ret) {
        WxTplMsg msg = new WxTplMsg();
        msg.setOpenId(jsonObject.getString("touser"));
        msg.setUrl(jsonObject.getString("url"));
        msg.setText(jsonObject.getString("data"));
        msg.setTplId(jsonObject.getString("template_id"));
        msg.setSendResult(ret);

        save(msg);
    }
}
