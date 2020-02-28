package site.binghai.shop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author huaishuo
 * @date 2020/2/28 下午10:10
 **/
@RequestMapping("manage")
@Controller
public class ImageUploaderController {

    @GetMapping("imageUploader")
    public String imageUploader() {
        return "manage/imageUploader";
    }
}
