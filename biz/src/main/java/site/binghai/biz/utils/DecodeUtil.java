package site.binghai.biz.utils;

import org.springframework.stereotype.Service;
import site.binghai.lib.utils.BaseBean;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

@Service
public class DecodeUtil extends BaseBean {

    public void urlDecode(Map<String, String> ctx) {
        ctx.forEach((k, v) -> {
            try {
                ctx.put(k, URLDecoder.decode(v, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error("urlDecode error!", e);
            }
        });
    }
}
