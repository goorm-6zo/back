package goorm.back.zo6.notice.infrastructure;

import goorm.back.zo6.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice,Long> {
    List<Notice> findByConferenceIdAndSessionIdIsNull(Long conferenceId);
    List<Notice> findByConferenceIdAndSessionId(Long conferenceId, Long sessionId);
}
