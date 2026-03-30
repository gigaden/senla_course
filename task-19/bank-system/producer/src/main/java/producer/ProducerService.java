package producer;

import common.TransferEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class ProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();
    private static final Logger log = LoggerFactory.getLogger(ProducerService.class);

    private final List<Long> accounts = new ArrayList<>();

    public ProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

        for (long i = 1; i <= 1000; i++) {
            accounts.add(i);
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void generate() throws Exception {

        for (int i = 0; i < 5; i++) {
            send();
        }
    }

    private void send() throws Exception {

        Long from = accounts.get(random.nextInt(accounts.size()));
        Long to = accounts.get(random.nextInt(accounts.size()));

        TransferEvent event = new TransferEvent(
                UUID.randomUUID(),
                from,
                to,
                BigDecimal.valueOf(random.nextInt(1000))
        );

        String json = mapper.writeValueAsString(event);

        kafkaTemplate.send("transfers", String.valueOf(from), json);

        log.info("Отправлено сообщение от продюсера: {}", json);
    }
}