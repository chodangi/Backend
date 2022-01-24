package MCcrew.Coinportal.cointemper;

import MCcrew.Coinportal.domain.Dto.CoinCommentDto;
import MCcrew.Coinportal.domain.CoinComment;
import MCcrew.Coinportal.login.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@RestController("/temper")
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
    @GetMapping("/coin-temper")
    public List<Double> coinTemper(){
        return coinTemperService.getCoinTemper();
    }

    /*
        코인 매수
        symbol = BTC or ETH or XRP
     */
    @GetMapping("/up/{symbol}")
    public double coinBuy(@PathVariable String symbol, @RequestHeader String jwt) throws UnsupportedEncodingException {
        Long userId = jwtService.getUserIdByJwt(jwt); // 회원만 이용가능
        if(userId == 0L){
            return -1L; // 글 삭제되지 않음.
        }else{
            return coinTemperService.temperIncrease(symbol);
        }
    }
    /*
            코인 매도
            symbol = BTC or ETH or XRP
    */
    @GetMapping("/down/{symbol}")
    public double coinSell(@PathVariable String symbol, @RequestHeader String jwt) throws UnsupportedEncodingException {
        Long userId = jwtService.getUserIdByJwt(jwt); // 회원만 이용가능
        if(userId == 0L){
            return -1L; // 글 삭제되지 않음.
        }else{
            return coinTemperService.temperDecrease(symbol);
        }
    }

    /*
        코인 체감 온도 댓글달기
        symbol = BTC or ETH or XRP
     */
    @PostMapping("/{symbol}/comment")
    public CoinComment createCommentController(@PathVariable String symbol, @RequestBody CoinCommentDto coinCommentDto, @RequestHeader String jwt) throws UnsupportedEncodingException {
        Long userIdx = jwtService.getUserIdByJwt(jwt);
        if(userIdx == 0L){
            return new CoinComment();
        }else{
            return coinTemperService.createComment(coinCommentDto, symbol, userIdx);
        }
    }

    /*
        코인 체감 온도 댓글 반환
        symbol = BTC or ETH or XRP
     */
    @GetMapping("/{symbol}/comments")
    public List<CoinComment> getCommentController(@PathVariable String symbol){
        return coinTemperService.getCommentList(symbol);
    }

    /*
        수정
     */
    @PostMapping("/comment")
    public CoinComment updateCommentController(@RequestBody CoinCommentDto coinCommentDto, @RequestHeader String jwt) throws UnsupportedEncodingException {
        if(jwt != null){ //회원의 글이라면
            Long userId = jwtService.getUserIdByJwt(jwt);
            if(userId == 0L)
                return new CoinComment();
            else
                return coinTemperService.updateCoinComment(coinCommentDto, userId);
        }else{ //비회원의 글이라면
            return coinTemperService.updateCoinCommentByNonUser(coinCommentDto);
        }
    }
    /*
        삭제
     */
    @DeleteMapping("/comment")
    public boolean deleteCommentController(@RequestBody CoinCommentDto coinCommentDto, @RequestHeader String jwt ) throws UnsupportedEncodingException {
        if(jwt != null){ //회원의 글이라면
            Long userId = jwtService.getUserIdByJwt(jwt);
            if(userId == 0L)
                return false;
            else
                return coinTemperService.deleteCoinComment(coinCommentDto, userId);
        }else{ //비회원의 글이라면
            return coinTemperService.deleteCoinCommentByNonUser(coinCommentDto);
        }
    }
    /*
        신고
    */
    @PostMapping("/comment-report")
    public int reportCommentController(@RequestParam Long commentId){
        return coinTemperService.reportCoinComment(commentId); // 신고수 반환
    }

    /*
        좋아요
     */
    @PostMapping("/comment-like")
    public int likeCommentController(@RequestParam Long commentId){
        return coinTemperService.likeCoinComment(commentId);
    }

    /*
        싫어요
     */
    @PostMapping("/comment-dislike")
    public int dislikeCommentController(@RequestParam Long commentId){
        return coinTemperService.dislikeCoinComment(commentId);
    }
}
