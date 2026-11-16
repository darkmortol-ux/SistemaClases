# SistemaClases

Plugin de clases RPG liviano para Paper 1.21.11, parte del proyecto **Plugins MC**.

Cada jugador elige una de 6 clases, cada una con pasivas permanentes, una habilidad activa
y una armadura única craftable que suaviza su debilidad. Las esencias para craftear la
armadura se consiguen con el **Aldeano de las Esencias** (misiones con cooldown). Se integra
con `EventosPersonalizados` (esencias del Jefe Aleatorio, Estrella del Cambio en el Cofre
Supremo) y funciona de forma independiente si ese plugin no está instalado.

---

## Índice

- [Las 6 clases](#las-6-clases)
- [Cómo elegir y cambiar de clase](#cómo-elegir-y-cambiar-de-clase)
- [Foco de Habilidad](#foco-de-habilidad)
- [Estrella del Cambio](#estrella-del-cambio)
- [Esencias de clase](#esencias-de-clase)
- [Aldeano de las Esencias (misiones)](#aldeano-de-las-esencias-misiones)
- [Armadura de clase](#armadura-de-clase)
- [Comandos](#comandos)
- [Permisos](#permisos)
- [Configuración](#configuración)
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

**Cómo conseguirlas:**

1. **Aldeano de las Esencias** — al completar una misión (ver sección siguiente).
2. **Jefe Aleatorio** (`EventosPersonalizados`) — dropea una esencia de clase **aleatoria** entre
   las 6. Probabilidad en el `config.yml` de ese plugin (`random-boss.essence-drop-chance`,
   por defecto 15%).
3. **Comando de administrador:** `/clase esencia dar <jugador> <clase|aleatoria> [cantidad]`

---

## Aldeano de las Esencias (misiones)

Un aldeano especial que da misiones difíciles y extremas. Al completarlas hay una probabilidad
de recibir esencias.

**Cómo funciona:**

1. Haz click derecho al aldeano: te da **una misión aleatoria** y se muestra una **bossbar** con
   el progreso (morada = difícil, roja = extrema).
2. Cumple el objetivo. Cuando la bossbar se pone verde, **vuelve con el aldeano** para reclamar.
3. Al reclamar hay **40% de probabilidad** de recibir **1 a 2 esencias** (cada una de una clase
   aleatoria entre las 6) y **60%** de no recibir ninguna.
4. Después de completar (o abandonar) una misión debes esperar **12 horas** para pedir otra.
   El cooldown se guarda y sobrevive a reinicios del servidor.

**Tipos de misión (50 en total: 30 difíciles y 20 extremas):**

| Tipo | Qué pide | Ejemplo |
|---|---|---|
| `KILL` | Matar mobs | Elimina 60 Wither Skeleton |
| `MINE` | Minar bloques (los minerales colocados por jugadores no cuentan) | Mina 24 de Ancient Debris |
| `ENTREGAR` | Entregar ítems al aldeano (solo ítems normales, sin nombre ni encantamientos) | Entrega 2x Nether Star |

Solo cuenta el progreso en modo supervivencia. Con `/clase mision` ves tu misión y el tiempo
restante del cooldown; con `/clase mision abandonar` abandonas la actual (el cooldown empieza
a correr igual).

**Colocar el aldeano (admin):** párate donde quieras y usa `/clase aldeano colocar`. Queda quieto,
invulnerable y sin menú de comercio. Para quitarlo, párate cerca (menos de 10 bloques) y usa
`/clase aldeano quitar`.

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

**Pechera y Pantalones** — tres pasos:

1. **Pieza sin terminar (mesa de crafteo):** cuero + relleno, sin esencia
   ```
   Pechera base:               Pantalones base:
   [Cuero][Cuero][Cuero]       [Cuero][Rell.][Cuero]
   [Rell.][Cuero][Rell.]       [Cuero][Cuero][Cuero]
   [Cuero][      ][Cuero]      [      ][Rell.][      ]
   ```
2. **Lingote Especial (mesa de crafteo):** 1 lingote de netherite al centro, rodeado de
   **8 esencias** de la clase
   ```
   [Esencia][Esencia][Esencia]
   [Esencia][Netherite][Esencia]
   [Esencia][Esencia][Esencia]
   ```
3. **Mesa de herrería:** pieza sin terminar en el slot de objeto y el **Lingote Especial** de la
   misma clase en el slot de material. El slot de plantilla se deja vacío. El resultado es la
   pieza final con el bonus de mitigación activo.

**Total por set completo: 18 esencias** (1 casco + 1 botas + 8 pechera + 8 pantalones).

---

## Comandos

| Comando | Descripción | Permiso |
|---|---|---|
| `/clase elegir <clase>` | Elige o cambia de clase | `clases.usar` |
| `/clase info` | Muestra tu clase, habilidad, cooldown y cambios disponibles | `clases.usar` |
| `/clase mision` | Muestra tu misión activa o el cooldown restante | `clases.usar` |
| `/clase mision abandonar` | Abandona tu misión (el cooldown empieza a correr) | `clases.usar` |
| `/clase reset <jugador>` | Quita la clase de un jugador | `clases.admin` |
| `/clase estrella dar <jugador> [cantidad]` | Entrega Estrella(s) del Cambio | `clases.admin` |
| `/clase esencia dar <jugador> <clase\|aleatoria> [cantidad]` | Entrega esencias | `clases.admin` |
| `/clase aldeano colocar` / `quitar` | Coloca o quita el Aldeano de las Esencias | `clases.admin` |
| `/clase mision reiniciar <jugador>` | Borra la misión y el cooldown de un jugador | `clases.admin` |
| `/clase mision recargar` | Recarga `misiones.yml` | `clases.admin` |

Alias: `/clases`, `/class`

---

## Permisos

| Permiso | Descripción | Default |
|---|---|---|
| `clases.usar` | Elegir clase, ver info, usar habilidades y misiones | `true` |
| `clases.admin` | Resetear clases, dar objetos, manejar el aldeano y las misiones | `op` |

---

## Configuración

### `config.yml`

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

### `misiones.yml`

```yaml
cooldown-horas: 12            # horas entre una misión y la siguiente
probabilidad-esencia: 0.40    # 40% de dar esencias, 60% de no dar ninguna
esencias-min: 1
esencias-max: 2
misiones:                     # las 50 misiones (tipo, objetivo, cantidad, dificultad)
  - { id: 1, dificultad: DIFICIL, tipo: KILL, objetivo: ZOMBIE, cantidad: 150 }
```

Tras editarlo, usa `/clase mision recargar`. El progreso de cada jugador se guarda aparte en
`misiones-datos.yml`.

---

## Requiere / Integraciones

- **Paper 1.21.11**, Java 21
- **Opcional:** `EventosPersonalizados` (softdepend) — sin él, la Estrella del Cambio y el drop de
  esencias del Jefe Aleatorio simplemente no aparecen; todo lo demás (clases, habilidades,
  armadura, aldeano y misiones) funciona igual. La integración es vía reflexión, no requiere el
  `.jar` en tiempo de compilación.
