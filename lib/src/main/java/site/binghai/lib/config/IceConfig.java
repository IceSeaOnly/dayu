package site.binghai.lib.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ice")
@PropertySource("classpath:application.properties")
@Data
public class IceConfig implements InitializingBean {
    public static IceConfig iceConfigHolder;

    private String appName;
    private String appRoot;
    private String debugCode;

    private String miaodiSms_ACCOUNT_SID;
    private String miaodiSms_AUTH_TOKEN;

    private String aliyunAccessKeyId;
    private String aliyunAccessKeySecret;
    private String aliyunMQAccessKeyId;
    private String aliyunMQAccessKeySecret;
    private String aliyunMQAccountEndpoint;

    private String aliyunAcmNamespace;
    private String aliyunAcmAccessKey;
    private String aliyunAcmSecretKey;

    private String wxAppid;
    private String wxSecret;
    private String wxAuthenticationUrl;
    private String wxValidateMD5Key;
    private String wxPayUrl;
    private String wxRefundUrl;
    private String defaultAvatarUrl;
    private String subscribePage;

    // ------------- weixin tpl -------------
    private String paySuccessTplId;
    private String orderCancelTplId;
    private String statisticsResultTplId;
    //预约单提醒
    private String appointmentOrderTplId;

    private static Map<String, String> setupParams = new HashMap<>();

    public static String getSetupParam(String key) {
        return setupParams.get(key);
    }

    public static void addSetupParam(String k, String v) {
        setupParams.put(k, v);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        iceConfigHolder = this;
    }
}
