package tech.liax.fatec_2025.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.liax.fatec_2025.Entities.ProcessesImageEntity;

@Repository
public interface ProcessesImageRepository extends JpaRepository<ProcessesImageEntity, Long> { }
