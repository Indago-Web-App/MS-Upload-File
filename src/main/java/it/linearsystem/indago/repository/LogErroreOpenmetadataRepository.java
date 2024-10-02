package it.linearsystem.indago.repository;

import it.linearsystem.indago.entity.LogErroreOpenmetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogErroreOpenmetadataRepository extends JpaRepository<LogErroreOpenmetadata, Long> {
}