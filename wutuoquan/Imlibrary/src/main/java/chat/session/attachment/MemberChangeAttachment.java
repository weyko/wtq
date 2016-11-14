package chat.session.attachment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Description:
 * Created  by: weyko on 2016/5/31.
 */
public class MemberChangeAttachment extends NotificationAttachment {
    private static final String TAG_ACCOUNTS = "ids";
    private static final String TAG_ACCOUNT = "id";
    private ArrayList<String> targets;

    public MemberChangeAttachment() {
    }

    public ArrayList<String> getTargets() {
        return this.targets;
    }

    public final void parse(JSONObject var1) {
        if(!var1.has("ids")) {
            if(var1.has("id")) {
                this.targets = new ArrayList(1);
                this.targets.add(JsonManager.d(var1, "id"));
            }

        } else {
            JSONArray var3 = JsonManager.f(var1, "ids");
            this.targets = new ArrayList(var3.length());

            for(int var2 = 0; var2 < var3.length(); ++var2) {
                this.targets.add(JsonManager.a(var3, var2));
            }

        }
    }
}
