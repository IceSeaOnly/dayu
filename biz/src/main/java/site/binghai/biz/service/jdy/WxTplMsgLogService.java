package site.binghai.biz.service.jdy;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.biz.consts.DiamondKey;
import site.binghai.biz.def.WxTplMessageHandler;
import site.binghai.biz.entity.jdy.WxTplMsg;
import site.binghai.biz.service.DiamondService;
import site.binghai.lib.service.BaseService;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WxTplMsgLogService extends BaseService<WxTplMsg> {
    private Map<String, List<WxTplMessageHandler>> handlers;
    @Autowired
    private DiamondService diamondService;

    @Transactional
    public void save(JSONObject jsonObject, String ret) {
        WxTplMsg msg = new WxTplMsg();
        msg.setOpenId(jsonObject.getString("touser"));
        msg.setUrl(jsonObject.getString("url"));
        msg.setText(jsonObject.getString("data"));
        msg.setTplId(jsonObject.getString("template_id"));
        msg.setSendResult(ret);

        save(msg);
        try {
            handle(msg);
        } catch (Exception e) {
            logger.error("handle wx message error!", e);
        }
    }

    private void handle(WxTplMsg msg) {
        if (isEmptyList(handlers.get(msg.getTplId()))) {
            return;
        }

        JSONObject map = JSONObject.parseObject(diamondService.get(DiamondKey.DISABLED_MESSAGE_LISTENER_MAP));
        handlers.get(msg.getTplId()).forEach(v -> {
            if (!map.containsKey(v.getClass().getSimpleName())) {
                v.accept(msg);
            }
        });
    }

    @Autowired
    public void listIn(List<WxTplMessageHandler> all) {
        handlers = new HashMap<>();
        for (WxTplMessageHandler each : all) {
            if (isEmptyList(handlers.get(each.focusOnTplId()))) {
                handlers.put(each.focusOnTplId(), emptyList());
            }
            handlers.get(each.focusOnTplId()).add(each);
        }
    }
}
