package com.ramailo.service;

import com.ramailo.auth.Identity;
import com.ramailo.auth.dto.ChangePasswordDTO;
import com.ramailo.auth.dto.ForgotPasswordDTO;
import com.ramailo.auth.dto.ResetPasswordDTO;
import com.ramailo.auth.dto.TokenDTO;

public interface IdentityService {

	Identity fetchById(Integer id);
	
	Identity fetchByEmail(String email);

	Identity fetchByEmailAndPassword(String email, String password);

	void saveRefreshToken(Identity user, TokenDTO tokenDTO);

	void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);

	void sendPasswordResetLink(ForgotPasswordDTO forgotPassword);

	Identity resetPassword(ResetPasswordDTO resetPassword);
}