package dev.liquidcatmofu.ueb;

import com.mojang.logging.LogUtils;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import dev.liquidcatmofu.ueb.command.UebCommands;
import dev.liquidcatmofu.ueb.compat.CompatDispatcher;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Objects;

@Mod(UniversalEnergyBridge.MOD_ID)
public final class UniversalEnergyBridge {
    public static final String MOD_ID = "universal_energy_bridge";
    public static final Logger LOGGER = LogUtils.getLogger();

    public UniversalEnergyBridge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::registerCapabilities);
        modBus.addListener(this::commonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BridgeConfig.SPEC);

        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, this::attachCapabilities);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    public static ResourceLocation id(String path) {
        return Objects.requireNonNull(ResourceLocation.tryBuild(MOD_ID, path));
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        CompatDispatcher.initialize();
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IUniversalEnergyStorage.class);
    }

    private void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        CompatDispatcher.attach(event);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        UebCommands.register(event.getDispatcher());
    }
}
