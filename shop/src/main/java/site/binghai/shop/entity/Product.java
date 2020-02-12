package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @date 2020/2/1 上午10:50
 **/
@Entity
@Data
public class Product extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long categoryId;
    private String title;
    private String simpleDesc;
    private Integer price;
    private Integer previousPrice;
    private String tags;
    private Integer sold;
    private Integer stock;
    private String productNo;
    private String imgUrl;
    private String brand;
    /**
     * 具体分类
     */
    private String subType;
    /**
     * 可选规格，json，kv
     */
    private String standards;
    /**
     * kv信息->袖长:七分袖,销售渠道类型:纯电商...
     */
    private String infos;
    private Boolean offline;
    private Double startOfDesc;
    private Double startOfQuality;

    public void setStartOfDesc(Double startOfDesc) {
        this.startOfDesc = (this.startOfDesc * sold + startOfDesc) / (sold + 1);
    }

    public void setStartOfQuality(Double startOfQuality) {
        this.startOfQuality = (this.startOfQuality * sold + startOfQuality) / (sold + 1);
    }
}
