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

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CoinTemperController(CoinTemperService coinTemperService, JwtService jwtService) {
        this.coinTemperService = coinTemperService;
        this.jwtService = jwtService;
    }

    /**
        현재 코인 체감 온도
     */
    @GetMapping("/temper/coin-temper")
    @ResponseBody
    public Message coinTemperController(){
        logger.info("coinTemperController(): 현재 코인 체감 온도 반환");
        try {
            List<Double> result = coinTemperService.getCoinTemper();
            return new Message(StatusEnum.OK, "OK", result);
        }catch(Exception e){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", new ArrayList());
        }
    }

    /**
        코인 매수
        symbol = BTC or ETH or XRP
     */
    @GetMapping("/temper/up/{symbol}")
    @ResponseBody
    public Message coinBuyController(@PathVariable String symbol, @RequestHeader String jwt){
        logger.info("coinBuyController(): " + symbol +"코인 온도가 증가합니다."); 
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }else{
            double result = coinTemperService.temperIncrease(symbol);
            return new Message(StatusEnum.OK, "OK" , result);
        }
    }

    /**
            코인 매도
            symbol = BTC or ETH or XRP
    */
    @GetMapping("/temper/down/{symbol}")
    @ResponseBody
    public Message coinSellController(@PathVariable String symbol, @RequestHeader String jwt){
        logger.info("coinSellController(): " + symbol +"코인 온도가 감소합니다.");
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", false);
        }else{
            double result = coinTemperService.temperDecrease(symbol);
            return new Message(StatusEnum.OK, "OK" , result);
        }
    }

    /**
        코인 체감 온도 댓글달기
        symbol = BTC or ETH or XRP
     */
    @PostMapping("/temper/{symbol}/comment")
    @ResponseBody
    public Message createCommentController(@PathVariable String symbol, @RequestBody CoinCommentDto coinCommentDto, @RequestHeader String jwt){
        logger.info("createCommentController(): " + symbol + "에 댓글을 작성합니다.");
        Long userIdx = jwtService.getUserIdByJwt(jwt);
        if(userIdx == 0L){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", new CoinComment());
        }else{
            CoinComment result = coinTemperService.createComment(coinCommentDto, symbol, userIdx);
            return new Message(StatusEnum.OK, "OK", result);
        }
    }

    /**
        코인 체감 온도 댓글 반환
        symbol = BTC or ETH or XRP
     */
    @GetMapping("/temper/{symbol}/comments")
    @ResponseBody
    public Message getCommentController(@PathVariable String symbol){
        logger.info("getCommentController(): " + symbol + "의 댓글을 반환합니다. ");
        List<CoinComment> coinCommentList = coinTemperService.getCommentList(symbol);
        return new Message(StatusEnum.OK, "OK", coinCommentList );
    }

    /**
        수정
     */
    @PostMapping("/temper/comment")
    @ResponseBody
    public Message updateCommentController(@RequestBody CoinCommentDto coinCommentDto, @RequestHeader String jwt){
        logger.info("updateCommentController(): 댓글을 수정합니다.");
        CoinComment coinComment;
        if(jwt != null){
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
        }else{
            coinComment = coinTemperService.updateCoinCommentByNonUser(coinCommentDto);
            if(coinComment.getId() == null){
                return new Message(StatusEnum.NOT_FOUND,"NOT_FOUND", coinComment);
            }else{
                return new Message(StatusEnum.OK,"OK", coinComment);
            }
        }
    }

    /**
        삭제
     */
    @DeleteMapping("/temper/comment")
    @ResponseBody
    public Message deleteCommentController(@RequestBody CoinCommentDto coinCommentDto, @RequestHeader String jwt ){
        logger.info("deleteCommentController(): 댓글을 삭제합니다.");
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

    /**
        신고
    */
    @PostMapping("/temper/comment-report")
    @ResponseBody
    public Message reportCommentController(@RequestParam Long commentId){
        logger.info("reportCommentController(): " + commentId+ "번 댓글을 신고합니다.");
        int report = coinTemperService.reportCoinComment(commentId);
        return new Message(StatusEnum.OK, "OK", report);
    }

    /**
        좋아요
     */
    @PostMapping("/temper/comment-like")
    @ResponseBody
    public Message likeCommentController(@RequestParam Long commentId){
        logger.info("likeCommentController(): " + commentId+ "번 댓글을 좋아합니다.");
        int like =  coinTemperService.likeCoinComment(commentId);
        return new Message(StatusEnum.OK, "OK", like);
    }

    /**
        싫어요
     */
    @PostMapping("/temper/comment-dislike")
    @ResponseBody
    public Message dislikeCommentController(@RequestParam Long commentId){
        logger.info("dislikeCommentController(): " + commentId+ "번 댓글을 싫어합니다.");

        int dislike =  coinTemperService.dislikeCoinComment(commentId);
        return new Message(StatusEnum.OK, "OK", dislike);
    }
}
