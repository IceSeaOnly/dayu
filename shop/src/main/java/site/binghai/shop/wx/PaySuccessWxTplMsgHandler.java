package site.binghai.shop.wx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.binghai.biz.def.WxTplMessageHandler;
import site.binghai.biz.entity.jdy.WxTplMsg;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.utils.BaseBean;

/**
 *
 * @date 2020/2/10 上午9:31
 **/
@Component
public class PaySuccessWxTplMsgHandler extends BaseBean implements WxTplMessageHandler {
    @Autowired
    private IceConfig iceConfig;

    @Override
    public String focusOnTplId() {
        return iceConfig.getPaySuccessTplId();
    }

    @Override
    public void accept(WxTplMsg message) {

    }
}
