# One Thing - App Specification Document

## Overview

**One Thing** is a backlog task management app that helps users tackle random household/personal tasks they wouldn't normally schedule. The core mechanic is a "roll" system that randomly selects one task based on available time and user preferences.

**Core Concept**: Instead of overwhelming users with a full todo list, the app picks ONE task for them based on how much time they have available.

---

## Data Models

### Task
```sql
CREATE TABLE tasks (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(255) NOT NULL,
    category_id     BIGINT NOT NULL REFERENCES categories(id),
    time_bracket    ENUM('<15', '15-30', '30+') NOT NULL,
    priority        INT DEFAULT 2 CHECK (priority BETWEEN 1 AND 3),
    last_completed  TIMESTAMP NULL,
    prev_completed  TIMESTAMP NULL,  -- For undo functionality
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    parent_id       BIGINT NULL REFERENCES tasks(id),  -- For subtask relationships
    user_id         BIGINT NOT NULL REFERENCES users(id)
);
```

**Fields:**
- `name`: Task display name (e.g., "Clean floors")
- `category_id`: Foreign key to category
- `time_bracket`: One of three values - `<15` (under 15 min), `15-30` (15-30 min), `30+` (over 30 min)
- `priority`: Base priority 1-3 (1=low, 2=medium, 3=high)
- `last_completed`: Timestamp of most recent completion (NULL if never)
- `prev_completed`: Previous completion timestamp (used for undo)
- `parent_id`: If this is a subtask, references the parent task

### Category
```sql
CREATE TABLE categories (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    icon        VARCHAR(50) NOT NULL,  -- Icon identifier
    color       VARCHAR(7) NOT NULL,   -- Hex color code
    user_id     BIGINT NOT NULL REFERENCES users(id)
);
```

**Default Categories:**
| Name         | Icon     | Color (Primary) |
|--------------|----------|-----------------|
| Living Space | home     | #3B82F6 (blue)  |
| Dog          | dog      | #F59E0B (amber) |
| Maintenance  | wrench   | #94A3B8 (slate) |
| Outdoor      | leaf     | #10B981 (emerald)|

### User Preferences
```sql
CREATE TABLE user_preferences (
    user_id             BIGINT PRIMARY KEY REFERENCES users(id),
    randomness_slider   INT DEFAULT 50 CHECK (randomness_slider BETWEEN 0 AND 100),
    selected_category   VARCHAR(100) DEFAULT 'All'  -- 'All' or category name
);
```

### Common Suggestions (Static/Seed Data)
```sql
CREATE TABLE task_suggestions (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(id)
);
```

---

## Time Bracket System

Three fixed time brackets used throughout the app:

| Bracket | Display Label | Description |
|---------|---------------|-------------|
| `<15`   | "<15m" or "Under 15 min" | Quick tasks |
| `15-30` | "15-30m" or "15-30 min" | Medium tasks |
| `30+`   | "30+m" or "30+ min" | Longer tasks |

**Color Coding:**
- `<15`: Teal (`#14B8A6`)
- `15-30`: Violet (`#8B5CF6`)
- `30+`: Rose (`#F43F5E`)

---

## Subtask / Parent Task Relationships

Tasks can have parent-child relationships for multi-part activities.

**Example:**
```
Full line comb session (30+) [PARENT]
├── Line comb mane (15-30) [CHILD]
└── Line comb legs (15-30) [CHILD]
```

**Completion Rules:**
1. Completing a **parent task** marks ALL children as completed (same timestamp)
2. Completing ALL children individually marks the parent as completed
3. Children show as subtasks indented under the parent in the task list
4. Parent tasks display a **progress circle** showing % of children completed

**"Recently Completed" Definition:**
A task is considered "recently completed" if `last_completed` is within the last 7 days.

---

## Priority & Selection Algorithm

### Effective Priority Calculation

The selection algorithm uses an "effective priority" that combines base priority with time-based aging:

```
effectivePriority = basePriority + ageFactor

where:
  ageFactor = min(daysSinceLastActivity / 7, 5)
  daysSinceLastActivity = days since (last_completed OR created_at if never completed)
```

**Result:** Tasks that haven't been done in a while naturally rise in priority (capped at +5 boost after 5 weeks).

### Task Selection (Roll) Algorithm

```
Input: timeBracket, categoryFilter, randomnessSlider (0-100)

1. Filter tasks by:
   - time_bracket matches selected bracket
   - category matches filter (or all if 'All' selected)

2. Calculate weights for each eligible task:
   priorityWeight = (100 - randomnessSlider) / 100
   taskWeight = 1 + (effectivePriority * priorityWeight * 2)

3. Weighted random selection:
   - Sum all weights to get totalWeight
   - Generate random number 0 to totalWeight
   - Iterate through tasks, subtracting weights until random ≤ 0
   - Return that task

Output: Single selected task
```

**Slider Behavior:**
- Slider at 0 (Priority): Heavily favors overdue/high-priority tasks
- Slider at 50 (Balanced): Mix of priority and randomness
- Slider at 100 (Random): Nearly equal chance for all eligible tasks

---

## UI Screens & Components

### Screen 1: Roll Tab (Home)

**Initial State - Time Selection:**
```
┌─────────────────────────────────────┐
│            One Thing                │
├─────────────────────────────────────┤
│                                     │
│         [Sparkle Icon]              │
│          ★ One Thing                │
│   Pick something from your backlog  │
│                                     │
│   How much time do you have?        │
│                                     │
│  ┌─────┐  ┌─────┐  ┌─────┐         │
│  │ <15 │  │15-30│  │ 30+ │         │
│  │ min │  │ min │  │ min │         │
│  └─────┘  └─────┘  └─────┘         │
│     ↑        ↑        ↑             │
│   Teal    Violet    Rose            │
│                                     │
│      [ ⚙ Customize ]                │
│                                     │
├─────────────────────────────────────┤
│   [★ Roll]        [☰ Tasks]         │
└─────────────────────────────────────┘
```

**Rolling Animation State:**
- Duration: 1 second
- If randomness ≥ 66: Show animated **dice** (white cube, bouncing, faces changing rapidly)
- If randomness < 66: Show **spinning gradient ring** with sparkle icon in center
- Below animation: 3 bouncing dots

**Task Displayed State:**
```
┌─────────────────────────────────────┐
│            One Thing                │
├─────────────────────────────────────┤
│                                     │
│    [Icon] Living Space • <15m       │
│                                     │
│  ╔═══════════════════════════════╗  │
│  ║                               ║  │
│  ║      Wipe door handles        ║  │
│  ║                               ║  │
│  ║      Last done: 3d ago        ║  │
│  ║                               ║  │
│  ╚═══════════════════════════════╝  │
│                                     │
│         ● ● ○  priority             │
│                                     │
│  ┌──────────┐  ┌──────────────┐    │
│  │  Reroll  │  │  I'll do it! │    │
│  └──────────┘  └──────────────┘    │
│                                     │
│       ← Pick different time         │
│                                     │
├─────────────────────────────────────┤
│   [★ Roll]        [☰ Tasks]         │
└─────────────────────────────────────┘
```

**Task Completed State:**
- Card turns green with border
- Checkmark icon bounces at top of card
- "✓ Completed just now!" text
- Buttons change to: "Done for now" | "One more!"

### Screen 2: Tasks Tab (List View)

```
┌─────────────────────────────────────┐
│            One Thing                │
├─────────────────────────────────────┤
│                                     │
│  ┌─ Living Space ──────────── 8 ─┐  │
│  │ ○ Clean floors      15-30  Never│ │
│  │ ● Wipe handles       <15   3d   │ │ ← Green circle = done
│  │ ○ Vacuum everywhere  30+   4d   │ │
│  │ + Add task                      │ │
│  └─────────────────────────────────┘  │
│                                     │
│  ┌─ Dog ───────────────────── 6 ─┐  │
│  │ [50%] Full line comb  30+      │ │ ← Progress circle
│  │    ├ ● Line comb mane 15-30 3d │ │ ← Subtask indented
│  │    └ ○ Line comb legs 15-30 Nev│ │
│  │ ○ Trim nails          <15  14d │ │
│  │ + Add task                      │ │
│  └─────────────────────────────────┘  │
│                                     │
├─────────────────────────────────────┤
│   [★ Roll]        [☰ Tasks]         │
└─────────────────────────────────────┘
```

**Category Card Structure:**
- Header: Icon + Name + Task count badge (collapsible via tap)
- Task rows: Circle + Name + Time badge + Last completed date
- Footer: "+ Add task" button

**Task Circle Behavior:**
- Empty circle: Not recently completed (clickable)
- Green circle with checkmark: Recently completed (within 7 days)
- Click to toggle completion
- Scale animation on click (125% → 100%)

**Progress Circle (Parent Tasks):**
- Shows percentage of children completed recently
- Number displayed in center (e.g., "50%")
- Indigo colored arc

### Screen 3: Add Task (Inline)

Replaces "+ Add task" row when activated:

```
┌─────────────────────────────────────┐
│ ┌─────────────────────────────────┐ │
│ │ Mop k░ichen                     │ │ ← Ghost text autocomplete
│ │                     Tab ↑↓ 1/5  │ │
│ └─────────────────────────────────┘ │
│                                     │
│ Time: [<15] [15-30] [30+]          │
│                                     │
│ [Cancel]        [Add Task]          │
└─────────────────────────────────────┘
```

**Autocomplete Input Behavior:**
1. Ghost text appears in gray showing suggestion
2. Filters suggestions that START WITH typed text
3. When empty, shows first suggestion as ghost text
4. **Tab** key: Accept suggestion (fills input)
5. **↑/↓ Arrow** keys: Cycle through matching suggestions
6. **Enter** key: Submit task
7. Right side shows: `Tab` badge + `↑↓` badge + `1/5` counter (when multiple suggestions)

**Suggestion Filtering:**
- Only show suggestions not already in the category
- Filter to suggestions starting with user input (case-insensitive)

### Screen 4: Customize Modal

```
┌─────────────────────────────────────┐
│  Customize                     [X]  │
├─────────────────────────────────────┤
│                                     │
│  ★ Priority ◄━━━━━━●━━━━► 🎲 Random │
│                                     │
│  "Balanced selection"               │
│                                     │
├─────────────────────────────────────┤
│                                     │
│  Roll from category                 │
│                                     │
│  [All] [Living Space] [Dog]         │
│  [Maintenance] [Outdoor]            │
│                                     │
└─────────────────────────────────────┘
```

**Slider Labels:**
- 0-32: "Focuses on overdue tasks"
- 33-65: "Balanced selection"
- 66-100: "Mostly random picks"

---

## API Endpoints

### Tasks

```
GET    /api/tasks                    - List all tasks for user
GET    /api/tasks?category={id}      - List tasks by category
POST   /api/tasks                    - Create task
PUT    /api/tasks/{id}               - Update task
DELETE /api/tasks/{id}               - Delete task
POST   /api/tasks/{id}/complete      - Mark task completed
POST   /api/tasks/{id}/uncomplete    - Undo completion (restore prev_completed)
```

### Roll

```
POST   /api/roll
Body: {
  time_bracket: "<15" | "15-30" | "30+",
  category_id: number | null,  // null = all categories
  randomness: number           // 0-100
}
Response: {
  task: Task
}
```

### Categories

```
GET    /api/categories               - List categories
POST   /api/categories               - Create category
PUT    /api/categories/{id}          - Update category
DELETE /api/categories/{id}          - Delete category
```

### Suggestions

```
GET    /api/suggestions?category={id} - Get suggestions for category
```

### Preferences

```
GET    /api/preferences              - Get user preferences
PUT    /api/preferences              - Update preferences
```

---

## UI Component Specifications

### Time Bracket Button
- Size: ~100px width, ~100px height
- Border radius: 24px (rounded-3xl)
- Contains: Clock icon + bracket text + "minutes" label
- Background: Semi-transparent time color
- Border: Time color at 40% opacity
- Hover: Scale 105%, increased shadow
- Active: Scale 95%

### Task Card (Roll Screen)
- Padding: 40px (p-10)
- Border radius: 32px (rounded-[2rem])
- Background: Gradient from slate-800
- Border: slate-700 at 50% opacity
- Shadow: 2xl, black at 30%
- Glow overlay: Indigo/purple gradient at 10% opacity

### Task Circle
- Normal size: 28px (w-7 h-7)
- Small size (subtasks): 16px (w-4 h-4)
- Border: 2px solid
- Uncompleted: Transparent bg, slate-600 border
- Completed: Emerald-500 bg and border, white checkmark
- Animation: Scale to 125% on click, then back to 100%

### Progress Circle
- Size: 40px
- Stroke width: 3px
- Background track: slate-700
- Progress arc: indigo-500
- Center text: 10px bold, indigo-400

### Category Card
- Border radius: 16px (rounded-2xl)
- Border: Category color at 30% opacity
- Header background: Category color at 20% opacity
- Collapsed: Show only header
- Expanded: Show tasks + add button

### Bottom Navigation
- Fixed to bottom
- Background: slate-900 at 90% opacity with blur
- Border top: slate-800 at 50%
- Two equal tabs: Roll (sparkle icon) | Tasks (list icon)
- Active tab: indigo-400 color
- Inactive: slate-500 color

---

## Animations

### Roll Animation
- **Duration**: 1000ms
- **Dice Mode** (randomness ≥ 66):
  - White 80x80px rounded square
  - Bounce animation at 0.3s interval
  - Dice face changes every 80ms (random 1-6)
  - Indigo dots on white background
- **Priority Mode** (randomness < 66):
  - Gradient ring (indigo → purple)
  - Spin animation at 0.6s per rotation
  - Inner circle: slate-900
  - Center: Static sparkle icon in indigo-400
- **Loading Dots**: 3 dots, bounce animation staggered by 0.15s

### Task Completion
- Circle scales to 125%, then settles at 100% (300ms)
- Card background transitions to green gradient (500ms)
- Checkmark bounces into view

### Button Press
- Active state: Scale 95%
- Transition: All 200ms

---

## Color Palette

### Base Colors (Dark Theme)
- Background: slate-950 → slate-900 gradient
- Card backgrounds: slate-800, slate-900
- Borders: slate-700, slate-800
- Text primary: white
- Text secondary: slate-400
- Text muted: slate-500

### Accent Colors
- Primary: indigo-500 (#6366F1)
- Success: emerald-500 (#10B981)
- Primary shadow: indigo-500 at 30%

### Category Colors
| Category     | Primary   | Light (20%)    | Text         |
|--------------|-----------|----------------|--------------|
| Living Space | blue-500  | blue-500/20    | blue-400     |
| Dog          | amber-500 | amber-500/20   | amber-400    |
| Maintenance  | slate-400 | slate-400/20   | slate-300    |
| Outdoor      | emerald-500| emerald-500/20| emerald-400  |

### Time Bracket Colors
| Bracket | Background    | Text        | Border          |
|---------|---------------|-------------|-----------------|
| <15     | teal-500/20   | teal-400    | teal-500/40     |
| 15-30   | violet-500/20 | violet-400  | violet-500/40   |
| 30+     | rose-500/20   | rose-400    | rose-500/40     |

---

## Date Formatting

Function to format `last_completed` timestamps:

```
formatDate(timestamp):
  if null → "Never"
  
  days = floor((now - timestamp) / 86400000)
  
  if days == 0 → "Today"
  if days == 1 → "Yesterday"
  if days < 7  → "{days}d ago"
  if days < 30 → "{floor(days/7)}w ago"
  else         → "{floor(days/30)}mo ago"
```

---

## Default Seed Data

### Task Suggestions by Category

**Living Space:**
- Mop kitchen, Organize closet, Clean windows, Declutter drawer
- Wash curtains, Clean oven, Organize pantry, Dust shelves
- Clean baseboards, Wipe light switches

**Dog:**
- Bath time, Clean water bowl, Wash toys, Check flea treatment
- Paw balm application, Deshed brushing, Clean food bowl
- Wash collar, Dental chew

**Maintenance:**
- Test CO detector, Clean garbage disposal, Check fire extinguisher
- Lubricate hinges, Clean range hood filter, Check weather stripping
- Test GFCI outlets

**Outdoor:**
- Sweep driveway, Clean outdoor lights, Trim hedges
- Power wash walkway, Clean bird feeder, Check sprinklers
- Clean grill, Tidy garage

---

## State Management Summary

### Global State
- `tasks`: Array of all user tasks
- `categories`: Array of categories
- `preferences`: { randomness: number, selectedCategory: string }

### Roll Tab State
- `selectedTime`: null | '<15' | '15-30' | '30+'
- `currentTask`: null | Task
- `isRolling`: boolean
- `justCompleted`: boolean

### Tasks Tab State
- `expandedCategories`: string[] (list of expanded category names)
- `addingToCategory`: null | string (category currently adding to)
- `newTaskName`: string
- `newTaskTime`: '<15' | '15-30' | '30+'

---

## Notes for Implementation

1. **Completion Toggle Logic**: When unchecking a task, restore `prev_completed` to `last_completed` (allows undo of accidental completion)

2. **Parent-Child Sync**: When completing a parent, also update all children. When checking completion status of parent, calculate based on children's status.

3. **Autocomplete Performance**: Suggestion filtering should be client-side for responsiveness. Fetch suggestions once per category.

4. **Offline Consideration**: Consider caching tasks locally for offline viewing. Queue completions for sync.

5. **Responsive Design**: This spec assumes mobile-first. The mockup is optimized for ~400px width. Tablet/desktop can expand card widths but keep centered layout.
