package site.binghai.biz.service;

import com.alibaba.edas.acm.ConfigService;
import com.alibaba.edas.acm.exception.ConfigException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import site.binghai.lib.service.AbastractMultiKVCacheService;

import java.util.Properties;

/**
 * 阿里云KV服务
 * */
@Service
public class DiamondService extends AbastractMultiKVCacheService<String, String> implements InitializingBean {
    @Override
    protected long setExpiredSecs() {
        return 300;
    }

    @Override
    protected String load(String key) {
        try {
            return ConfigService.getConfig(key, "DEFAULT_GROUP", 3000);
        } catch (ConfigException e) {
            logger.error("load data from diamond error! key:{}", key, e);
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Properties properties = new Properties();
        properties.put("endpoint", "acm.aliyun.com");
        properties.put("namespace", "0db50897-d72a-4511-9b94-471bb5417909");
        properties.put("accessKey", "LTAIbAtcJCgmnpCo");
        properties.put("secretKey", "s2pLvP2evIsjE1uxkv4s6GA1N7VLL3");
        ConfigService.init(properties);
        logger.info("=========== DiamondService started ===========");
    }
}
