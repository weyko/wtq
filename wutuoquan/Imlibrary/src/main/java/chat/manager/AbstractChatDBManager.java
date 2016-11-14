package chat.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import chat.base.IMClient;
import chat.common.config.Constant;

/**
 * @author weyko
 */
public class AbstractChatDBManager {
    /**
     * 数据库关键字
     */
    public final static String SPLIT = ",";
    public final static String SELECT = "SELECT ";
    public final static String ALL = " * ";
    public final static String FROM = " FROM ";
    public final static String WHERE = " WHERE ";
    public final static String EQ_INT = " = ";
    public final static String EQ_STR_LEFT = " ='";
    public final static String EQ_STR_RIGHT = "'";
    public final static String AND = " and ";
    public final static String ORDER = " order by ";
    public final static String DESC = " desc ";
    public final static String LIKE_LEFT = " like '%";
    public final static String LIKE_BINARY_LEFT = " like binary '%";
    public final static String LIKE_RIGHT = "%' ";
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sqlDB;
    private Context mContext;
    private final int CHAT_DATABASE_VERSION_CODE = 1;

    public AbstractChatDBManager() {
        openSqlite(IMClient.getInstance().getContext(), CHAT_DATABASE_VERSION_CODE);
    }

    private void openSqlite(Context context, int version) {
        mContext = context;
        if (databaseHelper == null)
            databaseHelper = new DatabaseHelper(context, version);
        if (sqlDB == null) {
            try {
                sqlDB = databaseHelper.getWritableDatabase();
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断表是否存在
     *
     * @return
     */
    public boolean isTableExist(SQLiteDatabase db, String tableName) {
        boolean isExist = false;
        if (db == null || TextUtils.isEmpty(tableName))
            return isExist;
        String sql = SELECT + "name" + FROM + DatabaseHelper.DATABASE_TABLE_NAME + WHERE + "type='table'" + AND + "name='" + tableName + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                isExist = tableName.equals(cursor.getString(0));
            }
            cursor.close();
            cursor = null;
        }
        return isExist;
    }

    /**
     * 动态创建消息表
     *
     * @return
     */
    public void createTableForMessage(SQLiteDatabase db, String sessionId) {
        if (db == null || TextUtils.isEmpty(sessionId))
            return;
        if (databaseHelper == null)
            databaseHelper = new DatabaseHelper(IMClient.getInstance().getContext(), CHAT_DATABASE_VERSION_CODE);
        databaseHelper.createTableForMessage(db, sessionId);
    }
    /**
     * @return void
     * @Title: destroy
     * @param:
     * @Description: 销毁数据库
     */
    public void destroy() {
        try {
            if (databaseHelper != null) {
                databaseHelper.close();
            }
            closeDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * @return void
     * @Title: open
     * @param:
     * @Description: 打开数据库
     */
    private void open(boolean isReadonly) {
        if (sqlDB == null) {
            if (isReadonly) {
                sqlDB = databaseHelper.getReadableDatabase();
            } else {
                sqlDB = databaseHelper.getWritableDatabase();
            }
        }
    }

    /**
     * @return void
     * @Title: reopen
     * @param:
     * @Description: 重启数据库
     */
    public final void reopen() {
        closeDB();
        open(false);
    }

    /**
     * @return void
     * @Title: closeDB
     * @param:
     * @Description: 关闭数据库
     */
    private void closeDB() {
        if (sqlDB != null) {
            sqlDB.close();
            sqlDB = null;
        }
    }

    /**
     * @return SQLiteDatabase
     * @Title: sqlDB
     * @param:
     * @Description: 引用数据库
     */
    protected final SQLiteDatabase sqliteDB() {
        open(false);
        return sqlDB;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        private final static String DATABASE_NAME = "w_chat.db";
        public final static String DATABASE_TABLE_NAME = "sqlite_master";
        /**
         * 会话表
         */
        public final static String TABLE_NAME_SESSION = "w_im_session";
        /**
         * 联系人表
         */
        public static final String TABLE_NAME_CONTACT = "w_im_contacts";
        /**
         * 消息表
         */
        public static final String TABLE_NAME_MESSAGE = "w_im_message";
        /**
         * 群组表
         */
        public static final String TABLE_NAME_GRUOPS = "w_im_groups";
        /**
         * 群组成员表
         */
        public static final String TABLE_NAME_GRUOP_MEMBERS = "w_im_group_members";
        /**
         * 商家表
         */
        public final static String TABLE_NAME_SHOP = "w_im_shop";
        /**
         * 新朋友表
         */
        public final static String TABLE_NAME_NEW_FRIEND = "w_im_friend";
        /**
         * 临时消息表
         */
        public final static String TABLE_NAME_TEMB_MSG = "w_im_temb_msg";
        private Context context;

        public DatabaseHelper(Context context, int version) {
            this(context, Constant.LOGINAPPTYPE + "_"
                    + IMClient.getInstance().getSSOUserId() + "_"
                    + DATABASE_NAME, null, version);
        }

        public DatabaseHelper(Context context, String name,
                              CursorFactory factory, int version) {
            super(context, name, factory, version);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            createTables(db);
        }

        /**
         * 添加表字段
         *
         * @param db
         * @param tableName  表名
         * @param columnName 字段名
         * @param culumnType 字段类型
         */
        private void addColumnForTable(SQLiteDatabase db, String tableName, String columnName, String culumnType) {
            String sql = "ALTER TABLE " + tableName + "  ADD " + columnName + " " + culumnType;
            db.execSQL(sql);
        }

        /**
         * @return void
         * @Title: onUpgradeOld
         * @param:
         * @Description: 更新旧表
         */
        private void onUpgradeOld(SQLiteDatabase db) {
            String sqls[] = {TABLE_NAME_SESSION, TABLE_NAME_CONTACT,
                    TABLE_NAME_MESSAGE, TABLE_NAME_GRUOPS,
                    TABLE_NAME_GRUOP_MEMBERS};
            for (String sql : sqls) {
                try {
                    db.execSQL("DROP TABLE IF EXISTS " + sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * @return void
         * @Title: createTables
         * @param:
         * @Description: 创建表
         */
        private void createTables(SQLiteDatabase db) {
            // 创建联系人表
            createTableForContacts(db);
            // 创建会话表
            createTableForSession(db);
            // 创建群表
            createTableForGroups(db);
            // 创建群组成员表
            createTableGroupMembers(db);
            // 创建新朋友表
            createTableForNewFriends(db);
            //创建临时表
            createTableTembMessage(db);
        }

        /**
         * @return void
         * @Title: createTableForContacts
         * @param:
         * @Description: 创建联系人表
         */
        private void createTableForContacts(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CONTACT
                    + " (" + ContactsColumn.ID
                    + "  INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ContactsColumn.W_ID + " TEXT, " + ContactsColumn.SHOP_ID
                    + " TEXT, " + ContactsColumn.AVATAR + " TEXT, "
                    + ContactsColumn.SIGN + " TEXT, " + ContactsColumn.NAME
                    + " TEXT, " + ContactsColumn.SEX + " INTEGER, "
                    + ContactsColumn.BIRTHDAY + " BIGINT, "
                    + ContactsColumn.FULL_AREA + " TEXT, "
                    + ContactsColumn.DOMAIN + " TEXT, "
                    + ContactsColumn.FANS_STATUS + " INTEGER, "
                    + ContactsColumn.IS_TOP + " INTEGER, "
                    + ContactsColumn.IS_FORBID + " INTEGER, "
                    + ContactsColumn.REMARK + " TEXT )";
            try {
                db.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @return void
         * @Title: createTableForMessage
         * @param:
         * @Description: 创建消息表
         */
        private void createTableForMessage(SQLiteDatabase db, String tableName) {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName
                    + " (" + MessageColumn.ID
                    + "  INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + MessageColumn.MSG_CODE + " TEXT, "
                    + MessageColumn.SESSION_ID + " TEXT, "
                    + MessageColumn.MSG_TIME + " BIGINT, "
                    + MessageColumn.CHAT_WITH + " TEXT, " + MessageColumn.FROM
                    + " TEXT, " + MessageColumn.TO + " TEXT, "
                    + MessageColumn.MSG_DIRECTION + " INTEGER, "
                    + MessageColumn.READ_STATUS + " INTEGER, "
                    + MessageColumn.LISTEN_STATUS + " INTEGER, "
                    + MessageColumn.MSG_STATUS + " INTEGER, "
                    + MessageColumn.SESSION + " TEXT, " + MessageColumn.SHOP_ID
                    + " TEXT, " + MessageColumn.MSG_BODY + " TEXT, "
                    + MessageColumn.SESSION_TYPE + " INTEGER, "
                    + MessageColumn.MSG_TYPE + " INTEGER, "
                    + MessageColumn.GROUP_TYPE + " INTEGER, "
                    + MessageColumn.REMARK + " TEXT )";
            try {
                db.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @return void
         * @Title: createTableForSession
         * @param:
         * @Description: 创建会话表
         */
        private void createTableForSession(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SESSION
                    + " (" + SessionColumn.ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + SessionColumn.SESSION_ID + " TEXT, "
                    + SessionColumn.CONTACT_ID + " TEXT , "
                    + SessionColumn.UNREAD_NUM + " INTEGER DEFAULT 0, "
                    + SessionColumn.MSG_CODE + "  TEXT, "
                    + SessionColumn.SESSION + "  TEXT, "
                    + SessionColumn.MSG_TIME + "  TEXT, "
                    + SessionColumn.DIRECT + " INTEGER DEFAULT 0, "
                    + SessionColumn.READ_STATUS + " INTEGER DEFAULT 0, "
                    + SessionColumn.MSG_STATUS + " INTEGER DEFAULT 0, "
                    + SessionColumn.SESSION_TYPE + " INTEGER DEFAULT 0, "
                    + SessionColumn.IS_TOP + " INTEGER DEFAULT 0, "
                    + SessionColumn.IS_FORBID + " INTEGER DEFAULT 0, "
                    + SessionColumn.MESSAGE_TYPE + " INTEGER DEFAULT 0, "
                    + SessionColumn.MESSAGE_COUNT + " INTEGER DEFAULT 0" + ")";
            try {
                db.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @return void
         * @Title: createTableForGroups
         * @param:
         * @Description: 创建群组数据表
         */
        private void createTableForGroups(SQLiteDatabase db) {
            String sql2 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_GRUOPS
                    + " (" + GroupColumn.ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GroupColumn.GROUP_ID + " TEXT , "
                    + GroupColumn.GROUP_NAME + " TEXT, "
                    + GroupColumn.GROUP_AVATAR + " TEXT, "
                    + GroupColumn.GROUP_OWNER + " TEXT, "
                    + GroupColumn.GROUP_DECLARED + "  TEXT, "
                    + GroupColumn.GROUP_MAX + " INTEGER DEFAULT 100, "
                    + GroupColumn.GROUP_PERMISSION + " INTEGER DEFAULT 0, "
                    + GroupColumn.GROUP_MEMBER_COUNTS + " INTEGER DEFAULT 0, "
                    + GroupColumn.GROUP_ISTOP + " INTEGER DEFAULT 0, "
                    + GroupColumn.GROUP_JOINED + " INTEGER DEFAULT 0, "
                    + GroupColumn.GROUP_ISNOTICE + " INTEGER DEFAULT 1, "
                    + GroupColumn.GROUP_ISPUBLIC + " INTEGER DEFAULT 1, "
                    + GroupColumn.IS_CONTACT_MODE + " INTEGER DEFAULT 1, "
                    + GroupColumn.ROLE_TYPE + " INTEGER , "
                    + GroupColumn.GROUP_DATE_CREATED + "  BIGINT" + ")";
            try {
                db.execSQL(sql2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @return void
         * @Title: createTableGroupMembers
         * @param:
         * @Description: 创建群组成员数据表
         */
        private void createTableGroupMembers(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLE_NAME_GRUOP_MEMBERS + " (" + GroupMembersColumn.ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GroupMembersColumn.W_ID + " TEXT, "
                    + GroupMembersColumn.OWN_GROUP_ID + " TEXT, "
                    + GroupMembersColumn.NICKNAME + " TEXT, "
                    + GroupMembersColumn.AVATAR + " TEXT, "
                    + GroupMembersColumn.REMARK + " TEXT, "
                    + GroupMembersColumn.DOMAIN + " TEXT, "
                    + GroupMembersColumn.ROLE + "  INTEGER DEFAULT 1, "
                    + GroupMembersColumn.ISBAN + "  INTEGER DEFAULT 0, "
                    + GroupMembersColumn.RULE + "  INTEGER DEFAULT 0, "
                    + GroupMembersColumn.ISENABLE + "  INTEGER DEFAULT 1, "
                    + GroupMembersColumn.VOIPACCOUNT + " TEXT " + ")";
            try {
                db.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @return void
         * @Title: createTableForShop
         * @param:
         * @Description: 创建商家表
         */
        private void createTableForShop(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SHOP
                    + " (" + SessionColumn.ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + SessionColumn.SESSION_ID + " TEXT, "
                    + SessionColumn.CONTACT_ID + " TEXT , "
                    + SessionColumn.UNREAD_NUM + " INTEGER DEFAULT 0, "
                    + SessionColumn.MSG_CODE + "  TEXT, "
                    + SessionColumn.SESSION + "  TEXT, "
                    + SessionColumn.MSG_TIME + "  TEXT, "
                    + SessionColumn.DIRECT + " INTEGER DEFAULT 0, "
                    + SessionColumn.READ_STATUS + " INTEGER DEFAULT 0, "
                    + SessionColumn.MSG_STATUS + " INTEGER DEFAULT 0, "
                    + SessionColumn.SESSION_TYPE + " INTEGER DEFAULT 0, "
                    + SessionColumn.IS_TOP + " INTEGER DEFAULT 0, "
                    + SessionColumn.IS_FORBID + " INTEGER DEFAULT 0, "
                    + SessionColumn.MESSAGE_TYPE + " INTEGER DEFAULT 0, "
                    + SessionColumn.MESSAGE_COUNT + " INTEGER DEFAULT 0" + ")";
            try {
                db.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @return void
         * @Title: createTableForNewFriends
         * @Description: 创建新朋友数据表
         */
        private void createTableForNewFriends(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLE_NAME_NEW_FRIEND + " (" + GroupMembersColumn.ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + NewFriendsColumn.W_ID + " TEXT, "
                    + SessionColumn.SESSION_ID + " TEXT, "
                    + NewFriendsColumn.MSG_CODE + " TEXT, "
                    + NewFriendsColumn.REMARK + " TEXT, "
                    + NewFriendsColumn.MSG_TIME + " BIGINT " + ")";
            try {
                db.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /**
         * @return void
         * @Title: createTableTembMessage
         * @Description: 创建临时消息表,用于处理异常消息
         */
        private void createTableTembMessage(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLE_NAME_TEMB_MSG + " (" + TembMsgColumn.ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TembMsgColumn.SESSION_ID + " TEXT, "
                    + TembMsgColumn.SHOP_ID + " TEXT, "
                    + TembMsgColumn.MSG_CODE + " TEXT, "
                    + TembMsgColumn.REMARK + " TEXT "
                    + ")";
            try {
                db.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class BaseColumn {
        public static final String ID = "ID";
        /**
         * 未读数
         */
        public static final String UNREAD_NUM = "unread_num";
    }

    /**
     * @author weyko zhong.xiwei@moxiangroup.com
     * @ClassName: ContactsColumn
     * @Description: 联系人表字段名称
     * @Company moxian
     * @date 2015年7月27日 下午5:32:23
     */
    public class ContactsColumn extends BaseColumn {
        /**
         * 魔线用户id
         */
        public final static String W_ID = "mxId";
        /**
         * 魔线店铺id（用于查看该聊天对象的店铺详情）
         */
        public final static String SHOP_ID = "shopId";
        /**
         * 头像
         */
        public final static String AVATAR = "avatar";
        /**
         * 用户名
         */
        public final static String NAME = "name";
        /**
         * 性别
         */
        public final static String SEX = "sex";
        /**
         * 生日
         */
        public final static String BIRTHDAY = "birthday";
        /**
         * 出生地
         */
        public final static String FULL_AREA = "fullArea";
        /**
         * 粉丝状态
         */
        public final static String FANS_STATUS = "fansStatus";
        /**
         * 个性签名
         */
        public final static String SIGN = "sign";
        /**
         * 备注
         */
        public final static String REMARK = "remark";
        /**
         * 是否置顶
         */
        public final static String IS_TOP = "isTop";
        /**
         * 是否免打扰
         */
        public final static String IS_FORBID = "isForbid";
        /**
         * 聊天域名
         */
        public final static String DOMAIN = "domain";
    }

    /**
     * @author weyko zhong.xiwei@moxiangroup.com
     * @ClassName: MessageColumn
     * @Description: 消息字段名
     * @Company moxian
     * @date 2015年7月27日 下午5:39:26
     */
    public class MessageColumn extends BaseColumn {
        /**
         * 消息编号
         */
        public final static String MSG_CODE = "msgCode";
        /**
         * 会话ID
         */
        public static final String SESSION_ID = "sid";
        /**
         * 消息修改时间
         */
        public final static String MSG_TIME = "msgTime";
        /**
         * 消息聊天对象
         */
        public final static String CHAT_WITH = "chatWith";
        /**
         * 消息发送者
         */
        public final static String FROM = "fromId";
        /**
         * 消息接受者
         */
        public final static String TO = "toId";
        /**
         * 消息发送方向，0:发送，1：接受
         */
        public final static String MSG_DIRECTION = "msgDirection";
        /**
         * 是否已读
         */
        public static final String READ_STATUS = "isRead";
        /**
         * 是否收听
         */
        public static final String LISTEN_STATUS = "isListen";
        /**
         * 消息状态:-1:发送失败,0：发送中，1：发送成功,2:准备发送
         */
        public final static String MSG_STATUS = "msgStatus";
        /**
         * 会话内容
         */
        public final static String SESSION = "session";
        /**
         * 消息体
         */
        public final static String MSG_BODY = "msgBody";
        /**
         * 消息类别  
         */
        public static final String SESSION_TYPE = "sessionType";
        /**
         * 消息类型
         */
        public static final String MSG_TYPE = "msgType";
        /**
         * 备注
         */
        public static final String REMARK = "remark";
        /**
         * 集合类型
         */
        public static final String GROUP_TYPE = "groupType";
        /**
         * 店铺ID
         */
        public static final String SHOP_ID = "shopid";
    }

    /**
     * @author weyko zhong.xiwei@moxiangroup.com
     * @ClassName: SessionColumn
     * @Description: 会话表字段名字
     * @Company moxian
     * @date 2015年7月27日 下午4:42:45
     */
    public class SessionColumn extends BaseColumn {
        /**
         * 会话id
         */
        public static final String SESSION_ID = "sessionId";
        /**
         * 总消息数
         */
        public static final String MESSAGE_COUNT = "sumCount";
        /**
         * 最后一条消息发送者
         */
        public static final String CONTACT_ID = "contactId";
        /**
         * 内容
         */
        public static final String SESSION = "session";
        /**
         * 内容ID
         */
        public static final String MSG_CODE = "msgCode";
        /**
         * 消息的发送方向
         */
        public static final String DIRECT = "direct";
        /**
         * 消息的发送状态
         */
        public static final String MSG_STATUS = "msgStatus";
        /**
         * 消息的已读状态
         */
        public static final String READ_STATUS = "readStatus";
        /**
         * 消息时间
         */
        public static final String MSG_TIME = "msgTime";
        /**
         * 消息类别
         */
        public static final String SESSION_TYPE = "sessionType";
        /**
         * 消息类型
         */
        public static final String MESSAGE_TYPE = "type";
        /**
         * 是否置顶
         */
        public static final String IS_TOP = "isTop";
        /**
         * 是否免打扰
         */
        public static final String IS_FORBID = "isForbid";
    }

    /**
     * @author weyko zhong.xiwei@moxiangroup.com
     * @ClassName: GroupColumn
     * @Description: 群组表
     * @Company moxian
     * @date 2015年8月4日 下午12:00:56
     */
    public class GroupColumn extends BaseColumn {
        /**
         * 群组ID
         */
        public static final String GROUP_ID = "groupid";
        /**
         * 群组名称
         */
        public static final String GROUP_NAME = "name";
        /**
         * 群组头像
         */
        public static final String GROUP_AVATAR = "avatar";
        /**
         * 群组拥有者
         */
        public static final String GROUP_OWNER = "owner";
        /**
         * 群组上限 0:临时组(上限100人) 1:普通组(上限200人) 2:VIP组(上限500人)
         */
        public static final String GROUP_MAX = "maxCount";
        /**
         * 群组公告
         */
        public static final String GROUP_DECLARED = "declared";
        /**
         * 群组创建日期
         */
        public static final String GROUP_DATE_CREATED = "createDate";
        /**
         * 群组成员数
         */
        public static final String GROUP_MEMBER_COUNTS = "count";
        /**
         * 群组加入权限
         */
        public static final String GROUP_PERMISSION = "permission";
        /**
         * 群组是否置顶
         */
        public static final String GROUP_ISTOP = "isTop";
        /**
         * 群组是否加入
         */
        public static final String GROUP_JOINED = "joined";
        /**
         * 是否通知
         */
        public static final String GROUP_ISNOTICE = "isNotice";
        /**
         * 是否开放群
         */
        public static final String GROUP_ISPUBLIC = "isPublic";
        /**
         * 是否通讯录模式
         */
        public static final String IS_CONTACT_MODE = "isContact";
        /**
         * 用户角色
         */
        public static final String ROLE_TYPE = "roleType";

    }

    /**
     * @author weyko zhong.xiwei@moxiangroup.com
     * @ClassName: GroupMembersColumn
     * @Description: 群组成员表
     * @Company moxian
     * @date 2015年8月4日 下午12:08:12
     */
    class GroupMembersColumn extends BaseColumn {
        // 魔线Id
        public static final String W_ID = "mxId";
        // 所属群组
        public static final String OWN_GROUP_ID = "groupId";
        // 所在群昵称
        public static final String NICKNAME = "nickName";
        // 所在群头像
        public static final String AVATAR = "avatar";
        // 是否禁言
        public static final String ISBAN = "isban";
        // 用户voip账号
        public static final String VOIPACCOUNT = "voipacCount";
        // 用户角色
        public static final String ROLE = "role";
        // 用户域名
        public static final String DOMAIN = "domain";
        // 用户的备注
        public static final String REMARK = "remark";
        // 是否接收群组消息
        public static final String RULE = "rule";
        // 是否属于该群
        public static final String ISENABLE = "isenable";
    }

    /**
     * @author weyko zhong.xiwei@moxiangroup.com
     * @ClassName: NewFriendsColumn
     * @Description: 新朋友表
     * @Company moxian
     * @date 2016年3月15日 11：37
     */
    class NewFriendsColumn extends BaseColumn {
        /**
         * 魔线Id
         */
        public static final String W_ID = "mxId";
        /***
         * 消息ID
         */
        public static final String MSG_CODE = "msgCode";
        /***
         * 消息时间
         */
        public static final String MSG_TIME = "msgTime";
        /**
         * 用户的备注
         */
        public static final String REMARK = "remark";
    }
    /**
     * @Description: 临时消息表字段
     */
    class TembMsgColumn {
        public static final String ID = "ID";
        /**
         * 会话Id
         */
        public static final String SESSION_ID = "sessionId";
        /***
         * 店铺ID
         */
        public static final String SHOP_ID = "shopId";
        /***
         * 消息ID
         */
        public static final String MSG_CODE = "msgCode";
        /**
         * 备注
         */
        public static final String REMARK = "remark";
    }
    /**
     * 获取数据库版本号
     *
     * @return 版本号
     */
    public int getVersionCode() {
        return CHAT_DATABASE_VERSION_CODE;
    }

    /**
     * 初始化消息观察者
     */
    private final MessageObservable msgObservable = new MessageObservable();

    /**
     * @return void
     * @Title: registerObserver
     * @param:
     * @Description: 注册观察者
     */
    protected void registerObserver(OnMessageChange observer) {
        msgObservable.registerObserver(observer);
    }

    /**
     * @return void
     * @Title: unregisterObserver
     * @param:
     * @Description: 取消注册观察者
     */
    protected void unregisterObserver(OnMessageChange observer) {
        msgObservable.unRegisterObserver(observer);
    }

    /**
     * @return void
     * @Title: notifyChanged
     * @param:
     * @Description: 通知改变
     */
    protected void notifyChanged(String session) {
        msgObservable.notifyChanged(session);
    }

    /**
     * @return void
     * @Title: release
     * @param:
     * @Description:释放资源
     */
    protected void release() {
        destroy();
        closeDB();
        databaseHelper = null;
    }
}
