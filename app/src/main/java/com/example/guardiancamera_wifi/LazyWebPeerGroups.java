package com.example.guardiancamera_wifi;

public class LazyWebPeerGroups {
    private LazyWebUserInfo [] protectees = {};
    private LazyWebUserInfo [] guardians = {};
    private LazyWebUserInfo [] requests_protectee = {};
    private LazyWebUserInfo [] requests_guardians = {};

    public void setProtectees (LazyWebUserInfo [] protectees_new) {
        protectees = protectees_new;
    }

    public void setGuardians (LazyWebUserInfo [] guardians_new) {
        guardians = guardians_new;
    }

    public void setProtecteeRequests (LazyWebUserInfo [] protecteeRequests) {
        requests_protectee = protecteeRequests;
    }

    public void setGuardianRequests (LazyWebUserInfo [] guardianRequests) {
        requests_guardians = guardianRequests;
    }

    public LazyWebUserInfo [] getProtectees() {
        return protectees;
    }

    public LazyWebUserInfo [] getGuardians() {
        return guardians;
    }

    public LazyWebUserInfo [] getProtecteeRequests() {
        return requests_protectee;
    }

    public LazyWebUserInfo [] getGuardianRequests() {
        return requests_guardians;
    }
}
