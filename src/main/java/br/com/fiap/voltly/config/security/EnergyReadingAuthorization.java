package br.com.fiap.voltly.config.security;

import br.com.fiap.voltly.domain.model.EnergyReading;
import br.com.fiap.voltly.domain.repository.EnergyReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("readingAuth")
@RequiredArgsConstructor
public class EnergyReadingAuthorization {

    private final EnergyReadingRepository repo;

    public boolean isOwnerByReading(Long readingId,
                                    br.com.fiap.voltly.domain.model.User principal) {

        return repo.findById(readingId)
                .map(EnergyReading::getSensor)
                .map(s -> s.getEquipment().getOwner().getId())
                .map(principal.getId()::equals)
                .orElse(false);
    }
}
