package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @date 2018/11/28 下午5:03
 **/
@Data
@Entity
public class ExcelConvertEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    /*
    * update secret
    * */
    private String secret;
    private String token;

    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String previousContent;
}
