package aiss.githubminer.etl;

import aiss.githubminer.model.github.*;
import aiss.githubminer.model.gitminer.*;
import aiss.githubminer.model.gitminer.Comment;
import aiss.githubminer.model.gitminer.Commit;
import aiss.githubminer.model.gitminer.Issue;
import aiss.githubminer.model.gitminer.User;

import java.util.ArrayList;
import java.util.List;

public class Transformer {

    public static Commit toGMCommit(GHCommit ghCommit) {
        Commit commit = new Commit();

        commit.setId(ghCommit.getSha());

        String fullMessage = ghCommit.getCommit().getMessage();
        String[] messageLines = fullMessage.split("\n", 2);
        commit.setTitle(messageLines[0]);
        commit.setMessage(fullMessage);

        commit.setAuthorName(ghCommit.getCommit().getAuthor().getName());
        commit.setAuthorEmail(ghCommit.getCommit().getAuthor().getEmail());

        if (ghCommit.getCommitter() != null) {
            commit.setCommitterName(ghCommit.getCommitter().getLogin());
            commit.setCommitterEmail(ghCommit.getCommit().getAuthor().getEmail()); // No siempre disponible
        } else {
            commit.setCommitterName("Unknown");
            commit.setCommitterEmail("");
        }

        commit.setAuthoredDate(ghCommit.getCommit().getAuthor().getDate());
        commit.setCommittedDate(ghCommit.getCommit().getCommitter().getDate());
        commit.setWebUrl(ghCommit.getHtmlUrl());

        return commit;
    }

    public static Issue toGMIssue(GHIssue ghIssue, List<Comment> comments) {
        Issue issue = new Issue();

        issue.setId(String.valueOf(ghIssue.getId()));
        issue.setRefId(String.valueOf(ghIssue.getNumber()));
        issue.setTitle(ghIssue.getTitle());
        issue.setDescription(ghIssue.getBody());
        issue.setState(ghIssue.getState());
        issue.setCreatedAt(ghIssue.getCreatedAt());
        issue.setUpdatedAt(ghIssue.getUpdatedAt());
        issue.setClosedAt(String.valueOf(ghIssue.getClosedAt()));

        List<String> labels = new ArrayList<>();
        if (ghIssue.getLabels() != null) {
            for (GHIssue.Label label : ghIssue.getLabels()) {
                labels.add(label.getName());
            }
        }
        issue.setLabels(labels);

        issue.setAuthor(toGMUser(ghIssue.getUser()));
        issue.setAssignee(ghIssue.getAssignee() != null ? toGMUser(ghIssue.getAssignee()) : null);
        issue.setUpvotes(0);
        issue.setDownvotes(0);
        issue.setWebUrl(ghIssue.getHtmlUrl());
        issue.setComments(comments);

        return issue;
    }

    public static Comment toGMComment(GHComment ghComment) {
        Comment comment = new Comment();

        comment.setId(String.valueOf(ghComment.getId()));
        comment.setBody(ghComment.getBody());
        comment.setCreatedAt(ghComment.getCreatedAt());
        comment.setUpdatedAt(ghComment.getUpdatedAt());
        comment.setAuthor(toGMUser(ghComment.getUser()));

        return comment;
    }

    public static User toGMUser(GHUser ghUser) {
        if (ghUser == null) return null;

        User user = new User();
        user.setId(String.valueOf(ghUser.getId()));          // ID convertido a String
        user.setUsername(ghUser.getLogin());                 // username en GitMiner es login en GitHub
        user.setName(ghUser.getName());                       // nombre real si está disponible
        user.setAvatarUrl(ghUser.getAvatarUrl());             // URL avatar
        user.setWebUrl(ghUser.getHtmlUrl());                  // URL del perfil web (html_url)

        return user;
    }



    public static Project toGMProject(GHProject ghProject) {
        Project project = new Project();

        project.setId(String.valueOf(ghProject.getId()));
        project.setName(ghProject.getName());
        project.setWebUrl(ghProject.getHtmlUrl());
        project.setCommits(new ArrayList<>());
        project.setIssues(new ArrayList<>());

        return project;
    }
}
