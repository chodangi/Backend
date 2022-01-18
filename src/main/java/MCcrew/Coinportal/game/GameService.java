package MCcrew.Coinportal.game;

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
import java.util.Random;

@Service
public class GameService {

    private final GameRepository gameRepository;

    private final Random randomGen = new Random();

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
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
        코인 훈수
     */
//    public boolean coinPredictByRandom(){
//        boolean BTC = randomGen.nextBoolean();
//        boolean ETH = randomGen.nextBoolean();
//        boolean XRP = randomGen.nextBoolean();
//
//    }
}

