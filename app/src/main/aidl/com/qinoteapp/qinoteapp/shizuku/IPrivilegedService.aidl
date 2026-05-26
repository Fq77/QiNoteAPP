package com.qinoteapp.qinoteapp.shizuku;
import com.qinoteapp.qinoteapp.shizuku.IPrivilegedLogCallback;
interface IPrivilegedService {
    void setLogCallback(IPrivilegedLogCallback callback);
    boolean setPackageNetworkingEnabled(int uid, boolean enabled);
}
