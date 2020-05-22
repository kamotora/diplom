package com.diplom.work.svc;

import com.diplom.work.exceptions.SettingsNotFound;
import com.diplom.work.core.Settings;
import com.diplom.work.repo.SettingsRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsService {
    private final SettingsRepository settingsRepository;

    @Autowired
    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public Settings getSettings() throws SettingsNotFound {
        List<Settings> all = settingsRepository.findAll();
        if (all.size() > 1) {
            LoggerFactory.getLogger(this.getClass()).error("Для настроек больше 1 записи в таблице! Нельзя так");
            // ибо зачем
            for(int i = 1; i < all.size(); i++)
                settingsRepository.delete(all.get(i));
        }
        if(all.isEmpty())
            throw new SettingsNotFound();
        return all.get(0);
    }

    public Settings save(Settings settings) {
        return settingsRepository.save(settings);
    }
}
