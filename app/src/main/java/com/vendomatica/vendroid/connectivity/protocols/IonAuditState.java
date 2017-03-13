package com.vendomatica.vendroid.connectivity.protocols;

import android.os.Bundle;

public interface IonAuditState {
    void onAuditDone(Bundle b);/*Call when reading over*/
    void onAuditError(Bundle b);/*Calls in case of any error*/
    void onAuditTimeOut(Bundle b);/*Calls when TimeOut occure*/
    void onAuditUpdate(Bundle b);
    void onAuditLog(Bundle b);/*Calls for log purpose*/
    void onAuditData(Bundle b);/*Use to transfer data between threads*/
    void onAuditDataRead(Bundle b);/*Just contains information about number read bytes*/
}
