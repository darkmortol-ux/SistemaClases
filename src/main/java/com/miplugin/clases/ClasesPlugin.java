package com.miplugin.clases;

import com.miplugin.clases.abilities.HabilidadRegistry;
import com.miplugin.clases.attributes.ArmorMitigationService;
import com.miplugin.clases.attributes.AttributeService;
import com.miplugin.clases.commands.ClaseCommand;
import com.miplugin.clases.items.ArmorItemService;
import com.miplugin.clases.items.EsenciaService;
import com.miplugin.clases.items.EstrellaCambioService;
import com.miplugin.clases.items.HabilidadItemService;
import com.miplugin.clases.listeners.AbilityTriggerListener;
import com.miplugin.clases.listeners.ArmorSetListener;
import com.miplugin.clases.listeners.ArmorSmithingListener;
import com.miplugin.clases.listeners.AsesinoCriticoListener;
import com.miplugin.clases.listeners.BolaDeFuegoImpactListener;
import com.miplugin.clases.listeners.PlayerJoinListener;
import com.miplugin.clases.listeners.TanqueEscudoListener;
import com.miplugin.clases.model.ClaseDefinicion;
import com.miplugin.clases.recipes.ArmorRecipeRegistrar;
import org.bukkit.plugin.java.JavaPlugin;

public class ClasesPlugin extends JavaPlugin {

    private ClaseKeys keys;
    private AttributeService attributeService;
    private ClaseManager claseManager;
    private HabilidadItemService habilidadItemService;
    private EstrellaCambioService estrellaCambioService;
    private HabilidadRegistry habilidadRegistry;
    private EsenciaService esenciaService;
    private ArmorItemService armorItemService;
    private ArmorMitigationService armorMitigationService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.keys = new ClaseKeys(this);
        this.attributeService = new AttributeService(this);
        this.habilidadItemService = new HabilidadItemService(this);
        this.estrellaCambioService = new EstrellaCambioService(this);
        this.esenciaService = new EsenciaService(this);
        this.armorItemService = new ArmorItemService(this);
        this.armorMitigationService = new ArmorMitigationService(this);

        this.habilidadRegistry = new HabilidadRegistry(this);

        this.claseManager = new ClaseManager(this, attributeService);
        this.claseManager.load();

        new ArmorRecipeRegistrar(this).registrarTodas();

        var claseCommand = new ClaseCommand(this);
        getCommand("clase").setExecutor(claseCommand);
        getCommand("clase").setTabCompleter(claseCommand);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilityTriggerListener(this), this);
        getServer().getPluginManager().registerEvents(new BolaDeFuegoImpactListener(this), this);
        getServer().getPluginManager().registerEvents(new AsesinoCriticoListener(this), this);
        getServer().getPluginManager().registerEvents(new TanqueEscudoListener(this), this);
        getServer().getPluginManager().registerEvents(new ArmorSmithingListener(this), this);
        getServer().getPluginManager().registerEvents(new ArmorSetListener(this), this);

        getLogger().info("SistemaClases habilitado con " + ClaseDefinicion.values().length + " clases.");
    }

    @Override
    public void onDisable() {
        if (claseManager != null) {
            claseManager.save();
        }
    }

    public ClaseKeys getKeys() { return keys; }
    public AttributeService getAttributeService() { return attributeService; }
    public ClaseManager getClaseManager() { return claseManager; }
    public HabilidadItemService getHabilidadItemService() { return habilidadItemService; }
    public EstrellaCambioService getEstrellaCambioService() { return estrellaCambioService; }
    public HabilidadRegistry getHabilidadRegistry() { return habilidadRegistry; }
    public EsenciaService getEsenciaService() { return esenciaService; }
    public ArmorItemService getArmorItemService() { return armorItemService; }
    public ArmorMitigationService getArmorMitigationService() { return armorMitigationService; }
}
