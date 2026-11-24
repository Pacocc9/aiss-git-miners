
package aiss.githubminer.model.gitminer;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"id", "name", "web_url", "commits", "issues"})
@Entity
@Table(name = "Project")
public class Project {

    @Id
    @JsonProperty("id")
    public String id;

    @JsonProperty("name")
    @NotEmpty(message = "The name of the project cannot be empty")
    public String name;

    @JsonProperty("web_url")
    @NotEmpty(message = "The URL of the project cannot be empty")
    public String webUrl;

    @JsonProperty("commits")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectId")
    private List<Commit> Commits;

    @JsonProperty("issues")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "projectId")
    private List<Issue> Issues;

    public Project() {
        Commits = new ArrayList<>();
        Issues = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public List<Commit> getCommits() {
        return Commits;
    }

    public void setCommits(List<Commit> Commits) {
        this.Commits = Commits;
    }

    public List<Issue> getIssues() {
        return Issues;
    }

    public void setIssues(List<Issue> Issues) {
        this.Issues = Issues;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Project.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("commits");
        sb.append('=');
        sb.append(((this.Commits == null)?"<null>":this.Commits));
        sb.append(',');
        sb.append("issues");
        sb.append('=');
        sb.append(((this.Issues == null)?"<null>":this.Issues));
        sb.append(',');

        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}
