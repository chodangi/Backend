package MCcrew.Coinportal.game;

import MCcrew.Coinportal.Dto.BetHistoryDto;
import MCcrew.Coinportal.domain.BetHistory;
import MCcrew.Coinportal.login.JwtService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GameController {  // 게임 관련 api 컨트롤러

    private final GameService gameService;
    private final JwtService jwtService;


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
    @GetMapping("/game/coinPrice")
    @ResponseBody
    public String coinInfo(@RequestParam("symbol") String coinSymbol){
        return gameService.getPriceFromBithumb(coinSymbol);
    }

    /*
        코인 차트 정보 가져오기
        <코인 심볼>
        BTC_KRW
        ETH_KRW
        XRP_KRW
     */
    @GetMapping("/game/coinChart")
    @ResponseBody
    public String coinChart(@RequestParam("symbol") String coinSymbol){
        return gameService.getChartFromBithumb(coinSymbol);
    }

    /*
        코인 궁예하기
     */
    @PostMapping("/game/predict")
    @ResponseBody
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
    @PostMapping("/game/predict/random")
    @ResponseBody
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
    @GetMapping("/game/myhistory")
    @ResponseBody
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
        타이머 작동 테스트
     */
    @GetMapping("/timer")
    @ResponseBody
    public boolean timerTest(){
        return gameService.gameTimer();
    }
}
