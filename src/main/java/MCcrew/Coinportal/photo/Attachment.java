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
//    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Attachment(Long id, String originFileName, String storePath) {
        this.id = id;
        this.originFilename = originFileName;
        this.storeFilename = storePath;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", originFilename='" + originFilename + '\'' +
                ", storeFilename='" + storeFilename + '\'' +
                ", post=" + post +
                '}';
    }
}