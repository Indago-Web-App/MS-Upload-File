package it.linearsystem.indago.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "utente", schema = "indago_user")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utente")
    private Long idUtente;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "cognome", nullable = false, length = 100)
    private String cognome;

    @Column(name = "cod_fiscale", nullable = false, unique = true, length = 16)
    private String codFiscale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profilo", nullable = false)
    private Profilo profilo;

    @Column(name = "data_creazione", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataCreazione;

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FileUpload> fileUploads;

}