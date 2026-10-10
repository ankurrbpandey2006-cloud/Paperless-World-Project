# UI / UX — Flows & Aesthetics

## Theme (UIStyle.java)
- Primary: #1A73E8 / #0D47A1 (Admin), #0F6B3A (Signer), #4C1D95 (Reviewer) — role-color header
- Background: #F5F7FB, Cards: white with rounded border
- Fonts: Segoe UI (Title 22 Bold, H2 15 Bold, Body 13)
- Tables: alternating header #EEF2FF, row height 26, grid #E5E7EB

## Responsible / Consistent
- Layout: BorderLayout + GridBagLayout — resizes cleanly; max-width cards.
- All dialogs via JOptionPane — consistent modal pattern.
- Buttons: primary (filled) vs outline — visual hierarchy.

## Flows

### 1. Login (LoginFrame)
Email + Password → `AuthService.login()` → role-based redirect. Demo credentials shown on screen. Enter key = login.

### 2. Administrator Dashboard
- Header: dark blue, user badge + Logout
- **User Management**: sortable table (ID, Name, Email, Role, Active) → Add / Edit / Delete / Refresh. Dialog validates email + password length.
- **Access Control**: role cards (counts) + static permission matrix + counts from `UserService.countByRole()`.
- **System Maintenance**: metrics (users, audits, JVM memory, processors, DB path, status ● Operational) + View Audit Logs (table dialog).

### 3. Signer Dashboard
- Header: green
- **Document Signing**: table (all docs, status, compliance, created). Buttons: New Document (title + content) → PENDING_SIGNATURE; Sign Selected → choose template → hash + SIGNED + async audit.
- **Signature Management**: table of user's templates → Create (name + styled data) → status; Delete.
- **Signed Documents**: table of all signatures (id, doc, signer, hash prefix, IP, time).

### 4. Reviewer Dashboard
- Header: purple
- **Document Review**: top table = signed docs (ID, Title, Status, Compliance, Owner); bottom table = reviews. Buttons: View Signature Logs (per-doc), View Audit Logs, Review Selected → dialog (comments, compliance dropdown, approved checkbox) → REVIEWED/REJECTED + compliance update + audit; Refresh.
- **Compliance Check**: summary (COMPLIANT / NON_COMPLIANT / PENDING counts) + review reports table.

### 5. Traceability
Every sign & review emits an `AuditLog` via `AsyncExecutor` — visible in Admin (all) and Reviewer (per-doc) audit viewers.

## Screenshots (capture these for PPT)
1. Login (with demo credentials visible)
2. Admin — User Management table
3. Admin — Access Control permission matrix
4. Admin — System Maintenance metrics + audit dialog
5. Signer — Document Signing + New Document dialog
6. Signer — Signature Management + Create Template dialog
7. Signer — Signed table
8. Reviewer — Document Review (both tables)
9. Reviewer — Signature Logs dialog
10. Reviewer — Review dialog + Compliance counts

> Tip: after running, create one doc → one template → sign → review to have non-empty data for screenshots.
