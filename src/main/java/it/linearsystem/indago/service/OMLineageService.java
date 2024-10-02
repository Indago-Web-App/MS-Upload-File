package it.linearsystem.indago.service;

import it.linearsystem.indago.bean.dto.DatabaseMap;
import it.linearsystem.indago.entity.FileUpload;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.openmetadata.client.api.LineageApi;
import org.openmetadata.client.api.TablesApi;
import org.openmetadata.client.gateway.OpenMetadata;
import org.openmetadata.client.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OMLineageService {

    private static final Logger logger = LoggerFactory.getLogger(OMLineageService.class);

    @Autowired
    private LogErroreFileService logErroreFileService;
    @Autowired
    private TablesApiService tablesApiService;

    @Getter
    @Setter
    public static class LineageToProcess {
        UUID idTabSorgente;
        UUID idTabDestinazione;
        Map<String, List<String>> lineageMapEntity;
    }

    public DatabaseMap.Lineage createLineage(FileProcessService.CellValue cellValue, String colNameSorgente) {
        return new DatabaseMap.Lineage(cellValue.getTecnologiaName(), cellValue.getSchemaName(), cellValue.getTabName(), cellValue.getColName(), colNameSorgente);
    }

    public void createLineageOMD(@NotNull OpenMetadata openMetadataGateway, @NotNull OMLineageService.LineageToProcess lineageProcess) {
        LineageApi lineageApi = openMetadataGateway.buildClient(LineageApi.class);

        // QUA CI METTIAMO LE TABELLE
        EntityReference fromEntity = new EntityReference();
        fromEntity.setId(lineageProcess.getIdTabSorgente());
        /*fromEntity.setFullyQualifiedName(omTabSorgente.getTableSorgente().getFullyQualifiedName());*/
        fromEntity.setType("table");

        EntityReference toEntity = new EntityReference();
        toEntity.setId(lineageProcess.getIdTabDestinazione());
        /*toEntity.setFullyQualifiedName(lineage.getFQNTable());*/
        toEntity.setType("table");

        // QUA LE COLONNE
        LineageDetails lineageDetails = new LineageDetails();
        List<ColumnLineage> columnLineages = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : lineageProcess.getLineageMapEntity().entrySet()) {
            ColumnLineage columnLineage = new ColumnLineage();

            String fqnColumn = entry.getKey();
            columnLineage.setToColumn(fqnColumn);

            List<String> sourceColumns = entry.getValue();
            columnLineage.setFromColumns(sourceColumns);

            columnLineages.add(columnLineage);
        }
        lineageDetails.setColumnsLineage(columnLineages);

        // CREO L'EDGE
        EntitiesEdge edge = new EntitiesEdge();
        edge.setFromEntity(fromEntity);
        edge.setToEntity(toEntity);
        edge.setLineageDetails(lineageDetails);

        AddLineage addLineage = new AddLineage();
        addLineage.setEdge(edge);

        lineageApi.addLineageEdge(addLineage);
    }

    public List<OMLineageService.LineageToProcess> prepareLineageFromList(List<DatabaseMap.TableMap> listTableMap, FileUpload fileUpload) {
        ExecutorService executor = Executors.newFixedThreadPool(5); // Configura il numero di thread secondo le tue esigenze

        // Mappa thread-safe per accumulare LineageToProcess
        Map<String, OMLineageService.LineageToProcess> lineageToProcessMap = new ConcurrentHashMap<>();

        // Contatore atomico per tenere traccia delle tabelle elaborate
        AtomicInteger recordsProcessed = new AtomicInteger(0);
        int totalRecords = listTableMap.size(); // Numero totale di tabelle da elaborare

        // Log iniziale
        logger.info("Inizio elaborazione di " + totalRecords + " tabelle.");

        List<CompletableFuture<List<OMLineageService.LineageToProcess>>> futures = listTableMap.stream()
                .map(tableMap -> CompletableFuture.supplyAsync(() -> {
                    try {
                        Table tableSorgente = tablesApiService.getTableByFQN(tableMap.getFQNTable(), fileUpload);
                        if (tableSorgente == null) {
                            return null;
                        }
                        UUID idSorgente = tableSorgente.getId();

                        List<OMLineageService.LineageToProcess> lineageList = new ArrayList<>();
                        for (DatabaseMap.Lineage lineage : tableMap.getLineage()) {

                            Table tableDestinazione = tablesApiService.getTableByFQN(lineage.getFQNTable(), fileUpload);
                            if (tableDestinazione == null) {
                                return null;
                            }
                            UUID idDestinazione = tableDestinazione.getId();

                            // Creiamo una chiave univoca per la coppia sorgente-destinazione
                            String key = idSorgente + "-" + idDestinazione;


                            // Accumuliamo le informazioni in modo thread-safe
                            lineageToProcessMap.compute(key, (k, lineageToProcess) -> {
                                if (lineageToProcess == null) {
                                    lineageToProcess = new OMLineageService.LineageToProcess();
                                    lineageToProcess.setIdTabSorgente(idSorgente);
                                    lineageToProcess.setIdTabDestinazione(idDestinazione);
                                    lineageToProcess.setLineageMapEntity(new ConcurrentHashMap<>());
                                }

                                Map<String, List<String>> lineageEntity = lineageToProcess.getLineageMapEntity();

                                // Chiave e valore per lineageEntity
                                String nameColumnFQN = lineage.getFQNColumn();
                                if (!checkEsistenzaColumnInTable(lineage.getNameColumn(), tableDestinazione)) {
                                    logger.error("LINEAGE - Colonna non presente in tabella DESTINAZIONE: " + tableDestinazione.getName() + ", colonna: " + lineage.getNameColumn());
                                    logErroreFileService.insertLogErroreFile(fileUpload, "LINEAGE - Colonna DESTINAZIONE LINEAGE non presente in tabella: " + tableDestinazione.getFullyQualifiedName() + ", colonna: " + lineage.getNameColumn());
                                    return null;
                                }

                                String nameColumnSorgenteFQN = tableSorgente.getFullyQualifiedName() + "." + lineage.getNameColumnSorgente();
                                if (!checkEsistenzaColumnInTable(lineage.getNameColumnSorgente(), tableSorgente)) {
                                    logger.error("LINEAGE - Colonna non presente in tabella SORGENTE: " + tableSorgente.getName() + ", colonna: " + lineage.getNameColumnSorgente());
                                    logErroreFileService.insertLogErroreFile(fileUpload, "LINEAGE - Colonna SORGENTE LINEAGE non presente in tabella: " + tableSorgente.getFullyQualifiedName() + ", colonna: " + lineage.getNameColumnSorgente());
                                    return null;
                                }

                                // Accumuliamo nameColumnSorgente nella lista associata a nameColumn
                                lineageEntity.compute(nameColumnFQN, (colKey, colList) -> {
                                    if (colList == null) {
                                        colList = Collections.synchronizedList(new ArrayList<>());
                                    }
                                    colList.add(nameColumnSorgenteFQN);
                                    return colList;
                                });

                                return lineageToProcess;
                            });
                        }
                        return lineageList;
                    } catch (Exception e) {
                        logger.error("LINEAGE - Errore preprocessamento Lineage: " + tableMap.getFQNTable() + ", errore: " + e.getMessage());
                        logErroreFileService.insertLogErroreFile(fileUpload, "LINEAGE - Errore BLOCCANTE preprocessamento lineage tabella: " + tableMap.getFQNTable() + ", errore: " + e.getMessage());
                        return Collections.<OMLineageService.LineageToProcess>emptyList();
                    } finally {
                        int processedCount = recordsProcessed.incrementAndGet();
                        // Registra il numero di record elaborati
                        if (processedCount == 1 || (processedCount % 30 == 0) || processedCount == totalRecords) {
                            logger.info("Ho elaborato {} su {} tableMap", processedCount, totalRecords);
                        }
                    }
                }, executor))
                .toList();

        // Aspettiamo il completamento di tutte le operazioni asincrone
        futures.forEach(CompletableFuture::join);

        executor.shutdown();

        List<OMLineageService.LineageToProcess> listResponse = new ArrayList<>(lineageToProcessMap.values());
        // Raccogliamo i risultati dalla mappa
        // return new ArrayList<>(lineageToProcessMap.values());
        return listResponse;
    }

    private boolean checkEsistenzaColumnInTable(String columnName, Table table) {
        return table.getColumns().stream().anyMatch(c -> c.getName().equals(columnName));
    }

}
