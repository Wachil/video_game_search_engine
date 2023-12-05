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
                processGameInfo(args[0], springContext);
            }
        }
    }

    private static void processGameInfo(String filePath, AbstractApplicationContext springContext) throws IOException {
        List<GameInfo> gameInfos = readGameInfos(filePath);
        RabbitTemplate template = getRabbitTemplate(springContext);
        sendGameInfos(gameInfos, template);
    }

    private static List<GameInfo> readGameInfos(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.asList(mapper.readValue(Paths.get(filePath).toFile(), GameInfo[].class));
    }

    private static RabbitTemplate getRabbitTemplate(AbstractApplicationContext springContext) {
        return (rabbitTemplate != null) ? rabbitTemplate : springContext.getBean(RabbitTemplate.class);
    }

    private static void sendGameInfos(List<GameInfo> gameInfos, RabbitTemplate template) {
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        for (GameInfo gameInfo : gameInfos) {
            sendMessage(gameInfo, template);
        }
    }

    private static void sendMessage(GameInfo gameInfo, RabbitTemplate template) {
        template.convertAndSend("", "game_info", gameInfo, message -> {
            message.getMessageProperties().getHeaders().put("game_id", gameInfo.id());
            return message;
        });
    }
}

