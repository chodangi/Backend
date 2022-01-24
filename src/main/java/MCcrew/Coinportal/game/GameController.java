package MCcrew.Coinportal.game;

import MCcrew.Coinportal.domain.Dto.BetHistoryDto;
import MCcrew.Coinportal.domain.Dto.UserRankingDto;
import MCcrew.Coinportal.domain.BetHistory;
import MCcrew.Coinportal.login.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController("/game")
public class GameController {  // 게임 관련 api 컨트롤러

    private final GameService gameService;
    private final JwtService jwtService;

    // 로깅
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public GameController(GameService gameService, JwtService jwtService) {
        this.gameService = gameService;
        this.jwtService = jwtService;
    }

    /*
            코인 현재가 가져오기
            <코인심볼>
            비트: BTC/KRW
            이더: ETH/KRW
            리플: XRP/KRW
        */
    @GetMapping("/coinPrice")
    public String coinInfo(@RequestParam("symbol") String coinSymbol) throws JsonProcessingException {
        return gameService.getPriceFromBithumb(coinSymbol);
    }

    /*
        코인 차트 정보 가져오기
        <코인 심볼>
        BTC_KRW
        ETH_KRW
        XRP_KRW
     */
    @GetMapping("/coinChart")
    public String coinChart(@RequestParam("symbol") String coinSymbol){
        return gameService.getChartFromBithumb(coinSymbol);
    }

    /*
        코인 궁예하기
     */
    @PostMapping("/predict")
    public BetHistory predictCoinController(@RequestBody BetHistoryDto betHistoryDto, @RequestHeader String jwt) throws UnsupportedEncodingException {
        if(jwt == null){
            return new BetHistory();
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new BetHistory();
        }else{
            return gameService.predict(betHistoryDto, userId);
        }
    }

    /*
        훈수 예측 따라가기
     */
    @PostMapping("/predict/random")
    public BetHistory predictCoinRandomController(@RequestHeader String jwt) throws UnsupportedEncodingException {
        if(jwt == null){
            return new BetHistory();
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new BetHistory();
        }else{
            return gameService.predictRandom(userId);
        }
    }

    /*
        내 전적 보기
     */
    @GetMapping("/myhistory")
    public List<BetHistory> getMyBetHistoryController(@RequestHeader String jwt) throws UnsupportedEncodingException {
        if(jwt == null){
            return new ArrayList<>();
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new ArrayList<>();
        }else{
            return gameService.getMyBetHistory(userId);
        }
    }

    /*
        현재 코인 훈수 보기
     */
    @GetMapping("/coinprediction")
    public BetHistoryDto getRandomCoinPredictionController(){
        return gameService.getRandomCoinPrediction();
    }

    /*
        타이머 작동 테스트
     */
//    @GetMapping("/timer")
//    @ResponseBody
//    public boolean timerTest(){
//        return gameService.gameTimer();
//    }

    /*
        유저 랭킹
     */
    @GetMapping("/ranking")
    public List<UserRankingDto> getUserRankingController(){
        return gameService.getGamePointRanking();
    }
}
