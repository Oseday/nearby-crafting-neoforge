package com.oseday.nearbycrafting;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(NearbyCrafting.MOD_ID)
public class NearbyCrafting {
    public static final String MOD_ID = "nearbycrafting";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NearbyCrafting(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        // We are not using global events right now, but keeping registration
        // NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("[NearbyCrafting] NeoForge barebones crafting from nearby containers enabled");
    }
}
