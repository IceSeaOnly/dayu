package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.AuditRecord;
import site.binghai.biz.entity.anywish.Wish;
import site.binghai.biz.enums.AuditStatusEnum;
import site.binghai.biz.enums.AuditTypeEnum;
import site.binghai.biz.service.AuditRecordService;
import site.binghai.biz.service.WishService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.WxUser;

import java.util.List;

@RestController
@RequestMapping("/user/anywish/")
public class AnyWishController extends BaseController {

    @Autowired
    private WishService wishService;
    @Autowired
    private AuditRecordService auditRecordService;

    @PostMapping("create")
    public Object create(@RequestParam Wish wish) {
        if (wish == null) {
            return fail("Error Request");
        }

        if (hasEmptyString(wish.getAge(), wish.getGender(), wish.getPhone(), wish.getText())) {
            return fail("Incomplete Request");
        }

        WxUser user = getUser();
        wish.setOpenId(user.getOpenId());
        wish.setWxAvatar(user.getAvatar());
        wish.setNickName(user.getUserName());
        wish.setId(null);
        wish.setReply(null);
        wish.setStatus(AuditStatusEnum.INIT.code);
        wish = wishService.save(wish);

        AuditRecord auditRecord = auditRecordService.create(AuditTypeEnum.ANY_WISH, wish.getId());
        wish.setAuditId(auditRecord.getId());
        wishService.update(wish);

        return success(wish, null);
    }

    @GetMapping("my")
    public Object my() {
        List<Wish> wishList = wishService.findMy(getUser());
        return success(wishList, null);
    }

    @GetMapping("query")
    private Object query(@RequestParam Long wishId) {
        Wish wish = wishService.findById(wishId);
        if (null == wish || !wish.getOpenId().equals(getUser().getOpenId())) {
            return fail("not exist");
        }
        return success(wish, null);
    }
}
