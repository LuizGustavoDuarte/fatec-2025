package tech.liax.fatec_2025.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TBL_IMAGE_PROCESS")
@Getter @Setter
public class ProcessesImageEntity {
    @Id
    @Column(name = "PROCESS_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long processId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID", nullable = false)
    private ImageEntity image;

    @Column(name = "PROCESS_CODE", length = 30)
    private String processCode;

    @Column(name = "RESULT_PATH", length = 120)
    private String resultPath;
}
