package it.linearsystem.indago.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "log_errore_file", schema = "indago_user")
public class LogErroreFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long idLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_file", nullable = false)
    private FileUpload fileUpload;

    @Column(name = "errore_descrizione", nullable = false, length = 1000)
    private String erroreDescrizione;

}
