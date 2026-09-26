package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.repository.BusinessRepository;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;

@Component
public class SlugService {

    private final BusinessRepository businessRepository;

    public SlugService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public String uniqueSlugFor(String name) {
        String base = slugify(name);
        if (base.length() < 3) base = base + "-agenda";
        String candidate = base;
        int suffix = 2;
        while (businessRepository.existsBySlug(candidate)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }

    public static String slugify(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.length() > 60 ? normalized.substring(0, 60).replaceAll("-$", "") : normalized;
    }
}
