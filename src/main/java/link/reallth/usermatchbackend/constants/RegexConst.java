package link.reallth.usermatchbackend.constants;

/**
 * regex constants
 *
 * @author ReAllTh
 */
public class RegexConst {
    private RegexConst() {
    }

    public static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,18}$";
}
