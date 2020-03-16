package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;
import site.binghai.shop.enums.TuanStatus;

import javax.persistence.*;

/**
 * @author icesea
 * @date 2020/2/23 下午9:21
 **/
@Data
@Entity
public class Tuan extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long productId;
    private Long leaderOrderId;
    private String title;
    private Long leaderId;
    private String leaderOpenId;
    private String leaderName;
    private String leaderAvatar;
    //标
    private String tag;
    //List<TuanFollower>
    @Column(columnDefinition = "TEXT")
    private String follower;
    //团人数
    private Integer totalSize;
    //当前团人数
    private Integer currentSize;

    private TuanStatus status;
    //关团时间
    private Long endTs;

    @Transient
    private String last;
}
