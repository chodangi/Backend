package MCcrew.Coinportal.photo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final FileStore fileStore;

    public AttachmentService(AttachmentRepository attachmentRepository, FileStore fileStore) {
        this.attachmentRepository = attachmentRepository;
        this.fileStore = fileStore;
    }

    public List<Attachment> saveAttachments(List<MultipartFile> multipartFileList, Long postId) throws IOException {
        List<Attachment> imageFiles = fileStore.storeFiles(multipartFileList, postId);
        return imageFiles;
    }

    public List<Attachment> findAttachments() {
        List<Attachment> attachments = attachmentRepository.findAll();
        return attachments;
    }
}