package MCcrew.Coinportal.login;

import MCcrew.Coinportal.domain.User;
import MCcrew.Coinportal.user.UserRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final LoginRepository loginRepository;

    @Autowired
    public LoginService(UserRepository userRepository, JwtService jwtService, LoginRepository loginRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.loginRepository = loginRepository;
    }

    public String getAccessToken (String authorize_code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //    POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //    POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=bbac0c1542268d22cd795e3d398071f5");
            sb.append("&redirect_uri=http://localhost:8080/login");
            sb.append("&code=" + authorize_code);
            bw.write(sb.toString());
            bw.flush();

            //    결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //    요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //    Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
//            JsonParser parser = new JsonParser();
//            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return access_Token;
    }

    public HashMap<String, String> getUserInfo (String access_Token) {
        //    요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        HashMap<String, String> userInfo = new HashMap<>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            //    요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakao_account.getAsJsonObject().get("email").getAsString();

            userInfo.put("nickname", nickname);
            userInfo.put("email", email);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return userInfo;
    }

    public void kakaoLogout(String access_Token) {
        String reqURL = "https://kapi.kakao.com/v1/user/logout";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getJwt(String code, HashMap<String, String> hashMap) throws UnsupportedEncodingException {
        String jwt = "";
        System.out.println("code : " + code);

        String access_Token = getAccessToken(code);
        System.out.println("access_Token : " + access_Token);

        HashMap<String, String> userInfo = getUserInfo(access_Token);
        System.out.println("login Controller : " + userInfo);

        // 새로 추가된 코드 jwt 작성
        String name = userInfo.get("nickname");
        String email = userInfo.get("email");

        // 이름과 이메일로 기존 사용자 조회
        //User findUser = userRepository.findByNameAndEmail(name, email);
        jwt = jwtService.generateJwt(name, email);
        if(userRepository.findByNameAndEmail(name, email).getId() == null){ // 존재하지 않는 회원이라면 새롭게 추가 진행
            User user = new User();
            user.setEmail(email);
            user.setUserNickname(getNicknameFromEmail(email)); // 이메일 기반 닉네임 생성
            user.setPoint(0);
            user.setDark(true);
            user.setOnAlarm(true);
            user.setStatus('A');
            userRepository.save(user);

            System.out.println("new User added. name = " + name + ", email = " + email + jwt);
            hashMap.put(name, jwt); // 로그인 유지
        }else{ // 존재하는 회원이라면
            hashMap.put(name, jwt);
            System.out.println("already in member , get past jwt in hashMap : ");
            jwt = hashMap.get(name); // 이미 저장되어있던 value인 jwt를 얻어낸다.
            System.out.println("jwt : " + jwt);
        }
        return jwt;
    }

    /*
        유저 존재 확인하기
     */
    public String checkUserExistence(Long userId) {
        User findUser = loginRepository.findById(userId);
        return findUser.getUserNickname();
    }
    /*
    @ 기호를 기준으로 이메일에서 닉네임을 뽑아냄.
    */
    public String getNicknameFromEmail(String email){
        int position = email.indexOf('@');
        String nickname = email.substring(0, position);
        return nickname;
    }

}
