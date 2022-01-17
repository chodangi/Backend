package MCcrew.Coinportal.game;

import MCcrew.Coinportal.Dto.BetHistoryDto;
import MCcrew.Coinportal.domain.BetHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class GameController {  // 게임 관련 api 컨트롤러

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
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
        코인 상승 하락 예측하기
     */
    @PostMapping("/game/predict")
    public boolean predictCoinController(@RequestBody BetHistoryDto betHistoryDto){
        return true;
    }
}
