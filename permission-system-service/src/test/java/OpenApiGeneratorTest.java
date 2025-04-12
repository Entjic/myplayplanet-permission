import net.myplayplanet.permission.service.PermissionSystemApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = PermissionSystemApplication.class) // Starts a minimal server
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class OpenApiGeneratorTest {
    // TODO: 17.02.2025 user random port and inject and runtime
    private static final String OUTPUT_PATH = "target/generated/openapi.json";

    private final RestTemplate restTemplate = new RestTemplate();

    @LocalServerPort
    private int randomServerPort;

    @Test
    void generateOpenApiSpec() throws IOException {
        // Fetch OpenAPI JSON
        String openApiJson = restTemplate.getForObject("http://localhost:" + randomServerPort + "/v3/api-docs", String.class);

        // Write to file
        File outputFile = new File(OUTPUT_PATH);
        outputFile.getParentFile().mkdirs(); // Ensure directories exist
        try (FileWriter writer = new FileWriter(outputFile, StandardCharsets.UTF_8)) {
            assert openApiJson != null;
            FileCopyUtils.copy(openApiJson, writer);
        }

        System.out.println("OpenAPI spec saved to: " + outputFile.getAbsolutePath());
    }
}

