package consumer;

import common.TransferEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ConsumerService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(ConsumerService.class);

    public ConsumerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @KafkaListener(topics = "transfers", containerFactory = "factory")
    public void listen(List<String> messages, Acknowledgment ack) {

        for (String msg : messages) {
            process(msg);
        }
        ack.acknowledge();
    }

    @Transactional
    public void process(String json) {
        log.info("Консьюмер начинает обработку сообщения: {}", json);

        try {
            TransferEvent t = mapper.readValue(json, TransferEvent.class);

            Double fromBalance = jdbcTemplate.queryForObject(
                    "SELECT balance FROM    account WHERE id=?",
                    Double.class, t.getFromAccountId()
            );

            if (fromBalance == null || fromBalance < t.getAmount().doubleValue()) {
                saveError(t);
                log.error("Ошибка при работе с балансом {}", fromBalance);
                return;
            }

            jdbcTemplate.update(
                    "UPDATE account SET balance = balance - ? WHERE id=?",
                    t.getAmount(), t.getFromAccountId()
            );

            jdbcTemplate.update(
                    "UPDATE account SET balance = balance + ? WHERE id=?",
                    t.getAmount(), t.getToAccountId()
            );

            jdbcTemplate.update(
                    "INSERT INTO transfer VALUES (?, ?, ?, ?, 'OK')",
                    t.getId(), t.getFromAccountId(), t.getToAccountId(), t.getAmount()
            );

            log.info("Успешно обработано сообщение {}", t.getId());

        } catch (Exception e) {
            log.error("Возникла ошибка при обработке сообщения", e);
            e.printStackTrace();
        }
    }

    private void saveError(TransferEvent t) {
        jdbcTemplate.update(
                "INSERT INTO transfer VALUES (?, ?, ?, ?, 'ERROR')",
                t.getId(), t.getFromAccountId(), t.getToAccountId(), t.getAmount()
        );
    }
}