package MCcrew.Coinportal.board;

import MCcrew.Coinportal.domain.Dto.PostDto;
import MCcrew.Coinportal.domain.Notice;
import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.domain.Preference;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.preference.PreferenceService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/search")
    public List<Post> searchByKeywordController(@RequestParam("keyword") String keyword){
        List<Post> postList = boardService.searchPostsByKeyword(keyword);
        return postList;
    }

    /*
        게시글 사용자 닉네임으로 검색
     */
    @GetMapping("/search/nickname")
    public List<Post> searchByNicknameController(@RequestParam("nickname") String nickname){
        List<Post> postList = boardService.searchPostsByNickname(nickname);
        return postList;
     }

     /*
        실시간 인기글 리스트 검색
      */
    @GetMapping("/search/popular")
    public List<Post> searchByPopularityController(){
        List<Post> postList = boardService.searchPostsByPopularity();
        return postList;
    }

    /*
        게시글 페이징 구현
     */
    @GetMapping("/page")
    public List<Object> listController(@RequestParam(value="boardName") String boardName, @RequestParam(value = "page", defaultValue = "1") int page){
        System.out.println("searching post about" + boardName + " with page " + page);
        List<Post> postList = boardService.getPostlist(boardName, page);
        int[] pageList = boardService.getPageList(boardName, page);
        List<Object> pagingInfo = new ArrayList<>();
        pagingInfo.add(postList);
        pagingInfo.add(pageList);
        return pagingInfo;
    }

    /**
     *  게시글 등록
     *  deprecated - AttachmentController의 post메소드로 바뀔 예정
     **/
    @PostMapping("/new-post")
    public boolean createContentByUser(@RequestBody PostDto postDto, @RequestHeader String jwt) throws UnsupportedEncodingException {
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return false; // 글 게시할 수 없음.
        }else{
            boardService.createPostByUser(postDto);
            return true;
        }
    }

    /*
        단일 게시글 조회
     */
    @GetMapping("/single-post")
    public Map<Post, Preference> getContentController(@RequestParam("postId") Long postId, @RequestHeader String jwt) throws UnsupportedEncodingException {
        HashMap hashMap = new HashMap();
        if(jwt == null){
            boardService.viewPost(postId); // 조회수 1 증가
            hashMap.put(boardService.getSinglePost(postId), new Preference());
            return hashMap;
        }else{
            Long userId = jwtService.getUserIdByJwt(jwt);
            if(userId == 0L){
                boardService.viewPost(postId); // 조회수 1 증가
                hashMap.put(boardService.getSinglePost(postId), new Preference());
                return hashMap;
            }else{
                boardService.viewPost(postId); // 조회수 1 증가
                hashMap.put(boardService.getSinglePost(postId), preferenceService.getMyLike(postId, userId));
                return hashMap;
            }
        }
    }

    /*
        전체 게시글 조회
     */
    @GetMapping("/multiple-post")
    public List<Post> getAllContentsController(){
        return boardService.getAllPost();
    }

    /*
        선택한 게시글 수정
     */
    @PostMapping("/post")
    public boolean updateController(@RequestBody PostDto postDto, @RequestHeader String jwt) throws UnsupportedEncodingException {
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return false; // 글 수정되지 않음.
        }else{
            Post resultPost = boardService.updatePost(postDto, userId);
            if(resultPost.getUserId() == null){
                return false;
            }else{
                return true;
            }
        }
    }
    /*
        선택한 게시글 좋아요
     */
    @PostMapping("/post/like")
    public int likeController(@RequestParam("postId") Long postId){
        return boardService.likePost(postId);
    }
    /*
        선택한 게시글 싫어요
     */
    @PostMapping("/post/dislike")
    public int dislikeController(@RequestParam("postId") Long postId){
        return boardService.dislikePost(postId);
    }

    /*
       선택한 게시글 신고
    */
    @PostMapping("/post/report")
    public int reportController(@RequestParam("postId") Long postId){
        return boardService.reportPost(postId);
    }

    /*
        삭제 상태로 변경
     */
    @PostMapping("/post/status2Delete")
    public boolean deleteController(@RequestParam("postId") Long postId, @RequestHeader String jwt) throws UnsupportedEncodingException {
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return false;
        }else{
            return boardService.status2Delete(postId, userId);
        }
    }

    /*
        선택한 게시글 디비에서 삭제
     */
    @PostMapping("/post/delete")
    public boolean deleteContent(@RequestParam("postId") Long postId, @RequestHeader String jwt) throws UnsupportedEncodingException {
        Long userId = 0L;
        try{
            userId = jwtService.getUserIdByJwt(jwt);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("jwt 예외가 발생했습니다.");
            return false;
        }
        if(userId == 0L){
            System.out.println("글 삭제 되지 않음. ");
            return false; // 글 삭제되지 않음.
        }else{
            return boardService.deletePost(postId);
        }
    }

    /*
        모든 공지글 가져오기
     */
    @GetMapping("/notice")
    public List<Notice> getNoticeController(){
        return boardService.getNotice();
    }
}