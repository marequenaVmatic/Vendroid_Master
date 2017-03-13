package com.vendomatica.vendroid.connectivity;

import java.util.List;

public interface IAuditManager {
    void onAuditStart();
    void onError(String msg);
    void onSuccess(List<String> filesList);
    void onAuditLog(String msg);
    void onAuditDataTransferedSize(Integer data);
}
