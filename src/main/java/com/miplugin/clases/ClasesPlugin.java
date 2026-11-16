package com.miplugin.clases;

import com.miplugin.clases.abilities.HabilidadRegistry;
import com.miplugin.clases.attributes.ArmorMitigationService;
import com.miplugin.clases.attributes.AttributeService;
import com.miplugin.clases.commands.ClaseCommand;
import com.miplugin.clases.items.ArmorItemService;
import com.miplugin.clases.items.EsenciaService;
import com.miplugin.clases.items.EstrellaCambioService;
import com.miplugin.clases.items.HabilidadItemService;
import com.miplugin.clases.items.LingoteService;
import com.miplugin.clases.listeners.AbilityTriggerListener;
import com.miplugin.clases.listeners.ArmorSetListener;
import com.miplugin.clases.listeners.AsesinoCriticoListener;
import com.miplugin.clases.listeners.BolaDeFuegoImpactListener;
import com.miplugin.clases.listeners.PlayerJoinListener;
import com.miplugin.clases.listeners.TanqueEscudoListener;
import com.miplugin.clases.missions.AldeanoListener;
import com.miplugin.clases.missions.AldeanoService;
import com.miplugin.clases.missions.MissionListener;
import com.miplugin.clases.missions.MissionManager;
import com.miplugin.clases.model.ClaseDefinicion;
import com.miplugin.clases.recipes.ArmorRecipeRegistrar;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ClasesPlugin extends JavaPlugin {

    private ClaseKeys keys;
    private AttributeService attributeService;
    private ClaseManager claseManager;
    private HabilidadItemService habilidadItemService;
    private EstrellaCambioService estrellaCambioService;
    private HabilidadRegistry habilidadRegistry;
    private EsenciaService esenciaService;
    private LingoteService lingoteService;
    private ArmorItemService armorItemService;
    private ArmorMitigationService armorMitigationService;
    private MissionManager missionManager;
    private AldeanoService aldeanoService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!new File(getDataFolder(), "misiones.yml").exists()) {
            saveResource("misiones.yml", false);
        }

        this.keys = new ClaseKeys(this);
        this.attributeService = new AttributeService(this);
        this.habilidadItemService = new HabilidadItemService(this);
        this.estrellaCambioService = new EstrellaCambioService(this);
        this.esenciaService = new EsenciaService(this);
        this.lingoteService = new LingoteService(this);
        this.armorItemService = new ArmorItemService(this);
        this.armorMitigationService = new ArmorMitigationService(this);

        this.habilidadRegistry = new HabilidadRegistry(this);

        this.claseManager = new ClaseManager(this, attributeService);
        this.claseManager.load();

        this.aldeanoService = new AldeanoService(this);
        this.missionManager = new MissionManager(this);
        this.missionManager.cargarConfig();
        this.missionManager.cargarDatos();

        new ArmorRecipeRegistrar(this).registrarTodas();

        var claseCommand = new ClaseCommand(this);
        getCommand("clase").setExecutor(claseCommand);
        getCommand("clase").setTabCompleter(claseCommand);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilityTriggerListener(this), this);
        getServer().getPluginManager().registerEvents(new BolaDeFuegoImpactListener(this), this);
        getServer().getPluginManager().registerEvents(new AsesinoCriticoListener(this), this);
        getServer().getPluginManager().registerEvents(new TanqueEscudoListener(this), this);
        getServer().getPluginManager().registerEvents(new ArmorSetListener(this), this);
        getServer().getPluginManager().registerEvents(new MissionListener(this), this);
        getServer().getPluginManager().registerEvents(new AldeanoListener(this), this);

        // Guarda el progreso de misiones cada 5 minutos (solo si hubo cambios).
        getServer().getScheduler().runTaskTimer(this, () -> missionManager.guardarSiHaceFalta(), 6000L, 6000L);

        getLogger().info("SistemaClases habilitado con " + ClaseDefinicion.values().length + " clases.");
    }

    @Override
    public void onDisable() {
        if (claseManager != null) {
            claseManager.save();
        }
        if (missionManager != null) {
            missionManager.guardarDatos();
            missionManager.quitarTodasLasBarras();
        }
    }

    public ClaseKeys getKeys() { return keys; }
    public AttributeService getAttributeService() { return attributeService; }
    public ClaseManager getClaseManager() { return claseManager; }
    public HabilidadItemService getHabilidadItemService() { return habilidadItemService; }
    public EstrellaCambioService getEstrellaCambioService() { return estrellaCambioService; }
    public HabilidadRegistry getHabilidadRegistry() { return habilidadRegistry; }
    public EsenciaService getEsenciaService() { return esenciaService; }
    public LingoteService getLingoteService() { return lingoteService; }
    public ArmorItemService getArmorItemService() { return armorItemService; }
    public ArmorMitigationService getArmorMitigationService() { return armorMitigationService; }
    public MissionManager getMissionManager() { return missionManager; }
    public AldeanoService getAldeanoService() { return aldeanoService; }
}
