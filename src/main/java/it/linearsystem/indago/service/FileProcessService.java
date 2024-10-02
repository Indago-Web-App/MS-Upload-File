package it.linearsystem.indago.service;

import it.linearsystem.indago.bean.dto.DatabaseMap;
import it.linearsystem.indago.bean.dto.FileProcessDto;
import it.linearsystem.indago.bean.dto.FileValidityDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openmetadata.client.model.Column;
import org.openmetadata.client.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class FileProcessService {

    private final Logger log = LoggerFactory.getLogger(FileProcessService.class);

    @Autowired
    private OMColumnService omColumnService;
    @Autowired
    private OMLineageService omLineageService;
    @Autowired
    private LogErroreFileService logErroreFileService;

    public FileProcessDto processFile(FileValidityDto fileValidityDto) throws IOException {
        DatabaseMap databaseMap = new DatabaseMap();
        databaseMap.setFileUpload(fileValidityDto.getFileUpload());
        try (XSSFWorkbook workbook = new XSSFWorkbook(fileValidityDto.getInputStream())) {
            // Carica il workbook dal file .xlsx

            // Ottieni il primo foglio
            XSSFSheet worksheet = workbook.getSheetAt(0);

            // Ottieni le regioni di celle unite
            List<CellRangeAddress> mergedRegions = worksheet.getMergedRegions();

            // Itera sulle righe del foglio
            int rowIndex = 0;
            for (Row row : worksheet) {
                if (rowIndex == 0) {
                    FileProcessDto result = validateFirstRow(row);
                    if (!result.isValid()) {
                        return result;  // Blocca il processo e ritorna il risultato
                    }
                } else if (rowIndex == 1) {
                    FileProcessDto result = validateSecondRow(row);
                    if (!result.isValid()) {
                        return result;  // Blocca il processo e ritorna il risultato
                    }
                } else {
                    // Per le altre righe, logga solo le prime due celle
                    createOrUpdateListTables(row, databaseMap, worksheet, mergedRegions, rowIndex);
                    if (rowIndex == 3 || rowIndex % 100 == 0) {
                        log.info("Processata riga: " + rowIndex);
                    }
                }
                rowIndex++;
            }
            log.info("FINE PROCESSAMENTO FILE - OK");
            return new FileProcessDto(true, "File validato correttamente", databaseMap);
        }
    }

    private void createOrUpdateListTables(Row row, DatabaseMap databaseMap, XSSFSheet worksheet, List<CellRangeAddress> mergedRegions, int rowIndex) {
        CellValue cellValueSorgente = new CellValue();
        CellValue cellValueDestinazione = new CellValue();


        // Ottieni i valori delle celle
        cellValueSorgente.setTecnologiaName(getMergedCellValue(worksheet, row.getCell(0), mergedRegions));
        cellValueSorgente.setSchemaName(getMergedCellValue(worksheet, row.getCell(1), mergedRegions));
        cellValueSorgente.setTabName(getMergedCellValue(worksheet, row.getCell(2), mergedRegions));
        cellValueSorgente.setColName(getMergedCellValue(worksheet, row.getCell(3), mergedRegions));
        cellValueSorgente.setColType(getMergedCellValue(worksheet, row.getCell(4), mergedRegions));
        if (cellValueSorgente.getTecnologiaName().isEmpty() || cellValueSorgente.getSchemaName().isEmpty() || cellValueSorgente.getTabName().isEmpty() ||
                cellValueSorgente.getColName().isEmpty() || cellValueSorgente.getColType().isEmpty()) {
            databaseMap.addErrore();
            logErroreFileService.insertLogErroreFile(databaseMap.getFileUpload(), rowIndex + " - Valori tabella sorgente non validi");
            return;
        }
        // COME FACCIO POI A CAPIRE QUALE DEVO USARE O meno! COSì non va BENE
        if (cellValueSorgente.getTecnologiaName().contains("\n") || cellValueSorgente.getSchemaName().contains("\n") || cellValueSorgente.getTabName().contains("\n")) {
            databaseMap.addErrore();
            logErroreFileService.insertLogErroreFile(databaseMap.getFileUpload(), rowIndex + " - Valori tabella sorgente non validi");
            return;
        }

        //
        cellValueDestinazione.setTecnologiaName(getMergedCellValue(worksheet, row.getCell(8), mergedRegions));
        cellValueDestinazione.setSchemaName(getMergedCellValue(worksheet, row.getCell(9), mergedRegions));
        cellValueDestinazione.setTabName(getMergedCellValue(worksheet, row.getCell(10), mergedRegions));
        cellValueDestinazione.setColName(getMergedCellValue(worksheet, row.getCell(11), mergedRegions));
        cellValueDestinazione.setColType(getMergedCellValue(worksheet, row.getCell(12), mergedRegions));
        cellValueDestinazione.setRegola(getMergedCellValue(worksheet, row.getCell(6), mergedRegions));

        // E VIA DI TAPULLI
        if (cellValueSorgente.getSchemaName().contains("\n")) {
            String[] schemas = cellValueSorgente.getSchemaName().split("\n");
            if (schemas[0].equals("MODELLO") && schemas[1].equals("UNICO")) {
                cellValueSorgente.setSchemaName("MODELLO_UNICO");
            } else {
                databaseMap.addErrore();
                logErroreFileService.insertLogErroreFile(databaseMap.getFileUpload(), rowIndex + " - Valori tabella sorgente non validi");
                return;
            }
        }
        if (cellValueDestinazione.getSchemaName().contains("\n")) {
            String[] schemas = cellValueDestinazione.getSchemaName().split("\n");
            if (schemas[0].equals("MODELLO") && schemas[1].equals("UNICO")) {
                cellValueDestinazione.setSchemaName("MODELLO_UNICO");
            } else {
                databaseMap.addErrore();
                logErroreFileService.insertLogErroreFile(databaseMap.getFileUpload(), rowIndex + " - Valori tabella sorgente non validi");
                return;
            }
        }

        //////////

        // Verifica se la mappa esiste già
        DatabaseMap.TableMap existingTableMapSorgente = databaseMap.getListTableMapSorgente().stream()
                .filter(dbMap -> dbMap.getTecnologia().equals(cellValueSorgente.getTecnologiaName())
                        && dbMap.getSchema().equals(cellValueSorgente.getSchemaName())
                        && dbMap.getTabella().getName().equals(cellValueSorgente.getTabName()))
                .findFirst()
                .orElse(null);

        if (existingTableMapSorgente != null) {
            try {
                updateTableMap(existingTableMapSorgente, cellValueSorgente, cellValueDestinazione);
            } catch (Exception e) {
                databaseMap.addErrore();
                logErroreFileService.insertLogErroreFile(databaseMap.getFileUpload(), rowIndex + " - " + e.getMessage());
                return;
            }
        } else {
            // Crea un nuovo DatabaseMap se non esiste
            try {
                databaseMap.getListTableMapSorgente().add(createNewTableMap(cellValueSorgente, cellValueDestinazione));
            } catch (Exception e) {
                databaseMap.addErrore();
                logErroreFileService.insertLogErroreFile(databaseMap.getFileUpload(), rowIndex + " - " + e.getMessage());
                return;
            }
        }

        // Estrai i dati per il database di DESTINAZIONE
        // Ottieni i valori delle celle

        // Verifica se la mappa esiste già
        DatabaseMap.TableMap existingTableMapDestinazione = databaseMap.getListTableMapDestinazione().stream()
                .filter(dbMap -> dbMap.getTecnologia().equals(cellValueDestinazione.getTecnologiaName())
                        && dbMap.getSchema().equals(cellValueDestinazione.getSchemaName())
                        && dbMap.getTabella().getName().equals(cellValueDestinazione.getTabName()))
                .findFirst()
                .orElse(null);

        if (existingTableMapDestinazione != null) {
            try {
                updateTableMap(existingTableMapDestinazione, cellValueDestinazione, null);
            } catch (Exception e) {
                databaseMap.addErrore();
                logErroreFileService.insertLogErroreFile(databaseMap.getFileUpload(), rowIndex + " - " + e.getMessage());
            }
        } else {
            try {
                databaseMap.getListTableMapDestinazione().add(createNewTableMap(cellValueDestinazione, null));
            } catch (Exception e) {
                databaseMap.addErrore();
                logErroreFileService.insertLogErroreFile(databaseMap.getFileUpload(), rowIndex + " - " + e.getMessage());
            }
        }
    }

    private void updateTableMap(DatabaseMap.TableMap existingTableMap, CellValue cellValue, CellValue cellValueDestinazione) {
        if (existingTableMap == null) {
            throw new RuntimeException("Si sta cercando di fare update di una tabella che non esiste");
        }
        List<Column> columns = existingTableMap.getTabella().getColumns();
        Table tableSorgente = existingTableMap.getTabella();

        if (!cellValue.getColName().isEmpty()) {
            tableSorgente.setColumns(omColumnService.createOrUpdateColumn(cellValue.getColName(), null, cellValue.getColType(), null, columns));
            if (cellValueDestinazione != null) {
                if (!cellValue.getColName().contains(",")) {
                    existingTableMap.getLineage().add(omLineageService.createLineage(cellValueDestinazione, cellValue.getColName()));
                } else {
                    List<String> listColName = Arrays.asList(cellValue.getColName().split(",\\s*"));
                    IntStream.range(0, listColName.size())
                            .forEach(index -> {
                                existingTableMap.getLineage().add(omLineageService.createLineage(cellValueDestinazione, listColName.get(index)));
                            });
                }
            }
        } else {
            throw new RuntimeException("La colonna è vuota, non la considero");
        }
    }

    private DatabaseMap.TableMap createNewTableMap(CellValue cellValue, CellValue cellValueDestinazione) {
        Table table = new Table();
        table.setName(cellValue.getTabName());

        List<Column> columns = new ArrayList<>();
        table.setColumns(omColumnService.createOrUpdateColumn(cellValue.getColName(), cellValue.getRegola(), cellValue.getColType(), null, columns));

        DatabaseMap.TableMap tableMap = new DatabaseMap.TableMap();
        tableMap.setTecnologia(cellValue.getTecnologiaName());
        tableMap.setSchema(cellValue.getSchemaName());
        tableMap.setTabella(table);
        if (cellValueDestinazione != null && !cellValue.getColName().contains(",")) {
            List<DatabaseMap.Lineage> listLineage = new ArrayList<>();
            listLineage.add(omLineageService.createLineage(cellValueDestinazione, cellValue.getColName()));
            tableMap.setLineage(listLineage);
        }
        return tableMap;
    }

    // Metodo per validare la prima riga
    private FileProcessDto validateFirstRow(Row row) {
        log.info("Validating first row...");

        Cell cell0 = row.getCell(0);
        Cell cell8 = row.getCell(8);

        // Verifica che nella cella[0] ci sia "SORGENTE"
        if (cell0 == null || !cell0.getStringCellValue().equalsIgnoreCase("SORGENTE")) {
            return new FileProcessDto(false, "Errore: La cella 0 della prima riga deve contenere 'SORGENTE'.", null);
        }

        // Verifica che nella cella[8] ci sia "DESTINAZIONE"
        if (cell8 == null || !cell8.getStringCellValue().equalsIgnoreCase("DESTINAZIONE")) {
            return new FileProcessDto(false, "Errore: La cella 8 della prima riga deve contenere 'DESTINAZIONE'.", null);
        }

        // Se entrambe le celle sono valide
        return new FileProcessDto(true, "Prima riga validata correttamente.", null);
    }

    // Metodo per validare la seconda riga
    private FileProcessDto validateSecondRow(Row row) {
        log.info("Validating second row...");

        // Array di valori attesi per la seconda riga
        String[] expectedValues = {
                "TECNOLOGIA", "SISTEMA/DB", "TABELLA", "CAMPO", "TIPO",
                "MDM", "REGOLA", "NOTE", "TECNOLOGIA1", "SISTEMA1/DB1",
                "TABELLA1", "CAMPO1", "TIPO1"
        };

        // Itera su ogni cella della riga e confronta con i valori attesi
        for (int i = 0; i < expectedValues.length; i++) {
            Cell cell = row.getCell(i);
            if (cell == null || !cell.getStringCellValue().equalsIgnoreCase(expectedValues[i])) {
                return new FileProcessDto(false, "Errore: La cella " + i + " della seconda riga deve contenere '" + expectedValues[i] + "'.", null);
            }
        }

        // Se tutte le celle sono valide
        return new FileProcessDto(true, "Seconda riga validata correttamente.", null);
    }

    // Metodo per ottenere il valore corretto di una cella, considerando le celle unite
    private String getMergedCellValue(XSSFSheet sheet, Cell cell, List<CellRangeAddress> mergedRegions) {
        if (cell == null) {
            return "";
        }
        for (CellRangeAddress range : mergedRegions) {
            if (range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                // Se la cella è unita, ritorna il valore della prima cella della regione
                Row firstRow = sheet.getRow(range.getFirstRow());
                Cell firstCell = firstRow.getCell(range.getFirstColumn());
                return getCellValue(firstCell);
            }
        }
        // Se non è unita, ritorna il valore normale della cella
        return getCellValue(cell);
    }

    // Metodo per ottenere il valore di una cella
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                if (cell.getStringCellValue().equals("MODELLO UNICO")) {
                    return "MODELLO_UNICO";
                }
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    @Getter
    @Setter
    public static class CellValue {
        String tecnologiaName;
        String schemaName;
        String tabName;
        String colName;
        String colType;
        String regola;
    }

}
