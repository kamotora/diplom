package com.diplom.work.svc;

import com.diplom.work.core.Settings;
import com.diplom.work.exceptions.SettingsNotFound;
import com.diplom.work.repo.SettingsRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SettingsService {
    private final SettingsRepository settingsRepository;
    private final UserService userService;

    @Autowired
    public SettingsService(SettingsRepository settingsRepository, UserService userService) {
        this.settingsRepository = settingsRepository;
        this.userService = userService;
    }

    public Settings getSettings() throws SettingsNotFound {
        List<Settings> all = settingsRepository.findAll();
        if (all.size() > 1) {
            LoggerFactory.getLogger(this.getClass()).error("Для настроек больше 1 записи в таблице! Нельзя так");
            // ибо зачем
            for (int i = 1; i < all.size(); i++)
                settingsRepository.delete(all.get(i));
        }
        if (all.isEmpty())
            throw new SettingsNotFound();
        return all.get(0);
    }

    public Optional<Settings> getSettingsOptional() {
        try {
            return Optional.of(getSettings());
        } catch (SettingsNotFound settingsNotFound) {
            return Optional.empty();
        }
    }

    public Settings save(Settings settings) {
        if (settings.getIsNeedCheckSign() == null) settings.setIsNeedCheckSign(false);
        if (settings.getIsUsersCanViewOnlyTheirRules() == null) settings.setIsUsersCanViewOnlyTheirRules(false);
        if (settings.getIsUsersCanAddRulesOnlyMyself() == null) settings.setIsUsersCanAddRulesOnlyMyself(false);
        if (settings.getIsTokensActivate() == null) settings.setIsTokensActivate(false);

        if (Boolean.TRUE.equals(settings.getIsTokensActivate()))
            userService.createTokensIfNotExists();

        return settingsRepository.save(settings);
    }
}
