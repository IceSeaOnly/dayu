package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.entity.Address;
import site.binghai.shop.entity.School;
import site.binghai.shop.service.AddressService;
import site.binghai.shop.service.SchoolService;

import java.util.List;
import java.util.Map;

/**
 * @date 2020/2/5 下午8:13
 **/
@Controller
@RequestMapping("shop")
public class AddressController extends BaseController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private SchoolService schoolService;

    @GetMapping("addressSelect")
    public String addressSelect(@RequestParam String cartIds, @RequestParam Long selectedCoupon, ModelMap map) {
        List<Address> addrs = addressService.findByUserId(getUser().getId());
        map.put("addrs", addrs);
        map.put("cartIds", cartIds);
        map.put("selectedCoupon", selectedCoupon);
        return "addressSelect";
    }

    @GetMapping("addAddress")
    public String addAddress(@RequestParam String cartIds, @RequestParam Long selectedCoupon, ModelMap map) {
        School school = schoolService.findById(getUser().getSchoolId());
        String recommendAddr = school.getSchoolName() + getUser().getCountry() + getUser().getProvince() + getUser()
            .getCity();
        map.put("cartIds", cartIds);
        map.put("selectedCoupon", selectedCoupon);
        map.put("recommendAddr", recommendAddr);
        map.put("recommendPhone", getUser().getUserName());
        map.put("recommendReceiver", getUser().getPhone());
        return "addressAdd";
    }

    @GetMapping("editAddress")
    public String editAddress(@RequestParam String cartIds, @RequestParam Long selectedCoupon,
                              @RequestParam Long updateId, ModelMap map) {
        Address address = addressService.findById(updateId);
        if (address == null || !address.getUserId().equals(getUser().getId())) {
            return e500("稳重点老弟，有错误");
        }
        map.put("address", address);
        map.put("cartIds", cartIds);
        map.put("selectedCoupon", selectedCoupon);
        return "addressEdit";
    }

    @PostMapping("addressAdd")
    @ResponseBody
    public Object addressAdd(@RequestBody Map map) {
        String receiverName = getString(map, "receiverName");
        String receiverPhone = getString(map, "receiverPhone");
        String receiverAddr = getString(map, "receiverAddr");
        Long updateId = getLong(map, "updateId");
        if (hasEmptyString(receiverAddr, receiverName, receiverPhone)) {
            return fail("你倒是填完整撒");
        }
        Address addr = new Address();
        addr.setUserId(getUser().getId());
        addr.setReceiverAddr(receiverAddr);
        addr.setReceiverName(receiverName);
        addr.setReceiverPhone(receiverPhone);
        if (updateId != null) {
            Address address = addressService.findById(updateId);
            if (address == null || !address.getUserId().equals(getUser().getId())) {
                return fail("稳重点老弟，有错误！");
            }
            addressService.delete(updateId);
        }
        addressService.save(addr);
        return success();
    }

    @ResponseBody
    @GetMapping("delAddress")
    public Object delAddress(@RequestParam Long id) {
        Address address = addressService.findById(id);
        if (address == null || !address.getUserId().equals(getUser().getId())) {
            return fail("稳重点老弟，看清楚了再点");
        }
        addressService.delete(id);
        return success();
    }
}
