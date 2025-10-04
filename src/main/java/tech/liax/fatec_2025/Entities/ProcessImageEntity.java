package tech.liax.fatec_2025.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TBL_IMAGEM_PROCESSO")
@Data
public class ProcessImageEntity {

    @Id
    @Column(name = "PROCESSO_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer processoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGEM_ID")
    private TblImagem imagem;

    @Column(name = "PROCESS_CODE", length = 30)
    private String nome;

    @Column(name = "RESULT_PATH", length = 120)
    private String descricao;

}
