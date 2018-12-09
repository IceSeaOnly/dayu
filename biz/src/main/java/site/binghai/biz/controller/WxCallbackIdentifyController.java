package site.binghai.biz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.lib.controller.BaseController;

/**
 * 微信扫码扩展入口
 * @author huaishuo
 * @date 2018/12/2 下午11:50
 **/
@RestController
@RequestMapping("/wx/callback/")
public class WxCallbackIdentifyController extends BaseController {

}
