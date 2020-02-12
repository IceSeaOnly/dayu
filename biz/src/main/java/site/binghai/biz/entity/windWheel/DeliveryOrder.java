package site.binghai.biz.entity.windWheel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import site.binghai.lib.entity.PayBizEntity;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.utils.TimeTools;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @date 2018/12/3 下午10:56
 **/
@Data
@Entity
public class DeliveryOrder extends PayBizEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String userName;
    private String openId;
    private String userPhone;
    private String userAddress;
    private String expressBrand;
    private Long expressId;
    //寄出日期
    private String expressOutDate;
    //快递联系电话
    private String expressPhone;
    private String remark;
    //管理员备注,json
    @Column(columnDefinition = "TEXT")
    private String manageRemark;

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.EXPRESS_DELIVERY;
    }

    /**
     * 管理员追加备注
     */
    public void appendManageRemark(String userName, String text) {
        JSONArray array;
        if (StringUtils.isBlank(manageRemark)) {
            array = new JSONArray();
        } else {
            array = JSONArray.parseArray(manageRemark);
        }
        JSONObject remark = new JSONObject();
        remark.put("time", TimeTools.now());
        remark.put("who", userName);
        remark.put("content", text);
        array.add(remark);

        manageRemark = array.toJSONString();
    }
}
