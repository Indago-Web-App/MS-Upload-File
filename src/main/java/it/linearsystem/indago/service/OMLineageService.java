package it.linearsystem.indago.service;

import it.linearsystem.indago.bean.dto.DatabaseMap;
import lombok.Getter;
import lombok.Setter;
import org.openmetadata.client.api.TablesApi;
import org.openmetadata.client.gateway.OpenMetadata;
import org.openmetadata.client.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OMLineageService {

    private static final Logger logger = LoggerFactory.getLogger(OMLineageService.class);

    @Getter
    @Setter
    public static class LineageToProcess {
        UUID idTabSorgente;
        UUID idTabDestinazione;
        Map<String, List<String>> lineageEntity;
    }

    public DatabaseMap.Lineage createLineage(FileProcessService.CellValue cellValue, String colNameSorgente) {
        return new DatabaseMap.Lineage(cellValue.getTecnologiaName(), cellValue.getSchemaName(), cellValue.getTabName(), cellValue.getColName(), colNameSorgente);
    }

    public void prepareLineageFromTabSorgente(OpenMetadata openMetadataGateway, DatabaseMap.TableMap tableMap, List<LineageToProcess> lineageMap) {
        TablesApi tablesApi = openMetadataGateway.buildClient(TablesApi.class);
        Table tableSorgente = tablesApi.getTableByFQN(tableMap.getFQNTable(), null);
        UUID idSorgente = tableSorgente.getId();

        for (DatabaseMap.Lineage lineageItem : tableMap.getLineage()) {
            Table tableDestinazione = tablesApi.getTableByFQN(lineageItem.getFQNTable(), null);
            UUID idDestinazione = tableDestinazione.getId();

            LineageToProcess existingLineage = null;
            for (LineageToProcess ltp : lineageMap) {
                if (ltp.getIdTabSorgente().equals(idSorgente) && ltp.getIdTabDestinazione().equals(idDestinazione)) {
                    existingLineage = ltp;
                    break;
                }
            }

            if (existingLineage == null) {
                // Non esiste, crea un nuovo LineageToProcess
                LineageToProcess lineageToProcess = new LineageToProcess();
                lineageToProcess.setIdTabSorgente(idSorgente);
                lineageToProcess.setIdTabDestinazione(idDestinazione);

                // Crea una nuova mappa per lineageEntity
                Map<String, List<String>> lineageEntity = new HashMap<>();
                List<String> sourceColumns = new ArrayList<>();
                if (!checkEsistenzaColumnInTable(lineageItem.getNameColumnSorgente(), tableSorgente)) {
                    logger.error("Colonna non presente in tabella: " + tableSorgente.getName() + ", colonna: " + lineageItem.getNameColumnSorgente());
                    break;
                }
                sourceColumns.add(tableSorgente.getFullyQualifiedName() + "." + lineageItem.getNameColumnSorgente());
                lineageEntity.put(lineageItem.getFQNColumn(), sourceColumns);
                lineageToProcess.setLineageEntity(lineageEntity);

                // Aggiungi il nuovo LineageToProcess alla lista
                lineageMap.add(lineageToProcess);
            } else {
                // Esiste già, aggiorna la mappa lineageEntity
                Map<String, List<String>> lineageEntity = existingLineage.getLineageEntity();
                String fqnColumn = lineageItem.getFQNColumn();

                if (!checkEsistenzaColumnInTable(lineageItem.getNameColumnSorgente(), tableSorgente)) {
                    logger.error("Colonna non presente in tabella: " + tableSorgente.getName() + ", colonna: " + lineageItem.getNameColumnSorgente());
                    break;
                }
                List<String> sourceColumns = lineageEntity.get(fqnColumn);
                if (sourceColumns == null) {
                    sourceColumns = new ArrayList<>();
                    lineageEntity.put(fqnColumn, sourceColumns);
                }
                sourceColumns.add(tableSorgente.getFullyQualifiedName() + "." + lineageItem.getNameColumnSorgente());
            }
        }
    }

    private boolean checkEsistenzaColumnInTable(String columnName, Table table) {
        return table.getColumns().stream().anyMatch(c -> c.getName().equals(columnName));
    }

}
