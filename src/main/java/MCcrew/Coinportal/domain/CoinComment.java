package MCcrew.Coinportal.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CoinComment {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private String coinSymbol;
    private String nickname;
    private String password;
    private String content;
    private Date createdAt;
    private int upCnt;
    private int downCnt;
    private int reportCnt;

    @Builder
    public CoinComment(Long id, Long userId, String coinSymbol, String nickname, String password, String content, Date createdAt, int upCnt, int downCnt, int reportCnt) {
        this.id = id;
        this.userId = userId;
        this.coinSymbol = coinSymbol;
        this.nickname = nickname;
        this.password = password;
        this.content = content;
        this.createdAt = createdAt;
        this.upCnt = upCnt;
        this.downCnt = downCnt;
        this.reportCnt = reportCnt;
    }
}
