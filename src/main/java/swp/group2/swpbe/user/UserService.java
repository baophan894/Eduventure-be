package swp.group2.swpbe.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import swp.group2.swpbe.JwtTokenProvider;
import swp.group2.swpbe.constant.ReviewState;
import swp.group2.swpbe.constant.UserRole;
import swp.group2.swpbe.document.DocumentRepository;
import swp.group2.swpbe.document.entities.Document;
import swp.group2.swpbe.document.entities.DocumentReview;
import swp.group2.swpbe.exception.ApiRequestException;
import swp.group2.swpbe.flashcard.FlashcardRepository;
import swp.group2.swpbe.flashcard.entities.Flashcard;
import swp.group2.swpbe.flashcard.entities.FlashcardReview;
import swp.group2.swpbe.user.dto.ChangePasswordDTO;
import swp.group2.swpbe.user.dto.LoginDTO;
import swp.group2.swpbe.user.dto.LoginSocialDTO;
import swp.group2.swpbe.user.dto.ResetPasswordDTO;
import swp.group2.swpbe.user.dto.SignupDTO;
import swp.group2.swpbe.user.dto.UpdatePasswordDTO;
import swp.group2.swpbe.user.dto.UpdateProfileDTO;
import swp.group2.swpbe.user.entities.User;
import swp.group2.swpbe.user.entities.Wallet;
import swp.group2.swpbe.user.entities.Withdraw;
import swp.group2.swpbe.user.response.UserProfileResponse;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    FlashcardRepository flashcardRepository;
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    WithdrawRepository withdrawRepository;
    int strength = 10;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(strength, new SecureRandom());

    @Value("${allow.origin}")
    private String allowedOrigins;

    public User signup(SignupDTO user) {
        String full_name = user.getFull_name();
        String email = user.getEmail().toLowerCase();
        String password = bCryptPasswordEncoder.encode(user.getPassword());
        if (userRepository.findByEmail(email) != null) {
            throw new ApiRequestException("Email already exist", HttpStatus.BAD_REQUEST);
        }
        try {
            String htmlContent = this.getVerifyMailTemplate("Tap the button below to confirm your email address",
                    "Verify", email);
            this.sendMail(email, "Verify email", htmlContent);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to send mail", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User newUser = userRepository.save(new User(full_name, email, password,
                "http://res.cloudinary.com/dswewjrly/image/upload/v1715831315/wmndhsmpxuihewekekzy.jpg", 0, null));
        newUser.setPassword("");
        return newUser;
    }

    public void reverify(String email) {
        try {
            String htmlContent = this.getVerifyMailTemplate("Tap the button below to confirm your email address",
                    "Verify", email);
            this.sendMail(email, "Verify email", htmlContent);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to send mail", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<Withdraw> listWithdraws(String userId) {
        return withdrawRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void sendMail(
            String email, String subject, String html) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("huyhkds170542@fpt.edu.vn");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(html, true);

        emailSender.send(message);

    }

    public User updateVerifyEmail(String token) {
        String email = "";
        try {
            email = jwtTokenProvider.verifyToken(token);
        } catch (Exception e) {
            throw new ApiRequestException("Invalid token", HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByEmailAndSid(email, null);
        user.setState(1);
        user.setUpdatedAt(new Date());
        return userRepository.save(user);
    }

    public void resetPassword(ResetPasswordDTO body) {
        String email = body.getEmail().toLowerCase();
        String html = this.getResetPasswordMailTemplate("Click here to reset password", "Reset password", email);
        try {
            this.sendMail(email, "Reset password", html);
        } catch (Exception e) {
            throw new ApiRequestException("Can't send email", HttpStatus.BAD_REQUEST);
        }
    }

    public void changePassword(ChangePasswordDTO body) {
        String email = "";
        try {
            email = jwtTokenProvider.verifyToken(body.getToken());
        } catch (Exception e) {
            throw new ApiRequestException("Invalid token!", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmailAndSid(email, null);
        if (user == null) {
            throw new ApiRequestException("User not found!", HttpStatus.BAD_REQUEST);
        }
        user.setUpdatedAt(new Date());
        user.setPassword(bCryptPasswordEncoder.encode(body.getNewPassword()));
        userRepository.save(user);

    }

    public User saveSocialUser(LoginSocialDTO user) {
        User userExist = userRepository.findByEmail(user.getEmail());
        if (userExist != null) {
            if (userExist.getState() == -1) {
                throw new ApiRequestException("Your account is blocked", HttpStatus.BAD_REQUEST);
            }
            return userExist;
        }
        userRepository.save(
                new User(user.getName(), user.getEmail().toLowerCase(), null, user.getPicture(), 1, user.getS_id()));
        User createdUser = userRepository.findBySid(user.getS_id());
        return createdUser;
    }

    public User login(LoginDTO body) {
        String email = body.getEmail().toLowerCase();
        User user = userRepository.findByEmailAndSid(email, null);
        if (user == null) {
            throw new ApiRequestException("Email not found", HttpStatus.BAD_REQUEST);
        }
        if (user.getState() == 0) {
            throw new ApiRequestException("not_verify_yet", HttpStatus.BAD_REQUEST);
        }
        if (user.getState() == -1) {
            throw new ApiRequestException("Your account is blocked", HttpStatus.BAD_REQUEST);
        }
        String password = body.getPassword();
        boolean isCorrectPassword = bCryptPasswordEncoder.matches(password, user.getPassword());
        if (isCorrectPassword == false) {
            throw new ApiRequestException("Wrong password", HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    public int getUserLegit(String userId) {
        List<Flashcard> flashcards = flashcardRepository.findByUserId(userId);
        List<Document> documents = documentRepository.findByUserId(userId);
        int totalHelpful = 0;
        int totalUnHelpful = 0;
        for (Flashcard flashcard : flashcards) {
            for (FlashcardReview review : flashcard.getReviews()) {
                if (review.getState().equals(ReviewState.helpful)) {
                    totalHelpful++;
                } else
                    totalUnHelpful++;

            }

        }
        for (Document document : documents) {
            for (DocumentReview review : document.getReviews()) {
                if (review.getState().equals(ReviewState.helpful)) {
                    totalHelpful++;
                } else
                    totalUnHelpful++;

            }

        }
        return totalHelpful - totalUnHelpful;
    }

    public User getUserProfile(String id) {
        User user = userRepository.findById(Integer.parseInt(id));
        Wallet userWallet = walletRepository.findByUserId(user.getId());
        UserProfileResponse response = new UserProfileResponse(user.getFullName(), user.getEmail(), "",
                user.getAvatarUrl(), user.getState(), "", this.getUserLegit(id));
        response.setAbout(user.getAbout());
        response.setDob(user.getDob());
        response.setGender(user.getGender());
        response.setId(user.getId());
        if (userWallet != null)
            response.setBalance(userWallet.getBalance());

        return response;

    }

    public void updatePassword(UpdatePasswordDTO body, String userId) {
        String newPassword = body.getNewPassword();
        String oldPassword = body.getOldPassword();
        User user = userRepository.findById(Integer.parseInt(userId));
        if (user == null) {
            throw new ApiRequestException("User not found", HttpStatus.BAD_REQUEST);
        }
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ApiRequestException("old password wrong", HttpStatus.BAD_REQUEST);
        }
        user.setUpdatedAt(new Date());
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);

    }

    public void updateAvatar(String url, String userId) {
        User user = userRepository.findById(Integer.parseInt(userId));
        user.setAvatarUrl(url);
        user.setUpdatedAt(new Date());
        userRepository.save(user);
    }

    public void updateProfile(UpdateProfileDTO body, String userId) {
        String fullName = body.getFullName();
        String about = body.getAbout();
        Date dob = body.getDob();
        String gender = body.getGender();
        User user = userRepository.findById(Integer.parseInt(userId));
        user.setFullName(fullName);
        user.setAbout(about);
        user.setDob(dob);
        user.setGender(gender);
        userRepository.save(user);

    }

    public List<Withdraw> listAllWithdraws() {
        return withdrawRepository.findAll();
    }

    public List<User> getAllExpert() {
        return userRepository.findByRole(UserRole.EXPERT);
    }

    public List<User> getAllUserRole() {
        return userRepository.findByRole(UserRole.USER);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public void updateUserState(int userId, int userState) {
        User user = userRepository.findById(userId);
        user.setState(userState);
        userRepository.save(user);
    }

    public String getResetPasswordMailTemplate(String message, String buttonTitle, String email) {
        String token;
        try {
            token = URLEncoder.encode(jwtTokenProvider.generateVerifyToken(email), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new ApiRequestException("Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return " <!DOCTYPE html>\n" + //
                "<html>\n" + //
                "<head>\n" + //
                "\n" + //
                "  <meta charset=\"utf-8\">\n" + //
                "  <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">\n" + //
                "  <title>Email Confirmation</title>\n" + //
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" + //
                "  <style type=\"text/css\">\n" + //
                "  /**\n" + //
                "   * Google webfonts. Recommended to include the .woff version for cross-client compatibility.\n" + //
                "   */\n" + //
                "  @media screen {\n" + //
                "    @font-face {\n" + //
                "      font-family: 'Source Sans Pro';\n" + //
                "      font-style: normal;\n" + //
                "      font-weight: 400;\n" + //
                "      src: local('Source Sans Pro Regular'), local('SourceSansPro-Regular'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format('woff');\n"
                + //
                "    }\n" + //
                "    @font-face {\n" + //
                "      font-family: 'Source Sans Pro';\n" + //
                "      font-style: normal;\n" + //
                "      font-weight: 700;\n" + //
                "      src: local('Source Sans Pro Bold'), local('SourceSansPro-Bold'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format('woff');\n"
                + //
                "    }\n" + //
                "  }\n" + //
                "  /**\n" + //
                "   * Avoid browser level font resizing.\n" + //
                "   * 1. Windows Mobile\n" + //
                "   * 2. iOS / OSX\n" + //
                "   */\n" + //
                "  body,\n" + //
                "  table,\n" + //
                "  td,\n" + //
                "  a {\n" + //
                "    -ms-text-size-adjust: 100%; /* 1 */\n" + //
                "    -webkit-text-size-adjust: 100%; /* 2 */\n" + //
                "  }\n" + //
                "  /**\n" + //
                "   * Remove extra space added to tables and cells in Outlook.\n" + //
                "   */\n" + //
                "  table,\n" + //
                "  td {\n" + //
                "    mso-table-rspace: 0pt;\n" + //
                "    mso-table-lspace: 0pt;\n" + //
                "  }\n" + //
                "  /**\n" + //
                "   * Better fluid images in Internet Explorer.\n" + //
                "   */\n" + //
                "  img {\n" + //
                "    -ms-interpolation-mode: bicubic;\n" + //
                "  }\n" + //
                "  /**\n" + //
                "   * Remove blue links for iOS devices.\n" + //
                "   */\n" + //
                "  a[x-apple-data-detectors] {\n" + //
                "    font-family: inherit !important;\n" + //
                "    font-size: inherit !important;\n" + //
                "    font-weight: inherit !important;\n" + //
                "    line-height: inherit !important;\n" + //
                "    color: inherit !important;\n" + //
                "    text-decoration: none !important;\n" + //
                "  }\n" + //
                "  /**\n" + //
                "   * Fix centering issues in Android 4.4.\n" + //
                "   */\n" + //
                "  div[style*=\"margin: 16px 0;\"] {\n" + //
                "    margin: 0 !important;\n" + //
                "  }\n" + //
                "  body {\n" + //
                "    width: 100% !important;\n" + //
                "    height: 100% !important;\n" + //
                "    padding: 0 !important;\n" + //
                "    margin: 0 !important;\n" + //
                "  }\n" + //
                "  /**\n" + //
                "   * Collapse table borders to avoid space between cells.\n" + //
                "   */\n" + //
                "  table {\n" + //
                "    border-collapse: collapse !important;\n" + //
                "  }\n" + //
                "  a {\n" + //
                "    color: #1a82e2;\n" + //
                "  }\n" + //
                "  img {\n" + //
                "    height: auto;\n" + //
                "    line-height: 100%;\n" + //
                "    text-decoration: none;\n" + //
                "    border: 0;\n" + //
                "    outline: none;\n" + //
                "  }\n" + //
                "  </style>\n" + //
                "\n" + //
                "</head>\n" + //
                "<body style=\"background-color: #e9ecef;\">\n" + //
                "\n" + //
                "  <!-- start preheader -->\n" + //
                "  <div class=\"preheader\" style=\"display: none; max-width: 0; max-height: 0; overflow: hidden; font-size: 1px; line-height: 1px; color: #fff; opacity: 0;\">\n"
                + //
                "    A preheader is the short summary text that follows the subject line when an email is viewed in the inbox.\n"
                + //
                "  </div>\n" + //
                "  <!-- end preheader -->\n" + //
                "\n" + //
                "  <!-- start body -->\n" + //
                "  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" + //
                "\n" + //
                "    <!-- start logo -->\n" + //
                "    <tr>\n" + //
                "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" + //
                "        <tr>\n" + //
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" + //
                "        <![endif]-->\n" + //
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n"
                + //
                "          <tr>\n" + //
                "            <td align=\"center\" valign=\"top\" style=\"padding: 36px 24px;\">\n" + //
                "              <a href=\"https://www.blogdesire.com\" target=\"_blank\" style=\"display: inline-block;\">\n"
                + //
                "                <img src=\"https://www.blogdesire.com/wp-content/uploads/2019/07/blogdesire-1.png\" alt=\"Logo\" border=\"0\" width=\"48\" style=\"display: block; width: 48px; max-width: 48px; min-width: 48px;\">\n"
                + //
                "              </a>\n" + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "        </table>\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        </td>\n" + //
                "        </tr>\n" + //
                "        </table>\n" + //
                "        <![endif]-->\n" + //
                "      </td>\n" + //
                "    </tr>\n" + //
                "    <!-- end logo -->\n" + //
                "\n" + //
                "    <!-- start hero -->\n" + //
                "    <tr>\n" + //
                "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" + //
                "        <tr>\n" + //
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" + //
                "        <![endif]-->\n" + //
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n"
                + //
                "          <tr>\n" + //
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;\">\n"
                + //
                "              <h1 style=\"margin: 0; font-size: 32px; font-weight: 700; letter-spacing: -1px; line-height: 48px;\">Confirm Your Email Address</h1>\n"
                + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "        </table>\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        </td>\n" + //
                "        </tr>\n" + //
                "        </table>\n" + //
                "        <![endif]-->\n" + //
                "      </td>\n" + //
                "    </tr>\n" + //
                "    <!-- end hero -->\n" + //
                "\n" + //
                "    <!-- start copy block -->\n" + //
                "    <tr>\n" + //
                "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" + //
                "        <tr>\n" + //
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" + //
                "        <![endif]-->\n" + //
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n"
                + //
                "\n" + //
                "          <!-- start copy -->\n" + //
                "          <tr>\n" + //
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n"
                + //
                "              <p style=\"margin: 0;\">" + message + "</p>\n" + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "          <!-- end copy -->\n" + //
                "\n" + //
                "          <!-- start button -->\n" + //
                "          <tr>\n" + //
                "            <td align=\"left\" bgcolor=\"#ffffff\">\n" + //
                "              <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" + //
                "                <tr>\n" + //
                "                  <td align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 12px;\">\n" + //
                "                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" + //
                "                      <tr>\n" + //
                "                        <td align=\"center\" bgcolor=\"#1a82e2\" style=\"border-radius: 6px;\">\n" + //
                "                          <a href=" + allowedOrigins + "/change-password?token=" + token
                + "  target=\"_blank\" style=\"display: inline-block; padding: 16px 36px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; color: #ffffff; text-decoration: none; border-radius: 6px;\">"
                + buttonTitle + "</a>\n" + //
                "                        </td>\n" + //
                "                      </tr>\n" + //
                "                    </table>\n" + //
                "                  </td>\n" + //
                "                </tr>\n" + //
                "              </table>\n" + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "          <!-- end button -->\n" + //
                "\n" + //
                "          <!-- start copy -->\n" + //
                "          <tr>\n" + //
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n"
                + //
                "              <p style=\"margin: 0;\">This verify is expired in 5 minutes. If you can't verify please login and request again!</p>\n"
                + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "          <!-- end copy -->\n" + //
                "\n" + //
                "          <!-- start copy -->\n" + //
                "          <tr>\n" + //
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px; border-bottom: 3px solid #d4dadf\">\n"
                + //
                "              <p style=\"margin: 0;\">Cheers,<br> Paste</p>\n" + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "          <!-- end copy -->\n" + //
                "\n" + //
                "        </table>\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        </td>\n" + //
                "        </tr>\n" + //
                "        </table>\n" + //
                "        <![endif]-->\n" + //
                "      </td>\n" + //
                "    </tr>\n" + //
                "    <!-- end copy block -->\n" + //
                "\n" + //
                "    <!-- start footer -->\n" + //
                "    <tr>\n" + //
                "      <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 24px;\">\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" + //
                "        <tr>\n" + //
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" + //
                "        <![endif]-->\n" + //
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n"
                + //
                "\n" + //
                "          <!-- start permission -->\n" + //
                "          <tr>\n" + //
                "            <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666;\">\n"
                + //
                "              <p style=\"margin: 0;\">You received this email because we received a request at FU records need. We need to verify your email. If you didn't request you can safely delete this email.</p>\n"
                + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "          <!-- end permission -->\n" + //
                "\n" + //
                "          <!-- start unsubscribe -->\n" + //
                "          <!-- end unsubscribe -->\n" + //
                "\n" + //
                "        </table>\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        </td>\n" + //
                "        </tr>\n" + //
                "        </table>\n" + //
                "        <![endif]-->\n" + //
                "      </td>\n" + //
                "    </tr>\n" + //
                "    <!-- end footer -->\n" + //
                "\n" + //
                "  </table>\n" + //
                "  <!-- end body -->\n" + //
                "\n" + //
                "</body>\n" + //
                "</html>";
    }

    public String getVerifyMailTemplate(String message, String buttonTitle, String subject) {
        String token;
        try {
            token = URLEncoder.encode(jwtTokenProvider.generateVerifyToken(subject), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new ApiRequestException("Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return " <!DOCTYPE html>\n" + //
                "<html>\n" + //
                "<head>\n" + //
                "\n" + //
                "  <meta charset=\"utf-8\">\n" + //
                "  <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">\n" + //
                "  <title>Email Confirmation</title>\n" + //
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" + //
                "  <style type=\"text/css\">\n" + //
                "  /**\n" + //
                "   * Google webfonts. Recommended to include the .woff version for cross-client compatibility.\n" + //
                "   */\n" + //
                "  @media screen {\n" + //
                "    @font-face {\n" + //
                "      font-family: 'Source Sans Pro';\n" + //
                "      font-style: normal;\n" + //
                "      font-weight: 400;\n" + //
                "      src: local('Source Sans Pro Regular'), local('SourceSansPro-Regular'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format('woff');\n"
                + //
                "    }\n" + //
                "    @font-face {\n" + //
                "      font-family: 'Source Sans Pro';\n" + //
                "      font-style: normal;\n" + //
                "      font-weight: 700;\n" + //
                "      src: local('Source Sans Pro Bold'), local('SourceSansPro-Bold'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format('woff');\n"
                + //
                "    }\n" + //
                "  }\n" + //
                "  /**\n" + //
                "   * Avoid browser level font resizing.\n" + //
                "   * 1. Windows Mobile\n" + //
                "   * 2. iOS / OSX\n" + //
                "   */\n" + //
                "  body,\n" + //
                "  table,\n" + //
                "  td,\n" + //
                "  a {\n" + //
                "    -ms-text-size-adjust: 100%; /* 1 */\n" + //
                "    -webkit-text-size-adjust: 100%; /* 2 */\n" + //
                "  }\n" + //
                "  /**\n" + //
                "   * Remove extra space added to tables and cells in Outlook.\n" + //
                "   */\n" + //
                "  table,\n" + //
                "  td {\n" + //
                "    mso-table-rspace: 0pt;\n" + //
                "    mso-table-lspace: 0pt;\n" + //
                "  }\n" + //
                "  /**\n" + //
                "   * Better fluid images in Internet Explorer.\n" + //
                "   */\n" + //
                "  img {\n" + //
                "    -ms-interpolation-mode: bicubic;\n" + //
                "  }\n" + //
                "  /**\n" + //
                "   * Remove blue links for iOS devices.\n" + //
                "   */\n" + //
                "  a[x-apple-data-detectors] {\n" + //
                "    font-family: inherit !important;\n" + //
                "    font-size: inherit !important;\n" + //
                "    font-weight: inherit !important;\n" + //
                "    line-height: inherit !important;\n" + //
                "    color: inherit !important;\n" + //
                "    text-decoration: none !important;\n" + //
                "  }\n" + //
                "  /**\n" + //
                "   * Fix centering issues in Android 4.4.\n" + //
                "   */\n" + //
                "  div[style*=\"margin: 16px 0;\"] {\n" + //
                "    margin: 0 !important;\n" + //
                "  }\n" + //
                "  body {\n" + //
                "    width: 100% !important;\n" + //
                "    height: 100% !important;\n" + //
                "    padding: 0 !important;\n" + //
                "    margin: 0 !important;\n" + //
                "  }\n" + //
                "  /**\n" + //
                "   * Collapse table borders to avoid space between cells.\n" + //
                "   */\n" + //
                "  table {\n" + //
                "    border-collapse: collapse !important;\n" + //
                "  }\n" + //
                "  a {\n" + //
                "    color: #1a82e2;\n" + //
                "  }\n" + //
                "  img {\n" + //
                "    height: auto;\n" + //
                "    line-height: 100%;\n" + //
                "    text-decoration: none;\n" + //
                "    border: 0;\n" + //
                "    outline: none;\n" + //
                "  }\n" + //
                "  </style>\n" + //
                "\n" + //
                "</head>\n" + //
                "<body style=\"background-color: #e9ecef;\">\n" + //
                "\n" + //
                "  <!-- start preheader -->\n" + //
                "  <div class=\"preheader\" style=\"display: none; max-width: 0; max-height: 0; overflow: hidden; font-size: 1px; line-height: 1px; color: #fff; opacity: 0;\">\n"
                + //
                "    A preheader is the short summary text that follows the subject line when an email is viewed in the inbox.\n"
                + //
                "  </div>\n" + //
                "  <!-- end preheader -->\n" + //
                "\n" + //
                "  <!-- start body -->\n" + //
                "  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" + //
                "\n" + //
                "    <!-- start logo -->\n" + //
                "    <tr>\n" + //
                "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" + //
                "        <tr>\n" + //
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" + //
                "        <![endif]-->\n" + //
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n"
                + //
                "          <tr>\n" + //
                "            <td align=\"center\" valign=\"top\" style=\"padding: 36px 24px;\">\n" + //
                "              <a href=\"https://www.blogdesire.com\" target=\"_blank\" style=\"display: inline-block;\">\n"
                + //
                "                <img src=\"https://www.blogdesire.com/wp-content/uploads/2019/07/blogdesire-1.png\" alt=\"Logo\" border=\"0\" width=\"48\" style=\"display: block; width: 48px; max-width: 48px; min-width: 48px;\">\n"
                + //
                "              </a>\n" + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "        </table>\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        </td>\n" + //
                "        </tr>\n" + //
                "        </table>\n" + //
                "        <![endif]-->\n" + //
                "      </td>\n" + //
                "    </tr>\n" + //
                "    <!-- end logo -->\n" + //
                "\n" + //
                "    <!-- start hero -->\n" + //
                "    <tr>\n" + //
                "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" + //
                "        <tr>\n" + //
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" + //
                "        <![endif]-->\n" + //
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n"
                + //
                "          <tr>\n" + //
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;\">\n"
                + //
                "              <h1 style=\"margin: 0; font-size: 32px; font-weight: 700; letter-spacing: -1px; line-height: 48px;\">Confirm Your Email Address</h1>\n"
                + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "        </table>\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        </td>\n" + //
                "        </tr>\n" + //
                "        </table>\n" + //
                "        <![endif]-->\n" + //
                "      </td>\n" + //
                "    </tr>\n" + //
                "    <!-- end hero -->\n" + //
                "\n" + //
                "    <!-- start copy block -->\n" + //
                "    <tr>\n" + //
                "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" + //
                "        <tr>\n" + //
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" + //
                "        <![endif]-->\n" + //
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n"
                + //
                "\n" + //
                "          <!-- start copy -->\n" + //
                "          <tr>\n" + //
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n"
                + //
                "              <p style=\"margin: 0;\">" + message + "</p>\n" + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "          <!-- end copy -->\n" + //
                "\n" + //
                "          <!-- start button -->\n" + //
                "          <tr>\n" + //
                "            <td align=\"left\" bgcolor=\"#ffffff\">\n" + //
                "              <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" + //
                "                <tr>\n" + //
                "                  <td align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 12px;\">\n" + //
                "                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" + //
                "                      <tr>\n" + //
                "                        <td align=\"center\" bgcolor=\"#1a82e2\" style=\"border-radius: 6px;\">\n" + //
                "                          <a href=" + allowedOrigins + ":8080/verify?token=" + token
                + "  target=\"_blank\" style=\"display: inline-block; padding: 16px 36px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; color: #ffffff; text-decoration: none; border-radius: 6px;\">"
                + buttonTitle + "</a>\n" + //
                "                        </td>\n" + //
                "                      </tr>\n" + //
                "                    </table>\n" + //
                "                  </td>\n" + //
                "                </tr>\n" + //
                "              </table>\n" + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "          <!-- end button -->\n" + //
                "\n" + //
                "          <!-- start copy -->\n" + //
                "          <tr>\n" + //
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n"
                + //
                "              <p style=\"margin: 0;\">This verify wil be expired in 5 minutes. If you can't verify, please login with your account we will send you a verify email again</p>\n"
                + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "          <!-- end copy -->\n" + //
                "\n" + //
                "          <!-- start copy -->\n" + //
                "          <tr>\n" + //
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px; border-bottom: 3px solid #d4dadf\">\n"
                + //
                "              <p style=\"margin: 0;\">Cheers,<br> Paste</p>\n" + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "          <!-- end copy -->\n" + //
                "\n" + //
                "        </table>\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        </td>\n" + //
                "        </tr>\n" + //
                "        </table>\n" + //
                "        <![endif]-->\n" + //
                "      </td>\n" + //
                "    </tr>\n" + //
                "    <!-- end copy block -->\n" + //
                "\n" + //
                "    <!-- start footer -->\n" + //
                "    <tr>\n" + //
                "      <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 24px;\">\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" + //
                "        <tr>\n" + //
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" + //
                "        <![endif]-->\n" + //
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n"
                + //
                "\n" + //
                "          <!-- start permission -->\n" + //
                "          <tr>\n" + //
                "            <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666;\">\n"
                + //
                "              <p style=\"margin: 0;\">You received this email because we received a request at FU records need. We need to verify your email. If you didn't request you can safely delete this email.</p>\n"
                + //
                "            </td>\n" + //
                "          </tr>\n" + //
                "          <!-- end permission -->\n" + //
                "\n" + //
                "          <!-- start unsubscribe -->\n" + //
                "          <!-- end unsubscribe -->\n" + //
                "\n" + //
                "        </table>\n" + //
                "        <!--[if (gte mso 9)|(IE)]>\n" + //
                "        </td>\n" + //
                "        </tr>\n" + //
                "        </table>\n" + //
                "        <![endif]-->\n" + //
                "      </td>\n" + //
                "    </tr>\n" + //
                "    <!-- end footer -->\n" + //
                "\n" + //
                "  </table>\n" + //
                "  <!-- end body -->\n" + //
                "\n" + //
                "</body>\n" + //
                "</html>";
    }
}
