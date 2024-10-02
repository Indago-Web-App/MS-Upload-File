package it.linearsystem.indago.bean.dto;

import it.linearsystem.indago.entity.FileUpload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openmetadata.client.model.Table;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DatabaseMap {

    FileUpload fileUpload;
    private Integer errori = 0;
    List<TableMap> listTableMapSorgente;
    List<TableMap> listTableMapDestinazione;

    public DatabaseMap() {
        listTableMapSorgente = new ArrayList<>();
        listTableMapDestinazione = new ArrayList<>();
    }

    public void addErrore() {
        this.errori++;
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
