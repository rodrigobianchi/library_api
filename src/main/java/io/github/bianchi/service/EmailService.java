package io.github.bianchi.service;

import java.util.List;

public interface EmailService {

    public void sendMails(String mensagemEmail, List<String> mailsList);

}
