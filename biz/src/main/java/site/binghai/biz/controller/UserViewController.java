package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.consts.DiamondKey;
import site.binghai.biz.service.DiamondService;
import site.binghai.biz.utils.DecodeUtil;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.utils.GroovyEngineUtils;
import site.binghai.lib.utils.UrlUtil;

import java.util.Map;

@RequestMapping("/user/view/")
@RestController
public class UserViewController extends BaseController {
    @Autowired
    private DiamondService diamondService;
    @Autowired
    private DecodeUtil decodeUtil;

    @RequestMapping("/page/{viewId}")
    public Object page(String nocache, @PathVariable String viewId) {
        String tpl = diamondService.get(viewId, nocache != null);
        if (hasEmptyString(tpl)) {
            return fail("no such page error!");
        }

        if (viewId.startsWith("dy")) {
            Map ctx = toJsonObject(getUser());
            ctx.putAll(UrlUtil.getRequestParams(getServletRequest()));
            decodeUtil.urlDecode(ctx);
            try {
                return GroovyEngineUtils.renderTemplate(String.valueOf(tpl), ctx);
            } catch (Exception e) {
                logger.error("render page error! viewId:{},ctx:{}", viewId, ctx, e);
                return diamondService.get(DiamondKey.ERROR_PAGE);
            }
        }

        return tpl;
    }
}
