package aiss.githubminer.Service;

import aiss.githubminer.etl.Transformer;
import aiss.githubminer.model.github.GHCommit;
import aiss.githubminer.model.gitminer.Commit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommitService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gitminer.url}")
    private String gitminerUrl;

    public List<Commit> fetchCommitsFromGitHub(String owner, String repo, int nCommits, int maxPages) {
        List<Commit> result = new ArrayList<>();

        String baseUrl = String.format(
                "https://api.github.com/repos/%s/%s/commits?per_page=%d",
                owner, repo, nCommits
        );

        String nextPage = baseUrl;
        int page = 0;

        while (nextPage != null && page < maxPages) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/vnd.github.v3+json");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<GHCommit>> response = restTemplate.exchange(
                    nextPage,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<GHCommit>>() {}
            );

            List<GHCommit> rawCommits = response.getBody();

            if (rawCommits != null) {
                for (GHCommit rawCommit : rawCommits) {
                    Commit commit = Transformer.toGMCommit(rawCommit);
                    result.add(commit);
                }
            }

            // Obtener siguiente página de los headers Link
            List<String> linkHeaders = response.getHeaders().get("Link");
            nextPage = null;

            if (linkHeaders != null) {
                for (String linkHeader : linkHeaders) {
                    String[] links = linkHeader.split(",");
                    for (String link : links) {
                        if (link.contains("rel=\"next\"")) {
                            int start = link.indexOf("<") + 1;
                            int end = link.indexOf(">");
                            nextPage = link.substring(start, end);
                            break;
                        }
                    }
                }
            }

            page++;
        }

        return result;
    }

    public void sendCommitsToGitMiner(List<Commit> commits) {
        for (Commit commit : commits) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Commit> request = new HttpEntity<>(commit, headers);
            restTemplate.postForEntity(gitminerUrl + "/commits", request, Void.class);
        }
    }


    public void fetchAndSend(String owner, String repo, int nCommits, int maxPages) {
        List<Commit> commits = fetchCommitsFromGitHub(owner, repo, nCommits, maxPages);
        sendCommitsToGitMiner(commits);
    }
}