package MCcrew.Coinportal.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CoinCommentDto {
    private Long commentId;
    private String coinSymbol;
    private String nickname;
    private String password;
    private String content;
}
