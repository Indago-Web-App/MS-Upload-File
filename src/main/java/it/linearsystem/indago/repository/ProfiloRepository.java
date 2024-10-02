package it.linearsystem.indago.repository;

import it.linearsystem.indago.entity.Profilo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfiloRepository extends JpaRepository<Profilo, Long> {
}