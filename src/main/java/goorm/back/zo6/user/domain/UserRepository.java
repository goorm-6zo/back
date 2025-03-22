package goorm.back.zo6.user.domain;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);

    Optional<User> findByPhone(String phone);

    User save(User user);

    Optional<User> findByEmail(String email);

    void deleteById(Long userId);

    Optional<User> findByName(String name);
}
