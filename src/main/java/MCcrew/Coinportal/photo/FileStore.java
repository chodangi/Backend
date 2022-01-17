package MCcrew.Coinportal.photo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}/")
    private String fileDirPath;

    // 전체 파일 저장
    public List<Attachment> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                attachments.add(storeFile(multipartFile));
            }
        }
        return attachments;
    }

    // 파일 저장 로직
    public Attachment storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFilename = createStoreFilename(originalFilename);
        System.out.println("storeFile Method - originalFilename: " + originalFilename);
        System.out.println("storeFile Method - storeFilename: " + storeFilename);
        multipartFile.transferTo(new File(createPath(storeFilename)));

        return Attachment.builder()
                .originFileName(originalFilename)
                .storePath(storeFilename)
                .build();
    }
    // 파일 경로 구성
    public String createPath(String storeFilename) {
        return fileDirPath + storeFilename;
    }

    // 저장할 파일 이름 구성
    private String createStoreFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        String storeFilename = uuid + ext;
        System.out.println("createStoreFilename Method - storeFilename: " + storeFilename);
        return storeFilename;
    }

    // 확장자 추출
    private String extractExt(String originalFilename) {
        int idx = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(idx);
        return ext;
    }
}