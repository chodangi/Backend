package MCcrew.Coinportal.board;

import MCcrew.Coinportal.Dto.PostDto;
import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.login.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BoardController {  // 게시판 관련 컨트롤러

    private final BoardService boardService;
    private final JwtService jwtService;

    @Autowired
    public BoardController(BoardService boardService, JwtService jwtService) {
        this.boardService = boardService;
        this.jwtService = jwtService;
    }

    /*
        게시글 키워드로 검색
     */
    @GetMapping("/community/search")
    @ResponseBody
    public List<Post> searchByKeywordController(@RequestParam("keyword") String keyword){
        List<Post> postList = boardService.searchPostsByKeyword(keyword);
        return postList;
    }

    /*
        게시글 사용자 닉네임으로 검색
     */
    @GetMapping("/community/search/nickname")
    @ResponseBody
    public List<Post> searchByNicknameController(@RequestParam("nickname") String nickname){
        List<Post> postList = boardService.searchPostsByNickname(nickname);
        return postList;
     }

     /*
        실시간 인기글 리스트 검색
      */
    @GetMapping("/community/search/popular")
    @ResponseBody
    public List<Post> searchByPopularityController(){
        List<Post> postList = boardService.searchPostsByPopularity();
        return postList;
    }

    /*
        게시글 페이징 구현
     */
    @GetMapping("/community/page")
    @ResponseBody
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
    @PostMapping("/community/new-post")
    @ResponseBody
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
    @GetMapping("/community/single-post")
    @ResponseBody
    public Post getContentController(@RequestParam("postId") Long postId){
        boardService.viewPost(postId); // 조회수 1 증가
        return boardService.getSinglePost(postId);
    }

    /*
        전체 게시글 조회
     */
    @GetMapping("/community/multiple-post")
    @ResponseBody
    public List<Post> getAllContentsController(){
        return boardService.getAllPost();
    }

    /*
        선택한 게시글 수정
     */
    @PostMapping("/community/post")
    @ResponseBody
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
    @PostMapping("/community/post/like")
    @ResponseBody
    public int likeController(@RequestParam("postId") Long postId){
        return boardService.likePost(postId);
    }
    /*
        선택한 게시글 싫어요
     */
    @PostMapping("/community/post/dislike")
    @ResponseBody
    public int dislikeController(@RequestParam("postId") Long postId){
        return boardService.dislikePost(postId);
    }

    /*
       선택한 게시글 신고
    */
    @PostMapping("/community/post/report")
    @ResponseBody
    public int reportController(@RequestParam("postId") Long postId){
        return boardService.reportPost(postId);
    }

    /*
        삭제 상태로 변경
     */
    @PostMapping("/community/post/status2Delete")
    @ResponseBody
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
    @PostMapping("/community/post/delete")
    @ResponseBody
    public boolean deleteContent(@RequestParam("postId") Long postId, @RequestHeader String jwt) throws UnsupportedEncodingException {
        Long userId = 0L;
        try{
            userId = jwtService.getUserIdByJwt(jwt);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(userId == 0L || userId == null){
            return false; // 글 삭제되지 않음.
        }else{
            return boardService.deletePost(postId);
        }
    }
}