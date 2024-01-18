package link.reallth.usermatchbackend.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import link.reallth.usermatchbackend.model.po.User;
import link.reallth.usermatchbackend.model.vo.UserVO;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;

/**
 * user utils
 *
 * @author ReAllTh
 */
public class BusinessBeanUtils {

    private BusinessBeanUtils() {
    }

    /**
     * transfer user to user view object
     *
     * @param user user to be transfer
     * @return user view object
     */
    public static UserVO getUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO, "tags");
        userVO.setTags(new Gson().fromJson(user.getTags(), new TypeToken<ArrayList<String>>() {
        }));
        return userVO;
    }

}
