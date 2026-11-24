package aiss.githubminer.model.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GHCommit {

    @JsonProperty("sha")
    private String sha;

    @JsonProperty("commit")
    private CommitDetail commit;

    @JsonProperty("committer")
    private User committer;

    @JsonProperty("html_url")
    private String htmlUrl;

    public static class CommitDetail {

        @JsonProperty("message")
        private String message;

        @JsonProperty("author")
        private CommitAuthor author;

        @JsonProperty("committer")
        private CommitAuthor committer;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public CommitAuthor getAuthor() {
            return author;
        }

        public void setAuthor(CommitAuthor author) {
            this.author = author;
        }

        public CommitAuthor getCommitter() {
            return committer;
        }

        public void setCommitter(CommitAuthor committer) {
            this.committer = committer;
        }
    }

    public static class CommitAuthor {
        @JsonProperty("name")
        private String name;

        @JsonProperty("email")
        private String email;

        @JsonProperty("date")
        private String date;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public static class User {
        @JsonProperty("login")
        private String login;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public CommitDetail getCommit() {
        return commit;
    }

    public void setCommit(CommitDetail commit) {
        this.commit = commit;
    }

    public User getCommitter() {
        return committer;
    }

    public void setCommitter(User committer) {
        this.committer = committer;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }
}
