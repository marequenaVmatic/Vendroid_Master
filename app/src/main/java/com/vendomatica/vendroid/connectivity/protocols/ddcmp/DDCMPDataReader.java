package com.vendomatica.vendroid.connectivity.protocols.ddcmp;

import android.os.Handler;
import android.os.Message;

import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.IonAuditState;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsBase;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsConstants;

import java.io.IOException;

public class DDCMPDataReader extends ProtocolsBase{

    private IonAuditState stateCallback;

    public DDCMPDataReader(final IonAuditState stateCallback)

    {
        this.stateCallback = stateCallback;


        IProtocolsDataManagement initialState = new DDCMPInitialState<IProtocolsDataManagement>(this, this);
        IProtocolsDataManagement collectState = new DDCMPCollectState<IProtocolsDataManagement>(this, this);
        IProtocolsDataManagement doneState = new DDCMPDoneState<IProtocolsDataManagement>(this, this);


        addEdge(initialState, DDCMPInitialState.INITIAL_STATE, collectState);

        addEdge(collectState, DDCMPCollectState.UPDATE_STATE, collectState);
        addEdge(collectState, DDCMPCollectState.DONE_STATE, doneState);

        state = initialState;

        DDCMPInternalStorage.getInstance().handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (stateCallback == null) return;

                switch(msg.what){
                    case ProtocolsConstants.MSG_ACTION_AUDIT_DONE:
                        stateCallback.onAuditDone(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_DATA:
                        stateCallback.onAuditData(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_ERROR:
                        stateCallback.onAuditError(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_LOG:
                        stateCallback.onAuditLog(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_TIMEOUT:
                        stateCallback.onAuditTimeOut(msg.getData());
                        break;
                    case ProtocolsConstants.MSG_ACTION_AUDIT_UPDATE:
                        stateCallback.onAuditUpdate(msg.getData());
                        break;
                }
            }
        };
    }



    @Override
    public boolean startAudit() {
        return true;
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
