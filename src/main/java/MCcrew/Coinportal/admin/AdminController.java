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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
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
    private String pwd; // 관리자 password

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
    /*
        추가 구현 사항
        1. 유저 체크
        2. 신고된 게시물 체크
        3. 게시물 수정 및 삭제
        4. 공지글 제작
        5. 배너 수정
        6. 모든 게시물 확인
        7. 모든 댓글 확인
     */

    /*
        모든 공지글 가져오기
     */
    @GetMapping("/notices")
    public ResponseEntity<List<Notice>> getNoticeController(){
        List<Notice> resultList = boardService.getNotice();
        if(resultList.isEmpty()){
            return new ResponseEntity<>(resultList, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

    /*
        공지글 작성
     */
    @PostMapping("/notice")
    public Notice createNoticeController(@RequestBody NoticeDto noticeDto){
        return adminService.createNotice(noticeDto);
    }

    /*
        공지글 수정
     */
    @PutMapping("/notice/{noticeId}")
    public Notice updateNoticeController(@RequestBody NoticeDto noticeDto, @PathVariable Long noticeId){
        return adminService.updateNotice(noticeDto, noticeId);
    }

    /*
        공지글 삭제
     */
    @DeleteMapping("/notice")
    public boolean deleteNoticeController(@PathVariable Long noticeId){
        return adminService.deleteNotice(noticeId);
    }
}