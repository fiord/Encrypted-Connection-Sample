package dev.fiord.encrypted_communication_sample.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private boolean isDataValid;

    LoginFormState(@Nullable Integer usernameError) {
        this.usernameError = usernameError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}