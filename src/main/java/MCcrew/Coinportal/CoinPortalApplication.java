package MCcrew.Coinportal;

import MCcrew.Coinportal.game.GameService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoinPortalApplication {
	public static void main(String[] args) {
		SpringApplication.run(CoinPortalApplication.class, args);
		// GameService.gameTimer(); // 게임 시작
	}
}
