package backend.academy.nodeservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "NodeService API",
                version = "v1"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Dev Server")
        }
)
public class NodeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NodeServiceApplication.class, args);
	}

}
