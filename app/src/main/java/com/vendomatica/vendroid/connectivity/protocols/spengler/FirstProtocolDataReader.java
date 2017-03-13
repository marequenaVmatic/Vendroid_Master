package com.vendomatica.vendroid.connectivity.protocols.spengler;


import android.os.Handler;
import android.os.Message;

import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.IonAuditState;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsBase;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsConstants;

import java.io.IOException;
import java.util.ArrayList;

public class FirstProtocolDataReader  extends ProtocolsBase {

    private IonAuditState stateCallback;


    public FirstProtocolDataReader(final IonAuditState stateCallback, ArrayList<String> fileList){
        this.stateCallback = stateCallback;

        SpenglerInternalStorage.getInstance().fileList = fileList;

        IProtocolsDataManagement initialState = new FirstProtocolInitialState<IProtocolsDataManagement>(this, this);
        IProtocolsDataManagement collectState = new FirstProtocolCollectState<IProtocolsDataManagement>(this, this);
        IProtocolsDataManagement doneState = new FirstProtocolDoneState<IProtocolsDataManagement>(this, this);


        addEdge(initialState, FirstProtocolInitialState.INITIAL_STATE, collectState);

        addEdge(collectState, FirstProtocolCollectState.UPDATE_STATE, collectState );
        addEdge(collectState, FirstProtocolCollectState.DONE, doneState);

        state = initialState;

        SpenglerInternalStorage.getInstance().handler = new Handler(){
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
                    case ProtocolsConstants.MSG_ACTION_AUDIT_DATA_READ:
                        stateCallback.onAuditDataRead(msg.getData());
                }
            }
        };
    }

    @Override
    public boolean startAudit() {
        state.startAudit();
        return true;
    }

    @Override
    public void stopAudit() {
        state.stopAudit();
    }

    @Override
    public void update(int deltaTime){
        try {
            state.update(deltaTime);
        }catch (IOException e){
            e.printStackTrace();
            stopAudit();
        }
    }

}
