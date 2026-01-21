package net.tzimom.chainbreak.config.service;

import net.tzimom.chainbreak.config.ChainBreakConfig;

public interface ChainBreakConfigService {
    void reload();

    ChainBreakConfig config();
}
