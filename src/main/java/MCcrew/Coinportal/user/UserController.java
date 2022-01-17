package MCcrew.Coinportal.user;

import MCcrew.Coinportal.domain.User;
import MCcrew.Coinportal.login.LoginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class UserController {   // 유저 프로필 관련 컨트롤러

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/allUserInHashMap")
//    @ResponseBody
//    public String getAllUserInHashMap(){
//        String result = "";
//        for(Map.Entry<String, String> entrySet: LoginController.getHashMap().entrySet()){
//            result += entrySet.getKey() + ":" + entrySet.getValue();
//        }
//        return result;
//    }

    @GetMapping("/user/all")
    @ResponseBody
    public List<User> getAllUserController(){
        return userService.getAllUser();
    }

    /*
        내가 쓴 글 반환
     */

    /*
        내가 쓴 댓글 반환
     */

    /*
        내가 좋아요 표시한 글 반환
     */

    /*
        내 설정값 반환
     */

    /*
        설정 변경
     */

    /*
        닉네임 수정하기
     */

    /*
        회원 탈퇴
     */
}
