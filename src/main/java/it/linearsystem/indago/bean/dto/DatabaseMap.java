package it.linearsystem.indago.bean.dto;

import lombok.Getter;
import lombok.Setter;
import org.openmetadata.schema.entity.data.Table;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DatabaseMap {

    Map<String, List<Table>> databaseSorgente;
    Map<String, List<Table>> databaseDestinazione;
}
