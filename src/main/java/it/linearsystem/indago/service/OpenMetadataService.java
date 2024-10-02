package it.linearsystem.indago.service;

import it.linearsystem.indago.bean.dto.DatabaseMap;
import it.linearsystem.indago.bean.dto.FileProcessDto;
import it.linearsystem.indago.openmetadata.OpenMetadataClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openmetadata.client.api.DatabaseSchemasApi;
import org.openmetadata.client.api.DatabaseServicesApi;
import org.openmetadata.client.api.DatabasesApi;
import org.openmetadata.client.api.TablesApi;
import org.openmetadata.client.gateway.OpenMetadata;
import org.openmetadata.client.model.*;
import org.openmetadata.schema.services.connections.database.HiveConnection;
import org.openmetadata.schema.services.connections.database.OracleConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OpenMetadataService {

    private static final Logger logger = LoggerFactory.getLogger(OpenMetadataService.class);

    @Autowired
    private OpenMetadataUtilityService openMetadataUtilityService;
    @Autowired
    private OpenMetadataClient openMetadataClient;
    @Autowired
    private OMLineageService omLineageService;


    public void processFile(FileProcessDto fileProcessed) {
        // Create OpenMetadata Gateway
        OpenMetadata openMetadataGateway = openMetadataUtilityService.getOpenMetadata();

        // initAmbiente(openMetadataGateway);

        // SORGENTE
        processTables(fileProcessed.getDatabaseMap().getListTableMapSorgente(), "sorgente", openMetadataGateway, fileProcessed);
        // DESTINAZIONE
        processTables(fileProcessed.getDatabaseMap().getListTableMapDestinazione(), "destinazione", openMetadataGateway, fileProcessed);

        // CREAZIONE LINEAGE
        logger.info("Inizio - PreProcess per lINEAGE");
        /*
            CREO UN'ARRAY IN CUI HO:
                - ID tabella sorgente
                - ID tabella destinazione
                - Mappa delle colonne destinazione con la lista delle colonne sorgenti
        */
        List<OMLineageService.LineageToProcess> lineageMap = omLineageService.prepareLineageFromList(fileProcessed.getDatabaseMap().getListTableMapSorgente(), fileProcessed.getDatabaseMap().getFileUpload());

        logger.info("Inizio - CREAZIONE lINEAGE - da processare: {}", lineageMap.size());
        AtomicInteger i = new AtomicInteger(1);
        lineageMap.forEach(lineage -> {
            omLineageService.createLineageOMD(openMetadataGateway, lineage);
            if (i.get() == 1 || (i.get() % 30 == 0) || i.get() == lineageMap.size()) {
                logger.info("Ho creato lineage: {}", i.get());
            }
            i.getAndIncrement();
        });

        logger.info("FINE - Ho inserito il database");
    }

    public void processTables(List<DatabaseMap.TableMap> listTableMap, String tipoTabella, OpenMetadata openMetadataGateway, FileProcessDto fileProcessed) {
        logger.info("Tabelle {} da elaborare: {}", tipoTabella, listTableMap.size());
        AtomicInteger recordsProcessed = new AtomicInteger(0);
        int totalRecords = listTableMap.size();

        int numberOfThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<?>> futures = new ArrayList<>();

        for (DatabaseMap.TableMap tableMap : listTableMap) {
            futures.add(executorService.submit(() -> {
                try {
                    DatabaseService databaseService = createDatabaseService(openMetadataGateway, tableMap.getTecnologia());
                    Database database = createDatabase(openMetadataGateway, databaseService, tableMap.getTecnologia(), "");
                    DatabaseSchema databaseSchema = createDatabaseSchema(openMetadataGateway, database, tableMap.getSchema(), "");
                    assert databaseSchema != null;
                    createTable(openMetadataGateway, databaseSchema, tableMap.getTabella());
                } catch (Exception e) {
                    logger.error("Errore durante l'elaborazione della tableMap: {}", tableMap, e);
                } finally {
                    // Incrementa il contatore dopo aver completato l'elaborazione
                    int processedCount = recordsProcessed.incrementAndGet();
                    // Registra il numero di record elaborati
                    if (processedCount == 1 || (processedCount % 30 == 0) || processedCount == totalRecords) {
                        logger.info("Ho elaborato {} su {} tableMap {}", processedCount, totalRecords, tipoTabella);
                    }
                }
            }));
        }

        // Attendi il completamento di tutti i task
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void createTable(@NotNull OpenMetadata openMetadataGateway, @NotNull DatabaseSchema databaseSchema, @NotNull Table table) {
        TablesApi tablesApi = openMetadataGateway.buildClient(TablesApi.class);

        CreateTable createTable = new CreateTable();
        createTable.setName(table.getName());
        createTable.setDescription(table.getDescription());
        createTable.columns(table.getColumns());
        createTable.setTableType(CreateTable.TableTypeEnum.REGULAR);
        createTable.setDatabaseSchema(databaseSchema.getFullyQualifiedName());

        Table tableNew = tablesApi.createOrUpdateTable(createTable);
        tablesApi.getTableByID(tableNew.getId(), "*", "all");
    }

    @Nullable
    private DatabaseSchema createDatabaseSchema(@NotNull OpenMetadata openMetadataGateway, Database database, String name, String description) {
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

    private Database createDatabase(OpenMetadata openMetadataGateway, DatabaseService service, @NotNull String dbName, String desc) {
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

    private DatabaseService createDatabaseService(@NotNull OpenMetadata openMetadataGateway, @NotNull String tecnologia) {
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

    private void initAmbiente(@NotNull OpenMetadata openMetadataGateway) {
        /*TablesApi tablesApi = openMetadataGateway.buildClient(TablesApi.class);
        DatabaseSchemasApi databaseSchemasApi = openMetadataGateway.buildClient(DatabaseSchemasApi.class);
        DatabasesApi databasesApi = openMetadataGateway.buildClient(DatabasesApi.class);*/

        DatabaseServicesApi databaseServicesApi = openMetadataGateway.buildClient(DatabaseServicesApi.class);

        DatabaseServiceList databaseServiceList = databaseServicesApi.listDatabaseServices(null);
        databaseServiceList.getData().forEach(dbs -> {
            DatabaseServicesApi.DeleteDatabaseServiceQueryParams deleteDatabaseServiceQueryParams = new DatabaseServicesApi.DeleteDatabaseServiceQueryParams();
            deleteDatabaseServiceQueryParams.recursive(true);
            deleteDatabaseServiceQueryParams.hardDelete(true);
            databaseServicesApi.deleteDatabaseService(dbs.getId(), deleteDatabaseServiceQueryParams);
        });
        logger.info("Ho terminato l'inizializzazione");
    }

}
