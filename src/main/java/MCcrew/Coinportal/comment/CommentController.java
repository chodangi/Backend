package MCcrew.Coinportal.comment;

import MCcrew.Coinportal.domain.Dto.CommentDto;
import MCcrew.Coinportal.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j
public class CommentController {
    private final CommentService commentService;

    // 로깅
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /*
         댓글 달기
     */
    @PostMapping("/")
    @ResponseBody
    public Comment commentCreateController(@RequestBody CommentDto commentDto){
        return commentService.createComment(commentDto);
    }

    /*
        댓글 수정
     */
    @PutMapping("/comment")
    @ResponseBody
    public Comment commentUpdateController(@RequestBody CommentDto commentDto, @RequestHeader String jwt) throws UnsupportedEncodingException {
        return commentService.updateComment(commentDto, jwt);
    }

    /*
        삭제 상태로 변경
     */
    @PutMapping("/comment/{commentId}}")
    @ResponseBody
    public boolean commentStatus2DeleteController(@PathVariable Long commentId, @RequestHeader String jwt) throws UnsupportedEncodingException {
        return commentService.status2Delete(commentId, jwt);
    }

    /*
        댓글 신고
     */
    @PostMapping("/comment/{commentId}")
    @ResponseBody
    public int reportController(@PathVariable Long commentId){
        return commentService.reportComment(commentId);
    }
}
