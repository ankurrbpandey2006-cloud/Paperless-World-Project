# 51 — Paperless World: E-Signature Platform

Secure and efficient digital document signing — eliminating physical signatures. Built for **HCL GUVI Hackathon Review 1** (Project structure + DB design + DB connectivity + UI/UX).

## Live Demo (how to run)

```bat
run.bat
```
Or manually:
```bat
javac --release 17 -cp "lib/*" -d bin src\App.java src\com\paperless\model\*.java src\com\paperless\exception\*.java src\com\paperless\util\*.java src\com\paperless\dao\*.java src\com\paperless\service\*.java src\com\paperless\ui\*.java
java  -cp "bin;lib/*" App
```

Tests:
```bat
test.bat
```
> Requires **Java 17+**. No Maven/Gradle needed — dependency is `lib/h2-2.2.224.jar`.

Demo logins (seeded on first run):
| Role | Email | Password |
|------|-------|----------|
| Administrator | `admin@paperless.com` | `admin123` |
| Signer | `alice@paperless.com` | `signer123` |
| Reviewer | `bob@paperless.com` | `reviewer123` |

## Architecture

```
src/
  App.java                          # Entry point — DB init + Login
  com/paperless/
    model/      Role, DocumentStatus, ComplianceStatus, Auditable (interface),
                BaseEntity (abstract), User, Document, SignatureTemplate,
                Signature, AuditLog, Review
    exception/  PaperlessException, AuthenticationException, ValidationException
    util/       DBUtil (JDBC + schema + seed), PasswordUtil (SHA-256),
                Validator, AsyncExecutor (thread pool)
    dao/        GenericDAO<T,K> (generics), UserDAO, DocumentDAO,
                SignatureDAO, SignatureTemplateDAO, AuditLogDAO, ReviewDAO
    service/    AuthService, UserService, DocumentService,
                SignatureService, ReviewService
    ui/         UIStyle, LoginFrame, AdminDashboard,
                SignerDashboard, ReviewerDashboard
test/com/paperless/PaperlessTest.java   # 15 JUnit 4 tests
lib/  h2-2.2.224.jar, junit-4.13.2.jar, hamcrest-core-1.3.jar
data/ paperless.mv.db  (auto-created H2 file DB)
.vscode/settings.json  (src + test, lib/**/*.jar)
```

## Spec Coverage

| Role | Functionality (from prompt) | Implementation |
|------|-----------------------------|---------------|
| **Administrator** | **User Management** — manage accounts/roles | User table + service + dashboard tab |
| | **Access Control** — role-based permissions | `Role` enum + permission matrix + UI |
| | **System Maintenance** — settings & health | Metrics (users, audits, JVM), audit log viewer |
| **Signer** | **Document Signing** — sign digital docs | New document + Sign with template (hash + audit) |
| | **Signature Management** — create/manage templates | CRUD for signature templates |
| | **Signed Documents** — list signed docs | Signed table (all signatures) |
| **Reviewer** | **Document Review** — review for authenticity | Signed docs + signature + audit logs viewer, review submission |
| | **Compliance Check** — regulatory compliance | Compliance status + counts dashboard, review reports |

Dashboards (as per spec):
- **Admin:** table of users/roles, graphical access permissions, system status & metrics
- **Signer:** signing interface, template tools, signed docs table
- **Reviewer:** signed docs + logs table, compliance status & reports

Also: **Audit traceability** on every sign/review (async via thread pool).

## Review 1 Rubric Mapping

| # | Criterion | Marks | Where it is shown |
|---|-----------|-------|-------------------|
| 1 | OOP (Polymorphism, Inheritance, ExceptionHandling, Interfaces) | 10 | `Auditable` interface + `BaseEntity` abstract class + `User/Document/... extends BaseEntity` + `PaperlessException` hierarchy + `getDisplaySummary()` polymorphism |
| 2 | Collections & Generics | 6 | `GenericDAO<T,K>`, `UserService.filterByRole<T>`, `groupByRole()`, `countByRole()`, `Collectors.groupingBy` in services, `List/Map` throughout |
| 3 | Multithreading & Synchronization | 4 | `AsyncExecutor` (FixedThreadPool for audits), `synchronized signDocument()` + `synchronized getAllUsers()`, concurrent test |
| 4 | Classes for DB operations | 7 | 6 DAO classes + 5 Service classes (separate responsibilities) |
| 5 | Database Connectivity (JDBC) | 3 | `DBUtil.getConnection()` via `DriverManager` + `PreparedStatement` everywhere |
| 6 | JDBC for DB connectivity (schema + CRUD) | 3 | `initDatabase()` DDL for 6 tables + FKs + seed, all DAO CRUD with `ResultSet` mapping |

## Database Design

H2 file DB `./data/paperless` (zero setup). Tables: `users`, `documents`, `signature_templates`, `signatures`, `audit_logs`, `reviews` with FKs. Schema is idempotent — rerunning never duplicates.

ER (simplified): `users 1—N documents`, `users 1—N signature_templates`, `documents 1—N signatures`, `documents 1—N audit_logs`, `documents 1—N reviews`.

## Build Verification

```
javac --release 17 -cp "lib/*" -d bin [all src]   # 43 classes
javac --release 17 -cp "bin;lib/*" -d bin test/com/paperless/PaperlessTest.java
java  -cp "bin;lib/*" org.junit.runner.JUnitCore com.paperless.PaperlessTest  # OK (15 tests)
```

## Screenshots (for presentation)

Login → role-based redirect → Admin (User Mgmt / Access Control / System Metrics) → Signer (New Doc / Create Template / Sign / Signed list) → Reviewer (View logs / Submit review / Compliance counts). Use the seeded accounts above to capture each flow.

## Notes

- Passwords are SHA-256 hashed (`PasswordUtil`); production should use BCrypt.
- Whole project is VS Code ready: open folder → Run `App.main()` or `run.bat`.
