package MCcrew.Coinportal;

import MCcrew.Coinportal.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class CoinPortalApplication {
	public static void main(String[] args) {
		SpringApplication.run(CoinPortalApplication.class, args);
	}
}
