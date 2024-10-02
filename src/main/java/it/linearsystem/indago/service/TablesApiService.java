package it.linearsystem.indago.service;

import it.linearsystem.indago.entity.FileUpload;
import org.openmetadata.client.api.TablesApi;
import org.openmetadata.client.model.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TablesApiService {

    private TablesApi tablesApi;

    @Autowired
    private OpenMetadataUtilityService openMetadataUtilityService;
    @Autowired
    private LogErroreOpenmetadataService logErroreOpenmetadataService;

    public Table getTableByFQN(String fqn, FileUpload fileUpload) {
        try {
            return getTablesApi().getTableByFQN(fqn, null);
        } catch (Exception e) {
            if (fileUpload != null) {
                logErroreOpenmetadataService.insertLogErroreOpenmetadata(fileUpload, "Errore recupero TABLE: " + fqn);
            }
            return null;
        }
    }

    private TablesApi getTablesApi() {
        if (this.tablesApi == null) {
            this.tablesApi = openMetadataUtilityService.getOpenMetadata().buildClient(TablesApi.class);
        }
        return this.tablesApi;
    }
}
