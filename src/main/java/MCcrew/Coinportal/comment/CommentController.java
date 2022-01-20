package MCcrew.Coinportal.comment;

import MCcrew.Coinportal.Dto.CommentDto;
import MCcrew.Coinportal.domain.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /*
         댓글 달기
     */
    @PostMapping("/community/post/comment")
    @ResponseBody
    public Comment commentCreateController(@RequestBody CommentDto commentDto){
        return commentService.createComment(commentDto);
    }

    /*
        댓글 수정
     */
    @PostMapping("/community/post/comment/update")
    @ResponseBody
    public Comment commentUpdateController(@RequestBody CommentDto commentDto, @RequestHeader String jwt) throws UnsupportedEncodingException {
        return commentService.updateComment(commentDto, jwt);
    }

    /*
        삭제 상태로 변경
     */
    @PostMapping("/community/post/comment/status2Delete")
    @ResponseBody
    public boolean commentStatus2DeleteController(@RequestParam("commentId") Long commentId, @RequestHeader String jwt) throws UnsupportedEncodingException {
        return commentService.status2Delete(commentId, jwt);
    }

    /*
        댓글 신고
     */
    @PostMapping("/community/post/comment/report")
    @ResponseBody
    public int reportController(@RequestParam("commentId") Long commentId){
        return commentService.reportComment(commentId);
    }
}
