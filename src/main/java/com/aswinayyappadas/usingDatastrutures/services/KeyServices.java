package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.user.UserData;

public class KeyServices implements UserData {
    public void storeSecretKeyByEmail(String email, String secretKeyString) {
        userEmailData.get(email).setJwt_secret_key(secretKeyString);
    }
    public String getJwtSecretKeyByEmail(String email) {
        return userEmailData.get(email).getJwt_secret_key();
    }
}
