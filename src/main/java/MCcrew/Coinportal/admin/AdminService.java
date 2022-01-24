package MCcrew.Coinportal.admin;

import MCcrew.Coinportal.domain.Dto.NoticeDto;
import MCcrew.Coinportal.domain.Notice;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /*
        모든 공지글 가져오기
     */
    public List<Notice> getAllNotice(){
        return adminRepository.findAll();
    }

    /*
        공지글 작성
     */
    public Notice createNotice(NoticeDto noticeDto) {
        Date date = new Date();
        Notice notice = new Notice();
        notice.setNickname(noticeDto.getNickname());
        notice.setContent(noticeDto.getContent());
        notice.setCreatedAt(date);
        notice.setUpdatedAt(date);
        return adminRepository.save(notice);
    }

    /*
        공지글 수정
     */
    public Notice updateNotice(NoticeDto noticeDto, Long noticeId) {
        Date date = new Date();
        Notice findNotice = adminRepository.findById(noticeId);
        findNotice.setNickname(noticeDto.getNickname());
        findNotice.setContent(noticeDto.getContent());
        findNotice.setUpdatedAt(date);
        return adminRepository.save(findNotice);
    }

    /*
        공지글 삭제
     */
    public boolean deleteNotice(Long noticeId) {
        int deletedNotice = adminRepository.deleteById(noticeId);
        if(deletedNotice > 0 ){
            return true;
        }else{
            return false;
        }
    }
}
