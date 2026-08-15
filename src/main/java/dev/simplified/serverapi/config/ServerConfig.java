package dev.simplified.serverapi.config;

import dev.simplified.annotations.AccessLevel;
import dev.simplified.annotations.BuildFlag;
import dev.simplified.annotations.ClassBuilder;
import dev.simplified.annotations.Getter;
import dev.simplified.annotations.Negate;
import dev.simplified.annotations.RequiredArgsConstructor;
import dev.simplified.annotations.SetterNames;
import dev.simplified.collection.Concurrent;
import dev.simplified.collection.ConcurrentList;
import dev.simplified.collection.ConcurrentMap;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.LogLevel;

/**
 * Immutable configuration class for the Spring Boot server, constructed via its generated
 * {@code Builder}.
 *
 * <p>Holds server tuning parameters, compression settings, thread pool sizing, and
 * application metadata. Each field maps to a Spring Boot property key and is emitted
 * by {@link #toProperties()} as a {@link ConcurrentMap} suitable for
 * {@link SpringApplication#setDefaultProperties}.</p>
 *
 * <p>Use {@code builder()} for full control over every setting, or {@link #optimized()}
 * for a production-tuned preset targeting a high-core-count server behind a reverse proxy.
 * Every field's declared value below is the builder's default for that setting.</p>
 */
@Getter
@ClassBuilder(
    constructorAccess = AccessLevel.PRIVATE,
    setters = @SetterNames(set = "with{}", flag = "is{}")
)
public final class ServerConfig {

    private final int port = 8080;
    @BuildFlag(nonNull = true)
    private final @NotNull String address = "0.0.0.0";
    @BuildFlag(nonNull = true)
    private final @NotNull String contextPath = "/";
    private final int maxThreads = 200;
    private final int minSpareThreads = 10;
    private final boolean virtualThreadsEnabled = false;
    private final int acceptCount = 100;
    private final int maxConnections = 8192;
    private final int connectionTimeout = 20;
    private final int keepAliveTimeout = 60;
    private final int maxKeepAliveRequests = 100;
    private final boolean compressionEnabled = false;
    @BuildFlag(nonNull = true)
    private final @NotNull MemorySize compressionMinSize = MemorySize.bytes(2048);
    @BuildFlag(nonNull = true)
    private final @NotNull ConcurrentList<String> compressionMimeTypes = Concurrent.newList(
        "text/html",
        "text/xml",
        "text/plain",
        "text/css",
        "text/javascript",
        "application/javascript",
        "application/json",
        "application/xml"
    );
    private final boolean http2Enabled = false;
    @BuildFlag(nonNull = true)
    private final @NotNull MemorySize maxRequestHeaderSize = MemorySize.kilobytes(8);
    @BuildFlag(nonNull = true)
    private final @NotNull MemorySize maxFormPostSize = MemorySize.megabytes(2);
    private final boolean multipartEnabled = true;
    @BuildFlag(nonNull = true)
    private final @NotNull MemorySize multipartMaxFileSize = MemorySize.megabytes(1);
    @BuildFlag(nonNull = true)
    private final @NotNull MemorySize multipartMaxRequestSize = MemorySize.megabytes(10);
    @BuildFlag(nonNull = true)
    private final @NotNull ShutdownMode shutdownMode = ShutdownMode.IMMEDIATE;
    private final int shutdownTimeout = 30;
    @BuildFlag(nonNull = true)
    private final @NotNull ForwardHeadersStrategy forwardHeadersStrategy = ForwardHeadersStrategy.NONE;
    @BuildFlag(nonNull = true)
    private final @NotNull String applicationName = "simplified-server";
    @BuildFlag(nonNull = true)
    private final @NotNull LogLevel rootLogLevel = LogLevel.INFO;
    @Negate("apiKeyAuthDisabled")
    private final boolean apiKeyAuthEnabled = true;
    @Negate("springdocDisabled")
    private final boolean springdocEnabled = true;
    @Negate("actuatorDisabled")
    private final boolean actuatorEnabled = false;
    @BuildFlag(nonNull = true)
    private final @NotNull ConcurrentList<String> actuatorExposedEndpoints = Concurrent.newList("health", "info");
    @BuildFlag(nonNull = true)
    private final @NotNull String actuatorBasePath = "/actuator";
    private final int managementPort = -1;
    @BuildFlag(nonNull = true)
    private final @NotNull HealthDetailsVisibility healthShowDetails = HealthDetailsVisibility.NEVER;

    /**
     * Builds a production-tuned {@link ServerConfig} targeting a high-core-count server
     * (16 threads, 96 GB RAM, 10 Gbps NIC) behind a reverse proxy serving JSON and images.
     *
     * @return a fully constructed, optimized server configuration
     */
    public static @NotNull Builder optimized() {
        return builder()
            .withMaxThreads(400)
            .withMinSpareThreads(50)
            .withVirtualThreadsEnabled(true)
            .withAcceptCount(200)
            .withActuatorEnabled(true)
            .withMaxConnections(10000)
            .withConnectionTimeout(10)
            .withCompressionEnabled(true)
            .withCompressionMinSize(MemorySize.bytes(512))
            .withCompressionMimeTypes(Concurrent.newList(
                "application/json",
                "application/javascript",
                "image/svg+xml",
                "text/html",
                "text/json",
                "text/javascript",
                "text/plain"
            ))
            .withHttp2Enabled(true)
            .withMaxRequestHeaderSize(MemorySize.kilobytes(16))
            .withMaxFormPostSize(MemorySize.megabytes(1))
            .withMultipartMaxFileSize(MemorySize.megabytes(50))
            .withMultipartMaxRequestSize(MemorySize.megabytes(75))
            .withShutdownMode(ShutdownMode.GRACEFUL)
            .withShutdownTimeout(20)
            .withForwardHeadersStrategy(ForwardHeadersStrategy.NATIVE);
    }

    /**
     * Converts this configuration to a Spring Boot property map suitable for
     * {@link SpringApplication#setDefaultProperties}.
     *
     * @return an unmodifiable map of Spring Boot property keys to their string values
     */
    public @NotNull ConcurrentMap<String, Object> toProperties() {
        ConcurrentMap<String, Object> props = Concurrent.newMap();

        props.put("server.port", this.port);
        props.put("server.address", this.address);
        props.put("server.servlet.context-path", this.contextPath);
        props.put("server.tomcat.threads.max", this.maxThreads);
        props.put("server.tomcat.threads.min-spare", this.minSpareThreads);
        props.put("spring.threads.virtual.enabled", this.virtualThreadsEnabled);
        props.put("server.tomcat.accept-count", this.acceptCount);
        props.put("server.tomcat.max-connections", this.maxConnections);
        props.put("server.tomcat.connection-timeout", this.connectionTimeout + "s");
        props.put("server.tomcat.keep-alive-timeout", this.keepAliveTimeout + "s");
        props.put("server.tomcat.max-keep-alive-requests", this.maxKeepAliveRequests);
        props.put("server.compression.enabled", this.compressionEnabled);
        props.put("server.compression.min-response-size", this.compressionMinSize.toPropertyValue());
        props.put("server.compression.mime-types", String.join(",", this.compressionMimeTypes));
        props.put("server.http2.enabled", this.http2Enabled);
        props.put("server.max-http-request-header-size", this.maxRequestHeaderSize.toPropertyValue());
        props.put("server.tomcat.max-http-form-post-size", this.maxFormPostSize.toPropertyValue());
        props.put("spring.servlet.multipart.enabled", this.multipartEnabled);
        props.put("spring.servlet.multipart.max-file-size", this.multipartMaxFileSize.toPropertyValue());
        props.put("spring.servlet.multipart.max-request-size", this.multipartMaxRequestSize.toPropertyValue());
        props.put("server.shutdown", this.shutdownMode.name().toLowerCase());
        props.put("spring.lifecycle.timeout-per-shutdown-phase", this.shutdownTimeout + "s");
        props.put("server.forward-headers-strategy", this.forwardHeadersStrategy.name());
        props.put("spring.application.name", this.applicationName);
        props.put("logging.level.root", this.rootLogLevel.name());
        props.put("spring.main.allow-bean-definition-overriding", true);
        props.put("api.key.authentication.enabled", this.apiKeyAuthEnabled);
        props.put("springdoc.api-docs.enabled", this.springdocEnabled);
        props.put("springdoc.api-docs.path", "/v3/api-docs");
        props.put("scalar.enabled", this.springdocEnabled);
        props.put("scalar.path", "/");

        if (this.actuatorEnabled) {
            props.put("management.endpoints.web.exposure.include", String.join(",", this.actuatorExposedEndpoints));
            props.put("management.endpoints.web.base-path", this.actuatorBasePath);
            props.put("management.endpoint.health.show-details", this.healthShowDetails.getValue());
            if (this.managementPort > 0)
                props.put("management.server.port", this.managementPort);
        } else {
            props.put("management.endpoints.enabled-by-default", false);
            props.put("management.endpoints.web.exposure.include", "");
        }

        return props.toUnmodifiable();
    }

    /**
     * Maps to the {@code server.shutdown} Spring Boot property.
     */
    public enum ShutdownMode {

        IMMEDIATE,
        GRACEFUL

    }

    /**
     * Maps to the {@code server.forward-headers-strategy} Spring Boot property.
     */
    public enum ForwardHeadersStrategy {

        NONE,
        NATIVE,
        FRAMEWORK

    }

    /**
     * Maps to the {@code management.endpoint.health.show-details} Spring Boot property.
     */
    @Getter
    @RequiredArgsConstructor
    public enum HealthDetailsVisibility {

        NEVER("never"),
        WHEN_AUTHORIZED("when-authorized"),
        ALWAYS("always");

        private final @NotNull String value;

    }

    /**
     * Typed size representation for Spring Boot memory-based properties.
     *
     * <p>Provides factory methods for common units and converts to the string
     * format expected by Spring Boot (e.g., {@code "8KB"}, {@code "2MB"}).</p>
     */
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class MemorySize {

        private final long value;
        private final @NotNull MemoryUnit unit;

        /**
         * Creates a size in bytes.
         *
         * @param value the number of bytes
         * @return a new memory size
         */
        public static @NotNull MemorySize bytes(long value) {
            return new MemorySize(value, MemoryUnit.BYTES);
        }

        /**
         * Creates a size in kilobytes.
         *
         * @param value the number of kilobytes
         * @return a new memory size
         */
        public static @NotNull MemorySize kilobytes(long value) {
            return new MemorySize(value, MemoryUnit.KB);
        }

        /**
         * Creates a size in megabytes.
         *
         * @param value the number of megabytes
         * @return a new memory size
         */
        public static @NotNull MemorySize megabytes(long value) {
            return new MemorySize(value, MemoryUnit.MB);
        }

        /**
         * Creates a size in gigabytes.
         *
         * @param value the number of gigabytes
         * @return a new memory size
         */
        public static @NotNull MemorySize gigabytes(long value) {
            return new MemorySize(value, MemoryUnit.GB);
        }

        /**
         * Converts this size to its Spring Boot property string representation.
         *
         * @return a string such as {@code "512"}, {@code "8KB"}, or {@code "2MB"}
         */
        public @NotNull String toPropertyValue() {
            return this.value + this.unit.getSuffix();
        }

        @Override
        public @NotNull String toString() {
            return toPropertyValue();
        }

    }

    /**
     * Unit of measurement for memory sizes.
     */
    @Getter
    @RequiredArgsConstructor
    public enum MemoryUnit {

        BYTES(""),
        KB("KB"),
        MB("MB"),
        GB("GB");

        private final @NotNull String suffix;

    }

}
