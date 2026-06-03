CREATE TABLE IF NOT EXISTS ranks (
    name TEXT PRIMARY KEY,
    prefix TEXT,
    color TEXT,
    permissions TEXT,
    weight INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS worlds (
    name TEXT PRIMARY KEY,
    seed INTEGER NOT NULL,
    world_type TEXT NOT NULL,
    difficulty TEXT NOT NULL,
    max_players INTEGER NOT NULL,
    spawn_x INTEGER NOT NULL,
    spawn_y INTEGER NOT NULL,
    spawn_z INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS players (
    uuid TEXT PRIMARY KEY,
    username TEXT NOT NULL,
    join_date TEXT NOT NULL,
    playtime_minutes INTEGER NOT NULL,
    xp INTEGER NOT NULL,
    balance REAL NOT NULL,
    player_type TEXT NOT NULL,
    rank_name TEXT,
    current_world TEXT,
    extra_homes INTEGER,
    staff_id TEXT,
    FOREIGN KEY (rank_name) REFERENCES ranks(name),
    FOREIGN KEY (current_world) REFERENCES worlds(name)
);

CREATE TABLE IF NOT EXISTS items (
    item_id TEXT PRIMARY KEY,
    display_name TEXT NOT NULL,
    max_stack_size INTEGER NOT NULL,
    rarity TEXT NOT NULL,
    item_type TEXT NOT NULL,
    durability INTEGER,
    material TEXT,
    damage INTEGER,
    hunger_restored INTEGER,
    effect TEXT,
    duration_seconds INTEGER,
    hardness INTEGER,
    stackable INTEGER,
    mineral_type TEXT,
    xp_drop INTEGER
);

CREATE TABLE IF NOT EXISTS plugins (
    name TEXT PRIMARY KEY,
    version TEXT NOT NULL,
    author TEXT NOT NULL,
    enabled INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS plugin_dependencies (
    plugin_name TEXT NOT NULL,
    depends_on TEXT NOT NULL,
    PRIMARY KEY (plugin_name, depends_on),
    FOREIGN KEY (plugin_name) REFERENCES plugins(name) ON DELETE CASCADE,
    FOREIGN KEY (depends_on) REFERENCES plugins(name)
);

CREATE TABLE IF NOT EXISTS achievements (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    xp_reward INTEGER NOT NULL,
    parent_id TEXT,
    FOREIGN KEY (parent_id) REFERENCES achievements(id)
);

CREATE TABLE IF NOT EXISTS player_achievements (
    player_uuid TEXT NOT NULL,
    achievement_id TEXT NOT NULL,
    PRIMARY KEY (player_uuid, achievement_id),
    FOREIGN KEY (player_uuid) REFERENCES players(uuid) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(id)
);
