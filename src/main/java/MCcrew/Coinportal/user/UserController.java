package MCcrew.Coinportal.user;

import MCcrew.Coinportal.domain.Dto.UserDto;
import MCcrew.Coinportal.board.BoardService;
import MCcrew.Coinportal.comment.CommentService;
import MCcrew.Coinportal.domain.Comment;
import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.domain.User;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.util.Message;
import MCcrew.Coinportal.util.StatusEnum;
import jdk.jshell.Snippet;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController("/profile")
public class UserController {   // 유저 프로필 관련 컨트롤러

    private final UserService userService;
    private final JwtService jwtService;
    private final CommentService commentService;
    private final BoardService boardService;

    // 로깅
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserController(UserService userService, JwtService jwtService, CommentService commentService, BoardService boardService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.commentService = commentService;
        this.boardService = boardService;
    }

    /*
                모든 유저 반환
             */
    @GetMapping("/users")
    public Message getAllUserController(){
        List<User> result = null;
        try {
            result = userService.getAllUser();
            return new Message(StatusEnum.OK, "OK", result);
        }catch(NoResultException e){
            return new Message(StatusEnum.NOT_FOUND, "NOT_FOUND", result);
        }
    }

    /*
        내가 작성한 게시글 반환
     */
    @GetMapping("/my-post")
    public Message getMyPostController(@RequestHeader String jwt) {
        if(jwt == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", false);
        }else{
            List<Post> result = boardService.getMyPost(userId);
            return new Message(StatusEnum.OK, "OK", result);
        }
    }

    /*
        내가 작성한 댓글 반환
     */
    @GetMapping("/my-comment")
    public Message getMyCommentController(@RequestHeader String jwt){
        if(jwt == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", false);
        }else{
            List<Comment> commentList =  commentService.getMyComment(userId);
            return new Message(StatusEnum.OK ,"OK", commentList);
        }
    }

    /*
        내 설정값 반환
     */
    @GetMapping("/my-settings")
    public Message getMySettingController(@RequestHeader String jwt ) {
        if(jwt == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", false);
        }else{
            User user =  userService.getUserById(userId);
            return new Message(StatusEnum.OK ,"OK", user);
        }
    }

    /*
        설정 변경 - 닉네임 변경도 해당 api 에서 수행
     */
    @PostMapping("/my-settings")
    public Message updateMySettingController(@RequestBody UserDto userDto, @RequestHeader String jwt ){
        if(jwt == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", false);
        }else{
            User user = userService.updateUser(userDto);
            return new Message(StatusEnum.OK ,"OK", user);
        }
    }

    /*
        회원 탈퇴
     */
    @DeleteMapping("/user")
    public Message deleteUserController(@RequestHeader String jwt) {
        if(jwt == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", false);
        }else{
            boolean result =  userService.deleteUser(userId);
            return new Message(StatusEnum.OK ,"OK", result);
        }
    }
}
