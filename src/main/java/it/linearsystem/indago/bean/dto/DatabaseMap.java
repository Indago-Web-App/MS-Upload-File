package it.linearsystem.indago.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openmetadata.client.model.Table;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DatabaseMap {

    List<TableMap> tabellaSorgente;
    List<TableMap> tabellaDestinazione;

    public DatabaseMap() {
        tabellaSorgente = new ArrayList<>();
        tabellaDestinazione = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class TableMap {
        String tecnologia;
        String schema;
        Table tabella;
        List<Lineage> lineage;

        public String getFQNTable() {
            return tecnologia + "_SERVICE" + "." + tecnologia + "." + schema + "." + tabella.getName();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Lineage {
        String tecnologia;
        String schema;
        String nameTable;
        String nameColumn;
        String nameColumnSorgente;

        public String getFQNTable() {
            return tecnologia + "_SERVICE" + "." + tecnologia + "." + schema + "." + nameTable;
        }
        public String getFQNColumn() {
            return tecnologia + "_SERVICE" + "." + tecnologia + "." + schema + "." + nameTable + "." + nameColumn;
        }
    }

}
