package tech.liax.fatec_2025.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.liax.fatec_2025.Entities.ProcessImageEntity;

public interface ProcessRepository extends JpaRepository<ProcessImageEntity, Integer> {

}
