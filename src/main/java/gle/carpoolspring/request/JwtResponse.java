package gle.carpoolspring.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private int id;
    private String username;
    private String email;
    private List<String> roles;
    public JwtResponse(String token, String refreshToken, int id, String username, String email, List<String> roles) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
