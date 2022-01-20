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
    private List<Long> playerList = new ArrayList<>();  // 플레이에 참여한 유저
    private final Random randomGen = new Random();

    private int botPreviousWins = 0;    // 훈수 이전 승리 횟수
    private int botTotalPlay = 0;       // 훈수 전체 플레이 횟수
    private int botPoint = 0;           // 훈수 점수
    private double botWinRate = 0.0;    // 훈수 승률

    private double botBtcPriceTemp = 0;        // 훈수 당시 btc 가격
    private double botEthPriceTemp = 0;        // 훈수 당시 eth 가격
    private double botXrpPriceTemp = 0;        // 훈수 당시 xrp 가격

    private boolean botBTC = false;
    private boolean botETH = false;
    private boolean botXRP = false;

    @Autowired
    public GameService(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    /*
        코인 게임 코어 로직
     */
    Date tempDate;
    /*
       승률 계산: (승리/전체플레이)*100 = 승률%
    */
    public double calWinRate(int previousWins, int totalPlay, int wins){
        return (double) (((previousWins + wins) / (totalPlay)) * 100);
    }

    public boolean gameTimer() {
        System.out.println("creating timer...");
        Timer m = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                double priceBTC = 0.0;
                double priceETH = 0.0;
                double priceXRP = 0.0;
                try {
                    priceBTC = Double.valueOf(getPriceFromBithumb("BTC/KRW"));
                    priceETH = Double.valueOf(getPriceFromBithumb("ETH/KRW"));
                    priceXRP = Double.valueOf(getPriceFromBithumb("XRP/KRW"));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("error calling coin price api in timer.");
                    m.cancel();
                    m.purge();
                }
                /*
                    훈수 승률 계산
                 */
                int wins = 0;
                if (botBtcPriceTemp >= priceBTC) {
                    if(botBTC == true){
                        ++wins;
                    }
                }else{
                    if(botBTC == false){
                        ++wins;
                    }
                }
                if (botEthPriceTemp >= priceETH) {
                    if(botETH == true){
                        ++wins;
                    }
                }
                else{
                    if(botETH == false){
                        ++wins;
                    }
                }
                if (botXrpPriceTemp >= priceXRP) {
                    if(botXRP == true){
                        ++wins;
                    }
                }else{
                    if(botXRP == false){
                        ++wins;
                    }
                }
                botPoint = (int) botPoint + wins * 100;
                botTotalPlay += 1;
                botPreviousWins += wins;
                botWinRate = calWinRate(botPreviousWins, botTotalPlay, wins);

                /*
                    유저들 승률 계산
                 */
                List<BetHistory> findBetHistory = gameRepository.findAllByDate(tempDate);
                wins = 0;
                for(BetHistory betHistory: findBetHistory){
                    User findUser = userRepository.findById(betHistory.getId());

                    if (betHistory.getBtcPriceNow() >= priceBTC) {
                        if(betHistory.isBTC() == true){
                            ++wins;
                        }
                    }else{
                        if(betHistory.isBTC() == false){
                            ++wins;
                        }
                    }
                    if (betHistory.getEthPriceNow() >= priceETH) {
                        if(betHistory.isETH() == true){
                            ++wins;
                        }
                    }
                    else{
                        if(betHistory.isETH() == false){
                            ++wins;
                        }
                    }
                    if (betHistory.getXrpPriceNow() >= priceXRP) {
                        if(betHistory.isXRP() == true){
                            ++wins;
                        }
                    }else{
                        if(betHistory.isXRP() == false){
                            ++wins;
                        }
                    }
                    findUser.setPoint(findUser.getPoint() + ((int) (100*wins)));
                    findUser.setWinsRate(calWinRate(findUser.getPreviousWins(), findUser.getTotalPlay(), wins));
                    findUser.setPreviousWins(findUser.getPreviousWins() + wins);
                    findUser.setTotalPlay(findUser.getTotalPlay() + 1);
                    userRepository.save(findUser);
                }

                // changing random prediction
                botBTC = randomGen.nextBoolean();
                botETH = randomGen.nextBoolean();
                botXRP = randomGen.nextBoolean();
                botBtcPriceTemp = Double.valueOf(getPriceFromBithumb("BTC/KRW"));
                botEthPriceTemp = Double.valueOf(getPriceFromBithumb("ETH/KRW"));
                botXrpPriceTemp = Double.valueOf(getPriceFromBithumb("XRP/KRW"));
                playerList.clear(); // 플레이한 유저 리스트 초기화
            }
        };

        System.out.println("executing timer...");
        m.schedule(task, 5000, 1000 * 60 * 60 * 8); // 5초 이후 실행 - 8시간 주기로 실행
        return true;
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
        if(playerList.contains(userId)) // 이미 플레이한 유저라면 이용 불가능
            return new BetHistory();

        playerList.add(userId);

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
        if(playerList.contains(userId)) // 이미 플레이한 유저라면 이용 불가능
            return new BetHistory();

        playerList.add(userId);

        Date date = new Date();
        BetHistory betHistory = new BetHistory();

        double priceBTC = Double.valueOf(getPriceFromBithumb("BTC/KRW"));
        double priceETH = Double.valueOf(getPriceFromBithumb("ETH/KRW"));
        double priceXRP = Double.valueOf(getPriceFromBithumb("XRP/KRW"));

        betHistory.setUserId(userId);
        betHistory.setPredictedAt(date);

        betHistory.setBTC(this.botBTC);
        betHistory.setETH(this.botETH);
        betHistory.setXRP(this.botXRP);

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
        betHistoryDto.setBTC(this.botBTC);
        betHistoryDto.setETH(this.botETH);
        betHistoryDto.setXRP(this.botXRP);
        return betHistoryDto;
    }
}

