package MCcrew.Coinportal.cointemper;

import MCcrew.Coinportal.domain.Dto.CoinCommentDto;
import MCcrew.Coinportal.domain.CoinComment;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.util.Message;
import MCcrew.Coinportal.util.StatusEnum;
import jdk.jshell.Snippet;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class CoinTemperController {

    private final CoinTemperService coinTemperService;
    private final JwtService jwtService;

    // 로깅
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CoinTemperController(CoinTemperService coinTemperService, JwtService jwtService) {
        this.coinTemperService = coinTemperService;
        this.jwtService = jwtService;
    }

    /*
        현재 코인 체감 온도
     */
    @GetMapping("/temper/coin-temper")
    @ResponseBody
    public Message coinTemperController(){
        try {
            List<Double> result = coinTemperService.getCoinTemper();
            return new Message(StatusEnum.OK, "OK", result);
        }catch(Exception e){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", new ArrayList());
        }
    }

    /*
        코인 매수
        symbol = BTC or ETH or XRP
     */
    @GetMapping("/temper/up/{symbol}")
    @ResponseBody
    public Message coinBuyController(@PathVariable String symbol, @RequestHeader String jwt){
        Long userId = jwtService.getUserIdByJwt(jwt); // 회원만 이용가능
        if(userId == 0L){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }else{
            double result = coinTemperService.temperIncrease(symbol);
            return new Message(StatusEnum.OK, "OK" , result);
        }
    }
    /*
            코인 매도
            symbol = BTC or ETH or XRP
    */
    @GetMapping("/temper/down/{symbol}")
    @ResponseBody
    public Message coinSellController(@PathVariable String symbol, @RequestHeader String jwt){
        Long userId = jwtService.getUserIdByJwt(jwt); // 회원만 이용가능
        if(userId == 0L){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }else{
            double result = coinTemperService.temperDecrease(symbol);
            return new Message(StatusEnum.OK, "OK" , result);
        }
    }

    /*
        코인 체감 온도 댓글달기
        symbol = BTC or ETH or XRP
     */
    @PostMapping("/temper/{symbol}/comment")
    @ResponseBody
    public Message createCommentController(@PathVariable String symbol, @RequestBody CoinCommentDto coinCommentDto, @RequestHeader String jwt){
        Long userIdx = jwtService.getUserIdByJwt(jwt);
        if(userIdx == 0L){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", new CoinComment());
        }else{
            CoinComment result = coinTemperService.createComment(coinCommentDto, symbol, userIdx);
            return new Message(StatusEnum.OK, "OK", result);
        }
    }

    /*
        코인 체감 온도 댓글 반환
        symbol = BTC or ETH or XRP
     */
    @GetMapping("/temper/{symbol}/comments")
    @ResponseBody
    public Message getCommentController(@PathVariable String symbol){
        List<CoinComment> coinCommentList = coinTemperService.getCommentList(symbol);
        return new Message(StatusEnum.OK, "OK", coinCommentList );
    }

    /*
        수정
     */
    @PostMapping("/temper/comment")
    @ResponseBody
    public Message updateCommentController(@RequestBody CoinCommentDto coinCommentDto, @RequestHeader String jwt){
        CoinComment coinComment;
        if(jwt != null){ //회원의 글이라면
            Long userId = jwtService.getUserIdByJwt(jwt);
            if(userId == 0L) {
                return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", new CoinComment());
            }
            else{
                coinComment = coinTemperService.updateCoinComment(coinCommentDto, userId);
                if(coinComment.getId() == null){
                    return new Message(StatusEnum.NOT_FOUND,"NOT_FOUND", coinComment);
                }else {
                    return new Message(StatusEnum.OK, "OK", coinComment);
                }
            }
        }else{ //비회원의 글이라면
            coinComment = coinTemperService.updateCoinCommentByNonUser(coinCommentDto);
            if(coinComment.getId() == null){
                return new Message(StatusEnum.NOT_FOUND,"NOT_FOUND", coinComment);
            }else{
                return new Message(StatusEnum.OK,"OK", coinComment);
            }
        }
    }
    /*
        삭제
     */
    @DeleteMapping("/temper/comment")
    @ResponseBody
    public Message deleteCommentController(@RequestBody CoinCommentDto coinCommentDto, @RequestHeader String jwt ){
        if(jwt != null){ //회원의 글이라면
            Long userId = jwtService.getUserIdByJwt(jwt);
            if(userId == 0L)
                return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
            else {
                boolean result = coinTemperService.deleteCoinComment(coinCommentDto, userId);
                if(result){
                    return new Message(StatusEnum.OK ,"OK", true);
                }
                return new Message(StatusEnum.NOT_FOUND ,"NOT_FOUND", false);
            }
        }else{ //비회원의 글이라면
            boolean result = coinTemperService.deleteCoinCommentByNonUser(coinCommentDto);
            if(result){
                return new Message(StatusEnum.OK ,"OK", true);
            }
            return new Message(StatusEnum.NOT_FOUND ,"NOT_FOUND", false);
        }
    }
    /*
        신고
    */
    @PostMapping("/temper/comment-report")
    @ResponseBody
    public Message reportCommentController(@RequestParam Long commentId){
        int report = coinTemperService.reportCoinComment(commentId); // 신고수 반환
        return new Message(StatusEnum.OK, "OK", report);
    }

    /*
        좋아요
     */
    @PostMapping("/temper/comment-like")
    @ResponseBody
    public Message likeCommentController(@RequestParam Long commentId){
        int like =  coinTemperService.likeCoinComment(commentId);
        return new Message(StatusEnum.OK, "OK", like);
    }

    /*
        싫어요
     */
    @PostMapping("/temper/comment-dislike")
    @ResponseBody
    public Message dislikeCommentController(@RequestParam Long commentId){
        int dislike =  coinTemperService.dislikeCoinComment(commentId);
        return new Message(StatusEnum.OK, "OK", dislike);
    }
}
