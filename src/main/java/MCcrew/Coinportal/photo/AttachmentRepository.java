package MCcrew.Coinportal.photo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    @Override
    List<Attachment> findAll();

    Optional<Attachment> findById(Long id);
}
