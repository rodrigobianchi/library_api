package io.github.bianchi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

	/*
	Teste de envio de e-mail

	@Autowired
	private EmailService emailService;
	@Bean
	public CommandLineRunner runner() {
		return args -> {
			List<String> emails = Arrays.asList("994643a527-ecc5e3@inbox.mailtrap.io");
			emailService.sendMails("Teste empréstimo envio de e-mail", emails);
			System.out.println("-----emails enviados-----");
		};
	}
	 */

    public static void main(String[] args) {
        SpringApplication.run(LibraryApiApplication.class, args);
    }

    /*
    Teste de agendamento
    @Scheduled(cron = "0 11 9 ? * MON-FRI")
    public void testeAgendamento() {
        System.out.println("-----teste de agendamento-----");
    }
    */

}
