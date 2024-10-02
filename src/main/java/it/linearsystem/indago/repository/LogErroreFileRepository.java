package it.linearsystem.indago.repository;

import it.linearsystem.indago.entity.LogErroreFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogErroreFileRepository extends JpaRepository<LogErroreFile, Long> {
}