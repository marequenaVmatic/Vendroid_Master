package com.vendomatica.vendroid.connectivity.protocols.jofemarrd;

import android.os.Handler;
import android.os.Message;

import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.IonAuditState;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsBase;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsConstants;

import java.io.IOException;

public class JofemarRDDataReader extends ProtocolsBase {

    public JofemarRDDataReader(final IonAuditState callback){

        IProtocolsDataManagement collectState = new JofemarRDCollectState<IProtocolsDataManagement>(this, this);

        addEdge(collectState, JofemarRDCollectState.UPDATE_STATE, collectState);

        state = collectState;

        JofemarRDInternalStorage.getInstance().handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (callback == null) return;

                switch(msg.what){
                    case ProtocolsConstants.MSG_ACTION_AUDIT_DONE:
                        callback.onAuditDone(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_DATA:
                        callback.onAuditData(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_ERROR:
                        callback.onAuditError(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_LOG:
                        callback.onAuditLog(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_TIMEOUT:
                        callback.onAuditTimeOut(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_UPDATE:
                        callback.onAuditUpdate(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_DATA_READ:
                        callback.onAuditDataRead(msg.getData());
                        break;
                }
            }
        };
    }

    @Override
    public boolean startAudit() {
        return false;
    }

    @Override
    public void stopAudit() {

    }

    @Override
    public void update(int deltaTime) throws IOException {
        try {
            state.update(deltaTime);
        }catch (IOException e){
            e.printStackTrace();
            stopAudit();
        }
    }
}
