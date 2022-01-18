package MCcrew.Coinportal.user;

import MCcrew.Coinportal.Dto.UserDto;
import MCcrew.Coinportal.Dto.UserRankingDto;
import MCcrew.Coinportal.board.BoardService;
import MCcrew.Coinportal.comment.CommentService;
import MCcrew.Coinportal.domain.Comment;
import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.domain.User;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.login.LoginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {   // 유저 프로필 관련 컨트롤러

    private final UserService userService;
    private final JwtService jwtService;
    private final CommentService commentService;
    private final BoardService boardService;

    public UserController(UserService userService, JwtService jwtService, CommentService commentService, BoardService boardService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.commentService = commentService;
        this.boardService = boardService;
    }

    /*
                모든 유저 반환
             */
    @GetMapping("/user/all")
    @ResponseBody
    public List<User> getAllUserController(){
        return userService.getAllUser();
    }

    /*
        내가 작성한 게시글 반환
     */
    @GetMapping("/user/mypost")
    @ResponseBody
    public List<Post> getMyPostController(@RequestHeader String jwt) throws UnsupportedEncodingException {
        if(jwt == null){
            return new ArrayList<>();
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new ArrayList<>();
        }else{
            return boardService.getMyPost(userId);
        }
    }

    /*
        내가 작성한 댓글 반환
     */
    @GetMapping("/user/mycomment")
    @ResponseBody
    public List<Comment> getMyCommentController(@RequestHeader String jwt) throws UnsupportedEncodingException {
        if(jwt == null){
            return new ArrayList<>();
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new ArrayList<>();
        }else{
            return commentService.getMyComment(userId);
        }
    }

//    /*
//        내가 좋아요 표시한 게시글 반환
//     */
//    @GetMapping("/user/mylikepost")
//    @ResponseBody
//    public List<Post> getMyLikePostController(){
//
//    }

    /*
        내 설정값 반환
     */
    @GetMapping("/user/mysetting")
    @ResponseBody
    public User getMySettingController(@RequestHeader String jwt ) throws UnsupportedEncodingException {
        if(jwt == null){
            return new User();
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new User();
        }else{
            return userService.getUserById(userId);
        }
    }

    /*
        설정 변경 - 닉네임 변경도 해당 api 에서 수행
     */
    @PostMapping("/user/updatemysetting")
    @ResponseBody
    public User updateMySettingController(@RequestBody UserDto userDto, @RequestHeader String jwt ) throws UnsupportedEncodingException {
        if(jwt == null){
            return new User();
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new User();
        }else{
            return userService.updateUser(userDto);
        }
    }

    /*
        회원 탈퇴
     */
    @PostMapping("/user/delete")
    @ResponseBody
    public boolean deleteUserController(@RequestHeader String jwt) throws UnsupportedEncodingException {
        if(jwt == null){
            return false;
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return false;
        }else{
            return userService.deleteUser(userId);
        }
    }

    /*
        유저 랭킹
     */
    @GetMapping("/user/ranking")
    @ResponseBody
    public List<UserRankingDto> getUserRankingController(){
        return userService.getUserRanking();
    }
}
