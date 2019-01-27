package com.qianbajin.shortvdhelper;

import java.io.Closeable;
import java.io.IOException;
/**
 * @author wWX407408
 * @des 关流工具
 * @date 2017/8/21  14:12
 */
public class IoUtil {

    /**
     * 关流操作
     *
     * @param closeables 可变参数流
     */
    public static void closeIO(Closeable... closeables) {
        if (closeables == null || closeables.length == 0) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
