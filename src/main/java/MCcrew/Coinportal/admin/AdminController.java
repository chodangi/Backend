package MCcrew.Coinportal.admin;

import MCcrew.Coinportal.board.BoardService;
import MCcrew.Coinportal.cointemper.CoinTemperService;
import MCcrew.Coinportal.comment.CommentService;
import MCcrew.Coinportal.game.GameService;
import MCcrew.Coinportal.login.LoginService;
import MCcrew.Coinportal.photo.AttachmentService;
import MCcrew.Coinportal.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminController {

    private final BoardService boardService;
    private final CoinTemperService coinTemperService;
    private final CommentService commentService;
    private final GameService gameService;
    private final LoginService loginService;
    private final AttachmentService attachmentService;
    private final UserService userService;

    public AdminController(BoardService boardService, CoinTemperService coinTemperService, CommentService commentService, GameService gameService, LoginService loginService, AttachmentService attachmentService, UserService userService) {
        this.boardService = boardService;
        this.coinTemperService = coinTemperService;
        this.commentService = commentService;
        this.gameService = gameService;
        this.loginService = loginService;
        this.attachmentService = attachmentService;
        this.userService = userService;
    }
}
