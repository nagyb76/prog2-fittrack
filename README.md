# FitTrack - Workout & Calorie Tracker

**Készítette:** Nagy Bence  
**Tárgy:** Programozás 2 – Java OOP Projektfeladat  

A FitTrack egy Java nyelvű, objektumorientált konzolos fitnesz- és edzéskövető alkalmazás. Különböző típusú edzésformák (kardió, súlyzós edzés, valamint nyújtás/jóga) naplózására, az elégetett kalóriák különböző algoritmusokkal történő becslésére, az edzések rendezésére/szűrésére és JSON alapú perzisztens tárolására szolgál.

---

## Főbb funkciók és OOP követelmények teljesülése

1. **Egységbezárás (Encapsulation):**
   - Minden belső állapot rejtett (`private` mezők), a hozzáférés ellenőrzött getter/setter metódusokon és konstruktorokon keresztül történik.
   - Konstruktor szintű üzleti validáció védi a rendszert az inkonzisztens adatoktól.
2. **Absztrakció (Abstraction):**
   - `WorkoutSession` absztrakt ősosztály, `CalorieCalculationStrategy` és `WorkoutRepository` interfészek.
3. **Öröklődés és polimorfizmus (Inheritance & Polymorphism):**
   - `WorkoutSession` $\rightarrow$ `CardioWorkout`, `StrengthWorkout`, `FlexibilityWorkout`.
   - Felüldefiniált viselkedések: `getWorkoutType()`, `getSpecificDetails()`, `getBaseMetValue()`.
4. **Kapcsolatok (Relationships):**
   - Kompozíció: `WorkoutData` és `UserProfile` (1:1).
   - Aggregáció: `WorkoutData` és `WorkoutSession` (0..*).
   - Függőség / delegáció: `WorkoutSession` és `CalorieCalculationStrategy`.
5. **Kollekciók és rendezés (Collections & Generics):**
   - `List<WorkoutSession>` (`ArrayList`).
   - Természetes rendezés: `Comparable<WorkoutSession>` (dátum szerint csökkenő).
   - Alternatív rendezések: `Comparator` metódusreferenciával és lambdával (időtartam, elégetett kalória, cím).
6. **Kivételkezelés (Exceptions):**
   - Saját ellenőrzött kivételek: `InvalidWorkoutDataException`, `WorkoutNotFoundException`.
   - Fájlműveleteknél `try-with-resources`.
7. **Perzisztencia (Persistence):**
   - JSON alapú tárolás Google Gson könyvtárral (`GsonWorkoutRepository`), polimorf típusadapterrel (`WorkoutSessionAdapter`, `LocalDateAdapter`).

---

## Alkalmazott tervezési minta: Strategy (Stratégia)

- **Probléma:** Az elégetett kalória többféleképpen becsülhető attól függően, hogy van-e mért pulzusadatunk vagy csak az edzés intenzitását (MET) és időtartamát ismerjük. Ha a számítási képleteket `if-else` ágakkal közvetlenül a `WorkoutSession` osztályokba égetnénk, az sértené az Open/Closed elvet.
- **Résztvevő osztályok (lásd `docs/uml_diagram.md`):**
  - `CalorieCalculationStrategy` (interfész – `strategy/CalorieCalculationStrategy.java`)
  - `MetBasedStrategy` (konkrét stratégia – `strategy/MetBasedStrategy.java`)
  - `HeartRateStrategy` (konkrét stratégia – `strategy/HeartRateStrategy.java`)
  - `WorkoutSession` (kliens/kontextus – `calculateCalories(profile, strategy)` delegáció)
  - `WorkoutTrackerService` (az aktív stratégiát futásidőben tárolja és cseréli)
- **Előny:** Az algoritmusok futásidőben szabadon cserélhetők a menüből, és új számítási modellek a meglévő domain osztályok módosítása nélkül hozzáadhatók.

---

## Főbb tervezési döntések és ismert korlátok

### Tervezési döntések
- **Polimorf JSON szerializáció:** Mivel a `WorkoutSession` absztrakt osztály, a `WorkoutSessionAdapter` egy `"type"` mezőt ír a JSON-be (`CARDIO`, `STRENGTH`, `FLEXIBILITY`), így visszatöltéskor pontosan az eredeti altípus példányosodik.
- **Külön rétegek (Separation of Concerns):** A `model`, `strategy`, `persistence`, `service`, `ui` és `exception` csomagok függetlenek, a `service` csak a `WorkoutRepository` absztrakciótól függ.
- **Automatikus mintaadat-betöltés:** Ha induláskor még nem létezik a `data/workouts.json`, a program alapértelmezett edzésekkel inicializálja a naplót.

### Ismert korlátok
- A konzolos felület egyfelhasználós (egyszerre egy `UserProfile`-t kezel).
- A pulzusalapú kalóriakalkuláció (Keytel-formula) a `CardioWorkout` esetében érhető el; ha nincs megadva pulzus (`0`), a stratégia automatikusan a MET-alapú számításra esik vissza.
- A mentett adatok alapértelmezett helye a helyi `data/workouts.json` fájl, automatikus felhőszinkronizáció nélkül.

---

## Projekt felépítése

```text
fittrack/
├── docs/
│   ├── uml_diagram.md
│   ├── uml_diagram.puml
│   └── user_stories.md
├── src/
│   ├── main/java/hu/prog2/fittrack/
│   │   ├── Main.java
│   │   ├── exception/
│   │   ├── model/
│   │   ├── persistence/
│   │   ├── service/
│   │   ├── strategy/
│   │   └── ui/
│   └── test/java/hu/prog2/fittrack/
│       ├── model/
│       ├── persistence/
│       ├── service/
│       └── strategy/
├── pom.xml
└── README.md
```

---

## Fordítás, tesztelés és futtatás

### Előfeltételek
- Java 17 vagy újabb (OpenJDK)
- Apache Maven 3.8+

### Tesztek futtatása
```bash
mvn clean test
```

### Alkalmazás fordítása és futtatható JAR csomagolása
```bash
mvn clean package
```

### Alkalmazás futtatása
```bash
java -jar target/fittrack-1.0.0.jar
```

---

## Példa használat (Sample Usage)

```text
===============================================================
               FITTRACK - WORKOUT & CALORIE TRACKER           
        Java OOP Projectwork | Strategy Pattern & Persistence 
===============================================================
Active Profile: Bence
Active Strategy: Heart-Rate Based (Keytel Precision)
===============================================================
--- MAIN MENU ---
1. Log New Workout
2. View & Sort Workouts
...
Select an option (0-9): 2

--- SORTING OPTIONS ---
1. Natural Ordering (Date: Most recent first) [Comparable]
...
Select sorting method (1-5): 1
----------------------------------------------------------------------------------------------------------------
ID    | Category     | Title                | Date       | Duration | Calories | Details
----------------------------------------------------------------------------------------------------------------
#4    | Cardio       | Interval Sprint S... | 2026-11-26 |   35 min |  587,6 k | Distance: 5.20 km, Avg Speed: 8.9 km/h, Avg HR: 168 bpm
#3    | Flexibility & Yoga | Post-run Mobility... | 2026-11-25 |   30 min |  150,0 k | Focus: Hips & Lower Back, Intensity: 3/5
#2    | Strength     | Upper Body Hypert... | 2026-11-24 |   60 min |  450,0 k | 16 sets x 10 reps @ 65.0 kg (Tonnage: 10400.0 kg)
#1    | Cardio       | Morning Trail Run    | 2026-11-23 |   45 min |  646,9 k | Distance: 7.50 km, Avg Speed: 10.0 km/h, Avg HR: 152 bpm
----------------------------------------------------------------------------------------------------------------
```
