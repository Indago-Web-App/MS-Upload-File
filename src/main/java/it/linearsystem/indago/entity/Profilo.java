package it.linearsystem.indago.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "profilo", schema = "indago_user")
public class Profilo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profilo")
    private Long idProfilo;

    @Column(name = "descrizione", nullable = false, length = 50)
    private String descrizione;

    @OneToMany(mappedBy = "profilo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Utente> utenti;

}