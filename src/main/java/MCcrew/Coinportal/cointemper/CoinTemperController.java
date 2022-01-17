package MCcrew.Coinportal.cointemper;

import MCcrew.Coinportal.Dto.CoinCommentDto;
import MCcrew.Coinportal.Dto.CommentDto;
import MCcrew.Coinportal.domain.CoinComment;
import MCcrew.Coinportal.domain.Comment;
import MCcrew.Coinportal.login.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class CoinTemperController {

    private final CoinTemperService coinTemperService;
    private final JwtService jwtService;

    @Autowired
    public CoinTemperController(CoinTemperService coinTemperService, JwtService jwtService) {
        this.coinTemperService = coinTemperService;
        this.jwtService = jwtService;
    }

    /*
        현재 코인 체감 온도
     */
    @GetMapping("/coin/temper")
    @ResponseBody
    public List<Double> coinTemper(){
        return coinTemperService.getCoinTemper();
    }

    /*
        코인 매수
        symbol = BTC or ETH or XRP
     */
    @GetMapping("/coin/temper/buy")
    @ResponseBody
    public double coinBuy(@RequestParam String symbol, @RequestHeader String jwt) throws UnsupportedEncodingException {
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
    @GetMapping("/coin/temper/sell")
    @ResponseBody
    public double coinSell(@RequestParam String symbol, @RequestHeader String jwt) throws UnsupportedEncodingException {
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
    @PostMapping("/coin/temper/comment")
    public CoinComment createCommentController(@RequestBody CoinCommentDto coinCommentDto, @RequestParam String symbol, @RequestHeader String jwt) throws UnsupportedEncodingException {
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
    @GetMapping("/coin/temper/commentList")
    @ResponseBody
    public List<CoinComment> getCommentController(@RequestParam String symbol){
        return coinTemperService.getCommentList(symbol);
    }

    /*
        수정
     */
    @PostMapping("/coin/temper/update")
    @ResponseBody
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
    @PostMapping("/coin/temper/delete")
    @ResponseBody
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
    @PostMapping("/coin/temper/report")
    @ResponseBody
    public int reportCommentController(@RequestParam Long commentId){
        return coinTemperService.reportCoinComment(commentId); // 신고수 반환
    }

    /*
        좋아요
     */
    @PostMapping("/coin/temper/like")
    @ResponseBody
    public int likeCommentController(@RequestParam Long commentId){
        return coinTemperService.likeCoinComment(commentId);
    }

    /*
        싫어요
     */
    @PostMapping("/coin/temper/dislike")
    @ResponseBody
    public int dislikeCommentController(@RequestParam Long commentId){
        return coinTemperService.dislikeCoinComment(commentId);
    }
}
