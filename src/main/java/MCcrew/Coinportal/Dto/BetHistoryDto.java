package MCcrew.Coinportal.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BetHistoryDto {
    private Long userId;
    private boolean BTC; // 비트
    private boolean ETH; // 이더
    private boolean XRP; // 리플
}
