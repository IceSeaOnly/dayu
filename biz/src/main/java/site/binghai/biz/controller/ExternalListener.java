package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.service.turntable.TicketService;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.utils.MD5;

/**
 * 外部事件监听器
 */
@RequestMapping("/external/")
@RestController
public class ExternalListener extends BaseController {
    @Autowired
    private IceConfig iceConfig;

}
l