package it.linearsystem.indago.openmetadata;

import org.openmetadata.schema.entity.Bot;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Component
@FeignClient(name = "openmetadataClient", url = "http://localhost:8585/api")
public interface OpenMetadataClient {
/*
    @PostMapping("/v1/dataAssets")
    ResponseEntity<String> createDataAsset(@RequestBody DataAssetRequest dataAssetRequest, @RequestHeader("Authorization") String authToken);*/

    @GetMapping("/v1/bots")
    ResponseEntity<?> getListBots(@RequestHeader("Authorization") String authToken);
}
