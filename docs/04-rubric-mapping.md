# HCL GUVI Review 1 — Rubric Mapping

Review 1 assesses: **Project structure, DB design, DB connectivity, UI / UX** and code quality. This doc maps each rubric item to evidence.

## Applicable Rubric (Java — Desktop track)

| # | Criterion | Marks | Evidence in this submission |
|---|-----------|-------|-----------------------------|
| 1 | OOP (Polymorphism, Inheritance, ExceptionHandling, Interfaces) | 10 | `model/Auditable.java` (interface + default method), `model/BaseEntity.java` (abstract), 6 models `extends BaseEntity`, `exception/PaperlessException` hierarchy, `getDisplaySummary()` polymorphism, `GenericDAO<T,K>` |
| 2 | Collections & Generics | 6 | `GenericDAO<T,K>`, `UserService.filterByRole<T>`, `groupByRole()`, `countByRole()`, `statusCounts()`, `Collectors.groupingBy`, `Comparator` sorting, `test/PaperlessTest` |
| 3 | Multithreading & Synchronization | 4 | `util/AsyncExecutor.java` (FixedThreadPool 4), `synchronized signDocument()` & `getAllUsers()`, async audit writes, `testConcurrentDocumentCreation` (5 threads) |
| 4 | Classes for DB operations | 7 | 6 DAOs (`UserDAO`, `DocumentDAO`, `SignatureDAO`, `SignatureTemplateDAO`, `AuditLogDAO`, `ReviewDAO`) + 5 Services (`AuthService`, `UserService`, `DocumentService`, `SignatureService`, `ReviewService`) — single-responsibility |
| 5 | Database Connectivity (JDBC) | 3 | `util/DBUtil.getConnection()` via `DriverManager`, `Class.forName("org.h2.Driver")`, `PreparedStatement` throughout |
| 6 | JDBC for DB connectivity (schema + CRUD) | 3 | `DBUtil.initDatabase()` — 6 tables DDL with FKs, idempotent seed, full CRUD with `ResultSet` mapping in every DAO |

Total: **33 marks** (desktop track).

## If evaluator uses Java Web track (alternative mapping)

| Web criterion | Evidence |
|---|---|
| Problem Understanding & Solution Design | `docs/01-project-overview.md` + `02-architecture.md` |
| Core Java Concepts | Same as #1–3 above |
| Database Integration (JDBC) | Same as #5–6 |
| Servlets & Web Integration | Architecture is servlet-ready: `service/*` + `dao/*` are framework-agnostic — UI is the only swap (Swing → JSP/Servlet). Noted in `docs/02-architecture.md`. |

## Review 1 Deliverables Checklist

- [x] Project structure (`src/` packages, `lib/`, `.vscode/settings.json`, `bin/` output)
- [x] Database design (`docs/03-database-design.md` + ER + `DBUtil` DDL)
- [x] Database connectivity (working H2 via JDBC, `run.bat` demonstrable)
- [x] UI / UX (Login + 3 role dashboards — screenshots in `docs/05-ui-flows.md`, live in `src/com/paperless/ui/`)
- [x] Code quality (layered, commented, modular, JUnit 15 tests, `.gitignore`)
- [x] Presentation file (`docs/Paperless-World-Review1.pptx`)

## How to Evaluate Quickly

1. `run.bat` → login with `admin@paperless.com / admin123`
2. Check Admin tabs: User Management / Access Control / System Maintenance → metrics & audit viewer
3. `alice@paperless.com / signer123` → New Document → Create Template → Sign
4. `bob@paperless.com / reviewer123` → View logs → Review → Compliance counts
5. `test.bat` → 15/15 OK

## Notes for Reviewer

- No Maven/Tomcat/MySQL install needed — pure JDK 17+.
- To switch to MySQL: change `DBUtil.URL/USER/PASS`.
- To switch to Servlets/JSP: keep `service/*` + `dao/*` + `model/*`; replace `ui/*` with servlets + JSPs (already isolated).
