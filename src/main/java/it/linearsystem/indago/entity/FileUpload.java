package it.linearsystem.indago.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "file_upload", schema = "indago_user")
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_file")
    private Long idFile;

    @Column(name = "nome_file", nullable = false, length = 255)
    private String nomeFile;

    @Lob
    @Column(name = "file_contenuto")
    private byte[] fileContenuto;

    @Column(name = "numero_errori", nullable = false, columnDefinition = "NUMBER DEFAULT 0")
    private Integer numeroErrori;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente utente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_stato", nullable = false)
    private StatoFile statoFile;

    @Column(name = "data_caricamento", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCaricamento;

    @OneToMany(mappedBy = "fileUpload", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LogErroreFile> logErroriFile;

}