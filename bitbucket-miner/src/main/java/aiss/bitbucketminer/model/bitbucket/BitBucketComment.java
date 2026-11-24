package aiss.bitbucketminer.model.bitbucket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BitBucketComment {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("content")
    private Content content;

    @JsonProperty("created_on")
    private String created_on;

    @JsonProperty("updated_on")
    private String updated_on;

    @JsonProperty("user")
    private User user;

    // Nested classes

    public static class Content {
        @JsonProperty("raw")
        private String raw;

        public String getRaw() {
            return raw;
        }
        public void setRaw(String raw) {
            this.raw = raw;
        }
    }

    public static class User {

        @JsonProperty("display_name")
        private String display_name;

        @JsonProperty("uuid")
        private String uuid;

        @JsonProperty("links")
        private Links links;

        // getters y setters

        public String getDisplay_name() {
            return display_name;
        }
        public void setDisplay_name(String display_name) {
            this.display_name = display_name;
        }

        public String getUuid() {
            return uuid;
        }
        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public Links getLinks() {
            return links;
        }
        public void setLinks(Links links) {
            this.links = links;
        }

        public static class Links {
            @JsonProperty("avatar")
            private Link avatar;

            @JsonProperty("html")
            private Link html;

            public Link getAvatar() {
                return avatar;
            }
            public void setAvatar(Link avatar) {
                this.avatar = avatar;
            }

            public Link getHtml() {
                return html;
            }
            public void setHtml(Link html) {
                this.html = html;
            }
        }

        public static class Link {
            @JsonProperty("href")
            private String href;

            public String getHref() {
                return href;
            }
            public void setHref(String href) {
                this.href = href;
            }
        }
    }

    // Getters & Setters for BitBucketComment

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Content getContent() {
        return content;
    }
    public void setContent(Content content) {
        this.content = content;
    }

    public String getCreated_on() {
        return created_on;
    }
    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getUpdated_on() {
        return updated_on;
    }
    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}


