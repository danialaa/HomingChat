package com.example.homing.models.helpers;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

public class CognitoHelper {
    private final String userPoolID = "us-east-1_jAk7Hcl3R";
    private final String clientID = "2uo5cgvjdpbelt1r8vji942sb3";
    private final String clientSecret = "";
    private final Regions cognitoRegion = Regions.US_EAST_1;
    private Context context;
    private static volatile CognitoHelper INSTANCE = null;

    public static synchronized CognitoHelper getINSTANCE(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CognitoHelper(context);
        }

        return INSTANCE;
    }

    private CognitoHelper(Context context) {
        this.context = context;
    }

    public String getUserPoolID() {
        return userPoolID;
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public Regions getCognitoRegion() {
        return cognitoRegion;
    }

    public CognitoUserPool getUserPool() {
        return new CognitoUserPool(context, userPoolID, clientID,
                null, cognitoRegion);
    }
}
