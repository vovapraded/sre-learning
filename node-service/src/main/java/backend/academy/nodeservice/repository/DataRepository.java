package backend.academy.nodeservice.repository;

import backend.academy.nodeservice.model.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DataRepository extends JpaRepository<DataEntity, Long> {
    Optional<DataEntity> findByKey(String key);
    boolean existsByKey(String key);
}
