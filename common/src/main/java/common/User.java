package common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    int userID;
    String login;
    String password;
    String nickname;
}
