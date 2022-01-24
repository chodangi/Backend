package MCcrew.Coinportal.photo;

import MCcrew.Coinportal.domain.Dto.PostDto;
import MCcrew.Coinportal.board.BoardService;
import MCcrew.Coinportal.domain.Post;
import MCcrew.Coinportal.login.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController("/attach")
public class AttachmentController {
    private final FileStore fileStore;
    private final JwtService jwtService;
    private final BoardService boardService;

    // 로깅
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public AttachmentController(FileStore fileStore, JwtService jwtService, BoardService boardService) {
        this.fileStore = fileStore;
        this.jwtService = jwtService;
        this.boardService = boardService;
    }

    /*
        게시글과 이미지 포스팅하기
     */
    @PostMapping("/post-image")
    public Post doPost(@ModelAttribute PostDto postDto, @RequestHeader String jwt) throws IOException {
        Long userIdx = 0L;
        try{ // 유저 인증
            userIdx = jwtService.getUserIdByJwt(jwt);
            System.out.println("dopost: 유저인증:" +userIdx);
        }catch(Exception e){
            e.printStackTrace();
            return new Post();
        }
        System.out.println("userIdx : " + userIdx);
        Post post = boardService.post(postDto, userIdx);
        return post;
    }

    /*
        이미지 로드
     */
    @GetMapping("/{filename}")
    public Resource processImg(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.createPath(filename));
    }

    /*
        이미지 다운로드
     */
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> processAttaches(@PathVariable String filename, @RequestParam String originName) throws MalformedURLException {
        UrlResource urlResource = new UrlResource("file:" + fileStore.createPath(filename));

        String encodedUploadFileName = UriUtils.encode(originName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }
}
