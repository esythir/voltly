package br.com.fiap.voltly.utils;

import br.com.fiap.voltly.domain.model.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationService {
    public void notifyByEmail(Alert alert) {
        log.info("Email alert for '{}': {}",
                alert.getEquipment().getName(), alert.getMessage());
    }

    public void notifyByWhatsApp(Alert alert) {
        log.info("WhatsApp alert for '{}': {}",
                alert.getEquipment().getName(), alert.getMessage());
    }

    public void notifyAll(Alert alert) {
        notifyByEmail(alert);
        notifyByWhatsApp(alert);
    }
}
