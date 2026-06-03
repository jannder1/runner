package br.ufg.inf.runner;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

record SignatureResult(
    UUID id,
    String documentHash,
    String signatureValue,
    OffsetDateTime timestamp,
    String certificateSubject,
    boolean isValid
) {}

record SignatureRequest(
    String documentContent,
    String practitionerId,
    String systemOid
) {
    public void validate() {
        Objects.requireNonNull(documentContent, "Document content is required");
        if (practitionerId == null || !practitionerId.matches("^[a-z0-9\\-\\.]{1,64}$")) {
            throw new IllegalArgumentException("Invalid Practitioner ID (FHIR standard)");
        }
        if (systemOid == null || !systemOid.startsWith("urn:oid:")) {
            throw new IllegalArgumentException("Invalid System OID format");
        }
    }
}

class SignatureService {
    public SignatureResult createSignature(SignatureRequest request) {
        request.validate();
        String simulatedHash = Integer.toHexString(request.documentContent().hashCode());
        
        return new SignatureResult(
            UUID.randomUUID(),
            simulatedHash,
            "RUNNER_SIM_SIG_" + UUID.randomUUID(),
            OffsetDateTime.now(),
            "CN=Runner Simulator, O=UFG, C=BR",
            true
        );
    }
}

public class Main {
    private static final SignatureService service = new SignatureService();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> shutdownTask;
    private static HttpServer server;
    private static long inactivityTimeoutMinutes = 15;

    public static void main(String[] args) {
        boolean localMode = false;
        int port = 8080;

        for (int i = 0; i < args.length; i++) {
            if ("--local".equalsIgnoreCase(args[i])) {
                localMode = true;
            } else if (args[i].startsWith("--port=")) {
                try {
                    port = Integer.parseInt(args[i].substring(7));
                } catch (NumberFormatException e) {
                    System.err.println("{\"log\":\"Invalid port specified, using default 8080\"}");
                }
            } else if (args[i].startsWith("--timeout=")) {
                try {
                    inactivityTimeoutMinutes = Long.parseLong(args[i].substring(10));
                } catch (NumberFormatException e) {
                    System.err.println("{\"log\":\"Invalid timeout specified, using default 15m\"}");
                }
            }
        }

        if (localMode) {
            handleColdStartCli(args);
        } else {
            startServerMode(port);
        }
    }

    private static void startServerMode(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            
            server.createContext("/health", exchange -> {
                resetInactivityTimer();
                byte[] response = "{\"status\":\"UP\"}".getBytes();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
                exchange.close();
            });

            server.createContext("/sign", exchange -> {
                resetInactivityTimer();
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    try {
                        String body = new String(exchange.getRequestBody().readAllBytes());
                        SignatureRequest request = new SignatureRequest(body, "p1", "urn:oid:2.16.840.1");
                        SignatureResult result = service.createSignature(request);
                        
                        String response = result.toString();
                        exchange.sendResponseHeaders(200, response.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } catch (IllegalArgumentException e) {
                        byte[] err = ("{\"error\":\"User Error\",\"details\":\"" + e.getMessage() + "\"}").getBytes();
                        exchange.sendResponseHeaders(400, err.length);
                        exchange.getResponseBody().write(err);
                    } catch (Exception e) {
                        byte[] err = ("{\"error\":\"System Error\",\"details\":\"" + e.getMessage() + "\"}").getBytes();
                        exchange.sendResponseHeaders(500, err.length);
                        exchange.getResponseBody().write(err);
                    }
                }
                exchange.close();
            });

            server.createContext("/shutdown", exchange -> {
                byte[] response = "{\"message\":\"Shutting down controlled...\"}".getBytes();
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
                shutdownGracefully();
            });

            resetInactivityTimer();
            System.err.println("{\"log\":\"Runner Server started step successfully on port " + port + "\"}");
            server.start();

        } catch (IOException e) {
            System.err.println("{\"log\":\"Failed to bind server to port " + port + ". Error: " + e.getMessage() + "\"}");
            System.exit(48); 
        }
    }

    private static synchronized void resetInactivityTimer() {
        if (shutdownTask != null) {
            shutdownTask.cancel(false);
        }
        shutdownTask = scheduler.schedule(Main::shutdownGracefully, inactivityTimeoutMinutes, TimeUnit.MINUTES);
    }

    private static void shutdownGracefully() {
        System.err.println("{\"log\":\"Auto-shutdown triggered due to inactivity window.\"}");
        if (server != null) {
            server.stop(2);
        }
        scheduler.shutdown();
        System.exit(0);
    }

    private static void handleColdStartCli(String[] args) {
        if (args.length < 2) {
            System.err.println("Error: Missing document content argument for local execution.");
            System.exit(64);
        }
        try {
            SignatureRequest request = new SignatureRequest(args[1], "cli-user", "urn:oid:1.2.3");
            SignatureResult result = service.createSignature(request);
            System.out.println(result.signatureValue());
            System.exit(0);
        } catch (IllegalArgumentException e) {
            System.err.println("User Input Error: " + e.getMessage());
            System.exit(65);
        } catch (Exception e) {
            System.err.println("System Execution Error: " + e.getMessage());
            System.exit(70);
        }
    }
}