package it.linearsystem.indago.service;

import it.linearsystem.indago.bean.dto.DatabaseMap;
import it.linearsystem.indago.bean.dto.FileProcessDto;
import it.linearsystem.indago.openmetadata.OpenMetadataClient;
import org.openmetadata.client.api.*;
import org.openmetadata.client.gateway.OpenMetadata;
import org.openmetadata.client.model.*;
import org.openmetadata.schema.security.client.OpenMetadataJWTClientConfig;
import org.openmetadata.schema.services.connections.database.HiveConnection;
import org.openmetadata.schema.services.connections.database.OracleConnection;
import org.openmetadata.schema.services.connections.metadata.AuthProvider;
import org.openmetadata.schema.services.connections.metadata.OpenMetadataConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OpenMetadataService {

    private static final Logger logger = LoggerFactory.getLogger(OpenMetadataService.class);

    @Value("${openmetadata.server.url}")
    private String urlOpenMetadata;
    @Value("${openmetadata.auth.token}")
    private String authToken;

    @Autowired
    private OpenMetadataClient openMetadataClient;
    @Autowired
    private OMLineageService omLineageService;

    public void processFile(FileProcessDto fileProcessed) {
        OpenMetadataConnection openMetadataConnection = createConnection();
        // Create OpenMetadata Gateway
        OpenMetadata openMetadataGateway = new OpenMetadata(openMetadataConnection);

        /*initAmbiente(openMetadataGateway);*/

        // SORGENTE
        logger.info("Tabelle sorgenti da elaborare: " + fileProcessed.getDatabaseMap().getTabellaSorgente().size());
        AtomicInteger i = new AtomicInteger(1);
        AtomicInteger finalI = i;
        fileProcessed.getDatabaseMap().getTabellaSorgente().forEach(tabella -> {
            Table table = commonProcess(openMetadataGateway, tabella);

            if (finalI.get() == 1 || (finalI.get() % 30 == 0) || finalI.get() == fileProcessed.getDatabaseMap().getTabellaSorgente().size()) {
                logger.info("Ho inserito la tabella sorgente: " + finalI.get());
            }
            finalI.getAndIncrement();
        });

        // DESTINAZIONE
        logger.info("Tabelle destinazione da elaborare: " + fileProcessed.getDatabaseMap().getTabellaDestinazione().size());
        i = new AtomicInteger(1);
        AtomicInteger finalI1 = i;
        fileProcessed.getDatabaseMap().getTabellaDestinazione().forEach(tabella -> {
            Table table = commonProcess(openMetadataGateway, tabella);

            if (finalI1.get() == 1 || (finalI1.get() % 30 == 0) || finalI1.get() == fileProcessed.getDatabaseMap().getTabellaDestinazione().size()) {
                logger.info("Ho inserito la tabella Destinazione: " + finalI1.get());
            }
            finalI1.getAndIncrement();
        });

        // CREAZIONE LINEAGE
        logger.info("Inizio - PreProcess per lINEAGE");
        List<OMLineageService.LineageToProcess> lineageMap = new ArrayList<>();
        fileProcessed.getDatabaseMap().getTabellaSorgente().forEach(tabella -> {
            omLineageService.prepareLineageFromTabSorgente(openMetadataGateway, tabella, lineageMap);
        });
        logger.info("Inizio - CREAZIONE lINEAGE - da processare: " + lineageMap.size());
        logger.info("Inizio - CREAZIONE lINEAGE");
        i = new AtomicInteger(1);
        AtomicInteger finalI2 = i;
        lineageMap.forEach(lineage -> {
            createLineageOMD(openMetadataGateway, lineage);
            if (finalI2.get() == 1 || (finalI2.get() % 30 == 0) || finalI2.get() == lineageMap.size()) {
                logger.info("Ho creato lineage: " + finalI2.get());
            }
            finalI2.getAndIncrement();
        });

        logger.info("FINE - Ho inserito il database");
    }

    private void createLineageOMD(OpenMetadata openMetadataGateway, OMLineageService.LineageToProcess lineageProcess) {
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

        for (Map.Entry<String, List<String>> entry : lineageProcess.getLineageEntity().entrySet()) {
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

    private static Table createTable(OpenMetadata openMetadataGateway, DatabaseSchema databaseSchema, Table table) {
        TablesApi tablesApi = openMetadataGateway.buildClient(TablesApi.class);

        CreateTable createTable = new CreateTable();
        createTable.setName(table.getName());
        createTable.setDescription(table.getDescription());
        createTable.columns(table.getColumns());
        createTable.setTableType(CreateTable.TableTypeEnum.REGULAR);
        createTable.setDatabaseSchema(databaseSchema.getFullyQualifiedName());

        Table tableNew = tablesApi.createOrUpdateTable(createTable);
        return tablesApi.getTableByID(tableNew.getId(), "*", "all");
    }

    private DatabaseSchema createDatabaseSchema(OpenMetadata openMetadataGateway, Database database, String name, String description) {
        DatabaseSchemasApi databaseSchemasApi = openMetadataGateway.buildClient(DatabaseSchemasApi.class);
        /*try {
            ResponseEntity<?> dbService = openMetadataClient.getDatabaseSchemasByName(database.getFullyQualifiedName() + "." + name, "Bearer " + authToken);
            if (dbService.getStatusCode().value() == 200) {
                Map<String, String> mapBody = (Map<String, String>) dbService.getBody();
                return databaseSchemasApi.getDBSchemaByID(UUID.fromString(mapBody.get("id")), null, null);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }*/
        try {
            CreateDatabaseSchema createschema = new CreateDatabaseSchema();
            createschema.setName(name);
            createschema.setDescription(description);
            createschema.setDisplayName(name);
            createschema.setDatabase(database.getFullyQualifiedName());
            return databaseSchemasApi.createOrUpdateDBSchema(createschema);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Database createDatabase(OpenMetadata openMetadataGateway, DatabaseService service, String dbName, String desc) {
        if (dbName.equals("MODELLO UNICO")) {
            logger.info("STO creando db MODELLO UNICO");
        }
        DatabasesApi databasesApi = openMetadataGateway.buildClient(DatabasesApi.class);
        /*ResponseEntity<?> dbService = openMetadataClient.getDatabaseByName(service.getFullyQualifiedName() + "." + dbName, "Bearer " + authToken);
        try {
            if (dbService.getStatusCode().value() == 200) {
                Map<String, String> mapBody = (Map<String, String>) dbService.getBody();
                return databasesApi.getDatabaseByID(UUID.fromString(mapBody.get("id")), null, null);
            }
        } catch (Exception e) {
            logger.info("DB - non trovato");
        }*/
        CreateDatabase createdatabase = new CreateDatabase();
        createdatabase.setName(dbName);
        createdatabase.setDescription(desc);
        createdatabase.setDisplayName(dbName);

        // Associa il DatabaseService al Database usando un EntityReference
        createdatabase.setService(service.getFullyQualifiedName());

        // Call API
        return databasesApi.createOrUpdateDatabase(createdatabase);
    }

    private DatabaseService createDatabaseService(OpenMetadata openMetadataGateway, String tecnologia) {
        // Call API
        DatabaseServicesApi databaseServicesApi = openMetadataGateway.buildClient(DatabaseServicesApi.class);

        DatabaseConnection databaseConnection = new DatabaseConnection();

        switch (tecnologia) {
            case "ORACLE":
                OracleConnection oracleConnection = new OracleConnection();
                oracleConnection.setScheme(OracleConnection.OracleScheme.ORACLE_CX_ORACLE);
                oracleConnection.setHostPort("localhost:1521");
                oracleConnection.setUsername("openmetadata_user");
                oracleConnection.setPassword("openmetadata_pass");
                databaseConnection.setConfig(oracleConnection);

                CreateDatabaseService crDbServiceOracle = new CreateDatabaseService();
                crDbServiceOracle.setServiceType(CreateDatabaseService.ServiceTypeEnum.ORACLE);
                crDbServiceOracle.setConnection(databaseConnection);
                crDbServiceOracle.setName(tecnologia + "_SERVICE");
                crDbServiceOracle.setDescription("");
                crDbServiceOracle.setConnection(databaseConnection);
                return databaseServicesApi.createOrUpdateDatabaseService(crDbServiceOracle);
            case "HIVE":
                HiveConnection hiveConnection = new HiveConnection();
                hiveConnection.setScheme(HiveConnection.HiveScheme.HIVE);
                hiveConnection.setHostPort("localhost:10000");
                hiveConnection.setUsername("openmetadata_user");
                hiveConnection.setPassword("openmetadata_pass");
                databaseConnection.setConfig(hiveConnection);

                CreateDatabaseService crDbServiceHive = new CreateDatabaseService();
                crDbServiceHive.setServiceType(CreateDatabaseService.ServiceTypeEnum.HIVE);
                crDbServiceHive.setConnection(databaseConnection);
                crDbServiceHive.setName(tecnologia + "_SERVICE");
                crDbServiceHive.setDescription("");
                crDbServiceHive.setConnection(databaseConnection);
                return databaseServicesApi.createOrUpdateDatabaseService(crDbServiceHive);
            default:
                // code block
                throw new RuntimeException("Unknown tecnologia: " + tecnologia);
        }
    }

    private Table commonProcess(OpenMetadata openMetadataGateway, DatabaseMap.TableMap tableMap) {
        // RICERCA SERVICE DATABASE
        DatabaseService databaseService = createDatabaseService(openMetadataGateway, tableMap.getTecnologia());
        // RICERCA DATABASE
        Database database = createDatabase(openMetadataGateway, databaseService, tableMap.getTecnologia(), "");
        // CREA DATABASE SCHEMA
        DatabaseSchema databaseSchema = createDatabaseSchema(openMetadataGateway, database, tableMap.getSchema(), "");
        // INSERISCI TABELLA
        assert databaseSchema != null;
        return createTable(openMetadataGateway, databaseSchema, tableMap.getTabella());
    }

    private void initAmbiente(OpenMetadata openMetadataGateway) {
        TablesApi tablesApi = openMetadataGateway.buildClient(TablesApi.class);
        DatabaseSchemasApi databaseSchemasApi = openMetadataGateway.buildClient(DatabaseSchemasApi.class);
        DatabasesApi databasesApi = openMetadataGateway.buildClient(DatabasesApi.class);
        DatabaseServicesApi databaseServicesApi = openMetadataGateway.buildClient(DatabaseServicesApi.class);

        DatabaseServiceList databaseServiceList = databaseServicesApi.listDatabaseServices(null);
        databaseServiceList.getData().forEach(dbs -> {
            DatabasesApi.ListDatabasesQueryParams listDatabasesQueryParams = new DatabasesApi.ListDatabasesQueryParams();
            listDatabasesQueryParams.put("service", dbs.getFullyQualifiedName());
            DatabaseList databaseList = databasesApi.listDatabases(listDatabasesQueryParams);
            logger.info("Ho Recuperato la lista degli schema appartenenti al Database: " + dbs.getFullyQualifiedName());

            databaseList.getData().forEach(db -> {
                DatabaseSchemasApi.ListDBSchemasQueryParams listDBSchemasQueryParams = new DatabaseSchemasApi.ListDBSchemasQueryParams();
                listDBSchemasQueryParams.put("database", db.getFullyQualifiedName());
                DatabaseSchemaList databaseSchemaList = databaseSchemasApi.listDBSchemas(listDBSchemasQueryParams);

                databaseSchemaList.getData().forEach(dbSchema -> {
                    TablesApi.ListTablesQueryParams listTablesQueryParams = new TablesApi.ListTablesQueryParams();
                    listTablesQueryParams.put("databaseSchema", dbSchema.getFullyQualifiedName());
                    TableList tableList = tablesApi.listTables(listTablesQueryParams);
                    tableList.getData().forEach(table -> {
                        tablesApi.deleteTable(table.getId(), null);
                    });
                    databaseSchemasApi.deleteDBSchema(dbSchema.getId(), null);
                });
                logger.info("Ho Recuperato la lista degli schema appartenenti al Database: " + db.getFullyQualifiedName());
                databasesApi.deleteDatabase(db.getId(), null);
            });
            databaseServicesApi.deleteDatabaseService(dbs.getId(), null);
        });
        logger.info("Ho terminato l'inizializzazione");
    }

    private OpenMetadataConnection createConnection() {
        OpenMetadataConnection server;
        server = new OpenMetadataConnection();
        server.setHostPort(urlOpenMetadata);
        server.setApiVersion("v1");

        OpenMetadataJWTClientConfig openMetadataJWTClientConfig = new OpenMetadataJWTClientConfig();
        openMetadataJWTClientConfig.setJwtToken(authToken);
        server.setAuthProvider(AuthProvider.OPENMETADATA);
        server.setSecurityConfig(openMetadataJWTClientConfig);
        return server;
    }
}
