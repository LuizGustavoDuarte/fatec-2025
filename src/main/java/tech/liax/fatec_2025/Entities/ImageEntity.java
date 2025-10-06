package tech.liax.fatec_2025.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TBL_IMAGE")
@Getter @Setter
public class ImageEntity {
    @Id
    @Column(name = "IMAGE_ID", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID imageId;

    @Column(name = "IMAGE_PATH", length = 120)
    private String imagePath;

    @OneToMany(mappedBy = "mainImage", fetch = FetchType.EAGER)
    private List<ProcessesImageEntity> processesImage;
}
