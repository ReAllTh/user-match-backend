package link.reallth.usermatchbackend.model.dto;

import lombok.Data;

import java.util.List;

/**
 * sign up dto
 *
 * @author ReAllTh
 */
@Data
public class SignUpDTO {
    private final String username;
    private final String email;
    private final String password;
    private final String avatar;
    private final List<String> tags;
}
