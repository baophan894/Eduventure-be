package swp.group2.swpbe.expert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import swp.group2.swpbe.CloudinaryService;
import swp.group2.swpbe.constant.ExpertRequestStatus;
import swp.group2.swpbe.constant.UserRole;
import swp.group2.swpbe.exception.ApiRequestException;
import swp.group2.swpbe.expert.entities.ExpertRequest;
import swp.group2.swpbe.user.UserRepository;
import swp.group2.swpbe.user.WalletRepository;
import swp.group2.swpbe.user.entities.User;
import swp.group2.swpbe.user.entities.Wallet;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ExpertRequestService {
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    ExpertRequestRepository expertRequestRepository;
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private JavaMailSender emailSender;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${allow.origin}")
    private String allowedOrigins;

    public void saveRequest(String userId, MultipartFile file) {
        ExpertRequest expertRequest = new ExpertRequest();
        expertRequest.setUserId(userId);
        expertRequest.setState(ExpertRequestStatus.PENDING);
        expertRequest.setCreatedAt(new Date());
        Map data = this.cloudinaryService.upload(file);
        String url = (String) data.get("url");
        expertRequest.setCvUrl(url);
        expertRequestRepository.save(expertRequest);
    }

    public List<ExpertRequest> getAllExpert() {
        return expertRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<ExpertRequest> getAllExpertByUserId(String userId) {
        return expertRequestRepository.findByUserId(userId);
    }

    public List<ExpertRequest> getOwnExpertRequests(String userId) {
        return expertRequestRepository.findByUserId(userId);
    }

    public void acceptExpertRequest(int id) {
        Stripe.apiKey = stripeApiKey;
        ExpertRequest expertRequest = expertRequestRepository.findById(id);
        int userId = Integer.parseInt(expertRequest.getUserId());

        User user = userRepository.findById(userId);
        String accountId = "";
        AccountCreateParams params = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS)
                .setCountry("US")
                .setEmail(user.getEmail())
                .build();
        try {
            Account account = Account.create(params);
            accountId = account.getId();
            Wallet wallet = new Wallet(userId, accountId);
            walletRepository.save(wallet);
        } catch (StripeException e) {
            throw new ApiRequestException("Can't create account", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        AccountLinkCreateParams paramsLink = AccountLinkCreateParams.builder()
                .setAccount(accountId)
                .setRefreshUrl("https://example.com/reauth")
                .setReturnUrl(allowedOrigins + "/profile")
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();
        try {
            AccountLink accountLink = AccountLink.create(paramsLink);
            try {
                this.sendMail(user.getEmail(), "Completed your wallet information",
                        "Click here to complete your wallet information" + " " + accountLink.getUrl());
                expertRequest.setState(ExpertRequestStatus.ACCEPTED);
                expertRequestRepository.save(expertRequest);
                user.setRole(UserRole.EXPERT);
                userRepository.save(user);
            } catch (MessagingException e) {
                throw new ApiRequestException("Can't send mail", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (StripeException e) {
            throw new ApiRequestException("Can't create account link", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void rejectExpertRequest(int id) {
        ExpertRequest expertRequest = expertRequestRepository.findById(id);
        expertRequest.setState(ExpertRequestStatus.REJECTED);
        expertRequestRepository.save(expertRequest);
    }

    public void sendMail(
            String email, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("huyhkds170542@fpt.edu.vn");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(text);
        emailSender.send(message);

    }

}
