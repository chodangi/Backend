package MCcrew.Coinportal.board;

import MCcrew.Coinportal.domain.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class BoardRepository{

    private final EntityManager em;

    @Autowired
    public BoardRepository(EntityManager em) {
        this.em = em;
    }

    /*
        게시물 등록
     */
    public Post save(Post post) {
        if(post.getId() == null){
            em.persist(post);
            return post;
        }else {
            em.merge(post);
            return post;
        }
    }

    /*
        단일 게시물 조회
     */
    public Post findById(Long id) {
        return em.find(Post.class, id);
    }

    /*
        모든 게시물 조회 
     */
    public List<Post> findAll() {
        String sql = "select p from Post p";
        return em.createQuery(sql, Post.class).getResultList(); // 전체 글 목록 가져오기
    }
    /*
        인기순으로 게시글 조회
     */
    public List<Post> findByPopularity() {
        String sql = "select p from Post p order by p.upCnt DESC";
        return em.createQuery(sql, Post.class).getResultList();
    }

    /*
        닉네임으로 게시물 조회
     */
    public Post findByNickname(String userNickname){
        String sql = "select p from Post p where p.userNickname = :userNickname";
        return em.createQuery(sql, Post.class)
                .setParameter("userNickname", userNickname).getResultList().get(0);
    }
    /*
        게시판별로 게시글 조회
     */
    public List<Post> findByBoardName(String boardName){
        String sql = "select p from Post p where p.boardName = :boardName";
        return em.createQuery(sql, Post.class).setParameter("boardName", boardName).getResultList();
    }

    /*
        해당 게시물 삭제
     */
    public int delete(Long postId) {
        String sql = "delete from Post p where p.id = :postId";
        Query query = em.createQuery(sql).setParameter("postId", postId);
        return query.executeUpdate(); // return number of deleted column
    }
}
