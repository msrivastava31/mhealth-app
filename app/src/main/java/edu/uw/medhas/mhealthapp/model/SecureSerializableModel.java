package edu.uw.medhas.mhealthapp.model;

import edu.uw.medhas.mhealthsecurityframework.storage.SecureSerializable;

/**
 * Created by medhas on 5/29/18.
 */

public class SecureSerializableModel implements SecureSerializable {
    private final String mData;

    public SecureSerializableModel(String data) {
        mData = data;
    }

    public String getData() {
        return mData;
    }
}
