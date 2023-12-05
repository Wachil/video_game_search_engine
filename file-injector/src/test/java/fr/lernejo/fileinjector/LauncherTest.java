package fr.lernejo.fileinjector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import org.mockito.ArgumentCaptor;

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
    void gamesMessagesThrowIOExceptionWithFileNotExist() {
        File gameFile = new File("src/test/resources");
        String jsonGameFilePath = gameFile.getAbsolutePath() + "/game.json";
        assertThrows(IOException.class, () -> Launcher.main(new String[]{jsonGameFilePath}));
    }

    @Test
    void exceptionThrownWhenFileDoesNotExist() {
        String nonExistentFilePath = "src/test/resources/nonexistent.json";
        assertThrows(IOException.class, () -> Launcher.main(new String[]{nonExistentFilePath}));
    }

    @Test
    void gamesMessagesWithMalformedFile() {
        File gameFile = new File("src/test/resources");
        String jsonGameFilePath = gameFile.getAbsolutePath() + "/malformed.json";
        assertThrows(IOException.class, () -> Launcher.main(new String[]{jsonGameFilePath}));
    }

    @Test
    void exceptionThrownWhenFileNoneExist() {
        String nonExistentFilePath = "src/test/resources/nonexistent.json";
        assertThrows(IOException.class, () -> Launcher.main(new String[]{nonExistentFilePath}));
    }

    @Test
    void gamesMessagesSuccessWithFileExist() throws IOException {
        File gameFile = new File("src/test/resources");
        String jsonGameFilePath = gameFile.getAbsolutePath() + "/games.json";
        Launcher.main(new String[]{jsonGameFilePath});
    }
    @Test
    void exceptionThrownWithInvalidFilePath() {
        String invalidFilePath = "invalid_path/games.json";
        assertThrows(IOException.class, () -> Launcher.main(new String[]{invalidFilePath}));
    }

    @Test
    void exceptionThrownWithInvalidFile() {
        String invalidFilePath = "invalid_path/games.json";
        assertThrows(IOException.class, () -> Launcher.main(new String[]{invalidFilePath}));
    }

    @Test
    void exceptionThrownWithMalformedJsonContent() {
        File gameFile = new File("src/test/resources/malformed.json");
        String malformedJsonFilePath = gameFile.getAbsolutePath();
        assertThrows(IOException.class, () -> Launcher.main(new String[]{malformedJsonFilePath}));
    }


}
