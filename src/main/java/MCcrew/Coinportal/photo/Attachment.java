package MCcrew.Coinportal.photo;

import MCcrew.Coinportal.domain.Post;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name="ATTACHMENT_SEQ_GENERATOR",
        sequenceName = "ATTACHMENT_SEQ"
)
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String originFilename;
    private String storeFilename;

//    @ManyToOne
//    @JoinColumn(name = "post_id")
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Attachment(Long id, String originFilename, String storeFilename, Post post) {
        this.id = id;
        this.originFilename = originFilename;
        this.storeFilename = storeFilename;
        this.post = post;
    }
}