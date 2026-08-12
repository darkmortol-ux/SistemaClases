# SistemaClases

Plugin de clases RPG liviano para Paper 1.21.11, parte del proyecto **Plugins MC**.

Cada jugador elige una de 6 clases, cada una con pasivas permanentes, una habilidad activa
y una armadura única craftable que suaviza su debilidad. Se integra con `EventosPersonalizados`
(esencias del Jefe Aleatorio, Estrella del Cambio en el Cofre Supremo) y funciona de forma
independiente si ese plugin no está instalado.

---

## Índice

- [Las 6 clases](#las-6-clases)
- [Cómo elegir y cambiar de clase](#cómo-elegir-y-cambiar-de-clase)
- [Foco de Habilidad](#foco-de-habilidad)
- [Estrella del Cambio](#estrella-del-cambio)
- [Armadura de clase](#armadura-de-clase)
- [Esencias de clase](#esencias-de-clase)
- [Comandos](#comandos)
- [Permisos](#permisos)
- [Configuración](#configuración-configyml)
- [Requiere / Integraciones](#requiere--integraciones)

---

## Las 6 clases

| Clase | Pasivas | Habilidad activa | Cooldown |
|---|---|---|---|
| **Guerrero** | +4 corazones, +15% daño cuerpo a cuerpo, -10% velocidad | **Grito de Guerra** — empuja enemigos cercanos y se da Resistencia II | 25s |
| **Mago** | -2 corazones, +20% velocidad de ataque | **Bola de Fuego** — proyectil que explota en área | 12s |
| **Arquero** | +20% velocidad de movimiento, +10% velocidad de ataque | **Lluvia de Flechas** — dispara 5 flechas en abanico sin gastar inventario | 20s |
| **Tanque** | +6 corazones, +30% resistencia a empuje, -15% daño | **Escudo Sagrado** — 5.5s con 50% menos daño recibido | 45s |
| **Asesino** | +25% daño crítico por la espalda/agachado, +10% velocidad | **Paso Sombrío** — teletransporte corto en la dirección en que mira | 25s |
| **Clérigo** | +2 corazones | **Bendición Curativa** — cura 4 corazones a todos los aliados en 8 bloques | 30s |

Las debilidades (velocidad, vida o daño reducidos) se pueden mitigar a la mitad llevando la
[armadura de clase](#armadura-de-clase) completa.

---

## Cómo elegir y cambiar de clase

Comando: `/clase elegir <guerrero|mago|arquero|tanque|asesino|clerigo>`

- **Elección inicial:** gratis, disponible para cualquier jugador con permiso `clases.usar` (por defecto todos).
- **1 cambio de clase gratis** después de la elección inicial (total: 2 asignaciones sin costo).
- **A partir del 3er cambio:** bloqueado, a menos que tengas una [Estrella del Cambio](#estrella-del-cambio) en la hotbar.
- Al elegir o cambiar de clase, tu **Foco de Habilidad** anterior se elimina automáticamente
  (inventario y offhand) y recibes el foco correspondiente a la nueva clase.
- Consulta tu estado con `/clase info`.

---

## Foco de Habilidad

Ítem entregado automáticamente al elegir clase — click derecho (mano principal, no offhand)
para activar tu habilidad, siempre que no esté en cooldown.

| Clase | Ítem |
|---|---|
| Guerrero | Hacha de hierro |
| Mago | Vara de Blaze |
| Arquero | Arco |
| Tanque | Escudo |
| Asesino | Pluma |
| Clérigo | Palo |

Solo funciona si coincide con tu clase actual — el foco de una clase que ya no tienes queda
inutilizado (y se elimina automáticamente en el próximo cambio de clase).

---

## Estrella del Cambio

Permite un cambio de clase adicional una vez agotado el límite gratuito de 2. Se consume al
usarse y **debe estar en la hotbar** (slots 1-9) al ejecutar `/clase elegir <clase>`.

**Cómo conseguirla:**

1. **Loot del Cofre Supremo** (`EventosPersonalizados`) — probabilidad configurable
   (`chance` en el `config.yml` de ese plugin, entrada `SISTEMA_CLASES:ESTRELLA_CAMBIO`).
2. **Comando de administrador:**
   ```
   /clase estrella dar <jugador> [cantidad]
   ```
   Requiere permiso `clases.admin`.

Valor de referencia configurable en `precio-estrella-cambio` (solo informativo, se muestra en
el lore del ítem — no se cobra automáticamente salvo que lo integres a tu economía).

---

## Armadura de clase

Set completo de 4 piezas de cuero teñido, único por clase, que **mitiga a la mitad la
debilidad** de Guerrero/Mago/Tanque, o da **+1 corazón y +5% resistencia a empuje** a
Arquero/Asesino/Clérigo (que no tienen debilidad propia que reducir). El efecto solo se activa
llevando las 4 piezas de la **misma clase** al mismo tiempo, y se revisa automáticamente cada
vez que cambias de equipo.

| Clase | Color | Material de relleno |
|---|---|---|
| Guerrero | Rojo oscuro | Blaze Powder |
| Mago | Azul | Amethyst Shard |
| Tanque | Gris plata | Iron Block |
| Arquero | Verde | Emerald |
| Asesino | Morado oscuro | Ender Pearl |
| Clérigo | Dorado pálido | Ghast Tear |

Las piezas finales llevan Irrompibilidad (nivel configurable, 1–8) y nombre personalizable por
clase y slot en el `config.yml` (`armor-names`).

### Cómo craftear un set completo

**Casco y Botas** — crafteo normal directo, 1 esencia cada una:

```
Casco:                     Botas:
[Cuero][Cuero][Cuero]      [Cuero][    ][Cuero]
[Rell.][Esencia][Rell.]    [Rell.][Esencia][Rell.]
[Cuero][      ][Cuero]     [Cuero][    ][Cuero]
```

**Pechera y Pantalones** — proceso en dos etapas, 2 esencias cada una:

1. **Etapa 1 (mesa de crafteo):** craftea la pieza "sin terminar" con cuero + relleno (sin esencia todavía)
   ```
   Pechera base:               Pantalones base:
   [Cuero][Cuero][Cuero]       [Cuero][Rell.][Cuero]
   [Rell.][Cuero][Rell.]       [Cuero][Cuero][Cuero]
   [Cuero][      ][Cuero]      [      ][Rell.][      ]
   ```
2. **Etapa 2 (mesa de herrería):** coloca la pieza base en el slot de "objeto" y **2 esencias** en el
   slot de "material" (deja vacío el slot de plantilla). El resultado es la pieza final con el
   bonus de mitigación activo.

**Total por set completo: 6 esencias** (1 + 1 + 2 + 2).

---

## Esencias de clase

Ingrediente necesario para craftear la armadura. Cada clase tiene su propia esencia, distinta
del material de relleno.

| Clase | Esencia | Material base |
|---|---|---|
| Guerrero | Corazón de Furia | Magma Cream |
| Mago | Cristal Arcano | Prismarine Crystals |
| Tanque | Núcleo Blindado | Echo Shard |
| Arquero | Pluma Certera | Spectral Arrow |
| Asesino | Sombra Condensada | Phantom Membrane |
| Clérigo | Lágrima Sagrada | Nautilus Shell |

**Cómo conseguirlas:** dropean del **Jefe Aleatorio** (`EventosPersonalizados`) al morir, de
clase **aleatoria** entre las 6 (no depende de qué clase tenga el jugador que remató al jefe).
Probabilidad configurable en el `config.yml` de `EventosPersonalizados`
(`random-boss.essence-drop-chance`, por defecto 15%). Fomenta el intercambio entre jugadores
de distintas clases.

---

## Comandos

| Comando | Descripción | Permiso |
|---|---|---|
| `/clase elegir <clase>` | Elige o cambia de clase | `clases.usar` |
| `/clase info` | Muestra tu clase, habilidad, cooldown y cambios disponibles | `clases.usar` |
| `/clase reset <jugador>` | Quita la clase de un jugador (admin) | `clases.admin` |
| `/clase estrella dar <jugador> [cantidad]` | Entrega Estrella(s) del Cambio | `clases.admin` |

Alias: `/clases`, `/class`

---

## Permisos

| Permiso | Descripción | Default |
|---|---|---|
| `clases.usar` | Elegir clase, ver info, usar habilidades | `true` |
| `clases.admin` | Resetear clases y dar estrellas | `op` |

---

## Configuración (`config.yml`)

```yaml
precio-estrella-cambio: 500000   # valor informativo mostrado en el lore de la Estrella

armor-durability:
  nivel-irrompibilidad: 3        # 1-8; el máximo vanilla normal es 3

armor-names:                     # nombres personalizables por clase y pieza
  guerrero:
    casco: "Casco del Guerrero"
    pechera: "Pechera del Guerrero"
    pantalones: "Pantalones del Guerrero"
    botas: "Botas del Guerrero"
  # ... resto de clases (mago, tanque, arquero, asesino, clerigo)
```

---

## Requiere / Integraciones

- **Paper 1.21.11**, Java 21
- **Opcional:** `EventosPersonalizados` (softdepend) — sin él, la Estrella del Cambio y las
  esencias de clase simplemente no aparecen en el loot de esos eventos; todo lo demás
  (clases, habilidades, crafteo manual si le das las esencias por comando, armadura) funciona
  igual. La integración es vía reflexión, no requiere el `.jar` en tiempo de compilación.

https://github.com/darkmortol-ux/EventosPersonalizados


---
Mis Plugins

https://github.com/darkmortol-ux/RangosMC
https://github.com/darkmortol-ux/BordePersonalizado
https://github.com/darkmortol-ux/ProteccionAreas
