package site.binghai.biz.entity.jdy;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Log extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String text;
}
