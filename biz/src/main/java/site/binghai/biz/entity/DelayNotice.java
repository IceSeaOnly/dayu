package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class DelayNotice extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String noticeType;
    @Column(columnDefinition = "TEXT")
    private String context;
    private Long fireTime;
    private Boolean fired;
}
