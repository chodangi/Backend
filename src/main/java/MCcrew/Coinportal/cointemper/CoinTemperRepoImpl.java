package MCcrew.Coinportal.cointemper;

import MCcrew.Coinportal.domain.CoinComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoinTemperRepoImpl extends JpaRepository<CoinComment, Long> {
    Page<CoinComment> findByLevelAndCoinSymbol(int level, String symbol, Pageable pageable);
    List<CoinComment> findByCommentGroupAndCoinSymbol(int group, String symbol);
}
