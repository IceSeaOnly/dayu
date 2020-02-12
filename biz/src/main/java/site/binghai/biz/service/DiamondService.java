package site.binghai.biz.service;

import com.alibaba.edas.acm.ConfigService;
import com.alibaba.edas.acm.exception.ConfigException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.service.AbastractMultiKVCacheService;

import java.util.Properties;

/**
 * 阿里云KV服务
 */
//@Service
public class DiamondService extends AbastractMultiKVCacheService<String, String> implements InitializingBean {
    @Autowired
    protected IceConfig iceConfig;
    protected final static String DEFAULT_GROUP = "DEFAULT_GROUP";

    @Override
    protected long setExpiredSecs() {
        return 300;
    }

    @Override
    protected String load(String key) {
        try {
            return ConfigService.getConfig(key, DEFAULT_GROUP, 3000);
        } catch (ConfigException e) {
            logger.error("load data from diamond error! key:{}", key, e);
        }
        return null;
    }

    public JSONObject getConf(String key) {
        return JSONObject.parseObject(get(key));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Properties properties = new Properties();
        properties.put("endpoint", "acm.aliyun.com");
        properties.put("namespace", iceConfig.getAliyunAcmNamespace());
        properties.put("accessKey", iceConfig.getAliyunAcmAccessKey());
        properties.put("secretKey", iceConfig.getAliyunAcmSecretKey());
        ConfigService.init(properties);
        logger.info("=========== DiamondService started ===========");
    }
}
