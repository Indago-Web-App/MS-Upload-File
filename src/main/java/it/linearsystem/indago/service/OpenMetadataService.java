package it.linearsystem.indago.service;

import it.linearsystem.indago.bean.dto.FileProcessDto;
import org.openmetadata.client.api.DatabaseServicesApi;
import org.openmetadata.client.api.DatabasesApi;
import org.openmetadata.client.gateway.OpenMetadata;
import org.openmetadata.client.model.*;
import org.openmetadata.schema.entity.data.Table;
import org.openmetadata.schema.security.client.OpenMetadataJWTClientConfig;
import org.openmetadata.schema.services.connections.database.MysqlConnection;
import org.openmetadata.schema.services.connections.metadata.AuthProvider;
import org.openmetadata.schema.services.connections.metadata.OpenMetadataConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OpenMetadataService {

    private static final Logger logger = LoggerFactory.getLogger(OpenMetadataService.class);

    @Value("${openmetadata.server.url}")
    private String urlOpenMetadata;
    @Value("${openmetadata.auth.token}")
    private String tokenOpenMetadata;

    public void processFile(FileProcessDto fileProcessed) {
        OpenMetadataConnection openMetadataConnection = createConnection();
        // Create OpenMetadata Gateway
        OpenMetadata openMetadataGateway = new OpenMetadata(openMetadataConnection);
        // PRocess SORGENTE
        /*DatabaseService databaseService = createMysqlDatabaseService(openMetadataGateway);*/
        Map<String, List<Table>> listDatabaseSorgente = fileProcessed.getDatabaseMap().getDatabaseSorgente();

        Database database = createDatabase(openMetadataGateway, null, "mydb", "My db description.");
        logger.info("Ho inserito un database");
    }

    private OpenMetadataConnection createConnection() {
        OpenMetadataConnection server;
        server = new OpenMetadataConnection();
        server.setHostPort(urlOpenMetadata);
        server.setApiVersion("v1");

        OpenMetadataJWTClientConfig openMetadataJWTClientConfig = new OpenMetadataJWTClientConfig();
        openMetadataJWTClientConfig.setJwtToken(tokenOpenMetadata);
        server.setAuthProvider(AuthProvider.OPENMETADATA);
        server.setSecurityConfig(openMetadataJWTClientConfig);
        return server;
    }

    private Database createDatabase(OpenMetadata openMetadataGateway, DatabaseService service, String dbName, String desc) {
        CreateDatabase createdatabase = new CreateDatabase();
        createdatabase.setName(dbName);
        createdatabase.setDescription(desc);
        createdatabase.setDisplayName(dbName);
        // Associa il DatabaseService al Database
        createdatabase.setService("databaseService");
        // Call API
        DatabasesApi databasesApi = openMetadataGateway.buildClient(DatabasesApi.class);
        return databasesApi.createOrUpdateDatabase(createdatabase);
    }

    // Helper per creare un riferimento all'entità
    private EntityReference buildEntityReference(String type, UUID id, String name) {
        EntityReference entityReference = new EntityReference();
        entityReference.setId(id);
        entityReference.setName(name);
        entityReference.setType(type);
        return entityReference;
    }

    private DatabaseService createMysqlDatabaseService(OpenMetadata openMetadataGateway) {
        // Create service request
        CreateDatabaseService createDatabaseService = new CreateDatabaseService();
        // Connection type definition
        DatabaseConnection databaseConnection = new DatabaseConnection();
        // Create MysqlConnection
        MysqlConnection databaseConnectionConfig = new MysqlConnection();
        databaseConnectionConfig.setScheme(MysqlConnection.MySQLScheme.MYSQL_PYMYSQL);
        databaseConnectionConfig.setHostPort("localhost:3306");
        databaseConnectionConfig.setUsername("openmetadata_user");
        /*databaseConnectionConfig.setAuthType();*/
        databaseConnectionConfig.setDatabaseSchema("openmetadata_db");
        databaseConnection.setConfig(databaseConnectionConfig);
        // Update create service request fields
        createDatabaseService.setConnection(databaseConnection);
        createDatabaseService.setName("mysql-test");
        createDatabaseService.setDescription("Mysql test connection.");
        createDatabaseService.setServiceType(CreateDatabaseService.ServiceTypeEnum.MYSQL);
        // Call API
        DatabaseServicesApi databaseServicesApi = openMetadataGateway.buildClient(DatabaseServicesApi.class);
        return databaseServicesApi.createOrUpdateDatabaseService(createDatabaseService);
    }
}
