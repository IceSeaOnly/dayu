package site.binghai.biz.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.service.DiamondService;
import site.binghai.biz.utils.DecodeUtil;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.utils.GroovyEngineUtils;
import site.binghai.lib.utils.UrlUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/view/")
public class ConstViewController extends BaseController {
    @Autowired
    private DiamondService diamondService;
    @Autowired
    private DecodeUtil decodeUtil;

    /**
     * 随机生成一个序列号
     */
    @RequestMapping("makeSequenceId")
    public Object makeSequenceId() {
        return success(null, UUID.randomUUID().toString());
    }

    @RequestMapping("/page/const/{viewId}")
    public Object page(String nocache, @PathVariable String viewId) {
        String tpl = diamondService.get(viewId,nocache != null);
        if (hasEmptyString(tpl)) {
            return fail("no such page error!");
        }

        return tpl;
    }

    @RequestMapping("/page/dyna/{viewId}")
    public Object dynamicPage(String nocache, @PathVariable String viewId) {
        Object tpl = page(nocache, viewId);

        if (tpl instanceof JSONObject) {
            return tpl;
        }

        if (hasEmptyString(tpl)) {
            return fail("no such page error!");
        }

        Map<String, String> ctx = UrlUtil.getRequestParams(getServletRequest());
        decodeUtil.urlDecode(ctx);

        try {
            String ret = GroovyEngineUtils.renderTemplate(String.valueOf(tpl), ctx);
            return ret;
        } catch (Exception e) {
            logger.error("render page error! viewId:{},ctx:{}", viewId, ctx, e);
        }
        return fail("page error,viewId:" + viewId + ",time:" + now());
    }



}
