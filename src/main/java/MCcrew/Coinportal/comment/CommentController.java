package MCcrew.Coinportal.comment;

import MCcrew.Coinportal.domain.Dto.CommentDto;
import MCcrew.Coinportal.domain.Comment;
import MCcrew.Coinportal.util.Message;
import MCcrew.Coinportal.util.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /*
         댓글 달기
     */
    @PostMapping("/comment")
    @ResponseBody
    public Message commentCreateController(@RequestBody CommentDto commentDto){
        Comment comment =  commentService.createComment(commentDto);
        return new Message(StatusEnum.OK, "OK", comment);
    }

    /*
        댓글 수정
     */
    @PutMapping("/comment")
    @ResponseBody
    public Message commentUpdateController(@RequestBody CommentDto commentDto, @RequestHeader String jwt){
        Comment comment =  commentService.updateComment(commentDto, jwt);
        if(comment.getId() == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", comment);
        }
        return new Message(StatusEnum.OK, "OK", comment);
    }

    /*
        삭제 상태로 변경
     */
    @PutMapping("/comment/{commentId}}")
    @ResponseBody
    public Message commentStatus2DeleteController(@PathVariable Long commentId, @RequestHeader String jwt){
         commentService.status2Delete(commentId, jwt);
         return new Message(StatusEnum.OK, "OK", true);
    }

    /*
        댓글 신고
     */
    @PostMapping("/comment/{commentId}")
    @ResponseBody
    public Message reportController(@PathVariable Long commentId){
        int result = commentService.reportComment(commentId);
        return new Message(StatusEnum.OK, "OK", result);
    }
}
