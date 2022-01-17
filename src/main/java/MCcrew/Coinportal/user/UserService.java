package MCcrew.Coinportal.user;

import MCcrew.Coinportal.Dto.PostDto;
import MCcrew.Coinportal.Dto.UserDto;
import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {  // 유저 프로필 핵심 로직 구현

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
        유저 엔티티 한개 반환
     */
    public User getUserEntity(Long id){
        return userRepository.findById(id);
    }

    /*
        유저 정보 변경
     */
    public User updateUser(UserDto userDto, String originalNickname){
        User findUser = userRepository.findByNickname(originalNickname);
        findUser.setUserNickname(userDto.getUserNickname());
        findUser.setDark(userDto.isDark());
        findUser.setOnAlarm(userDto.isOnAlarm());
        return userRepository.save(findUser);
    }

    /*
        전체 유저 반환
     */
    public List<User> getAllUser() {
        return userRepository.findAll();
    }
}
