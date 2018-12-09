package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.service.ExpressBrandService;
import site.binghai.lib.controller.BaseController;

import java.util.List;

/**
 * @author huaishuo
 * @date 2018/12/8 下午12:42
 **/
@RestController
@RequestMapping("/user/exp/brand")
public class ExpbrandController extends BaseController {
    @Autowired
    private ExpressBrandService expressBrandService;

    @GetMapping("list")
    public Object list(@RequestParam Integer type){
        List list = expressBrandService.findByType(type);

        return success(list,null);
    }
}
