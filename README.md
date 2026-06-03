# Minecraft Server Management System

Java OOP assignment for *Programare Avansată pe Obiecte*.

A management/statistics panel for a Minecraft multiplayer server: worlds, players, ranks, inventories, plugins, achievements, and economy.

---

## Object Types

1. **`Player`** — uuid, username, joinDate, playtimeMinutes, currentWorld, balance, xp; hierarchy: `RegularPlayer`, `VIPPlayer`, `StaffMember` → `Moderator`, `Administrator`
2. **`World`** — name, seed, worldType (enum), difficulty, maxPlayers, spawnX/Y/Z
3. **`Item`** — abstract root: `Tool` → `Pickaxe`/`Sword`/`Axe`; `Consumable` → `Food`/`Potion`; `Block` → `BuildingBlock`/`OreBlock`
4. **`Inventory`** — belongs to a `Player`, holds `ItemStack` (item + count) entries
5. **`Rank`** — name, prefix, color, permissions set, weight (used for sorted ordering)
6. **`Achievement`** — id, title, description, xpReward, optional parentAchievement (dependency tree)
7. **`Plugin`** — name, version, author, enabled flag, dependencies list
8. **`Session`** — play session for a `Player`: loginTime, logoutTime, worldVisited, deaths, mobsKilled
9. **`Transaction`** — marketplace sale: buyer, seller, itemStack, price, timestamp

---

## Actions / Queries

1. **Add player** — register a new player with a default rank (`PlayerService.addPlayer`)
2. **Login / logout** — open and close a `Session`, update playtime (`SessionService`)
3. **Teleport player** — move player to another world, checks capacity and permissions (`WorldService.teleport`)
4. **Add item to inventory** — add an `ItemStack` to a player's inventory (`InventoryService.addItem`)
5. **Marketplace transaction** — transfer item + balance between two players (`EconomyService.executeTransaction`)
6. **Grant achievement** — verify parent dependencies, award XP (`AchievementService.grantAchievement`)
7. **Promote player** — assign a new rank, staff-only action (`PlayerService.promoteTo`)
8. **Kick / ban player** — staff action; ban removes player from all collections (`StaffService`)
9. **Install / disable plugin** — checks dependency graph in both directions (`PluginService`)
10. **Top N players by playtime** — reads directly from the sorted `TreeSet` leaderboard (`PlayerService.topNByPlaytime`)
11. **World statistics** — player count per world (`WorldService.worldStats`)
12. **Search items by rarity / type** — polymorphic search across all inventories (`InventoryService.searchByRarity`, `searchByType`)
13. **Economic report** — richest players and most expensive transactions (`EconomyService`)

---

## Etapa I — Requirements

| Requirement | Where |
|---|---|
| Private/protected fields + accessors | All `model/` classes have private fields with getters/setters; `protected` in `Player` so subclasses can access fields directly |
| ≥ 2 collections, ≥ 1 sorted | `HashMap<UUID,Player>`, `ArrayList<Plugin>`, **`TreeSet<Player>`** sorted by playtime desc (leaderboard), `TreeMap<Rank,Set<Player>>` sorted by rank weight desc, in `ServerState.java` |
| Inheritance in collections | `Player` subtypes (RegularPlayer, VIPPlayer, Moderator, Administrator) stored together in `Map<UUID,Player>` and `TreeSet`; `Item` subtypes (Pickaxe, Food, OreBlock, ...) stored together in each `Inventory` |
| ≥ 1 service class | `PlayerService`, `WorldService`, `InventoryService`, `EconomyService`, `AchievementService`, `PluginService`, `SessionService`, `StaffService` |
| `Main` with service calls | `Main.java` instantiates all services and calls them for demo |

---

## Etapa II — Requirements

### 1. Relational Database Persistence via JDBC

**Database:** SQLite (`mcserver.db`), schema at `src/main/resources/schema.sql`.

#### Generic singleton services for DB read/write

| Class | Role |
|---|---|
| `persistence/Repository.java` | Interface: `save`, `findById`, `findAll`, `update`, `delete` |
| `persistence/GenericDAO.java` | Abstract class implementing `Repository<T,ID>`; obtains connection from `DatabaseConnection.getInstance()` |
| `persistence/DatabaseConnection.java` | **Singleton** that holds the single `java.sql.Connection`; `getInstance()` is `synchronized` |

#### CRUD services

| DAO | Entity | PK type | Notes |
|---|---|---|---|
| `PlayerDAO` | `Player` (+ subtypes) | `UUID` | stores `player_type` discriminator; reconstructs correct subclass on read |
| `WorldDAO` | `World` | `String` (name) | straightforward flat mapping |
| `RankDAO` | `Rank` | `String` (name) | permissions stored as `;`-separated string |
| `PluginDAO` | `Plugin` | `String` (name) | dependencies in join table `plugin_dependencies` |
| `AchievementDAO` | `Achievement` | `String` (id) | parent resolved recursively; `saveGrant`/`loadAllGrants` for player–achievement links |
| `ItemDAO` | `Item` (+ subtypes) | `String` (itemId) | single-table inheritance with `item_type` discriminator |

### 2. Audit Service

**Class:** `audit/AuditService.java` — **singleton**, thread-safe.

**File:** `audit.csv` (created on first run, header written once).

**Format:** `action_name,details,timestamp` — matches the requirement exactly.

**Usage:** every service method that performs one of the 13 actions calls:
```java
AuditService.getInstance().logAction("action_name", "details…");
// or
AuditService.getInstance().logAction("action_name");   // no details variant
```

---

## Build & Run

```bash
./compile.sh          # compile all sources to out/
./run.sh              # run Main class
```

For a clean rebuild:
```bash
./compile.sh clean
./compile.sh
```
