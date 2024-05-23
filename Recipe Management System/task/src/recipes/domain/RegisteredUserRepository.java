package recipes.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RegisteredUserRepository extends CrudRepository<RegisteredUser, Long> {
    Optional<RegisteredUser> findByUsername(String username);
}
