package site.binghai.biz.service;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import site.binghai.biz.def.Result;
import site.binghai.biz.entity.WxCallbackIdentification;
import site.binghai.biz.enums.WxCallbackIdentificationStatusEnum;
import site.binghai.lib.service.BaseService;

import javax.transaction.Transactional;
import java.util.UUID;


@Service
public class WxCallbackIdentificationService extends BaseService<WxCallbackIdentification> {
    //过期时间 3分钟
    private Long expireTs = 3 * 60 * 1000L;

    @Transactional
    public WxCallbackIdentification newIdentification(Pair<String, String> params, String cbUrl, String ip) {
        WxCallbackIdentification i = new WxCallbackIdentification();
        i.setToken(UUID.randomUUID().toString());
        i.setStatus(WxCallbackIdentificationStatusEnum.INIT.name());
        i.setSessionKey(params.getFirst());
        i.setSessionValue(params.getSecond());
        i.setCallBackUrl(cbUrl);
        i.setClientIp(ip);
        return save(i);
    }

    @Transactional
    public Result<WxCallbackIdentification> identify(Long id, String token, String openId) {
        WxCallbackIdentification item = findById(id);
        if (item == null || !item.getToken().equals(token)) {
            return new Result(false, "非法参数", null, null);
        }
        if (!item.getStatus().equals(WxCallbackIdentificationStatusEnum.INIT.name())) {
            return new Result(false, "二维码已失效", null, null);
        }
        if (now() - item.getCreated() > expireTs) {
            item.setStatus(WxCallbackIdentificationStatusEnum.EXPIRED.name());
            update(item);
            return new Result(false, "二维码已过期", null, null);
        }
        item.setStatus(WxCallbackIdentificationStatusEnum.DONE.name());
        item.setOpenId(openId);
        update(item);
        return new Result(item);
    }
}
