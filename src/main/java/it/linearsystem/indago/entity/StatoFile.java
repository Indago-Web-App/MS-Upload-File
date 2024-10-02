package it.linearsystem.indago.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "stato_file", schema = "indago_user")
public class StatoFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stato")
    private Long idStato;

    @Column(name = "descrizione", nullable = false, length = 50)
    private String descrizione;

    @OneToMany(mappedBy = "statoFile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FileUpload> fileUploads;

}