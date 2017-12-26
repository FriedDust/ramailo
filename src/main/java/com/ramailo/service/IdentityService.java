package com.ramailo.service;

import com.ramailo.dto.auth.ChangePasswordDTO;
import com.ramailo.dto.auth.ForgotPasswordDTO;
import com.ramailo.auth.Identity;
import com.ramailo.dto.auth.ResetPasswordDTO;
import com.ramailo.dto.auth.TokenDTO;

public interface IdentityService {

	Identity fetchByEmail(String email);

	Identity fetchByEmailAndPassword(String email, String password);

	void saveRefreshToken(Identity user, TokenDTO tokenDTO);

	void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);

	void sendPasswordResetLink(ForgotPasswordDTO forgotPassword);

	Identity resetPassword(ResetPasswordDTO resetPassword);
}