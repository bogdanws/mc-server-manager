# Minecraft Server Management System

Java OOP assignment for *Programare Avansată pe Obiecte*.

A management/statistics panel for a Minecraft multiplayer server: worlds, players, ranks, inventories, plugins, achievements, and economy.

## Object Types

1. **`Player`** — uuid, username, joinDate, playtimeMinutes, currentWorld, balance; hierarchy: `RegularPlayer`, `VIPPlayer`, `StaffMember` → `Moderator`, `Administrator`
2. **`World`** — name, seed, worldType (enum), difficulty, maxPlayers, spawnX/Y/Z
3. **`Item`** — abstract root of item hierarchy: `Tool` → `Pickaxe`/`Sword`/`Axe`; `Consumable` → `Food`/`Potion`; `Block` → `BuildingBlock`/`OreBlock`
4. **`Inventory`** — belongs to a `Player`, holds `ItemStack` (item + count) entries
5. **`Rank`** — name, prefix, color, permissions set, weight (used for sorted ordering)
6. **`Achievement`** — id, title, description, xpReward, optional parentAchievement (dependency tree)
7. **`Plugin`** — name, version, author, enabled flag, dependencies list
8. **`Session`** — a play session for a `Player`: loginTime, logoutTime, worldVisited, deaths, mobsKilled
9. **`Transaction`** — marketplace sale: buyer, seller, itemStack, price, timestamp

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

## Requirements

| Requirement | Implementation |
|---|---|
| Private/protected fields + accessors | All `model/` classes have private fields with getters/setters; `protected` used in `Player` so subclasses can access fields directly |
| ≥ 2 collections, ≥ 1 sorted | `HashMap<UUID,Player>`, `ArrayList<Plugin>`, `TreeSet<Player>` sorted by playtime desc (leaderboard), `TreeMap<Rank,Set<Player>>` sorted by rank weight desc, in `ServerState.java` |
| Inheritance in collections | `Player` subtypes (`RegularPlayer`, `VIPPlayer`, `Moderator`, `Administrator`) stored together in `Map<UUID,Player>` and `TreeSet`; `Item` subtypes (`Pickaxe`, `Food`, `OreBlock`, …) stored together in each `Inventory`'s item list |
| ≥ 1 service class | `PlayerService`, `WorldService`, `InventoryService`, `EconomyService`, `AchievementService`, `PluginService`, `SessionService`, `StaffService` |
| `Main` with service calls | `Main.java` instantiates all services and calls them for demo |

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
