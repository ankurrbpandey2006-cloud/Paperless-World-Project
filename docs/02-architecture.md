# Architecture & Design

## Layered Architecture

```
UI (Swing)
  ↓
Service Layer  (business logic, validation, async audit, collections)
  ↓
DAO Layer      (JDBC, GenericDAO<T,K>, PreparedStatement, ResultSet mapping)
  ↓
DBUtil → H2 file DB  (./data/paperless.mv.db)
```

## Package Structure

```
src/App.java
src/com/paperless/
  model/       Role, DocumentStatus, ComplianceStatus, Auditable, BaseEntity,
               User, Document, SignatureTemplate, Signature, AuditLog, Review
  exception/   PaperlessException → AuthenticationException, ValidationException
  util/        DBUtil, PasswordUtil, Validator, AsyncExecutor
  dao/         GenericDAO<T,K>, UserDAO, DocumentDAO, SignatureDAO,
               SignatureTemplateDAO, AuditLogDAO, ReviewDAO
  service/     AuthService, UserService, DocumentService,
               SignatureService, ReviewService
  ui/          UIStyle, LoginFrame, AdminDashboard, SignerDashboard, ReviewerDashboard
test/com/paperless/PaperlessTest.java
```

## Design Decisions

- **Swing over Servlets/JSP** for Review 1: runs with only `java` installed — no Tomcat or servlet container needed. Reviewer can double-click `run.bat`. JSP/Servlet migration is trivial (services/DAOs are UI-agnostic).
- **H2 file DB**: zero credential/config vs MySQL; same JDBC API — swap `DBUtil.URL` to MySQL when deploying. Satisfies "Database Connectivity (JDBC)" rubric directly.
- **Layering**: UI never touches `Connection`; services orchestrate validation + DAO + async audit. Keeps rubric "classes for DB operations" cleanly separated.

## OOP Mapping

| Rubric ask | Implementation |
|---|---|
| Inheritance | `BaseEntity` ← `User, Document, Signature, ...` |
| Interfaces | `Auditable`, `GenericDAO<T,K>` |
| Polymorphism | `getDisplaySummary()` + `auditInfo()` default method |
| ExceptionHandling | `PaperlessException` hierarchy + `throws`/`try-with-resources` |
| Abstraction | `BaseEntity` abstract, `GenericDAO` interface |

## Concurrency

- `AsyncExecutor` fixed pool (4 threads) for fire-and-forget audit writes.
- `synchronized` on `SignatureService.signDocument()` and `UserService.getAllUsers()` — correctness under concurrent UI actions. Demonstrated by `testConcurrentDocumentCreation`.

## Data Flow Example — Signing

`LoginFrame → SignerDashboard.signSelected() → SignatureService.signDocument() [synchronized] → SignatureDAO.save() + DocumentDAO.update(SIGNED) → AsyncExecutor → AuditLogDAO.save()`
