package drinks.system.accessservice.domain.port.out;

import drinks.system.accessservice.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;

/**
 * Puerto de salida para acceso a datos de usuarios.
 * Define el contrato de persistencia usando exclusivamente tipos del dominio.
 */
public interface UserRepositoryPort {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User save(User user);

    Page<User> findAll(Pageable pageable, Boolean isActive, Long branchId, String search);

    void updateLastLogin(Long userId, Instant lastLogin);
}
