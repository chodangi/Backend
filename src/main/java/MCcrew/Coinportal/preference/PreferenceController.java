package MCcrew.Coinportal.preference;

import MCcrew.Coinportal.domain.Preference;
import MCcrew.Coinportal.login.JwtService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PreferenceController {

    private final PreferenceService preferenceService;
    private final JwtService jwtService;

    public PreferenceController(PreferenceService preferenceService, JwtService jwtService) {
        this.preferenceService = preferenceService;
        this.jwtService = jwtService;
    }

    /*
        좋아요 클릭
    */
    @PostMapping("/post/like")
    @ResponseBody
    public Preference likeController(@RequestParam Long postId, @RequestHeader String jwt) throws UnsupportedEncodingException {
        if(jwt == null){
            return new Preference();
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Preference();
        }else{
            return preferenceService.clickLikes(postId, userId);
        }
    }

    /*
        싫어요 클릭
     */
    @PostMapping("/post/dislike")
    @ResponseBody
    public Preference dislikeController(@RequestParam Long postId, @RequestHeader String jwt) throws UnsupportedEncodingException {
        if(jwt == null){
            return new Preference();
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Preference();
        }else{
            return preferenceService.clickDislikes(postId, userId);
        }
    }

    /*
        내가 누른 좋아요 모두 보기
     */
    @GetMapping("/post/myLike")
    @ResponseBody
    public List<Preference> myLikeController(@RequestHeader String jwt) throws UnsupportedEncodingException {
        if(jwt == null){
            return new ArrayList<>();
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new ArrayList<>();
        }else{
            return preferenceService.getMyLikeAll(userId);
        }
    }
}