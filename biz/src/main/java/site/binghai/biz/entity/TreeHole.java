package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 树洞记录
 * */
@Entity
@Data
public class TreeHole extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String content;
    private Long auditId;
    private Long payId;
    private Long userId;
    private String openId;
    private boolean passed;
    private boolean consumed;
    private Long buyerId;
    private String buyerOpenId;
}
