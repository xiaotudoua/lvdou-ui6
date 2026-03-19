package com.lvdoui6.android.tv.lvdou;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

public class HawkInfo {

    private static final String KEY_READ_MESSAGES = "read_messages";

    // 保存已读消息ID列表
    public static void saveReadMessages(List<String> readMessageIds) {
        Hawk.put(KEY_READ_MESSAGES, readMessageIds);
    }

    // 获取已读消息ID列表
    public static List<String> getReadMessages() {
        return Hawk.get(KEY_READ_MESSAGES, new ArrayList<String>());
    }

    // 检查消息是否已读
    public static boolean isMessageRead(String messageId) {
        List<String> readMessageIds = getReadMessages();
        return readMessageIds.contains(messageId);
    }

    // 标记消息为已读
    public static void markMessageAsRead(String messageId) {
        List<String> readMessageIds = getReadMessages();
        if (!readMessageIds.contains(messageId)) {
            readMessageIds.add(messageId);
            saveReadMessages(readMessageIds);
        }
    }
}
