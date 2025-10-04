package tech.liax.fatec_2025.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TBL_IMAGE")
@Getter @Setter
public class ImageEntity {
    @Id
    @Column(name = "IMAGE_ID", nullable = false, unique = true)
    private UUID imageId;

    @Column(name = "IMAGE_PATH", length = 120)
    private String imagePath;

    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessesImageEntity> processesImage = new ArrayList<>();
}
