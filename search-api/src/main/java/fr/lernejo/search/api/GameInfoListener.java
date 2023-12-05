package fr.lernejo.search.api;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GameInfoListener {

    private final RestHighLevelClient elasticsearchClient;

    @Autowired
    public GameInfoListener(RestHighLevelClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    @RabbitListener(queues = AmqpConfiguration.GAME_INFO_QUEUE)
    public void onMessage(Message message) {
        String messageBody = new String(message.getBody());
        MessageProperties properties = message.getMessageProperties();
        String gameId = properties.getHeader("game_id");

        indexMessageInElasticsearch(gameId, messageBody);
    }

    private void indexMessageInElasticsearch(String gameId, String messageBody) {
        IndexRequest indexRequest = new IndexRequest("games")
            .id(gameId)
            .source(messageBody, XContentType.JSON);

        try {
            elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

