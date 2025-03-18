package goorm.back.zo6.notice.infrastructure;

import goorm.back.zo6.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice,Long> {
}
