package it.linearsystem.indago.service;

import it.linearsystem.indago.bean.dto.DatabaseMap;
import it.linearsystem.indago.bean.dto.FileProcessDto;
import it.linearsystem.indago.bean.dto.FileValidityDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openmetadata.schema.entity.data.Table;
import org.openmetadata.schema.type.Column;
import org.openmetadata.schema.type.ColumnConstraint;
import org.openmetadata.schema.type.ColumnDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileProcessService {

    private final Logger log = LoggerFactory.getLogger(FileProcessService.class);

    public FileProcessDto processFile(FileValidityDto fileValidityDto) throws IOException {
        XSSFWorkbook workbook = null;
        DatabaseMap databaseMap = new DatabaseMap();
        try {
            // Carica il workbook dal file .xlsx
            workbook = new XSSFWorkbook(fileValidityDto.getInputStream());

            // Ottieni il primo foglio
            XSSFSheet worksheet = workbook.getSheetAt(0);

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
                    databaseMap = updateDatabaseMap(row, databaseMap);
                }
                rowIndex++;
            }
            return new FileProcessDto(true, "File validato correttamente", databaseMap);
        } finally {
            // Chiude il workbook se aperto
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    private DatabaseMap updateDatabaseMap(Row row, DatabaseMap databaseMap) {
        // Estrai i dati per il database di origine
        Cell databaseSorgenteCell = row.getCell(1);
        Cell tabellaSorgenteCell = row.getCell(2);
        Cell colonnaSorgenteCell = row.getCell(3);
        Cell typeColonnaSorgenteCell = row.getCell(4);

        // Se i dati di origine sono presenti, crea o aggiorna la tabella
        if (databaseSorgenteCell != null && tabellaSorgenteCell != null && colonnaSorgenteCell != null) {
            Map<String, List<Table>> database = createOrUpdateTable(
                    databaseSorgenteCell.getStringCellValue(),
                    tabellaSorgenteCell.getStringCellValue(),
                    colonnaSorgenteCell.getStringCellValue(),
                    typeColonnaSorgenteCell.getStringCellValue(),
                    databaseMap,
                    true  // Indica che è la sorgente
            );
            databaseMap.setDatabaseSorgente(database);
        }

        // Estrai i dati per il database di destinazione
        Cell databaseDestinazioneCell = row.getCell(9);
        Cell tabellaDestinazioneCell = row.getCell(10);
        Cell colonnaDestinazioneCell = row.getCell(11);
        Cell typeColonnaDestinazioneCell = row.getCell(12);

        // Se i dati di destinazione sono presenti, crea o aggiorna la tabella
        if (databaseDestinazioneCell != null && tabellaDestinazioneCell != null && colonnaDestinazioneCell != null) {
            Map<String, List<Table>> database = createOrUpdateTable(
                    databaseDestinazioneCell.getStringCellValue(),
                    tabellaDestinazioneCell.getStringCellValue(),
                    colonnaDestinazioneCell.getStringCellValue(),
                    typeColonnaDestinazioneCell.getStringCellValue(),
                    databaseMap,
                    false  // Indica che è la destinazione
            );
            databaseMap.setDatabaseDestinazione(database);
        }
        return databaseMap;
    }

    private Map<String, List<Table>> createOrUpdateTable(String nomeDatabase, String nomeTabella, String nomeColonna, String typeColonna, DatabaseMap databaseMap, boolean isSorgente) {
        // Recupera il database corretto (origine o destinazione) in base al flag `isSorgente`
        Map<String, List<Table>> database = isSorgente ? databaseMap.getDatabaseSorgente() == null ? new HashMap<>() : databaseMap.getDatabaseSorgente()
                : databaseMap.getDatabaseDestinazione() == null ? new HashMap<>() : databaseMap.getDatabaseDestinazione();

        // Controlla se il database esiste già nella mappa
        if (!database.containsKey(nomeDatabase)) {
            // Se non esiste, crea una nuova lista di tabelle
            System.out.println("Creazione di un nuovo database per: " + nomeDatabase);
            database.put(nomeDatabase, new ArrayList<>()); // Crea la nuova lista di tabelle
        }

        // Recupera la lista di Table per il database
        List<Table> tableList = database.get(nomeDatabase);

        // Verifica se la tabella esiste già
        Table table = tableList.stream()
                .filter(t -> t.getName().equals(nomeTabella))
                .findAny().orElse(null);

        if (table != null) {
            // Se la tabella esiste già, fai un log
            System.out.println("La tabella " + nomeTabella + " esiste già nel database " + nomeDatabase);
            // Verifica se la colonna esiste già
            boolean colonnaEsiste = table.getColumns().stream()
                    .anyMatch(col -> col.getName().equals(nomeColonna));
            if (!colonnaEsiste) {
                // Aggiungi la colonna se non esiste
                Column column = createColumn(nomeColonna, null, typeColonna, null);
                table.getColumns().add(column);
                System.out.println("Aggiunta la colonna " + nomeColonna + " alla tabella " + nomeTabella);
            }
        } else {
            // Se non esiste, crea e aggiungi la tabella
            table = new Table();
            table.setName(nomeTabella);  // Nome corretto della tabella
            List<Column> columns = new ArrayList<>();
            columns.add(createColumn(nomeColonna, null, typeColonna, null));
            table.setColumns(columns);
            tableList.add(table);
            System.out.println("Aggiunta la tabella " + nomeTabella + " al database " + nomeDatabase + " con la colonna " + nomeColonna);
        }
        return database;
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

    // Metodo per loggare solo le prime due celle di righe successive
    private void logRowFirstTwoCells(Row row) {
        log.info("Logging first two cells of row " + row.getRowNum());
        for (int cellNum = 0; cellNum < 2; cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null) {
                log.info("ploadFile - ROW " + row.getRowNum() + " - CELL " + cellNum + " Value: " + getCellValueAsString(cell));
            } else {
                log.info("ploadFile - ROW " + row.getRowNum() + " - CELL " + cellNum + " is null");
            }
        }
    }

    // Metodo per ottenere il valore della cella come stringa
    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "Unknown Value";
        }
    }

    private Column createColumn(
            String colName,
            String colDesc,
            String tipoColonnaStr,
            ColumnConstraint constraint) {
        Column column = new Column();
        column.setName(colName);
        column.setDescription(colDesc);
        column.setDisplayName(colName);

        // Data TYPE
        ColumnDataType dataType = null;
        Integer lunghezza = null;
        Integer precisione = null;
        Integer scala = null;

        // Esegui il parsing della stringa del tipo colonna, ad esempio "varchar(255)" o "number(15,0)"
        if (tipoColonnaStr != null && !tipoColonnaStr.isEmpty()) {
            tipoColonnaStr = tipoColonnaStr.toLowerCase().trim();

            // Verifica il tipo di dato in base alla stringa
            if (tipoColonnaStr.startsWith("varchar")) {
                dataType = ColumnDataType.VARCHAR;
                lunghezza = parseLength(tipoColonnaStr);  // Esempio: varchar(255) -> lunghezza 255
            } else if (tipoColonnaStr.startsWith("number")) {
                dataType = ColumnDataType.NUMBER;
                int[] precisionAndScale = parsePrecisionAndScale(tipoColonnaStr);  // Esempio: number(15,0) -> precisione 15, scala 0
                if (precisionAndScale != null) {
                    precisione = precisionAndScale[0];
                    scala = precisionAndScale[1];
                }
            } else {
                throw new RuntimeException("Tipo colonna non esistente");
            }
            // Aggiungi altri tipi di dato a seconda delle necessità, ad esempio:
            // else if (tipoColonnaStr.startsWith("date")) {
            //     dataType = ColumnDataType.DATE;
            // }
        }

        column.setDataType(dataType);
        column.setDataLength(lunghezza);
        column.setPrecision(precisione);
        column.setScale(scala);

        if (constraint != null) {
            column.setConstraint(constraint);
        }
        return column;
    }

    private Integer parseLength(String tipoColonnaStr) {
        // Cerca il valore numerico all'interno delle parentesi per il tipo varchar
        int start = tipoColonnaStr.indexOf('(');
        int end = tipoColonnaStr.indexOf(')');
        if (start != -1 && end != -1) {
            try {
                return Integer.parseInt(tipoColonnaStr.substring(start + 1, end));
            } catch (NumberFormatException e) {
                System.out.println("Errore nel parsing della lunghezza: " + tipoColonnaStr);
            }
        }
        return null;  // Nessuna lunghezza trovata o errore di parsing
    }

    private int[] parsePrecisionAndScale(String tipoColonnaStr) {
        // Cerca i valori di precisione e scala all'interno delle parentesi per il tipo number
        int start = tipoColonnaStr.indexOf('(');
        int end = tipoColonnaStr.indexOf(')');
        if (start != -1 && end != -1) {
            try {
                String[] values = tipoColonnaStr.substring(start + 1, end).split(",");
                int precision = Integer.parseInt(values[0].trim());
                int scale = values.length > 1 ? Integer.parseInt(values[1].trim()) : 0;  // Se non specificato, la scala è 0
                return new int[]{precision, scale};
            } catch (NumberFormatException e) {
                System.out.println("Errore nel parsing di precisione e scala: " + tipoColonnaStr);
            }
        }
        return null;  // Nessun valore trovato o errore di parsing
    }


}
