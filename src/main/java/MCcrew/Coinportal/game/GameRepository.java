package MCcrew.Coinportal.game;

import MCcrew.Coinportal.domain.BetHistory;
import MCcrew.Coinportal.domain.Comment;
import MCcrew.Coinportal.domain.Post;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@Transactional
public class GameRepository {

    private EntityManager em;

    public GameRepository(EntityManager em) {
        this.em = em;
    }

    public BetHistory save(BetHistory betHistory){
        if(betHistory.getId() == null){
            em.persist(betHistory);
            return betHistory;
        }else{
            em.merge(betHistory);
            return betHistory;
        }
    }

    public List<BetHistory> findById(Long userId) {
       String sql = "select b from BetHistory b where b.userId = :userId";
       return em.createQuery(sql, BetHistory.class).setParameter("userId", userId).getResultList();
    }
}
