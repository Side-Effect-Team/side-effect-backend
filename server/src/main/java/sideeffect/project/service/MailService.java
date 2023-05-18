package sideeffect.project.service;

import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import sideeffect.project.domain.applicant.ApplicantStatus;
import sideeffect.project.domain.notification.MessageConstruct;
import sideeffect.project.domain.user.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${email.enable:false}")
    private boolean emailEnable;

    @Async
    public void sendMail(String team, User user, ApplicantStatus applicantStatus) {
        if (!emailEnable) {
            log.info("메일을 전송하지 않습니다.");
            return;
        }

        try {
            mailSender.send(createMessage(team, user, applicantStatus));
        } catch (MessagingException e) {
            log.error("error", e);
        }
    }

    private MimeMessage createMessage(String team, User user, ApplicantStatus applicantStatus)
        throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject(createTitle(team));
        helper.setTo(user.getEmail());
        helper.setText(createHtml(applicantStatus, createVariables(team, user.getNickname())), true);
        return message;
    }

    private Map<String, String> createVariables(String team, String nickname) {
        if (team == null) {
            return Map.of("name", nickname);
        }
        return Map.of("team", team, "name", nickname);
    }

    private String createTitle(String team) {
        if (team == null) {
            return MessageConstruct.TITLE;
        }
        return team + " " + MessageConstruct.TITLE;
    }

    private String createHtml(ApplicantStatus applicantStatus, Map<String, String> values) {
        Context context = new Context();
        values.forEach(context::setVariable);

        if (applicantStatus == ApplicantStatus.APPROVED) {
            return templateEngine.process("approved", context);
        } else if (applicantStatus == ApplicantStatus.PENDING) {
            return templateEngine.process("pending", context);
        }
        return templateEngine.process("rejected", context);
    }
}
