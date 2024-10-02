package it.linearsystem.indago.service;

import org.jetbrains.annotations.NotNull;
import org.openmetadata.client.gateway.OpenMetadata;
import org.openmetadata.schema.security.client.OpenMetadataJWTClientConfig;
import org.openmetadata.schema.services.connections.metadata.AuthProvider;
import org.openmetadata.schema.services.connections.metadata.OpenMetadataConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenMetadataUtilityService {

    @Value("${openmetadata.server.url}")
    private String urlOpenMetadata;
    @Value("${openmetadata.auth.token}")
    private String authToken;
    private OpenMetadata openMetadataGateway;

    public OpenMetadata getOpenMetadata() {
        if (this.openMetadataGateway == null) {
            // Create OpenMetadata Gateway
            this.openMetadataGateway = new OpenMetadata(createConnection());
        }
        return this.openMetadataGateway;
    }

    @NotNull
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
