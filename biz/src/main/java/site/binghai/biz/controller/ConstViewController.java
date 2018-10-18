package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.service.DiamondService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.utils.GroovyEngineUtils;
import site.binghai.lib.utils.UrlUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/const/view/")
public class ConstViewController extends BaseController {
    @Autowired
    private DiamondService diamondService;

    /**
     * 随机生成一个序列号
     */
    @RequestMapping("makeSequenceId")
    public Object makeSequenceId() {
        return success(null, UUID.randomUUID().toString());
    }

    @RequestMapping("/page/{viewId}")
    public Object page(String nocache, @PathVariable String viewId) {
        Map<String, String> ctx = UrlUtil.getRequestParams(getServletRequest());
        urlDecode(ctx);
        String tpl = nocache == null ? diamondService.get(viewId) : diamondService.refreshGet(viewId);
        if (hasEmptyString(tpl)) {
            return fail("no such page error!");
        }

        try {
            String ret = GroovyEngineUtils.renderTemplate(tpl, ctx);
            return ret;
        } catch (Exception e) {
            logger.error("render page error! viewId:{},ctx:{}", viewId, ctx, e);
        }
        return fail("page error,viewId:" + viewId + ",time:" + now());
    }

    private void urlDecode(Map<String, String> ctx) {
        ctx.forEach((k, v) -> {
            try {
                ctx.put(k, URLDecoder.decode(v, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error("urlDecode error!", e);
            }
        });
    }

}
