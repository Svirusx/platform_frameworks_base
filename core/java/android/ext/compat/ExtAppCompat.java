package android.ext.compat;

import android.annotation.Nullable;
import android.app.ActivityThread;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.ext.settings.app.AswUseHardenedMalloc;
import android.os.Binder;

import com.android.internal.util.GoogleCameraUtils;
import com.android.internal.util.PackageSpec;

/** @hide */
public class ExtAppCompat {

    @Nullable
    public static AppCompatConfig getAppCompatConfig(ApplicationInfo appInfo, int userId) {
        switch (appInfo.packageName) {
            case GoogleCameraUtils.PACKAGE_NAME -> {
                if (validatePackageSpec(GoogleCameraUtils.PACKAGE_SPEC, userId)) {
                    var c = new AppCompatConfig(GoogleCameraUtils.PACKAGE_SPEC);
                    c.incompatibleWith(AswUseHardenedMalloc.class);
                    return c;
                }
            }
        }

        return null;
    }

    private static boolean validatePackageSpec(PackageSpec s, int userId) {
        IPackageManager pm = ActivityThread.getPackageManager();
        PackageInfo pi;

        final long token = Binder.clearCallingIdentity();
        try {
            pi = pm.getPackageInfo(s.packageName, PackageManager.GET_SIGNING_CERTIFICATES, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            Binder.restoreCallingIdentity(token);
        }

        return s.validate(pi);
    }
}
