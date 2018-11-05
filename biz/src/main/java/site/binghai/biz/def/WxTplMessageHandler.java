package site.binghai.biz.def;

import site.binghai.biz.entity.jdy.WxTplMsg;

public interface WxTplMessageHandler {
    String focusOnTplId();
    void accept(WxTplMsg message);
}
