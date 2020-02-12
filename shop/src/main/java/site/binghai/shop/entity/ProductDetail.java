package site.binghai.shop.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.*;
import java.util.List;

/**
 *
 * @date 2020/2/1 上午10:55
 **/
@Data
@Entity
public class ProductDetail extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long productId;
    @Column(columnDefinition = "TEXT")
    /**
     * default
     * <img src="http://cdn.binghai.site/o_1dvvauk7ur3g16bi43orq219isa.png"/>
     * */
    private String html;
    @Transient
    private JSONObject infos;
    @Transient
    private JSONObject standards;
    @Transient
    private String submitForm;
    @Transient
    private Product product;
    @Transient
    private List<ProductComment> comments;
}
