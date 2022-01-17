package MCcrew.Coinportal.Dto;

import MCcrew.Coinportal.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long userId;
    private String userNickname;
    private boolean isDark;
    private boolean onAlarm;
}
