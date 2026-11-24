package aiss.bitbucketminer.etl;

import aiss.bitbucketminer.model.bitbucket.BitBucketComment;
import aiss.bitbucketminer.model.bitbucket.BitBucketProject;
import aiss.bitbucketminer.model.bitbucket.BitbucketCommit;
import aiss.bitbucketminer.model.gitminer.*;

import aiss.bitbucketminer.model.bitbucket.BitBucketIssue;

import java.util.ArrayList;
import java.util.List;

public class Transformer {

    public static Commit toGitMinerCommit(BitbucketCommit bitbucketCommit) {
        Commit commit = new Commit();

        // ID del commit
        commit.setId(bitbucketCommit.getHash());

        // Título (usamos la primera línea del mensaje como título)
        String[] messageLines = bitbucketCommit.getMessage().split("\n", 2);
        commit.setTitle(messageLines[0]);

        // Mensaje completo
        commit.setMessage(bitbucketCommit.getMessage());

        // Autor
        String rawAuthor = bitbucketCommit.getAuthor() != null ? bitbucketCommit.getAuthor().getRaw() : "Unknown";
        String[] parts = rawAuthor.split("<");
        String authorName = parts[0].trim();
        String authorEmail = (parts.length > 1) ? parts[1].replace(">", "").trim() : "";

        commit.setAuthorName(authorName);
        commit.setAuthorEmail(authorEmail);
        commit.setCommitterName(authorName);    // Bitbucket no distingue, así que usamos el mismo
        commit.setCommitterEmail(authorEmail);

        // Fechas
        String date = bitbucketCommit.getDate();
        commit.setAuthoredDate(date);
        commit.setCommittedDate(date);

        // URL web del commit
        String webUrl = bitbucketCommit.getLinks().get("html").getHref();
        commit.setWebUrl(webUrl);

        return commit;
    }

    public static Issue toGitMinerIssue(BitBucketIssue bitbucketIssue) {
        Issue issue = new Issue();

        // Aquí ponemos el id con el valor que antes iba a refId
        issue.setId(bitbucketIssue.getId() != null ? String.valueOf(bitbucketIssue.getId()) : null);

        // Si quieres conservar refId también, pero si no, puedes eliminar esta línea
        issue.setRefId(null);

        issue.setTitle(bitbucketIssue.getTitle());

        String description = (bitbucketIssue.getContent() != null)
                ? bitbucketIssue.getContent().getRaw()
                : "";
        issue.setDescription(description);

        issue.setState(bitbucketIssue.getState());
        issue.setCreatedAt(bitbucketIssue.getCreated_on());
        issue.setUpdatedAt(bitbucketIssue.getUpdated_on());
        issue.setClosedAt(null);  // No disponible en Bitbucket Issues

        List<String> labels = new ArrayList<>();
        if (bitbucketIssue.getKind() != null) labels.add(bitbucketIssue.getKind());
        if (bitbucketIssue.getPriority() != null) labels.add(bitbucketIssue.getPriority());
        issue.setLabels(labels);

        User author = new User();
        BitBucketIssue.Reporter reporter = bitbucketIssue.getReporter();

        if (reporter != null) {
            author.setId(reporter.getUuid());

            if (reporter.getNickname() != null && !reporter.getNickname().isEmpty()) {
                author.setUsername(reporter.getNickname());
            } else {
                author.setUsername(reporter.getDisplay_name());
            }

            author.setName(reporter.getDisplay_name());

            if (reporter.getLinks() != null) {
                if (reporter.getLinks().getAvatar() != null) {
                    author.setAvatarUrl(reporter.getLinks().getAvatar().getHref());
                } else {
                    author.setAvatarUrl(null);
                }

                if (reporter.getLinks().getHtml() != null) {
                    author.setWebUrl(reporter.getLinks().getHtml().getHref());
                } else {
                    author.setWebUrl(null);
                }
            } else {
                author.setAvatarUrl(null);
                author.setWebUrl(null);
            }
        } else {
            author.setName("Unknown");
            author.setUsername("unknown");
            author.setAvatarUrl(null);
            author.setWebUrl(null);
            author.setId(null);
        }
        issue.setAuthor(author);

        issue.setAssignee(null);

        issue.setUpvotes(0);
        issue.setDownvotes(0);

        String webUrl = (bitbucketIssue.getLinks() != null
                && bitbucketIssue.getLinks().getHtml() != null)
                ? bitbucketIssue.getLinks().getHtml().getHref()
                : "";
        issue.setWebUrl(webUrl);

        issue.setComments(new ArrayList<>());

        return issue;
    }


    public static Comment toGitMinerComment(BitBucketComment bitbucketComment) {
        Comment comment = new Comment();

        comment.setId(bitbucketComment.getId() != null ? String.valueOf(bitbucketComment.getId()) : null);

        String body = (bitbucketComment.getContent() != null)
                ? bitbucketComment.getContent().getRaw()
                : "";
        comment.setBody(body);

        comment.setCreatedAt(bitbucketComment.getCreated_on() != null ? bitbucketComment.getCreated_on() : "");
        comment.setUpdatedAt(bitbucketComment.getUpdated_on() != null ? bitbucketComment.getUpdated_on() : "");

        User author = new User();
        if (bitbucketComment.getUser() != null) {
            author.setName(bitbucketComment.getUser().getDisplay_name());
            author.setUsername(bitbucketComment.getUser().getDisplay_name() != null
                    ? bitbucketComment.getUser().getDisplay_name()
                    : "unknown");
            author.setId(bitbucketComment.getUser().getUuid());

            if (bitbucketComment.getUser().getLinks() != null) {
                if (bitbucketComment.getUser().getLinks().getAvatar() != null) {
                    author.setAvatarUrl(bitbucketComment.getUser().getLinks().getAvatar().getHref());
                } else {
                    author.setAvatarUrl(null);
                }
                if (bitbucketComment.getUser().getLinks().getHtml() != null) {
                    author.setWebUrl(bitbucketComment.getUser().getLinks().getHtml().getHref());
                } else {
                    author.setWebUrl(null);
                }
            } else {
                author.setAvatarUrl(null);
                author.setWebUrl(null);
            }
        } else {
            author.setName("Unknown");
            author.setUsername("unknown");
            author.setId(null);
            author.setAvatarUrl(null);
            author.setWebUrl(null);
        }
        comment.setAuthor(author);

        return comment;
    }



    public static Project toGitMinerProject(BitBucketProject bitbucketProject) {
        Project project = new Project();

        // ID (Bitbucket nos da uuid)
        project.setId(bitbucketProject.getUuid());

        // Name
        project.setName(bitbucketProject.getName());

        // Web URL
        String webUrl = (bitbucketProject.getLinks() != null
                && bitbucketProject.getLinks().getHtml() != null)
                ? bitbucketProject.getLinks().getHtml().getHref()
                : "";
        project.setWebUrl(webUrl);

        // Inicializamos las listas vacías (ya lo hace el constructor pero por claridad)
        project.setCommits(new ArrayList<>());
        project.setIssues(new ArrayList<>());

        return project;
    }



}
