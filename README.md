# MICROVERSE Android — milestone 0.1

Progetto iniziale Android nativo in Kotlin. Include:
- Motore di simulazione separato dalla UI.
- Organismi con genoma simulato, bisogni, memoria locale e decisioni probabilistiche.
- Riproduzione con mutazione, mortalità e pressione ambientale.
- Modalità osservatore/creatore e visualizzazione 2D con pan.
- Salvataggio iniziale SQLite.
- Workflow GitHub Actions per compilare un APK debug.

## Compilazione
Aprire il progetto con Android Studio oppure caricare il repository su GitHub.
Il workflow `.github/workflows/android.yml` compila con `gradle assembleDebug` e pubblica l'APK come artifact.

## Stato e limiti
Questa è una base tecnica iniziale, non la simulazione completa descritta nel progetto.
La persistenza salva un riepilogo JSON, ma il ripristino completo dello stato e l'avanzamento offline sono ancora da implementare.
Il motore usa un modello biologico semplificato: non simula molecole, coscienza o libero arbitrio reale.
Le prossime milestone devono aggiungere ripristino completo, tempo offline controllato, specie e catene trofiche, epidemie, biomi, storia/fossili, test e ottimizzazione.
