package tech.liax.fatec_2025.Entities;

import jakarta.persistence.*;
import lombok.*;
import tech.liax.fatec_2025.Utils.ProcessCodeEnum;

@Entity
@Table(name = "TBL_IMAGE_PROCESS")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessesImageEntity {
    @Id
    @Column(name = "PROCESS_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long processId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAIN_IMAGE_ID", nullable = false)
    private ImageEntity mainImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESSED_IMAGE_ID", nullable = false)
    private ImageEntity processedImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROCESS_CODE", nullable = false)
    private ProcessCodeEnum processCode;
}
