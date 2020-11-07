package com.example.guardiancamera_wifi;

public class LazyWebPeerGroups {
    private LazyWebUser[] protectees = {};
    private LazyWebUser[] guardians = {};
    private LazyWebUser[] requests_protectee = {};
    private LazyWebUser[] requests_guardians = {};

    public void setProtectees (LazyWebUser[] protectees_new) {
        protectees = protectees_new;
    }

    public void setGuardians (LazyWebUser[] guardians_new) {
        guardians = guardians_new;
    }

    public void setProtecteeRequests (LazyWebUser[] protecteeRequests) {
        requests_protectee = protecteeRequests;
    }

    public void setGuardianRequests (LazyWebUser[] guardianRequests) {
        requests_guardians = guardianRequests;
    }

    public LazyWebUser[] getProtectees() {
        return protectees;
    }

    public LazyWebUser[] getGuardians() {
        return guardians;
    }

    public LazyWebUser[] getProtecteeRequests() {
        return requests_protectee;
    }

    public LazyWebUser[] getGuardianRequests() {
        return requests_guardians;
    }
}
