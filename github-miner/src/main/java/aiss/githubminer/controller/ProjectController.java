package aiss.githubminer.controller;

import aiss.githubminer.model.gitminer.Commit;
import aiss.githubminer.model.gitminer.Issue;
import aiss.githubminer.model.gitminer.Project;
import aiss.githubminer.Service.CommitService;
import aiss.githubminer.Service.IssueService;
import aiss.githubminer.Service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/github")
public class ProjectController {

    @Autowired
    CommitService commitService;

    @Autowired
    IssueService issueService;

    @Autowired
    ProjectService projectService;

    // Obtener commits (preview)
    @GetMapping("/{owner}/{repo}/commits")
    public List<Commit> previewCommits(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(defaultValue = "5") int nCommits,
            @RequestParam(defaultValue = "2") int maxPages
    ) {
        return commitService.fetchCommitsFromGitHub(owner, repo, nCommits, maxPages);
    }

    // Enviar commits a GitMiner
    @PostMapping("/{owner}/{repo}/commits")
    public void sendCommits(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(defaultValue = "5") int nCommits,
            @RequestParam(defaultValue = "2") int maxPages
    ) {
        commitService.fetchAndSend(owner, repo, nCommits, maxPages);
    }

    // Obtener issues (preview)
    @GetMapping("/{owner}/{repo}/issues")
    public List<Issue> previewIssues(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(defaultValue = "5") int nIssues,
            @RequestParam(defaultValue = "2") int maxPages
    ) {
        return issueService.fetchIssuesFromGitHub(owner, repo, nIssues, maxPages);
    }

    // Enviar issues a GitMiner
    @PostMapping("/{owner}/{repo}/issues")
    public void sendIssues(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(defaultValue = "5") int nIssues,
            @RequestParam(defaultValue = "2") int maxPages
    ) {
        issueService.fetchAndSend(owner, repo, nIssues, maxPages);
    }

    // Obtener proyecto completo (preview)
    @GetMapping("/{owner}/{repo}")
    public Project previewFullProject(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(defaultValue = "5") int nCommits,
            @RequestParam(defaultValue = "5") int nIssues,
            @RequestParam(defaultValue = "2") int maxPages
    ) {
        // Obtener el proyecto base
        Project project = projectService.fetchProjectFromGitHub(owner, repo);

        // Obtener commits
        List<Commit> commits = commitService.fetchCommitsFromGitHub(owner, repo, nCommits, maxPages);
        project.setCommits(commits);

        // Obtener issues
        List<Issue> issues = issueService.fetchIssuesFromGitHub(owner, repo, nIssues, maxPages);
        project.setIssues(issues);

        return project;
    }

    // Enviar proyecto completo a GitMiner
    @PostMapping("/{owner}/{repo}")
    @ResponseStatus(HttpStatus.CREATED)
    public Project sendFullProject(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(defaultValue = "5") int nCommits,
            @RequestParam(defaultValue = "5") int nIssues,
            @RequestParam(defaultValue = "2") int maxPages) {

            Project project = projectService.fetchProjectFromGitHub(owner, repo);

            List<Commit> commits = commitService.fetchCommitsFromGitHub(owner, repo, nCommits, maxPages);
            project.setCommits(commits);

            List<Issue> issues = issueService.fetchIssuesFromGitHub(owner, repo, nIssues, maxPages);
            project.setIssues(issues);

            projectService.sendProjectToGitMiner(project);

            return project;
    }
}