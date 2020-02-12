package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.Address;

import java.util.List;

/**
 *
 * @date 2020/2/5 下午8:24
 **/
@Service
public class AddressService extends BaseService<Address> {

    public List<Address> findByUserId(Long userId) {
        Address exp = new Address();
        exp.setUserId(userId);
        return empty(sortQuery(exp, "id", Boolean.TRUE));
    }
}
