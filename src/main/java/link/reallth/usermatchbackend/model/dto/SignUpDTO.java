package link.reallth.usermatchbackend.model.dto;

import lombok.Data;

import java.util.List;

/**
 * sign up data transfer object
 *
 * @author ReAllTh
 */
@Data
public class SignUpDTO {
    private String username;
    private String email;
    private String password;
    private String avatar;
    private List<String> tags;
}