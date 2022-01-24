package MCcrew.Coinportal.preference;

import MCcrew.Coinportal.domain.Preference;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.util.Message;
import MCcrew.Coinportal.util.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController("/preference")
public class PreferenceController {

    private final PreferenceService preferenceService;
    private final JwtService jwtService;

    // 로깅
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public PreferenceController(PreferenceService preferenceService, JwtService jwtService) {
        this.preferenceService = preferenceService;
        this.jwtService = jwtService;
    }

    /*
        게시글 좋아요 클릭
    */
    @PostMapping("/post-like/{post-id}")
    public Message likeController(@PathVariable("post-id") Long postId, @RequestHeader String jwt)  {
        if(jwt == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", new Preference());
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", new Preference());
        }else{
            Preference preference = preferenceService.clickLikes(postId, userId);
            if(preference.getUserId() == null){
                return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", preference);
            }
            return new Message(StatusEnum.OK, "OK", preference);
        }
    }

    /*
        게시글 싫어요 클릭
     */
    @PostMapping("/post-dislike/{post-id}")
    public Message dislikeController(@PathVariable("post-id") Long postId, @RequestHeader String jwt){
        if(jwt == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", new Preference());
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", new Preference());
        }else{
            Preference preference = preferenceService.clickDislikes(postId, userId);
            if(preference.getUserId() == null){
                return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", preference);
            }
            return new Message(StatusEnum.OK, "OK", preference);
        }
    }

    /*
        내가 누른 좋아요 모두 보기
     */
    @GetMapping("/my-like")
    public Message myLikeController(@RequestHeader String jwt){
        if(jwt == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", new ArrayList<>());
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", new ArrayList<>());

        }else{
            List<Preference> preferenceList = preferenceService.getMyLikeAll(userId);
            return new Message(StatusEnum.OK, "OK" , preferenceList);
        }
    }
}