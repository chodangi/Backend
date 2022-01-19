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


    private int botWins = 0;            // 훈수 승리 횟수
    private int botTotalPlay = 0;       // 훈수 전체 플레이 횟수
    private double botWinRate = 0.0;    // 훈수 승률
    private double botBtcPrice = 0;        // 훈수 당시 btc 가격
    private double botEthPrice = 0;        // 훈수 당시 eth 가격
    private double botXrpPrice = 0;        // 훈수 당시 xrp 가격

    private boolean BTC = false;
    private boolean ETH = false;
    private boolean XRP = false;

    @Autowired
    public GameService(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    /*
        코인 게임 코어 로직
     */
    boolean tempBTC;
    boolean tempETH;
    boolean tempXRP;

    Date tempDate;
    // (승리/전체플레이)*100 = 승률%
    public boolean gameTimer(){
        System.out.println("creating timer...");
        Timer m = new Timer();
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                try {
                    double priceBTC = Double.valueOf(getPriceFromBithumb("BTC/KRW"));
                    double priceETH = Double.valueOf(getPriceFromBithumb("ETH/KRW"));
                    double priceXRP = Double.valueOf(getPriceFromBithumb("XRP/KRW"));
                }catch(Exception e){
                    e.printStackTrace();
                    System.out.println("error calling coin price api in timer.");
                    m.cancel();
                    m.purge();
                }
                /*
                    훈수 승률 계산
                 */

                /*
                    유저들 승률 계산
                 */
                List<BetHistory> findBetHistory = gameRepository.findAllByDate(tempDate);


                /*
                    새로운 랜덤 훈수 생성
                 */

                // chaging random prediction
                BTC = randomGen.nextBoolean();
                ETH = randomGen.nextBoolean();
                XRP = randomGen.nextBoolean();
                tempBTC = BTC;
                tempETH = ETH;
                tempXRP = XRP;
            }
        };

        System.out.println("executing timer...");
        m.schedule(task, 5000, 1000*60*60*8); // 5초 이후 실행 - 8시간 주기로 실행
        return true;
    }
    /*
        승률 계산
     */
    public double calWinRate(){
        return 1.1;
    }

    /*
                빗썸에서 코인 현재가격 가져오기
             */
    public String getPriceFromBithumb(String coinSymbol){
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
        // closing_price 만 얻도록 파싱
        String closing_price = "";
        try{
        JSONObject jsonObject = new JSONObject(response.getBody().toString());
        JSONObject data = jsonObject.getJSONObject("data");
        closing_price = (String) data.get("closing_price");
        }catch(Exception e){
            e.printStackTrace();
            return "Exception while parsing json Ojbject";
        }
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

        double priceBTC = Double.valueOf(getPriceFromBithumb("BTC/KRW"));
        double priceETH = Double.valueOf(getPriceFromBithumb("ETH/KRW"));
        double priceXRP = Double.valueOf(getPriceFromBithumb("XRP/KRW"));

        betHistory.setUserId(userId);
        betHistory.setPredictedAt(date);

        betHistory.setBTC(betHistoryDto.isBTC());
        betHistory.setETH(betHistoryDto.isETH());
        betHistory.setXRP(betHistoryDto.isXRP());

        betHistory.setBtcPriceNow(priceBTC);
        betHistory.setEthPriceNow(priceETH);
        betHistory.setXrpPriceNow(priceXRP);

        return gameRepository.save(betHistory);
    }

    /*
        코인 훈수 따라가기
     */
    public BetHistory predictRandom(Long userId) {
        Date date = new Date();
        BetHistory betHistory = new BetHistory();

        double priceBTC = Double.valueOf(getPriceFromBithumb("BTC/KRW"));
        double priceETH = Double.valueOf(getPriceFromBithumb("ETH/KRW"));
        double priceXRP = Double.valueOf(getPriceFromBithumb("XRP/KRW"));

        betHistory.setUserId(userId);
        betHistory.setPredictedAt(date);

        betHistory.setBTC(this.BTC);
        betHistory.setETH(this.ETH);
        betHistory.setXRP(this.XRP);

        betHistory.setBtcPriceNow(priceBTC);
        betHistory.setEthPriceNow(priceETH);
        betHistory.setXrpPriceNow(priceXRP);

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

