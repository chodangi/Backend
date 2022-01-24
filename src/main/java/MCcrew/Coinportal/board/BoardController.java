package MCcrew.Coinportal.board;

import MCcrew.Coinportal.domain.Dto.PostDto;
import MCcrew.Coinportal.domain.Notice;
import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.domain.Preference;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.preference.PreferenceService;
import MCcrew.Coinportal.util.Message;
import MCcrew.Coinportal.util.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController("/community")
public class BoardController {  // 게시판 관련 컨트롤러

    private final BoardService boardService;
    private final JwtService jwtService;
    private final PreferenceService preferenceService;

    // 로깅
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public BoardController(BoardService boardService, JwtService jwtService, PreferenceService preferenceService) {
        this.boardService = boardService;
        this.jwtService = jwtService;
        this.preferenceService = preferenceService;
    }

    /*
            게시글 키워드로 검색
         */
    @GetMapping("/post/{keyword}")
    public Message searchByKeywordController(@PathVariable String keyword){
        List<Post> postList = boardService.searchPostsByKeyword(keyword);
        if(postList.size() == 0){
            return new Message(StatusEnum.NOT_FOUND, "NOT_FOUND", postList);
        }
        return new Message(StatusEnum.OK,"OK",postList);
    }

    /*
        게시글 사용자 닉네임으로 검색
     */
    @GetMapping("/post/{nickname}")
    public Message searchByNicknameController(@PathVariable String nickname){
        List<Post> postList = boardService.searchPostsByNickname(nickname);
        if(postList.size() == 0){
            return new Message(StatusEnum.NOT_FOUND, "NOT_FOUND", postList);
        }
        return new Message(StatusEnum.OK,"OK",postList);
     }

     /*
        실시간 인기글 리스트 검색
      */
    @GetMapping("/up-count")
    public Message searchByPopularityController(){
        List<Post> postList = boardService.searchPostsByPopularity();
        if(postList.size() == 0){
            return new Message(StatusEnum.NOT_FOUND, "NOT_FOUND", postList);
        }
        return new Message(StatusEnum.OK,"OK",postList);
    }

    /*
        게시글 페이징 구현
     */
    @GetMapping("/{board-name}/{page}")
    public Message listController(@PathVariable("board-name") String boardName, @PathVariable("page") int page){
        System.out.println("searching post about" + boardName + " with page " + page);
        List<Post> postList = boardService.getPostlist(boardName, page);
        if(postList.size() == 0){
            return new Message(StatusEnum.NOT_FOUND, "NOT_FOUND", postList);
        }
        int[] pageList = boardService.getPageList(boardName, page);
        List<Object> pagingInfo = new ArrayList<>();
        pagingInfo.add(postList);
        pagingInfo.add(pageList);
        return new Message(StatusEnum.OK, "OK", pagingInfo);
    }

    /*
        단일 게시글 조회
     */
    @GetMapping("/post/{post-id}")
    public Message getContentController(@PathVariable("post-id") Long postId, @RequestHeader String jwt){
        Long userId = 0L;
        HashMap hashMap = new HashMap();
        if(jwt == null){
            boardService.viewPost(postId); // 조회수 1 증가
            hashMap.put(boardService.getSinglePost(postId), new Preference());
            return new Message(StatusEnum.OK, "OK", hashMap);
        }else{
            userId = jwtService.getUserIdByJwt(jwt);
            if(userId == 0L){
                boardService.viewPost(postId); // 조회수 1 증가
                hashMap.put(boardService.getSinglePost(postId), new Preference());
                return new Message(StatusEnum.OK, "OK", hashMap);
            }else{
                boardService.viewPost(postId); // 조회수 1 증가
                hashMap.put(boardService.getSinglePost(postId), preferenceService.getMyLike(postId, userId));
                return new Message(StatusEnum.OK, "OK", hashMap);
            }
        }
    }

    /*
        전체 게시글 조회
     */
    @GetMapping("/posts")
    public Message getAllContentsController(){
        List<Post> postList = boardService.getAllPost();
        if(postList.size() == 0){
            return new Message(StatusEnum.NOT_FOUND, "NOT_FOUND", postList);
        }
        return new Message(StatusEnum.OK, "OK", postList);
    }

    /*
        선택한 게시글 수정
     */
    @PutMapping("/post")
    public Message updateController(@RequestBody PostDto postDto, @RequestHeader String jwt){
        Long userId = 0L;
        userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }else{
            Post resultPost = boardService.updatePost(postDto, userId);
            if(resultPost.getUserId() == null){
                return new Message(StatusEnum.NOT_FOUND, "NOT_FOUND", false);
            }else{
                return new Message(StatusEnum.OK, "OK", resultPost);
            }
        }
    }

    /*
       선택한 게시글 신고
    */
    @PostMapping("/post/report")
    public Message reportController(@RequestParam("postId") Long postId){
        try{
        int reportCnt = boardService.reportPost(postId);
        return new Message(StatusEnum.OK, "OK", reportCnt);
        }catch(Exception e){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", 0);
        }
    }

    /*
        삭제 상태로 변경
     */
    @PostMapping("/post/status/{post-id}")
    public Message deleteController(@PathVariable("post-id") Long postId, @RequestHeader String jwt){
        Long userId = 0L;
            userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }else{
            return new Message(StatusEnum.OK, "OK", true);
        }
    }

    /*
        선택한 게시글 디비에서 삭제
     */
    @DeleteMapping("/post/{post-id}")
    public Message deleteContent(@PathVariable("post-id") Long postId, @RequestHeader String jwt){
        Long userId = 0L;
            userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }else{
            boolean result =  boardService.deletePost(postId);
            if(result)
                return new Message(StatusEnum.OK, "OK", true);
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }
    }

    /*
        모든 공지글 가져오기
     */
    @GetMapping("/notices")
    public Message getNoticeController(){
        List<Notice> notices = boardService.getNotice();
        if(notices.size() == 0){
            return new Message(StatusEnum.NOT_FOUND, "NOT_FOUND", notices);
        }
        return new Message(StatusEnum.OK, "OK", notices);
    }
}