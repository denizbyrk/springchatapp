package com.chatapp.chat_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.chatapp.chat_backend.model.User;
import com.chatapp.chat_backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	

	@GetMapping()
	public String getProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User user = userService.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		model.addAttribute("currentUser", user);
		return "profile";
	}

	@GetMapping("/change-password")
	public String showChangePasswordPage() {
		
		return "change-password";
	}

	@PostMapping("/change-password")
	public String processPasswordChange(@AuthenticationPrincipal UserDetails userDetails,
	                                    @RequestParam String currentPassword,
	                                    @RequestParam String newPassword,
	                                    @RequestParam String confirmNewPassword,
	                                    RedirectAttributes redirectAttributes,
	                                    HttpServletRequest request,
	                                    HttpServletResponse response) {

	    User user = userService.findByEmail(userDetails.getUsername())
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
	        redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
	        return "redirect:/profile/change-password";
	    }

	    if (!newPassword.equals(confirmNewPassword)) {
	        redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
	        return "redirect:/profile/change-password";
	    }

	    // Şifre güncelleniyor
	    user.setPassword(passwordEncoder.encode(newPassword));
	    userService.save(user);

	    // Oturumu sonlandır (logout)
	    SecurityContextHolder.clearContext(); // Spring Security oturumunu temizle
	    request.getSession().invalidate();    // HTTP oturumunu sonlandır

	    // Login sayfasına yönlendir (logout mesajıyla birlikte)
	    return "redirect:/login?logout";
	}

	@GetMapping("/delete-account")
    public String showDeleteAccountPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Silme öncesi bilgi göster (isteğe bağlı)
        String deletionInfo = userService.getDeletionInfo(user);
        model.addAttribute("currentUser", user);
        model.addAttribute("deletionInfo", deletionInfo);
        
        return "delete-account";
    }

    @PostMapping("/delete-account")
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam String password,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(password, user.getPassword())) {
                model.addAttribute("error", "Wrong password.");
                model.addAttribute("currentUser", user);
                return "delete-account";
            }
            
            // Kullanıcıyı ve tüm verilerini sil
            userService.delete(user);
            
            // Oturumu sonlandır
            request.getSession().invalidate();
            
            // Başarı mesajı ile yönlendir
            redirectAttributes.addFlashAttribute("message", "Account deleted successfully.");
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("error", "Hesap silinirken hata oluştu: " + e.getMessage());
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("currentUser", user);
            return "delete-account";
        }
    }
}