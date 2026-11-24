package aiss.githubminer.Service;

import aiss.githubminer.etl.Transformer;
import aiss.githubminer.model.github.GHProject;
import aiss.githubminer.model.gitminer.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProjectService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gitminer.url}")
    private String gitminerUrl;

    public Project fetchProjectFromGitHub(String owner, String repo) {
        String url = String.format(
                "https://api.github.com/repos/%s/%s",
                owner, repo
        );

        GHProject ghProject = restTemplate.getForObject(url, GHProject.class);

        return Transformer.toGMProject(ghProject);
    }

    public void sendProjectToGitMiner(Project project) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Project> request = new HttpEntity<>(project, headers);
        restTemplate.postForEntity(gitminerUrl + "/projects", request, Void.class);
    }

    public void fetchAndSend(String owner, String repo) {
        Project project = fetchProjectFromGitHub(owner, repo);
        sendProjectToGitMiner(project);
    }
}

