package site.binghai.lib.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.lib.entity.WxInfo;
import site.binghai.lib.utils.BaseBean;
import site.binghai.lib.utils.HttpUtils;

@Service
public class WxCommonService extends BaseBean {
    private static final String WX_QUERY_USER_INFO
        = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
    @Autowired
    private AccessTokenService accessTokenService;

    public WxInfo getUserInfo(String openId) {
        JSONObject ret =
            HttpUtils.sendJSONGet(String.format(WX_QUERY_USER_INFO, accessTokenService.get(), openId), null);
        if (ret != null) {
            logger.info("query WxInfo :{}", ret);
            return ret.toJavaObject(WxInfo.class);
        }
        return null;
    }
}
