package chat.session.observe;

import java.util.List;

/**
 * Description:
 * Created  by: weyko on 2016/10/14.
 */

public interface MatchPolicy {
    List<IMType> findMatchEventTypes(IMType imType, Object event);
}
