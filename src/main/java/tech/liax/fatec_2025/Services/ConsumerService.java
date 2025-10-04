package tech.liax.fatec_2025.Services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tech.liax.fatec_2025.Config.RabbitMQConfig;

@Service
public class ConsumerService {
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        // TODO
        System.out.println("Mensagem recebida: " + message);
    }
}
