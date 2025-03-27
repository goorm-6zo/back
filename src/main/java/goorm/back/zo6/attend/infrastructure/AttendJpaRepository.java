package goorm.back.zo6.attend.infrastructure;

import goorm.back.zo6.attend.domain.Attend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendJpaRepository extends JpaRepository<Attend, Long> {
}