# FitTrack - Workout & Calorie Tracker

FitTrack egy Java nyelvű, objektumorientált asztali és konzolos fitnesz- és edzéskövető alkalmazás. Különböző típusú edzésformák (kardió, saját testsúlyos / súlyzós edzés, valamint nyújtás és jóga) naplózására, elemzésére és perzisztens tárolására szolgál.

---

## Főbb funkciók és OOP követelmények teljesülése

1. **Egységbezárás (Encapsulation):**
   - Minden belső állapot rejtett (`private` mezők), a hozzáférés védett getter/setter metódusokon és konstruktorokon keresztül történik.
   - Konstruktor szintű üzleti validáció védi a rendszert az inkonzisztens adatoktól.
2. **Absztrakció (Abstraction):**
   - `WorkoutSession` absztrakt ősosztály, `CalorieCalculationStrategy` és `WorkoutRepository` interfészek.
3. **Öröklődés és polimorfizmus (Inheritance & Polymorphism):**
   - `WorkoutSession` $\rightarrow$ `CardioWorkout`, `StrengthWorkout`, `FlexibilityWorkout`.
   - Felüldefiniált viselkedések: `getWorkoutType()`, `getSpecificDetails()`, `getBaseMetValue()`.
4. **Kapcsolatok (Relationships):**
   - Kompozíció: `WorkoutData` és `UserProfile`.
   - Aggregáció: `WorkoutData` és `WorkoutSession`.
   - Függőség / delegáció: `WorkoutSession` és `CalorieCalculationStrategy`.
5. **Kollekciók és rendezés (Collections & Generics):**
   - `List<WorkoutSession>` (`ArrayList`).
   - Természetes rendezés: `Comparable<WorkoutSession>` (dátum szerint csökkenő).
   - Alternatív rendezések: `Comparator` metódusreferenciával és lambdával (időtartam, elégetett kalória, cím).
6. **Kivételkezelés (Exceptions):**
   - Saját ellenőrzött kivételek: `InvalidWorkoutDataException`, `WorkoutNotFoundException`.
   - Fájlműveleteknél `try-with-resources`.
7. **Perzisztencia (Persistence):**
   - JSON alapú tárolás Google Gson könyvtárral (`GsonWorkoutRepository`), polimorf típusadapterrel.
8. **Tervezési minta (Design Pattern):**
   - **Stratégia (Strategy Pattern):** `CalorieCalculationStrategy`, `MetBasedStrategy`, `HeartRateStrategy`.
   - Lehetővé teszi az elégetett kalória kiszámítási módjának futásidejű cseréjét anélkül, hogy a domain osztályokat módosítani kellene.

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

## Fordítás és futtatás

### Előfeltételek
- Java 17 vagy újabb (OpenJDK)
- Apache Maven 3.8+

### Tesztek futtatása
```bash
mvn clean test
```

### Alkalmazás fordítása és csomagolása
```bash
mvn clean package
```

### Alkalmazás futtatása
```bash
java -jar target/fittrack-1.0.0.jar
```
vagy Maven exec segítségével:
```bash
mvn compile exec:java -Dexec.mainClass="hu.prog2.fittrack.Main"
```
