package fr.lernejo.fileinjector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Launcher {

    private static RabbitTemplate rabbitTemplate = null;

    public static void setRabbitTemplate(RabbitTemplate template) {
        rabbitTemplate = template;
    }

    public static void main(String[] args) throws IOException {
        try (AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class)) {
            if (args.length > 0) {
                ObjectMapper mapper = new ObjectMapper();
                List<GameInfo> gameInfos = Arrays.asList(mapper.readValue(Paths.get(args[0]).toFile(), GameInfo[].class));

                RabbitTemplate template = (rabbitTemplate != null) ? rabbitTemplate : springContext.getBean(RabbitTemplate.class);
                template.setMessageConverter(new Jackson2JsonMessageConverter());

                for (GameInfo gameInfo : gameInfos) {
                    template.convertAndSend("", "game_info", gameInfo, message -> {
                        message.getMessageProperties().getHeaders().put("game_id", gameInfo.id());
                        return message;
                    });
                }
            }
        }
    }
}

