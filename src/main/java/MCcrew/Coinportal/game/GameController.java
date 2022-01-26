package MCcrew.Coinportal.game;

import MCcrew.Coinportal.domain.Dto.BetHistoryDto;
import MCcrew.Coinportal.domain.Dto.UserRankingDto;
import MCcrew.Coinportal.domain.BetHistory;
import MCcrew.Coinportal.login.JwtService;
import MCcrew.Coinportal.util.Message;
import MCcrew.Coinportal.util.StatusEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final JwtService jwtService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public GameController(GameService gameService, JwtService jwtService) {
        this.gameService = gameService;
        this.jwtService = jwtService;
    }

    /**
            코인 현재가격 가져오기
            <코인심볼>
            비트: BTC/KRW
            이더: ETH/KRW
            리플: XRP/KRW
     */
    @GetMapping("/coin-price/{symbol}")
    public Message coinInfo(@PathVariable String symbol){
        logger.info("coinInfo(): "+ symbol + "코인의 현재가격 가져오기");
        String result =  gameService.getPriceFromBithumb(symbol);
        if(result.equals("null")){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", result);
        }
        return new Message(StatusEnum.OK, "OK", result);
    }

    /**
        코인 차트 정보 가져오기
        <코인 심볼>
        BTC_KRW
        ETH_KRW
        XRP_KRW
     */
    @GetMapping("/coin-chart/{symbol}")
    public Message coinChart(@PathVariable String symbol){
        logger.info("coinChart(): "+ symbol + "코인의 차트 정보 가져오기");
        String result = "";
        try {
            result = gameService.getChartFromBithumb(symbol);
        }catch(Exception e){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", result);
        }
        return new Message(StatusEnum.OK, "OK", result);
    }

    /**
        코인 궁예하기
     */
    @PostMapping("/game-play")
    public Message predictCoinController(@RequestBody BetHistoryDto betHistoryDto, @RequestHeader String jwt){
        logger.info("predictCoinController(): 코인 궁예시작하기 - 게임 스타트 ");
        if(jwt == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", new BetHistory());
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", new BetHistory());
        }else{
            BetHistory betHistory = gameService.predict(betHistoryDto, userId);
            return new Message(StatusEnum.OK, "OK", betHistory);
        }
    }

    /**
        코인 훈수 예측 따라가기
     */
    @PostMapping("/random")
    public Message predictCoinRandomController(@RequestHeader String jwt){
        logger.info("predictCoinRandomController(): 코인 훈수 예측 따라가기 - 랜덤 생성");
        if(jwt == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", new BetHistory());
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", new BetHistory());
        }else{
            BetHistory betHistory = gameService.predictRandom(userId);
            return new Message(StatusEnum.OK, "OK", betHistory);
        }
    }

    /**
        내 전적 보기
     */
    @GetMapping("/my-history")
    public Message getMyBetHistoryController(@RequestHeader String jwt){
        logger.info("getMyBetHistoryController(): 내 전적 보기");
        if(jwt == null){
            return new Message(StatusEnum.BAD_REQUEST, "BAD_REQUEST", new ArrayList<>());
        }
        Long userId = jwtService.getUserIdByJwt(jwt);
        if(userId == 0L){
            return new Message(StatusEnum.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", new ArrayList<>());
        }else{
            List<BetHistory> betHistoryList = gameService.getMyBetHistory(userId);
            return new Message(StatusEnum.OK, "OK", betHistoryList);
        }
    }

    /**
        현재 코인 훈수 보기
     */
    @GetMapping("/coin-prediction")
    public Message getRandomCoinPredictionController(){
        logger.info("getRandomCoinPredictionController(): 현재 코인 훈수 보기 ");
        BetHistoryDto betHistoryDto =  gameService.getRandomCoinPrediction();
        return new Message(StatusEnum.OK, "OK", betHistoryDto);
    }

    /**
        유저 랭킹
     */
    @GetMapping("/ranking")
    public Message getUserRankingController(){
        logger.info("getUserRankingController(): 유저 랭킹 보기");
        List<UserRankingDto> resultList = gameService.getGamePointRanking();
        return new Message(StatusEnum.OK, "OK", resultList);
    }
}
