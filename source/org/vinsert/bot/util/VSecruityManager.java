package org.vinsert.bot.util;

import java.security.AccessControlContext;
import java.security.Permission;

/**
 * @author iJava
 */
public class VSecruityManager extends SecurityManager {

    @Override
    public void checkPermission(final Permission perm) {
    }

    @Override
    public void checkPermission(final Permission perm, final Object context) {
        if (context instanceof AccessControlContext) {
            ((AccessControlContext) context).checkPermission(perm);
        } else {
            throw new SecurityException();
        }
    }

    @Override
    public void checkLink(final String lib) {
        if (lib.contains("jaclib") && (lib.endsWith(".dll") || lib.endsWith(".dat") || lib.endsWith(".dylib"))) {
            throw new SecurityException("Denied access to " + lib);
        } else {
            checkPermission(new RuntimePermission("loadLibrary." + lib));
        }
    }
}
