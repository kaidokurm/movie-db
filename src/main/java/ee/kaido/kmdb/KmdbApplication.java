package ee.kaido.kmdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ee.kaido.kmdb.bonus_futures.auth.AuthenticationRequest;
import ee.kaido.kmdb.bonus_futures.auth.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class KmdbApplication {

    public static void main(String[] args) {
        SpringApplication.run(KmdbApplication.class, args);
    }

//    //    Run this if db is first time created and want to create a admin
//    // TODO !!!REMOVE!!! for testing, create admin and manager and replace postman
//    // file tokens
//    @Bean
//    public CommandLineRunner commandLineRunner(
//            AuthenticationService service) {
//        return args -> {
//            var admin = RegisterRequest.builder()
//                    .firstname("Admin")
//                    .lastname("Admin")
//                    .email("admin@mail.com")
//                    .password("password")
//                    .role(ADMIN)
//                    .build();
//            String token = service.register(admin).getAccessToken();
//            System.out.println("Admin token: " + token);
//            updatePostmanJsonTokens(token);
//
//            var manager = RegisterRequest.builder()
//                    .firstname("Manager")
//                    .lastname("Admin")
//                    .email("manager@mail.com")
//                    .password("password")
//                    .role(MANAGER)
//                    .build();
//            System.out.println("Manager token: " + service.register(manager).getAccessToken());
//        };
//    }

    // TODO !!!REMOVE!!! for testing, login admin and replace postman
    // file tokens
    @Bean
    public CommandLineRunner commandLineRunner(
            AuthenticationService service) {
        return args -> {
            var admin = AuthenticationRequest.builder()
                    .email("admin@mail.com")
                    .password("password")
                    .build();
            String token = service.authenticate(admin).getAccessToken();
            System.out.println("Admin token: " + token);
            updatePostmanJsonTokens(token);
        };
    }

    private void updatePostmanJsonTokens(String newToken) {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File("src/main/resources/Movie Database API.postman_collection.json");

        // Update the JSON file
        try {
            // Read the JSON file into a JsonNode
            JsonNode jsonNode = objectMapper.readTree(jsonFile);

            // Iterate through all items in the JSON
            for (JsonNode item : jsonNode.get("item")) {
                for (JsonNode subItem : item.get("item")) {
                    if (subItem.has("request") && subItem.get("request").has("auth")) {
                        JsonNode authNode = subItem.get("request").get("auth");
                        if (authNode.has("bearer") && authNode.get("bearer").has("token")) {
                            JsonNode token = authNode.get("bearer");
                            ((ObjectNode) token).put("token", newToken);
                        }
                    }
                }
            }

            // Write the modified JsonNode back to the JSON file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, jsonNode);
            System.out.println("Token updated successfully in 'Movie Database API.postman_collection.json'.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while changing the token: " + e.getMessage());
        }
    }
}
