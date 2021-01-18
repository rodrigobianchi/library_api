package io.github.bianchi.service.impl;

import io.github.bianchi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${apllication.mail.default.sender}")
    private String sender;

    @Value("${application.mail.lateloans.subject}")
    private String subject;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMails(String mensagemEmail, List<String> mailsList) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String[] mails = mailsList.toArray(new String[mailsList.size()]);

        String titulo = StringUtils.toEncodedString(subject.getBytes(Charset.forName("ISO-8859-1")),
                Charset.forName("UTF-8"));

        mailMessage.setFrom(sender);
        mailMessage.setSubject(titulo);
        mailMessage.setText(mensagemEmail);
        mailMessage.setTo(mails);

        javaMailSender.send(mailMessage);
    }
}
