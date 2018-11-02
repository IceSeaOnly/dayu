package site.binghai.lib.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.utils.HttpUtils;

@Service
public class AccessTokenService extends AbastractCacheService<String> implements InitializingBean {
    private static AccessTokenService holder;
    private static final String QUERY_URL
        = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    @Autowired
    private IceConfig iceConfig;

    @Override
    public String load() {
        JSONObject ret =
            HttpUtils.sendJSONGet(String.format(QUERY_URL, iceConfig.getWxAppid(), iceConfig.getWxSecret()), null);
        return ret.getString("access_token");
    }

    @Override
    public long setExpiredSecs() {
        return 60 * 30;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        holder = this;
    }

    public static AccessTokenService getServiceHolder() {
        return holder;
    }
}
