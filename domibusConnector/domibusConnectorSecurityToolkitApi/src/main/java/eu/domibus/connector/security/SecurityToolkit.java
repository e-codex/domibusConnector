package eu.domibus.connector.security;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

public interface SecurityToolkit {

    public BuildContainerResponse createContainer(DocumentFactory df, BuildContainerRequest request);

    @Getter
    @AllArgsConstructor
    @Builder(toBuilder = true)
    class BuildContainerRequest {
        @NonNull
        private final DocumentFactory.SecurityDoc businessDoc;
        @NonNull
        private final List<DocumentFactory.SecurityDoc> attachments = new ArrayList<>();
        private final TrustHandlerParameters trustHandlerParameters;
    }

    interface TrustHandlerParameters {
        String getName(); //name of trust handler
    }

    class BuildContainerResponse {
        DocumentFactory.SecurityDoc asicContainer;
        DocumentFactory.SecurityDoc tokenXml;
    }


    public interface DocumentFactory {
        SecurityDoc writeDocument(java.io.OutputStream os, WriteDocumentRequest w);

        java.io.InputStream readDocument(SecurityDoc securityDoc);

        class WriteDocumentRequest {
            @NonNull
            private final String name = "";
            private String mimetype = "";
            private Long size = -1l;
            private Digest digest;
        }

        @Getter
        @AllArgsConstructor
        @Builder(toBuilder = true)
        class SecurityDoc {
            @NonNull
            private final String id;
            private String name = "";
            private String mimetype = "";
            private Long size = -1l;
            private Digest digest;
        }

    }


    @Getter
    @AllArgsConstructor
    @Builder(toBuilder = true)
    class Digest {
        @NonNull
        private final String digestValue; //as hex
        @NonNull
        private final String digestAlgorithm;
    }

}
