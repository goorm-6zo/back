package goorm.back.zo6.user.domain;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    User save(User user);
    Boolean isExist(String phone);

    Optional<User> findByEmail(String email);

    void deleteById(Long userId);

    Optional<User> findByName(String name);
}
