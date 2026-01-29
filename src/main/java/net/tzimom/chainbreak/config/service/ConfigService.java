package net.tzimom.chainbreak.config.service;

import net.tzimom.chainbreak.config.ChainBreakConfig;

public interface ConfigService {
    void reload();

    ChainBreakConfig config();
}
