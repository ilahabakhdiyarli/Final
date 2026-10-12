package com.grad.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** POST /auth/login cavabı (schemas/login-response.json). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

    private String token;
    private User user;

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {

        private String id;
        private String name;
        private String email;
        private String role;
        private String phone;
        private String avatar;
        private String createdAt;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }

        public String getPhone() {
            return phone;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}
