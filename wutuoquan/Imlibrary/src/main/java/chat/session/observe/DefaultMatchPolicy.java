package chat.session.observe;

import java.util.LinkedList;
import java.util.List;

/**
 * Description:
 * Created  by: weyko on 2016/10/14.
 */

public class DefaultMatchPolicy implements MatchPolicy {
    @Override
    public List<IMType> findMatchEventTypes(IMType imType, Object aEvent) {
        Class eventClass = aEvent.getClass();
        LinkedList result;
        for(result = new LinkedList(); eventClass != null; eventClass = eventClass.getSuperclass()) {
            result.add(new IMType(eventClass, imType.tag));
            this.addInterfaces(result, eventClass, imType.tag);
        }
        return result;
    }
    private void addInterfaces(LinkedList result, Class eventClass, String tag) {
        if(eventClass != null) {
            Class[] interfacesClasses = eventClass.getInterfaces();
            Class[] var8 = interfacesClasses;
            int var7 = interfacesClasses.length;

            for(int var6 = 0; var6 < var7; ++var6) {
                Class interfaceClass = var8[var6];
                if(!result.contains(interfaceClass)) {
                    result.add(new IMType(interfaceClass, tag));
                    this.addInterfaces(result, interfaceClass, tag);
                }
            }
        }
    }
}
