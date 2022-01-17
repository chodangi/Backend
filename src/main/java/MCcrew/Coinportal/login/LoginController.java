package MCcrew.Coinportal.login;

import MCcrew.Coinportal.domain.User;
import MCcrew.Coinportal.user.UserRepository;
import MCcrew.Coinportal.user.UserService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Controller
public class LoginController {
    private final LoginService loginService;
    private final JwtService jwtService;

    @Value("${kakao.oauth.client_id}")
    private String client_id;
    @Value("${kakao.oauth.redirect_uri}")
    private String redirect_uri;
    @Value("${kakao.oauth.logout_redirect_uri}")
    private String logout_redirect_uri;

    @Getter
    private static HashMap<String, String> hashMap = new HashMap<>(); // 사용자 로그인 상태를 유지

    @Autowired
    public LoginController(LoginService loginService, JwtService jwtService) {
        this.loginService = loginService;
        this.jwtService = jwtService;
    }

    /*
        디폴트 컨트롤러
     */
    @GetMapping(value = "/")
    public String indexController(){
        return "authLogin";
    }

    @GetMapping("/tokakao")
    public String tokakaoController(){
        return "redirect:https://kauth.kakao.com/oauth/authorize"
                + "?client_id="
                + client_id
                + "&redirect_uri="
                + redirect_uri;
    }
    @GetMapping("/tokakao/logout")
    public String tokakaoLogoutController(){
        return "redirect:https://kauth.kakao.com/oauth/logout"
                + "?client_id="
                + client_id
                + "&logout_redirect_uri="
                + logout_redirect_uri;
    }


    /*
        로그인 컨트롤러
     */
    @GetMapping(value = "/login")
    public String loginController(@RequestParam("code") String code, RedirectAttributes re) throws UnsupportedEncodingException {
            String jwt = loginService.getJwt(code, hashMap);
            re.addAttribute("jwt", jwt);
            System.out.println("generated jwt = " + jwt);
            System.out.println("loginController to redirect:/");
            return "redirect:/";
    }

    /*
        로그아웃 컨트롤러
     */
    @GetMapping(value="/logout")
    public void logoutController() throws Exception {
        String reqURL = "https://kauth.kakao.com/oauth/logout";
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setDoOutput(true);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        StringBuilder sb = new StringBuilder();
        sb.append("?client_id=" + client_id);
        sb.append("&logout_redirect_uri=" + logout_redirect_uri);
        bw.write(sb.toString());
        bw.flush();
        //    결과 코드가 200이라면 성공
        int responseCode = conn.getResponseCode();
        System.out.println("responseCode : " + responseCode);
    }
}
