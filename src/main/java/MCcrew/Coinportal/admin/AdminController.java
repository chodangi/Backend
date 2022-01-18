package MCcrew.Coinportal.admin;

import MCcrew.Coinportal.Dto.NoticeDto;
import MCcrew.Coinportal.board.BoardService;
import MCcrew.Coinportal.cointemper.CoinTemperService;
import MCcrew.Coinportal.comment.CommentService;
import MCcrew.Coinportal.domain.Notice;
import MCcrew.Coinportal.game.GameService;
import MCcrew.Coinportal.login.LoginService;
import MCcrew.Coinportal.photo.AttachmentService;
import MCcrew.Coinportal.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminController {

    private final BoardService boardService;
    private final CoinTemperService coinTemperService;
    private final CommentService commentService;
    private final GameService gameService;
    private final LoginService loginService;
    private final AttachmentService attachmentService;
    private final UserService userService;
    private final AdminService adminService;

    @Value("${admin.pwd}")
    private String pwd;

    public AdminController(BoardService boardService, CoinTemperService coinTemperService, CommentService commentService, GameService gameService, LoginService loginService, AttachmentService attachmentService, UserService userService, AdminService adminService) {
        this.boardService = boardService;
        this.coinTemperService = coinTemperService;
        this.commentService = commentService;
        this.gameService = gameService;
        this.loginService = loginService;
        this.attachmentService = attachmentService;
        this.userService = userService;
        this.adminService = adminService;
    }

    public boolean pwdCheck(Long pwd){
        if(pwd.equals(this.pwd)){
            return true;
        }else{
            return false;
        }
    }

//    /*
//        유저 체크
//     */
//    @GetMapping("/admin/1")
//    @ResponseBody
//    public boolean allUserController(@RequestBody Long pwd){
//
//    }
//
//    /*
//        신고된 게시물 체크
//     */
//    @GetMapping("/admin/1")
//    @ResponseBody
//    public boolean allUserController(@RequestBody Long pwd){
//
//    }
//
//    /*
//        게시물 수정 및 삭제
//     */
//    @GetMapping("/admin/1")
//    @ResponseBody
//    public boolean allUserController(@RequestBody Long pwd){
//
//    }
//
//    /*
//        공지글 제작
//     */
//    @GetMapping("/admin/1")
//    @ResponseBody
//    public boolean allUserController(@RequestBody Long pwd){
//
//    }
//    /*
//        배너 수정
//     */
//    @GetMapping("/admin/1")
//    @ResponseBody
//    public boolean allUserController(@RequestBody Long pwd){
//
//    }
//    /*
//        모든 게시물 확인
//     */
//    @GetMapping("/admin/1")
//    @ResponseBody
//    public boolean allUserController(@RequestBody Long pwd){
//
//    }
//
//    /*
//        모든 댓글 확인
//     */
//    @GetMapping("/admin/1")
//    @ResponseBody
//    public boolean allUserController(@RequestBody Long pwd){
//
//    }

    /*
        모든 공지글 가져오기
     */
    @GetMapping("/admin/notice/all")
    @ResponseBody
    public List<Notice> getNoticeController(){
        return boardService.getNotice();
    }

    /*
        관리자 공지글 작성
     */
    @PostMapping("/admin/notice/create")
    @ResponseBody
    public Notice createNoticeController(@RequestBody NoticeDto noticeDto){
        return adminService.createNotice(noticeDto);
    }

    /*
        공지글 수정
     */
    @PostMapping("/admin/notice/update")
    @ResponseBody
    public Notice updateNoticeController(@RequestBody NoticeDto noticeDto, @RequestParam Long noticeId){
        return adminService.updateNotice(noticeDto, noticeId);
    }

    /*
        공지글 삭제
     */
    @PostMapping("/admin/notice/delete")
    @ResponseBody
    public boolean deleteNoticeController(@RequestBody Long noticeId){
        return adminService.deleteNotice(noticeId);
    }
}