package it.linearsystem.indago.service;

import org.openmetadata.client.model.Column;
import org.openmetadata.schema.type.ColumnConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Service
public class OMColumnService {

    private final Logger log = LoggerFactory.getLogger(OMColumnService.class);

    public Column updateColumn(Column column) {
        // TODO COSA DEVO VERIFICARE??
        return column;
    }

    public List<Column> createOrUpdateColumn(String colName, String colDesc, String colType, String colConstraint, List<Column> columns) {
        if (colName.contains(",")) {
            if (colType.contains(",")) {
                List<String> listColName = Arrays.asList(colName.split(",\\s*"));

                Pattern pattern = Pattern.compile("\\w+\\([^)]+\\)|\\w+"); // Gestisce tipi con parentesi e quelli semplici
                Matcher matcher = pattern.matcher(colType);

                List<String> listColType = new ArrayList<>();
                while (matcher.find()) {
                    listColType.add(matcher.group());
                }
                if (listColName.size() != listColType.size()) {
                    log.error("Numero di listColName e tipi di colonna non corrispondono!");
                    throw new RuntimeException("Le listColName sono: " + listColName.size() + " ma i tipi sono: " + listColType.size());
                }

                IntStream.range(0, listColName.size())
                        .forEach(index -> {
                            Column column = startInitColumn(listColName.get(index), colDesc, listColType.get(index), null, columns);
                            if (column != null) {
                                columns.add(column);
                            }
                        });
            } else {
                throw new RuntimeException("La colonna a più campi ma è presente solo un tipo");
            }
        } else {
            Column column = startInitColumn(colName, colDesc, colType, null, columns);
            if (column != null) {
                columns.add(column);
            }
        }
        return columns;
    }

    private Column startInitColumn(String colName, String colDesc, String colType, ColumnConstraint constraint, List<Column> columns) {
        Column col = columns.stream().filter(c -> c.getName().equals(colName)).findFirst().orElse(null);
        if (col == null) {
            if (colType == null || colType.isEmpty()) {
                throw new RuntimeException("Questa colonna Non ha il tipo e quindi non l'inserisco:" + colName + " -- type: " + colType);
            }
            return initColumn(colName, colDesc, colType, constraint);
        } else {
            // throw new RuntimeException("Questa colonna esiste già e forse devo aggiornarla? -- name:" + colName + " -- type: " + colType);
            return null;
        }
    }

    private Column initColumn(String colName, String colDesc, String colType, ColumnConstraint constraint) {
        Column column = new Column();
        column.setName(colName);
        column.setDescription(colDesc);
        column.setDisplayName(colName);

        // Data TYPE
        Column.DataTypeEnum dataType = null;
        Integer lunghezza = null;
        Integer precisione = null;
        Integer scala = null;

        // Esegui il parsing della stringa del tipo colonna, ad esempio "varchar(255)" o "number(15,0)"
        if (colType != null && !colType.isEmpty()) {
            colType = colType.toLowerCase().trim();

            // Verifica il tipo di dato in base alla stringa
            if (colType.startsWith("varchar")) {
                dataType = Column.DataTypeEnum.VARCHAR;
                try {
                    lunghezza = parseVarchar(colType);  // Esempio: varchar(255) -> lunghezza 255
                } catch (Exception e) {
                    throw new RuntimeException("Eccezione nel parsing del tipo della colonna");
                }
            } else if (colType.startsWith("char")) {
                dataType = Column.DataTypeEnum.CHAR;
                try {
                    lunghezza = parseVarchar(colType);  // Esempio: varchar(255) -> lunghezza 255
                } catch (Exception e) {
                    throw new RuntimeException("Eccezione nel parsing del tipo della colonna");
                    /*throw new RuntimeException("Tipo colonna CHAR errore lunghezza: " + colType);*/
                }
            } else if (colType.startsWith("int")) {
                dataType = Column.DataTypeEnum.INT;
                try {
                    int[] precisionAndScale = parseNumber(colType);  // Esempio: number(15,0) -> precisione 15, scala 0
                    if (precisionAndScale != null) {
                        precisione = precisionAndScale[0];
                        scala = precisionAndScale[1];
                    } else {
                        throw new RuntimeException("Eccezione nel parsing del tipo della colonna");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Eccezione nel parsing del tipo della colonna");
                }
            } else if (colType.startsWith("number")) {
                dataType = Column.DataTypeEnum.NUMBER;
                try {
                    int[] precisionAndScale = parseNumber(colType);  // Esempio: number(15,0) -> precisione 15, scala 0
                    if (precisionAndScale != null) {
                        precisione = precisionAndScale[0];
                        scala = precisionAndScale[1];
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Eccezione nel parsing del tipo della colonna");
                }
            } else if (colType.startsWith("decimal")) {
                dataType = Column.DataTypeEnum.DECIMAL;
                try {
                    int[] precisionAndScale = parseNumber(colType);  // Esempio: number(15,0) -> precisione 15, scala 0
                    if (precisionAndScale != null) {
                        precisione = precisionAndScale[0];
                        scala = precisionAndScale[1];
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Eccezione nel parsing del tipo della colonna");
                }
            } else if (colType.startsWith("float")) {
                dataType = Column.DataTypeEnum.FLOAT;
                try {
                    int[] precisionAndScale = parseNumber(colType);  // Esempio: number(15,0) -> precisione 15, scala 0
                    if (precisionAndScale != null) {
                        precisione = precisionAndScale[0];
                        scala = precisionAndScale[1];
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Eccezione nel parsing del tipo della colonna");
                }
            } else if (colType.startsWith("double")) {
                dataType = Column.DataTypeEnum.DOUBLE;
                try {
                    int[] precisionAndScale = parseNumber(colType);  // Esempio: number(15,0) -> precisione 15, scala 0
                    if (precisionAndScale != null) {
                        precisione = precisionAndScale[0];
                        scala = precisionAndScale[1];
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Eccezione nel parsing del tipo della colonna");
                }
            } else if (colType.startsWith("date")) {
                dataType = Column.DataTypeEnum.DATE;
                // TODO serve la lunghezza per una data??
            } else if (colType.startsWith("timestamp")) {
                dataType = Column.DataTypeEnum.TIMESTAMP;
                // TODO serve la lunghezza per una data??
            } else {
                throw new RuntimeException("Tipo colonna non esistente: " + colType);
            }
        }

        column.setDataType(dataType);
        column.setDataLength(lunghezza);
        column.setPrecision(precisione);
        column.setScale(scala);

        return column;
    }

    private Integer parseVarchar(String tipoColonnaStr) {
        // Cerca il valore numerico all'interno delle parentesi per il tipo varchar
        int start = tipoColonnaStr.indexOf('(');
        int end = tipoColonnaStr.indexOf(')');
        if (start != -1 && end != -1 && start < end) {
            try {
                String lengthStr = tipoColonnaStr.substring(start + 1, end).split(" ")[0].trim();
                return Integer.parseInt(lengthStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Errore nel parsing della lunghezza: " + tipoColonnaStr);
            }
        }
        throw new RuntimeException("Errore: formato della colonna non valido per la stringa: " + tipoColonnaStr);  // Nessuna lunghezza trovata
    }

    private int[] parseNumber(String tipoColonnaStr) {
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
                throw new RuntimeException("Errore nel parsing della lunghezza: " + tipoColonnaStr);
            }
        }
        throw new RuntimeException("Errore: formato della colonna number: " + tipoColonnaStr);  // Nessuna lunghezza trovata
    }


}
