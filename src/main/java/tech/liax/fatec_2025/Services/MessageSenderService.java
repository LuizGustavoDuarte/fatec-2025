package tech.liax.fatec_2025.Services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.liax.fatec_2025.Exceptions.MessageSendException;

@Service
@RequiredArgsConstructor
public class MessageSenderService {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MessageSenderService.class);

    @Value("${spring.rabbitmq.exchangeName}")
    private String exchangeName;

    @Value("${spring.rabbitmq.routingKey}")
    private String routingKey;

    public void sendMessage(String message) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
            logger.info("Mensagem enviada: {}", message);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem: {}", e.getMessage(), e);
            throw new MessageSendException("Falha ao enviar mensagem para RabbitMQ", e);
        }
    }

}
