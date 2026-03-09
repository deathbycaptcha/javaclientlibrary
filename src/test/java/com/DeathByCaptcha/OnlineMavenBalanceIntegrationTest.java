package com.DeathByCaptcha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Assume;
import org.junit.Test;

public class OnlineMavenBalanceIntegrationTest {

    private static final String DBC_MAVEN_VERSION = "4.7.0";

    @Test
    public void testMavenCentralVersionAndBalanceCheck() throws java.lang.Exception {
        String username = getenvTrimmed("DBC_USERNAME");
        String password = getenvTrimmed("DBC_PASSWORD");
        Assume.assumeTrue("Skipping online integration test: DBC_USERNAME is missing", !username.isEmpty());
        Assume.assumeTrue("Skipping online integration test: DBC_PASSWORD is missing", !password.isEmpty());
        
        // Check Java version baseline for Maven Central compatibility tests
        String javaVersion = System.getProperty("java.version");
        int javaMajor = getMajorJavaVersion(javaVersion);
        Assume.assumeTrue(
            "Skipping online integration test: requires Java 17+ but running " + javaVersion,
            javaMajor >= 17
        );

        String expectedVersion = DBC_MAVEN_VERSION;
        Path tempProject = Files.createTempDirectory("dbc-online-maven-it-");
        Path isolatedMavenRepo = tempProject.resolve(".m2/repository");
        Path expectedArtifactJar = isolatedMavenRepo.resolve(
            "io/github/deathbycaptcha/deathbycaptcha-java-library/"
                + expectedVersion
                + "/deathbycaptcha-java-library-"
                + expectedVersion
                + ".jar"
        );

            System.out.println("[IT-DEBUG] expectedVersion=" + expectedVersion);
            System.out.println("[IT-DEBUG] DBC_USERNAME(masked)=" + maskValue(username));
            System.out.println("[IT-DEBUG] DBC_PASSWORD_SET=" + !password.isEmpty());
            System.out.println("[IT-DEBUG] tempProject=" + tempProject);
            System.out.println("[IT-DEBUG] isolatedMavenRepo=" + isolatedMavenRepo);
            System.out.println("[IT-DEBUG] expectedArtifactJar=" + expectedArtifactJar);

        try {
            writeProjectFiles(tempProject, expectedVersion);
            assertFalse("Artifact should not exist before online fetch", Files.exists(expectedArtifactJar));

            CommandResult dependencyResult = runCommand(
                    tempProject,
                    List.of(
                            "mvn",
                            "-B",
                    "-U",
                    "-Dmaven.repo.local=" + isolatedMavenRepo,
                    "org.apache.maven.plugins:maven-dependency-plugin:3.7.1:get",
                    "-Dartifact=io.github.deathbycaptcha:deathbycaptcha-java-library:" + expectedVersion,
                    "-Dtransitive=false",
                    "-DremoteRepositories=central::default::https://repo1.maven.org/maven2"
                    )
            );

            assertEquals("Dependency resolution command failed:\n" + dependencyResult.output, 0, dependencyResult.exitCode);
            assertTrue(
                    "Expected successful artifact resolution for version " + expectedVersion + ", got:\n" + dependencyResult.output,
                    dependencyResult.output.contains("BUILD SUCCESS")
            );
                System.out.println("[IT-DEBUG] dependency:get exitCode=" + dependencyResult.exitCode);
                System.out.println("[IT-DEBUG] dependency:get output contains central=" +
                    (dependencyResult.output.contains("repo1.maven.org")
                        || dependencyResult.output.contains("Downloading from central")
                        || dependencyResult.output.contains("Downloaded from central")));
                assertTrue("Expected artifact jar in isolated Maven repo after fetch", Files.exists(expectedArtifactJar));
                assertTrue(
                    "Expected dependency fetch to hit central repository, got:\n" + dependencyResult.output,
                    dependencyResult.output.contains("repo1.maven.org")
                        || dependencyResult.output.contains("Downloading from central")
                        || dependencyResult.output.contains("Downloaded from central")
                );

            CommandResult testResult = runCommand(
                    tempProject,
                    List.of("mvn", "-B", "-Dmaven.repo.local=" + isolatedMavenRepo, "test")
            );

            // Check if the test failed due to class version mismatch
            if (testResult.exitCode != 0 && testResult.output.contains("class file has wrong version")) {
                System.out.println("[IT-WARN] Maven Central library compiled with newer Java version than current runtime");
                System.out.println("[IT-WARN] Current Java: " + System.getProperty("java.version"));
                System.out.println("[IT-WARN] Consider upgrading Java or this test will be skipped");
                Assume.assumeTrue("Maven Central library requires newer Java version", false);
            }

            assertEquals("Integration Maven test project failed:\n" + testResult.output, 0, testResult.exitCode);
                System.out.println("[IT-DEBUG] maven test exitCode=" + testResult.exitCode);
                assertTrue("Expected BUILD SUCCESS in integration test output, got:\n" + testResult.output,
                    testResult.output.contains("BUILD SUCCESS"));
        } finally {
            deleteRecursively(tempProject);
        }
    }

    private static void writeProjectFiles(Path projectRoot, String expectedVersion) throws IOException {
        String pom = String.format("""
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>

                  <groupId>it.deathbycaptcha</groupId>
                  <artifactId>online-maven-balance-it</artifactId>
                  <version>1.0.0</version>

                  <properties>
                    <maven.compiler.source>17</maven.compiler.source>
                    <maven.compiler.target>17</maven.compiler.target>
                    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                                        <dbc.version>%s</dbc.version>
                  </properties>

                  <dependencies>
                    <dependency>
                      <groupId>io.github.deathbycaptcha</groupId>
                      <artifactId>deathbycaptcha-java-library</artifactId>
                      <version>${dbc.version}</version>
                    </dependency>
                    <dependency>
                      <groupId>junit</groupId>
                      <artifactId>junit</artifactId>
                      <version>4.13.2</version>
                      <scope>test</scope>
                    </dependency>
                  </dependencies>
                </project>
                """, expectedVersion);

        String testClass = """
                package it;

                import com.DeathByCaptcha.Client;
                import com.DeathByCaptcha.HttpClient;
                import org.junit.Test;

                import static org.junit.Assert.assertTrue;

                public class BalanceCheckIT {
                    @Test
                    public void balanceIsNonNegative() throws Exception {
                        String username = System.getenv("DBC_USERNAME");
                        String password = System.getenv("DBC_PASSWORD");
                        String maskedUsername = (username == null || username.isEmpty())
                                ? "<empty>"
                                : username.charAt(0) + "***";
                        System.out.println("IT_DEBUG_USER_NAME=" + System.getProperty("user.name"));
                        System.out.println("IT_DEBUG_USER_HOME=" + System.getProperty("user.home"));
                        System.out.println("IT_DEBUG_JAVA_VERSION=" + System.getProperty("java.version"));
                        System.out.println("IT_DEBUG_OS=" + System.getProperty("os.name") + " " + System.getProperty("os.version"));
                        System.out.println("IT_DEBUG_MAVEN_REPO_LOCAL=" + System.getProperty("maven.repo.local"));
                        System.out.println("IT_DEBUG_DBC_USERNAME(masked)=" + maskedUsername);
                        System.out.println("IT_DEBUG_DBC_PASSWORD_SET=" + (password != null && !password.isEmpty()));
                        Client client = new HttpClient(username, password);
                        double balance = client.getBalance();
                        System.out.println("BALANCE_OK=" + balance);
                        assertTrue("Expected balance >= 0 but got " + balance, balance >= 0.0);
                    }
                }
                """;

        Files.writeString(projectRoot.resolve("pom.xml"), pom, StandardCharsets.UTF_8);

        Path testDir = projectRoot.resolve("src/test/java/it");
        Files.createDirectories(testDir);
        Files.writeString(testDir.resolve("BalanceCheckIT.java"), testClass, StandardCharsets.UTF_8);
    }

    private static CommandResult runCommand(Path workingDir, List<String> command) throws IOException, InterruptedException {
        System.out.println("[IT-DEBUG] Running command in " + workingDir + ": " + String.join(" ", command));
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDir.toFile());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        int exitCode = process.waitFor();
        String output = String.join("\n", lines);
        System.out.println("[IT-DEBUG] Command finished with exitCode=" + exitCode + ", outputLines=" + lines.size());
        return new CommandResult(exitCode, output);
    }

    private static String getenvTrimmed(String key) {
        String value = System.getenv(key);
        return value == null ? "" : value.trim();
    }
    
    private static int getMajorJavaVersion(String version) {
        try {
            // Handle versions like "21.0.10", "1.8.0_292", "17", etc.
            if (version.startsWith("1.")) {
                // Old format: 1.8.0_292 -> 8
                return Integer.parseInt(version.substring(2, version.indexOf('.', 2)));
            } else {
                // New format: 21.0.10 -> 21
                int dotIndex = version.indexOf('.');
                if (dotIndex > 0) {
                    return Integer.parseInt(version.substring(0, dotIndex));
                }
                return Integer.parseInt(version);
            }
        } catch (RuntimeException e) {
            return 0; // Unknown version
        }
    }

    private static String maskValue(String value) {
        if (value == null || value.isEmpty()) {
            return "<empty>";
        }
        if (value.length() == 1) {
            return "*";
        }
        return value.charAt(0) + "***" + value.charAt(value.length() - 1);
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (path == null || !Files.exists(path)) {
            return;
        }
        Files.walk(path)
                .sorted((a, b) -> b.getNameCount() - a.getNameCount())
                .forEach(current -> {
                    try {
                        Files.deleteIfExists(current);
                    } catch (IOException ignored) {
                    }
                });
    }

    private static class CommandResult {
        final int exitCode;
        final String output;

        CommandResult(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }
    }
}