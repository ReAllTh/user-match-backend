package link.reallth.usermatchbackend.model.dto;

import lombok.Data;

/**
 * user delete data transfer object
 *
 * @author ReAllTh
 */
@Data
public class UserDeleteDTO {

    private String id;
    private String username;
    private String email;
}
