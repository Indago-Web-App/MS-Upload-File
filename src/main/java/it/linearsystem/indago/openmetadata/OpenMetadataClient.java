package it.linearsystem.indago.openmetadata;

import it.linearsystem.indago.bean.dto.DataAssetRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(name = "openmetadataClient", url = "http://localhost:8585/api")
public interface OpenMetadataClient {

    @PostMapping("/v1/dataAssets")
    ResponseEntity<String> createDataAsset(@RequestBody DataAssetRequest dataAssetRequest, @RequestHeader("Authorization") String authToken);

    @GetMapping("/v1/bots")
    ResponseEntity<?> getListBots(@RequestHeader("Authorization") String authToken);

    @GetMapping("/v1/services/databaseServices/name/{name}")
    ResponseEntity<?> getDatabaseServiceByName(@PathVariable("name") String name, @RequestHeader("Authorization") String authToken);

    @GetMapping("/v1/databaseSchemas/name/{name}")
    ResponseEntity<?> getDatabaseSchemasByName(@PathVariable("name") String name, @RequestHeader("Authorization") String authToken);

    @GetMapping("/v1/databases/name/{name}")
    ResponseEntity<?> getDatabaseByName(@PathVariable("name") String name, @RequestHeader("Authorization") String authToken);

}
