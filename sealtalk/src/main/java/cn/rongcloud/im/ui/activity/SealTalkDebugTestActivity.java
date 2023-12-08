package cn.rongcloud.im.ui.activity;

import static java.lang.System.exit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import cn.rongcloud.im.R;
import cn.rongcloud.im.ui.test.BindChatRTCRoomActivity;
import cn.rongcloud.im.ui.test.ChatRoomTestActivity;
import cn.rongcloud.im.ui.test.CommonConversationListTestActivity;
import cn.rongcloud.im.ui.test.DeviceInfoActivity;
import cn.rongcloud.im.ui.test.DiscussionActivity;
import cn.rongcloud.im.ui.test.GRRConversationListTestActivity;
import cn.rongcloud.im.ui.test.MsgDeliveryConversationListActivity;
import cn.rongcloud.im.ui.test.MsgExpansionConversationListActivity;
import cn.rongcloud.im.ui.test.PushConfigActivity;
import cn.rongcloud.im.ui.test.ShortageConversationListActivity;
import cn.rongcloud.im.ui.test.TagTestActivity;
import cn.rongcloud.im.ui.view.SettingItemView;
import cn.rongcloud.im.utils.DialogWithYesOrNoUtils;
import cn.rongcloud.im.utils.ToastUtils;
import cn.rongcloud.im.viewmodel.UserInfoViewModel;
import io.rong.imkit.RongIM;
import io.rong.imkit.config.ConversationClickListener;
import io.rong.imkit.config.RongConfigCenter;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class SealTalkDebugTestActivity extends TitleBaseActivity implements View.OnClickListener {
    private SettingItemView pushConfigModeSiv;
    private SettingItemView pushDiscussion;
    private SettingItemView pushLanguageSiv;
    private SettingItemView chatRoomSiv;
    private SettingItemView messageExpansion;
    private SettingItemView ultraGroup;
    private SettingItemView ultraGroupUnreadMentionDigests;
    private SettingItemView ultraGroupConversationByTargetIds;
    private SettingItemView tag;
    private SettingItemView messageDelivery;
    private SettingItemView shortage;
    private SettingItemView shortageDialog;
    private SettingItemView isDelRemoteMsgDialog;
    private SettingItemView isSoundDialog;
    private SettingItemView isVibrateDialog;
    private SettingItemView groupReadReceiptV2Siv;
    private SettingItemView deviceInfo;
    private SettingItemView referMsgTest;
    private SettingItemView permissionlistener;
    private SettingItemView ultraDebug; // 超级群的debug模式
    private SettingItemView isHideLoginPagePicCode; // 是否忽略登录图片验证码
    private SettingItemView quickIntercept; // 是否忽略登录图片验证码
    private SettingItemView createNotificationChannel;
    private SettingItemView bindChatRTCRoom;
    public static final String SP_IS_SHOW = "is_show";
    public static final String SP_PERMISSION_NAME = "permission_config";
    public static final String ULTRA_DEBUG_CONFIG = "ultra_debug_config";
    public static final String ULTRA_IS_DEBUG_KEY = "ultra_isdebug";
    public static final String LOGIN_DEBUG_CONFIG = "login_debug_config";
    public static final String LOGIN_IS_HIDE_PIC_CODE = "login_is_hide_pic_code";

    private UserInfoViewModel userInfoViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sealtalk_debug_test);
        initView();
        initViewModel();
    }

    /** 初始化布局 */
    private void initView() {
        getTitleBar().setTitle(R.string.seal_main_mine_about);

        groupReadReceiptV2Siv = findViewById(R.id.siv_grr_v2_sender_test);
        pushLanguageSiv = findViewById(R.id.siv_push_language);
        pushConfigModeSiv = findViewById(R.id.siv_push_config);
        chatRoomSiv = findViewById(R.id.siv_chatroom);
        pushConfigModeSiv.setOnClickListener(this);
        pushLanguageSiv.setOnClickListener(this);
        chatRoomSiv.setOnClickListener(this);

        pushDiscussion = findViewById(R.id.siv_discussion);
        pushDiscussion.setOnClickListener(this);

        messageExpansion = findViewById(R.id.siv_message_expansion);
        messageExpansion.setOnClickListener(this);

        ultraGroup = findViewById(R.id.siv_ultra_group);
        ultraGroup.setOnClickListener(this);

        ultraGroupUnreadMentionDigests =
                findViewById(R.id.siv_ultra_group_unread_mention_msg_digests);
        ultraGroupUnreadMentionDigests.setOnClickListener(this);

        ultraGroupConversationByTargetIds =
                findViewById(R.id.siv_ultra_group_conversation_by_target_id);
        ultraGroupConversationByTargetIds.setOnClickListener(this);

        shortage = findViewById(R.id.siv_shortage);
        shortage.setOnClickListener(this);

        shortageDialog = findViewById(R.id.siv_shortage_dialog);
        shortageDialog.setOnClickListener(this);

        isDelRemoteMsgDialog = findViewById(R.id.siv_delete_remote_dialog);
        isDelRemoteMsgDialog.setOnClickListener(this);

        isSoundDialog = findViewById(R.id.siv_sound_dialog);
        isSoundDialog.setOnClickListener(this);

        isVibrateDialog = findViewById(R.id.siv_vibrate_dialog);
        isVibrateDialog.setOnClickListener(this);

        tag = findViewById(R.id.siv_tag);
        tag.setOnClickListener(this);

        messageDelivery = findViewById(R.id.siv_delivery);
        messageDelivery.setOnClickListener(this);

        deviceInfo = findViewById(R.id.siv_umeng_info);
        deviceInfo.setOnClickListener(this);

        referMsgTest = findViewById(R.id.siv_reference_msg_test);
        referMsgTest.setOnClickListener(this);

        groupReadReceiptV2Siv.setOnClickListener(this);
        findViewById(R.id.siv_block_msg_test).setOnClickListener(this);

        permissionlistener = findViewById(R.id.siv_permission_listener);
        SharedPreferences permissionConfigSP =
                getSharedPreferences(SP_PERMISSION_NAME, MODE_PRIVATE);
        permissionlistener.setSwitchCheckListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        permissionConfigSP.edit().putBoolean(SP_IS_SHOW, isChecked).commit();
                    }
                });
        ultraDebug = findViewById(R.id.siv_ultra_debug);
        ultraDebug.setChecked(
                getSharedPreferences(SealTalkDebugTestActivity.ULTRA_DEBUG_CONFIG, MODE_PRIVATE)
                        .getBoolean(ULTRA_IS_DEBUG_KEY, false));
        ultraDebug.setSwitchCheckListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        DialogWithYesOrNoUtils.getInstance()
                                .showDialog(
                                        SealTalkDebugTestActivity.this,
                                        getString(R.string.setting_close_ultra_group_debug_promt),
                                        new DialogWithYesOrNoUtils.DialogCallBack() {
                                            @Override
                                            public void executeEvent() {
                                                getSharedPreferences(
                                                                ULTRA_DEBUG_CONFIG, MODE_PRIVATE)
                                                        .edit()
                                                        .putBoolean(ULTRA_IS_DEBUG_KEY, isChecked)
                                                        .commit();
                                                exit(0);
                                            }

                                            @Override
                                            public void executeEditEvent(String editText) {}

                                            @Override
                                            public void updatePassword(
                                                    String oldPassword, String newPassword) {}
                                        });
                    }
                });
        isHideLoginPagePicCode = findViewById(R.id.siv_login_ignore_pic_code);
        quickIntercept = findViewById(R.id.quick_intercept);
        quickIntercept.setSwitchCheckListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            RongIM.setConversationClickListener(
                                    new ConversationClickListener() {
                                        @Override
                                        public boolean onUserPortraitClick(
                                                Context context,
                                                Conversation.ConversationType conversationType,
                                                UserInfo user,
                                                String targetId) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onUserPortraitLongClick(
                                                Context context,
                                                Conversation.ConversationType conversationType,
                                                UserInfo user,
                                                String targetId) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onMessageClick(
                                                Context context, View view, Message message) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onMessageLongClick(
                                                Context context, View view, Message message) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onMessageLinkClick(
                                                Context context, String link, Message message) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onReadReceiptStateClick(
                                                Context context, Message message) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onQuickReplyClick(Context context) {
                                            Toast.makeText(
                                                            context,
                                                            "拦截了常用语点击事件",
                                                            Toast.LENGTH_SHORT)
                                                    .show();
                                            return true;
                                        }
                                    });
                        } else {
                            RongIM.setConversationClickListener(
                                    new ConversationClickListener() {
                                        @Override
                                        public boolean onUserPortraitClick(
                                                Context context,
                                                Conversation.ConversationType conversationType,
                                                UserInfo user,
                                                String targetId) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onUserPortraitLongClick(
                                                Context context,
                                                Conversation.ConversationType conversationType,
                                                UserInfo user,
                                                String targetId) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onMessageClick(
                                                Context context, View view, Message message) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onMessageLongClick(
                                                Context context, View view, Message message) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onMessageLinkClick(
                                                Context context, String link, Message message) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onReadReceiptStateClick(
                                                Context context, Message message) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onQuickReplyClick(Context context) {
                                            return false;
                                        }
                                    });
                        }
                    }
                });
        isHideLoginPagePicCode.setChecked(
                getSharedPreferences(SealTalkDebugTestActivity.LOGIN_DEBUG_CONFIG, MODE_PRIVATE)
                        .getBoolean(LOGIN_IS_HIDE_PIC_CODE, false));
        isHideLoginPagePicCode.setSwitchCheckListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        getSharedPreferences(LOGIN_DEBUG_CONFIG, MODE_PRIVATE)
                                .edit()
                                .putBoolean(LOGIN_IS_HIDE_PIC_CODE, isChecked)
                                .commit();
                    }
                });
        createNotificationChannel = findViewById(R.id.siv_create_notification_channel);
        createNotificationChannel.setOnClickListener(this);

        bindChatRTCRoom = findViewById(R.id.siv_bind_chat_rtc_room);
        bindChatRTCRoom.setOnClickListener(this);
    }

    private void initViewModel() {
        userInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.siv_push_config:
                toPushConfig();
                break;
            case R.id.siv_discussion:
                toDiscussion();
                break;
            case R.id.siv_push_language:
                toInputTitleDialog();
                break;
            case R.id.siv_chatroom:
                toChatRoom();
                break;
            case R.id.siv_message_expansion:
                toMessageExpansion();
                break;
            case R.id.siv_ultra_group:
                toUltraGroup();
                break;
            case R.id.siv_ultra_group_unread_mention_msg_digests:
                toUltraGroupUnreadMentionDigest();
                break;
            case R.id.siv_ultra_group_conversation_by_target_id:
                toUltraGroupConversationListByTargetIds();
                break;
            case R.id.siv_tag:
                toTagTest();
                break;
            case R.id.siv_delivery:
                toMessageDelivery();
                break;
            case R.id.siv_shortage:
                toShortage();
                break;

            case R.id.siv_shortage_dialog:
                toShortageDialog();
                break;
            case R.id.siv_delete_remote_dialog:
                toDelRemoteMessage();
                break;
            case R.id.siv_sound_dialog:
                toSound();
                break;
            case R.id.siv_vibrate_dialog:
                toVibrate();
                break;
            case R.id.siv_grr_v2_sender_test:
                toGroupReadReceiptTest(1);
                break;
            case R.id.siv_umeng_info:
                toDeviceInfo();
                break;
            case R.id.siv_reference_msg_test:
                toReferMsgTest();
                break;
            case R.id.siv_block_msg_test:
                toReferMsgTest();
                break;
            case R.id.siv_create_notification_channel:
                showCreateNotificationDialog();
                break;
            case R.id.siv_bind_chat_rtc_room:
                bindChatRTCRoom();
                break;
            case R.id.quick_intercept:
                bindChatRTCRoom();
                break;
            default:
                // Do nothing
                break;
        }
    }

    private void toVibrate() {
        final EditText editText = new EditText(this);
        editText.setHint("是否震动：0 不震动 1 震动");
        editText.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置是否震动")
                .setView(editText)
                .setPositiveButton(
                        "确定",
                        (dialog, which) -> {
                            if (editText.getText() == null) {
                                return;
                            }
                            String dialogText = editText.getText().toString();
                            if ("0".equals(dialogText)) {
                                RongConfigCenter.featureConfig().setVibrateInForeground(false);
                            } else if ("1".equals(dialogText)) {
                                RongConfigCenter.featureConfig().setVibrateInForeground(true);
                            }
                        })
                .show();
    }

    private void toSound() {
        final EditText editText = new EditText(this);
        editText.setHint("是否响铃：0 不响铃 1 响铃");
        editText.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置是否响铃")
                .setView(editText)
                .setPositiveButton(
                        "确定",
                        (dialog, which) -> {
                            if (editText.getText() == null) {
                                return;
                            }
                            String dialogText = editText.getText().toString();
                            if ("0".equals(dialogText)) {
                                RongConfigCenter.featureConfig().setSoundInForeground(false);
                            } else if ("1".equals(dialogText)) {
                                RongConfigCenter.featureConfig().setSoundInForeground(false);
                            }
                        })
                .show();
    }

    private void toDelRemoteMessage() {
        final EditText editText = new EditText(this);
        editText.setHint("是否删除：0 不删除 1 删除");
        editText.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置是否删除远端消息")
                .setView(editText)
                .setPositiveButton(
                        "确定",
                        (dialog, which) -> {
                            if (editText.getText() == null) {
                                return;
                            }
                            String dialogText = editText.getText().toString();
                            if ("0".equals(dialogText)) {
                                RongConfigCenter.conversationConfig()
                                        .setNeedDeleteRemoteMessage(false);
                            } else if ("1".equals(dialogText)) {
                                RongConfigCenter.conversationConfig()
                                        .setNeedDeleteRemoteMessage(true);
                            }
                        })
                .show();
    }

    private void toShortageDialog() {
        showShortageDialog();
    }

    private void showShortageDialog() {
        final EditText editText = new EditText(this);
        editText.setHint("请输入 类型：0 always 1 ask 2 onlySuc");
        editText.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置消息断档类型")
                .setView(editText)
                .setPositiveButton(
                        "确定",
                        (dialog, which) -> {
                            if (editText.getText() == null) {
                                return;
                            }
                            String dialogText = editText.getText().toString();
                            if ("0".equals(dialogText)) {
                                RongConfigCenter.conversationConfig()
                                        .setConversationLoadMessageType(
                                                IRongCoreEnum.ConversationLoadMessageType.ALWAYS);
                            } else if ("1".equals(dialogText)) {
                                RongConfigCenter.conversationConfig()
                                        .setConversationLoadMessageType(
                                                IRongCoreEnum.ConversationLoadMessageType.ASK);
                            } else if ("2".equals(dialogText)) {
                                RongConfigCenter.conversationConfig()
                                        .setConversationLoadMessageType(
                                                IRongCoreEnum.ConversationLoadMessageType
                                                        .ONLY_SUCCESS);
                            }
                        })
                .show();
    }

    private void showCreateNotificationDialog() {
        final EditText channelIdEditText = new EditText(this);
        channelIdEditText.setHint("请输入 channelId");
        channelIdEditText.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("创建推送通道")
                .setView(channelIdEditText)
                .setPositiveButton(
                        "确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String channelId = channelIdEditText.getText().toString();
                                if (!TextUtils.isEmpty(channelId)
                                        && android.os.Build.VERSION.SDK_INT
                                                >= android.os.Build.VERSION_CODES.O) {
                                    NotificationManager mNotificationManager =
                                            (NotificationManager)
                                                    getSystemService(NOTIFICATION_SERVICE);
                                    Uri uri =
                                            RingtoneManager.getDefaultUri(
                                                    RingtoneManager.TYPE_RINGTONE);
                                    NotificationChannel notificationChannel =
                                            new NotificationChannel(
                                                    channelId,
                                                    channelId,
                                                    NotificationManager.IMPORTANCE_HIGH);
                                    notificationChannel.enableLights(false);
                                    notificationChannel.setLightColor(Color.GREEN);
                                    notificationChannel.enableVibration(false);
                                    notificationChannel.setSound(uri, null);
                                    mNotificationManager.createNotificationChannel(
                                            notificationChannel);
                                }
                            }
                        })
                .show();
    }

    private void toUltraGroup() {
        Intent intent = new Intent(this, UltraGroupConversationListActivity.class);
        startActivity(intent);
    }

    private void toUltraGroupUnreadMentionDigest() {
        startActivity(new Intent(this, UltraGroupUnreadMentionDigestsActivity.class));
    }

    private void toUltraGroupConversationListByTargetIds() {
        ConversationListByTargetIdsActivity.start(this, "超级群TargetId会话列表");
    }

    private void toReferMsgTest() {
        Intent intent = new Intent(this, CommonConversationListTestActivity.class);
        startActivity(intent);
    }

    private void toDeviceInfo() {
        Intent intent = new Intent(this, DeviceInfoActivity.class);
        startActivity(intent);
    }

    private void toShortage() {
        Intent intent = new Intent(this, ShortageConversationListActivity.class);
        startActivity(intent);
    }

    private void toGroupReadReceiptTest(int type) {
        Intent intent = new Intent(this, GRRConversationListTestActivity.class);
        startActivity(intent);
    }

    private void toTagTest() {
        Intent intent = new Intent(this, TagTestActivity.class);
        startActivity(intent);
    }

    private void toMessageDelivery() {
        Intent intent = new Intent(this, MsgDeliveryConversationListActivity.class);
        startActivity(intent);
    }

    private void toMessageExpansion() {
        Intent intent = new Intent(this, MsgExpansionConversationListActivity.class);
        startActivity(intent);
    }

    private void toChatRoom() {
        Intent intent = new Intent(this, ChatRoomTestActivity.class);
        startActivity(intent);
    }

    private void toInputTitleDialog() {
        final EditText inputLanguage = new EditText(this);
        inputLanguage.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置推送语言")
                .setView(inputLanguage)
                .setPositiveButton(
                        "确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String languageCode = inputLanguage.getText().toString();
                                RongIMClient.getInstance()
                                        .setPushLanguageCode(
                                                languageCode,
                                                new RongIMClient.OperationCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        ToastUtils.showToast("设置成功");
                                                    }

                                                    @Override
                                                    public void onError(
                                                            RongIMClient.ErrorCode errorCode) {
                                                        ToastUtils.showToast("设置失败");
                                                    }
                                                });
                            }
                        })
                .show();
    }

    private void toPushConfig() {
        Intent intent = new Intent(this, PushConfigActivity.class);
        startActivity(intent);
    }

    private void toDiscussion() {
        Intent intent = new Intent(this, DiscussionActivity.class);
        startActivity(intent);
    }

    private void bindChatRTCRoom() {
        Intent intent = new Intent(this, BindChatRTCRoomActivity.class);
        startActivity(intent);
    }
}
