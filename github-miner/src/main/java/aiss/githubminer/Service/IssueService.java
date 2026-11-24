package aiss.githubminer.Service;

import aiss.githubminer.etl.Transformer;
import aiss.githubminer.model.github.GHIssue;
import aiss.githubminer.model.github.GHComment;
import aiss.githubminer.model.gitminer.Comment;
import aiss.githubminer.model.gitminer.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class IssueService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gitminer.url}")
    private String gitminerUrl;

    public List<Issue> fetchIssuesFromGitHub(String owner, String repo, int nIssues, int maxPages) {
        List<Issue> result = new ArrayList<>();

        String baseUrl = String.format(
                "https://api.github.com/repos/%s/%s/issues?per_page=%d&state=all",
                owner, repo, nIssues
        );

        int page = 1;

        while (page <= maxPages) {
            String url = baseUrl + "&page=" + page;

            try {
                ResponseEntity<List<GHIssue>> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<GHIssue>>() {}
                );

                List<GHIssue> rawIssues = response.getBody();

                if (rawIssues == null || rawIssues.isEmpty()) break;

                for (GHIssue rawIssue : rawIssues) {
                    // ⚠️ GitHub incluye PRs en el endpoint de issues → los filtramos
                    if (rawIssue.getPullRequest() != null) continue;

                    List<Comment> comments = fetchCommentsForIssue(owner, repo, rawIssue.getNumber());
                    Issue issue = Transformer.toGMIssue(rawIssue, comments);
                    result.add(issue);
                }

                page++;

            } catch (Exception e) {
                System.err.println("Error al obtener issues de GitHub: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }

        return result;
    }



    public List<Comment> fetchCommentsForIssue(String owner, String repo, Integer issueNumber) {
        String url = String.format(
                "https://api.github.com/repos/%s/%s/issues/%d/comments",
                owner, repo, issueNumber
        );

        ResponseEntity<List<GHComment>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GHComment>>() {}
        );

        List<GHComment> rawComments = response.getBody();
        List<Comment> comments = new ArrayList<>();

        if (rawComments != null) {
            for (GHComment ghComment : rawComments) {
                Comment comment = Transformer.toGMComment(ghComment);
                if (comment.getBody() != null && !comment.getBody().isBlank()) {
                    comments.add(comment);
                }
            }
        }

        return comments;
    }

    public void sendIssuesToGitMiner(List<Issue> issues) {
        for (Issue issue : issues) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Issue> request = new HttpEntity<>(issue, headers);
            restTemplate.postForEntity(gitminerUrl + "/issues", request, Void.class);
        }
    }

    public void fetchAndSend(String owner, String repo, int nIssues, int maxPages) {
        List<Issue> issues = fetchIssuesFromGitHub(owner, repo, nIssues, maxPages);
        sendIssuesToGitMiner(issues);
    }
}
