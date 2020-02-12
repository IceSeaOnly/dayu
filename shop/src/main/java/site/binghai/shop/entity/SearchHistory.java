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
public class SearchHistory extends BaseEntity {
    @GeneratedValue
    @Id
    private Long id;
    private Long userId;
    private String content;
    /**
     * true：不在搜索历史展示
     * */
    private Boolean cleaned;

    public SearchHistory(Long userId, String content) {
        this.userId = userId;
        this.content = content;
        this.cleaned = Boolean.FALSE;
    }

    public SearchHistory() {
    }
}
