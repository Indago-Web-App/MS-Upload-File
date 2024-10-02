package it.linearsystem.indago.repository;

import it.linearsystem.indago.entity.StatoFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatoFileRepository extends JpaRepository<StatoFile, Long> {
}