package MCcrew.Coinportal.game;

import MCcrew.Coinportal.Dto.BetHistoryDto;
import MCcrew.Coinportal.domain.BetHistory;
import MCcrew.Coinportal.domain.User;
import MCcrew.Coinportal.user.UserRepository;
import MCcrew.Coinportal.user.UserService;
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

    public GameService(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
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
        return response.toString();
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
        boolean BTC = randomGen.nextBoolean();
        boolean ETH = randomGen.nextBoolean();
        boolean XRP = randomGen.nextBoolean();

        BetHistory betHistory = new BetHistory();
        betHistory.setUserId(userId);
        betHistory.setPredictedAt(date);
        betHistory.setBTC(BTC);
        betHistory.setETH(ETH);
        betHistory.setXRP(XRP);
        return gameRepository.save(betHistory);
    }

    /*
        게임 타이머
     */
    public static boolean gameTimer(){
        System.out.println("starting timer");
        Timer m = new Timer();
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                System.out.println("test");
            }
        };
        System.out.println("execute timer");
        m.schedule(task, 5000, 2000);
        return true;
    }

    /*
        내 전적 보기
     */
    public List<BetHistory> getMyBetHistory(Long userId) {
        return gameRepository.findById(userId);
    }
}

