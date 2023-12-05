package fr.lernejo.search.api;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class GameController {

    private final RestHighLevelClient client;

    @Autowired
    public GameController(RestHighLevelClient client) {
        this.client = client;
    }

    @GetMapping("/api/games")
    public List<Map<String, Object>> searchGames(@RequestParam String query) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(new QueryStringQueryBuilder(query));
        sourceBuilder.size(10);
        searchRequest.source(sourceBuilder);

        List<Map<String, Object>> results = new ArrayList<>();
        client.search(searchRequest, RequestOptions.DEFAULT)
            .getHits()
            .forEach(hit -> results.add(hit.getSourceAsMap()));

        return results;
    }
}


