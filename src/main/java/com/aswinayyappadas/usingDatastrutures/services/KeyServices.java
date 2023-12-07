package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.user.UserData;

public class KeyServices implements UserData {

    // Method to store a secret key associated with a user's email
    public void storeSecretKeyByEmail(String email, String secretKeyString) {
        // Set the JWT secret key for the user identified by the email
        userEmailData.get(email).setJwt_secret_key(secretKeyString);
    }

    // Method to retrieve the JWT secret key associated with a user's email
    public String getJwtSecretKeyByEmail(String email) {
        // Retrieve and return the JWT secret key for the user identified by the email
        return userEmailData.get(email).getJwt_secret_key();
    }
}
