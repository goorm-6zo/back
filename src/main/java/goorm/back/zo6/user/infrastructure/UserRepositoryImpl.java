package goorm.back.zo6.user.infrastructure;

import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Builder
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Boolean isExist(String phone) {
        return userJpaRepository.existsByPhone(phone);
    }

    @Override
    public Optional<User> findByEmailAndIsDeleted(String email, boolean isDeleted) {
        return userJpaRepository.findByEmailAndIsDeleted(email, isDeleted);
    }
}
