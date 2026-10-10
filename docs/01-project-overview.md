# Project Overview — Paperless World (PS 51)

## Summary
E-signature platform to facilitate **secure and efficient signing of digital documents**, eliminating physical signatures.

## Objectives
- Streamline document signing electronically
- Reduce need for physical signatures, improve turnaround time
- Enhance security and traceability (every sign/review is audited)

## User Types
| Role | Responsibility |
|------|---------------|
| **Administrator** | Manages platform, user roles, access permissions |
| **Signer** | Signs digital documents electronically |
| **Reviewer** | Reviews signed docs for authenticity & compliance |

## Functionalities (from brief)

**For Administrator**
- User Management — Input: user details/roles → Confirmation on create/update/delete
- Access Control — Input: role-based permissions → Assign/manage permissions
- System Maintenance — Input: system settings → System status reports

**For Signer**
- Document Signing — Input: digital doc + signature → Confirmation of signing
- Signature Management — Input: signature templates → Signature status

**For Reviewer**
- Document Review — Input: signed docs + logs → Review reports
- Compliance Check — Input: compliance criteria → Compliance status

## Dashboards (expected)
- Admin: table of users/roles, graphical permissions, system metrics
- Signer: signing interface, template tools, signed docs table
- Reviewer: signed docs + logs table, compliance status & reports

## Tech Stack (Review 1)
- Language: Java 17, Swing (desktop UI — no server setup needed for submission)
- DB: H2 file (`./data/paperless`) via JDBC (zero-install, demonstrable on any machine)
- Tests: JUnit 4
- Build: plain `javac` + `.vscode/settings.json` (no Maven install required)
