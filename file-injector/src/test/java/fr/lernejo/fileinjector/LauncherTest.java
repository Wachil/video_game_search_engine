package fr.lernejo.fileinjector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class LauncherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Launcher.setRabbitTemplate(rabbitTemplate);
    }

    @Test
    void main_terminates_before_5_sec() {
        assertTimeoutPreemptively(
            Duration.ofSeconds(5),
            () -> Launcher.main(new String[]{}));
    }

    @Test
    void gamesMessagesSuccessWithFileExist() throws IOException {
        File gameFile = new File("src/test/resources");
        String jsonGameFilePath = gameFile.getAbsolutePath() + "/games.json";
        Launcher.main(new String[]{jsonGameFilePath});
    }

    @Test
    void gamesMessagesThrowIOExceptionWithFileNotExist() {
        File gameFile = new File("src/test/resources");
        String jsonGameFilePath = gameFile.getAbsolutePath() + "/game.json";
        assertThrows(IOException.class, () -> Launcher.main(new String[]{jsonGameFilePath}));
    }
}
