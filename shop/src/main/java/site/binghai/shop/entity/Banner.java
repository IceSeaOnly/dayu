package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @date 2020/1/31 下午8:39
 **/
@Data
@Entity
public class Banner extends BaseEntity {
    @GeneratedValue
    @Id
    private Long id;
    private String title;
    private String url;

    public Banner(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public Banner() {
    }
}
