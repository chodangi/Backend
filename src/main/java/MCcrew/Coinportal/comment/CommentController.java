package MCcrew.Coinportal.comment;

import MCcrew.Coinportal.domain.Dto.CommentDto;
import MCcrew.Coinportal.domain.Comment;
import MCcrew.Coinportal.util.Message;
import MCcrew.Coinportal.util.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
public class CommentController {
    private final CommentService commentService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
         댓글 달기
     */
    @PostMapping("/comment")
    @ResponseBody
    public Message commentCreateController(@RequestBody CommentDto commentDto){
        logger.info("commentCreateController(): 댓글을 작성합니다.");
        Comment comment =  commentService.createComment(commentDto);
        return new Message(StatusEnum.OK, "OK", comment);
    }

    /**
        댓글 수정
     */
    @PutMapping("/comment")
    @ResponseBody
    public Message commentUpdateController(@RequestBody CommentDto commentDto, @RequestHeader String jwt){
        logger.info("commentUpdateController(): 댓글을 수정합니다.");
        Comment comment =  commentService.updateComment(commentDto, jwt);
        if(comment.getId() == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", comment);
        }
        return new Message(StatusEnum.OK, "OK", comment);
    }

    /**
        삭제 상태로 변경
     */
    @PutMapping("/comment/{commentId}")
    @ResponseBody
    public Message commentStatus2DeleteController(@PathVariable("commentId") Long commentId, @RequestHeader String jwt){
        logger.info("commentStatus2DeleteController(): "+ commentId + "번 댓글을 삭제 상태로 변경합니다.");
        commentService.status2Delete(commentId, jwt);
        return new Message(StatusEnum.OK, "OK", true);
    }

    /**
        댓글 신고
     */
    @PostMapping("/comment/{commentId}")
    @ResponseBody
    public Message reportController(@PathVariable Long commentId){
        logger.info("reportController(): "+ commentId + "번 댓글을 삭제합니다.");
        int result = commentService.reportComment(commentId);
        return new Message(StatusEnum.OK, "OK", result);
    }
}
