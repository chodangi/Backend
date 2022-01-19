package MCcrew.Coinportal.game;

import MCcrew.Coinportal.Dto.BetHistoryDto;
import MCcrew.Coinportal.domain.BetHistory;
import MCcrew.Coinportal.domain.User;
import MCcrew.Coinportal.user.UserRepository;
import MCcrew.Coinportal.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    private final Random randomGen = new Random();
    private double botWinRate = 0.0; // 봇 승률

    @Autowired
    public GameService(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    private boolean BTC = false;
    private boolean ETH = false;
    private boolean XRP = false;

    /*
        코인 게임 코어 로직
     */
//    public boolean gameTimer(){
//        System.out.println("creating timer...");
//        Timer m = new Timer();
//
//        TimerTask task = new TimerTask(){
//            @Override
//            public void run() {
//
//                // chaging random prediction
//                BTC = randomGen.nextBoolean();
//                ETH = randomGen.nextBoolean();
//                XRP = randomGen.nextBoolean();
//                try {
//                    String priceBTC = getPriceFromBithumb("BTC/KRW");
//                    String priceETH = getPriceFromBithumb("ETH/KRW");
//                    String priceXRP = getPriceFromBithumb("XRP/KRW");
//                }catch(Exception e){
//                    e.printStackTrace();
//                    System.out.println("error calling coin price api in timer");
//                }
//                List<BetHistory> findBetHistory = gameRepository.findAllByDate
//
//            }
//        };
//
//        System.out.println("executing timer...");
//        m.schedule(task, 5000, 60*60*60*8); // 5초 이후 실행 - 8시간 주기로 실행
//        return true;
//    }

    /*
                빗썸에서 코인 현재가격 가져오기
             */
    public String getPriceFromBithumb(String coinSymbol) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.bithumb.com/public/ticker/" + coinSymbol);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);

        // closing_price만 얻도록 파싱
        JSONObject jsonObject = new JSONObject(response.getBody().toString());
        JSONObject data = jsonObject.getJSONObject("data");
        String closing_price = (String) data.get("closing_price");
        return closing_price;
    }

    /*
        <반환값 포맷>
        기준시간 - 시가 - 종가 - 고가 - 저가 - 거래량
     */
    public String getChartFromBithumb(String coinSymbol) {
        String intervals = "1h";  // 한시간으로 설정

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.bithumb.com/public/candlestick/" + coinSymbol + "/" + intervals);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);
        return response.toString();
    }

    /*
        코인 궁예하기
     */
    public BetHistory predict(BetHistoryDto betHistoryDto, Long userId) {
        Date date = new Date();
        BetHistory betHistory = new BetHistory();

        betHistory.setUserId(userId);
        betHistory.setPredictedAt(date);
        betHistory.setBTC(betHistoryDto.isBTC());
        betHistory.setETH(betHistoryDto.isETH());
        betHistory.setXRP(betHistoryDto.isXRP());
        return gameRepository.save(betHistory);
    }

    /*
        코인 훈수 따라가기
     */
    public BetHistory predictRandom(Long userId) {
        Date date = new Date();

        BetHistory betHistory = new BetHistory();
        betHistory.setUserId(userId);
        betHistory.setPredictedAt(date);
        betHistory.setBTC(this.BTC);
        betHistory.setETH(this.ETH);
        betHistory.setXRP(this.XRP);
        return gameRepository.save(betHistory);
    }

    /*
        내 전적 보기
     */
    public List<BetHistory> getMyBetHistory(Long userId) {
        return gameRepository.findById(userId);
    }

    /*
        현재 코인 훈수 보기
     */
    public BetHistoryDto getRandomCoinPrediction() {
        BetHistoryDto betHistoryDto = new BetHistoryDto();
        betHistoryDto.setBTC(this.BTC);
        betHistoryDto.setETH(this.ETH);
        betHistoryDto.setXRP(this.XRP);
        return betHistoryDto;
    }
}

