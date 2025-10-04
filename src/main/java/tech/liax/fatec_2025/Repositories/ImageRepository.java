package tech.liax.fatec_2025.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.liax.fatec_2025.Entities.ImageEntity;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageEntity, UUID> {
}
