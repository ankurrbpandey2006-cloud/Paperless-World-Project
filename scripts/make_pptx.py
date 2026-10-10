#!/usr/bin/env python3
"""Paperless World PS51 - Review 1 - 10 slides, larger fonts"""
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN
from pptx.enum.shapes import MSO_SHAPE

PRIMARY = RGBColor(0x1A, 0x73, 0xE8)
PRIMARY_DARK = RGBColor(0x0D, 0x47, 0xA1)
BG = RGBColor(0xF5, 0xF7, 0xFB)
TEXT = RGBColor(0x1F, 0x29, 0x37)
MUTED = RGBColor(0x6B, 0x72, 0x80)
WHITE = RGBColor(0xFF, 0xFF, 0xFF)
GREEN = RGBColor(0x0F, 0x6B, 0x3A)
PURPLE = RGBColor(0x4C, 0x1D, 0x95)
LIGHT_BLUE = RGBColor(0xDB, 0xEA, 0xFE)
LIGHT_GREEN = RGBColor(0xD1, 0xFA, 0xE5)
LIGHT_PURPLE = RGBColor(0xED, 0xE9, 0xFE)

prs = Presentation()
prs.slide_width = Inches(13.33)
prs.slide_height = Inches(7.5)

def bg(slide, color):
    f = slide.background.fill
    f.solid()
    f.fore_color.rgb = color

def rect(slide, l, t, w, h, fill, line=None, r=None):
    shp = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE if r else MSO_SHAPE.RECTANGLE, l, t, w, h)
    shp.fill.solid(); shp.fill.fore_color.rgb = fill
    if line is None: shp.line.fill.background()
    else: shp.line.color.rgb = line; shp.line.width = Pt(1)
    return shp

def txt(slide, l, t, w, h, text, sz=12, bold=False, color=TEXT, align=PP_ALIGN.LEFT, name="Calibri"):
    tb = slide.shapes.add_textbox(l, t, w, h)
    tf = tb.text_frame; tf.word_wrap = True
    p = tf.paragraphs[0]; p.text = text
    p.font.size = Pt(sz); p.font.bold = bold; p.font.color.rgb = color; p.font.name = name
    p.alignment = align
    return tb

def bullets(slide, l, t, w, h, items, sz=11, color=TEXT, bullet="• ", sp_after=4, line_sp=16):
    tb = slide.shapes.add_textbox(l, t, w, h)
    tf = tb.text_frame; tf.word_wrap = True
    for i, it in enumerate(items):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.text = bullet + it
        p.font.size = Pt(sz); p.font.color.rgb = color; p.font.name = "Calibri"
        p.space_after = Pt(sp_after); p.line_spacing = Pt(line_sp)
    return tb

def header(slide, title, subtitle, color=PRIMARY_DARK):
    rect(slide, Inches(0), Inches(0), prs.slide_width, Inches(1.05), color)
    txt(slide, Inches(0.5), Inches(0.22), Inches(9), Inches(0.40), title, sz=20, bold=True, color=WHITE)
    txt(slide, Inches(0.5), Inches(0.60), Inches(9), Inches(0.28), subtitle, sz=11, color=RGBColor(0xBF,0xD3,0xFF))
    rect(slide, Inches(10.6), Inches(0.30), Inches(2.25), Inches(0.48), WHITE, r=Inches(0.06))
    txt(slide, Inches(10.6), Inches(0.36), Inches(2.25), Inches(0.38), "PS 51  •  Review 1", sz=9, bold=True, color=color, align=PP_ALIGN.CENTER)

def card(slide, l, t, w, h, title, items, accent=PRIMARY, sz=10.5):
    rect(slide, l, t, w, h, WHITE, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.10))
    rect(slide, l, t, w, Inches(0.42), accent, r=Inches(0.10))
    txt(slide, l+Inches(0.20), t+Inches(0.08), w-Inches(0.40), Inches(0.28), title, sz=12, bold=True, color=WHITE)
    bullets(slide, l+Inches(0.22), t+Inches(0.55), w-Inches(0.44), h-Inches(0.65), items, sz=sz)

# ── 1 Title ──
sl = prs.slides.add_slide(prs.slide_layouts[6])
bg(sl, WHITE)
rect(sl, Inches(0), Inches(0), prs.slide_width, Inches(2.15), PRIMARY_DARK)
txt(sl, Inches(0.6), Inches(0.45), Inches(12), Inches(0.40), "HCL GUVI Hackathon — Review 1", sz=12, color=RGBColor(0xBF,0xD3,0xFF))
txt(sl, Inches(0.6), Inches(0.85), Inches(12), Inches(0.70), "Paperless World", sz=36, bold=True, color=WHITE)
txt(sl, Inches(0.6), Inches(1.52), Inches(8.5), Inches(0.32), "51 — E-Signature Platform  •  Secure & efficient digital signing", sz=13, color=WHITE)
txt(sl, Inches(0.6), Inches(2.40), Inches(12), Inches(0.28), "Java 17  •  Swing  •  JDBC (H2)  •  JUnit 4  •  Desktop — no server required", sz=9, color=MUTED)
rect(sl, Inches(9.0), Inches(0.95), Inches(3.55), Inches(1.70), WHITE, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.12))
txt(sl, Inches(9.25), Inches(1.08), Inches(3.1), Inches(0.24), "Team", sz=8, bold=True, color=MUTED)
txt(sl, Inches(9.25), Inches(1.30), Inches(3.1), Inches(0.24), "abhishek.25scse1010473@galgotiasuniversity.ac.in", sz=8, color=TEXT)
txt(sl, Inches(9.25), Inches(1.58), Inches(3.1), Inches(0.24), "Stack: Java / H2 / Swing / JDBC", sz=8, color=MUTED)
txt(sl, Inches(9.25), Inches(1.80), Inches(3.1), Inches(0.24), "Run:  run.bat  (JDK 17+ only)", sz=8, color=MUTED)
txt(sl, Inches(9.25), Inches(2.06), Inches(3.1), Inches(0.24), "Demo logins visible on login screen →", sz=8, bold=True, color=PRIMARY)
card(sl, Inches(0.45), Inches(3.05), Inches(4.15), Inches(2.95), "What we built", [
    "E-signature platform — eliminates physical signatures",
    "3 roles: Administrator / Signer / Reviewer",
    "6 functionalities + 3 dashboards (as per brief)",
    "Every sign & review is audited for traceability",
    "Runs offline: H2 file DB, no MySQL/Tomcat",
], PRIMARY, sz=10)
card(sl, Inches(4.75), Inches(3.05), Inches(4.15), Inches(2.95), "Review 1 focus (asked)", [
    "Project structure  ✓  layered, .vscode ready",
    "Database design  ✓  6 tables + ER diagram",
    "Database connectivity  ✓  JDBC working",
    "UI/UX — aesthetics & responsiveness  ✓",
    "Code quality — modular, commented, tested",
], GREEN, sz=10)
card(sl, Inches(9.05), Inches(3.05), Inches(3.85), Inches(2.95), "Quick verify (30 sec)", [
    "run.bat  →  Login (admin / admin123)",
    "Admin: users / permissions / metrics",
    "Signer: New Doc → Sign with template",
    "Reviewer: View Logs → Review → Compliance",
    "test.bat → 15/15 OK",
], PURPLE, sz=10)
txt(sl, Inches(0.6), Inches(6.35), Inches(12), Inches(0.22), "Agenda:  Problem  →  User Types  →  Admin  →  Signer  →  Reviewer  →  Architecture  →  Database  →  UI/UX  →  Rubric  →  Demo", sz=8, color=MUTED, align=PP_ALIGN.CENTER)
txt(sl, Inches(0.6), Inches(6.95), Inches(12), Inches(0.22), "© 2024 GUVI Geek Network — PS 51 • Submission for Review 1", sz=7, color=MUTED, align=PP_ALIGN.CENTER)

# ── 2 Problem & Objectives ──
sl = prs.slides.add_slide(prs.slide_layouts[6])
bg(sl, BG)
header(sl, "Problem & Objectives", "Why Paperless World matters")
card(sl, Inches(0.45), Inches(1.35), Inches(6.30), Inches(2.50), "Problem Statement", [
    "Manual signatures cause delays & need physical storage",
    "Weak traceability — hard to verify who signed when",
    "High turnaround time, paper costs, security gaps",
    "Need: secure electronic alternative",
], PRIMARY, sz=10.5)
card(sl, Inches(7.00), Inches(1.35), Inches(5.90), Inches(2.50), "Objectives & Outcomes", [
    "Streamline signing of digital documents electronically",
    "Reduce / eliminate need for physical signatures",
    "Improve turnaround time & operational efficiency",
    "Enhance security & traceability via audit logs",
], GREEN, sz=10.5)
rect(sl, Inches(0.45), Inches(4.10), Inches(12.45), Inches(2.00), WHITE, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.10))
txt(sl, Inches(0.70), Inches(4.25), Inches(12), Inches(0.30), "Summary — as per brief", sz=12, bold=True, color=TEXT)
bullets(sl, Inches(0.70), Inches(4.60), Inches(11.90), Inches(1.30), [
    "Develop an e-signature platform to facilitate secure and efficient signing of digital documents, eliminating the need for physical signatures.",
    "The platform will reduce dependence on paper, speed up approvals, and provide a verifiable audit trail for every signature and review.",
], sz=10, color=MUTED)

# ── 3 User Types ──
sl = prs.slides.add_slide(prs.slide_layouts[6])
bg(sl, BG)
header(sl, "User Types & Roles", "3 roles — clear responsibilities & permissions")
# big role cards
for idx, (name, icon, desc, col, light) in enumerate([
    ("Administrator", "◈", "Manages platform, user roles & access", PRIMARY_DARK, RGBColor(0xEE,0xF2,0xFF)),
    ("Signer", "✍", "Signs digital documents electronically", GREEN, LIGHT_GREEN),
    ("Reviewer", "◎", "Reviews signed docs for authenticity & compliance", PURPLE, LIGHT_PURPLE),
]):
    l = Inches(0.45 + idx*4.45)
    rect(sl, l, Inches(1.35), Inches(4.15), Inches(2.40), WHITE, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.12))
    rect(sl, l, Inches(1.35), Inches(4.15), Inches(0.55), col, r=Inches(0.12))
    txt(sl, l+Inches(0.15), Inches(1.45), Inches(0.55), Inches(0.35), icon, sz=18, bold=True, color=WHITE, align=PP_ALIGN.CENTER)
    txt(sl, l+Inches(0.70), Inches(1.42), Inches(3.20), Inches(0.35), name, sz=14, bold=True, color=col)
    txt(sl, l+Inches(0.20), Inches(1.98), Inches(3.75), Inches(0.50), desc, sz=10, color=MUTED)
    txt(sl, l+Inches(0.20), Inches(2.50), Inches(3.75), Inches(0.24), "Login: role-based dashboard redirect", sz=8, color=MUTED)
card(sl, Inches(0.45), Inches(4.05), Inches(12.45), Inches(1.60), "Access Control at a Glance", [
    "Administrator → Full: User Mgmt, Access Control, System Maintenance, Audit viewer",
    "Signer → Document Signing, Signature Templates, Signed Documents list",
    "Reviewer → Document Review (signed docs + logs), Compliance Check & Reports",
], MUTED, sz=10)
rect(sl, Inches(0.45), Inches(5.85), Inches(12.45), Inches(0.85), LIGHT_BLUE, r=Inches(0.08))
txt(sl, Inches(0.65), Inches(5.98), Inches(12), Inches(0.60), "Demo accounts seeded on first run:   admin@paperless.com / admin123   •   alice@paperless.com / signer123   •   bob@paperless.com / reviewer123", sz=9, bold=True, color=PRIMARY_DARK, align=PP_ALIGN.CENTER)

# ── 4 Administrator ──
sl = prs.slides.add_slide(prs.slide_layouts[6])
bg(sl, BG)
header(sl, "Administrator — Functionalities & Dashboard", "Manages users, permissions & system health")
card(sl, Inches(0.45), Inches(1.35), Inches(4.15), Inches(2.55), "User Management", [
    "Input: user details, roles",
    "Output: confirmation on create / update / delete",
    "Function: full CRUD — AdminDashboard table",
    "Validation: email format, password ≥ 6 chars",
], PRIMARY_DARK, sz=10)
card(sl, Inches(4.75), Inches(1.35), Inches(4.15), Inches(2.55), "Access Control", [
    "Input: role-based access permissions",
    "Output: access control status",
    "Function: assign & manage permissions",
    "UI: graphical cards by role + matrix",
], PRIMARY, sz=10)
card(sl, Inches(9.05), Inches(1.35), Inches(3.85), Inches(2.55), "System Maintenance", [
    "Input: system settings",
    "Output: system status reports",
    "Function: health & performance metrics",
    "JVM memory, auditors, DB path, status ● Operational",
], GREEN, sz=10)
rect(sl, Inches(0.45), Inches(4.15), Inches(12.45), Inches(1.95), WHITE, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.10))
txt(sl, Inches(0.70), Inches(4.30), Inches(12), Inches(0.30), "Admin Dashboard — 3 tabs (with aesthetics)", sz=12, bold=True, color=TEXT)
bullets(sl, Inches(0.70), Inches(4.65), Inches(11.90), Inches(1.30), [
    "Header: dark blue (#0D47A1) + user badge + Logout — consistent across roles with color coding",
    "User Mgmt tab: sortable table (ID, Name, Email, Role, Active) + Add / Edit / Delete / Refresh dialogs",
    "Access Control tab: role count cards + permission matrix (text) — data from UserService.countByRole()",
    "System tab: metrics (users, audits, JVM, processors) +  View Audit Logs dialog (full table)",
], sz=10, color=MUTED)

# ── 5 Signer ──
sl = prs.slides.add_slide(prs.slide_layouts[6])
bg(sl, BG)
header(sl, "Signer — Functionalities & Dashboard", "Creates, signs and tracks digital documents")
card(sl, Inches(0.45), Inches(1.35), Inches(4.15), Inches(2.45), "Document Signing", [
    "Input: digital document + signature",
    "Output: confirmation of successful signing",
    "Flow: pick doc → choose template → hash → SIGNED",
    "Async audit: SIGN_DOCUMENT event",
], GREEN, sz=10)
card(sl, Inches(4.75), Inches(1.35), Inches(4.15), Inches(2.45), "Signature Management", [
    "Input: signature templates (styled text / base64)",
    "Output: signature status",
    "CRUD: create / delete templates per user",
    "SignatureTemplate table — default flag support",
], PRIMARY, sz=10)
card(sl, Inches(9.05), Inches(1.35), Inches(3.85), Inches(2.45), "Signed Documents", [
    "Table listing signed docs & their status",
    "Columns: ID, Doc, Signer, Hash prefix, IP, Time",
    "Source: signatures table (all signatures)",
    "Refresh to see latest after signing",
], PURPLE, sz=10)
rect(sl, Inches(0.45), Inches(4.10), Inches(12.45), Inches(2.00), WHITE, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.10))
txt(sl, Inches(0.70), Inches(4.25), Inches(12), Inches(0.30), "Signer Dashboard — 3 tabs (green header #0F6B3A)", sz=12, bold=True, color=TEXT)
bullets(sl, Inches(0.70), Inches(4.60), Inches(11.90), Inches(1.30), [
    "Document Signing tab: all docs table (status, compliance, created) + New Document dialog (title + content) → PENDING_SIGNATURE",
    "Signature Management tab: user's templates + Create Template dialog (name + styled data) → status feedback",
    "Signed tab: signatures table — verifies hash + IP traceability; synchronized signDocument() ensures thread safety",
], sz=10, color=MUTED)

# ── 6 Reviewer ──
sl = prs.slides.add_slide(prs.slide_layouts[6])
bg(sl, BG)
header(sl, "Reviewer — Functionalities & Dashboard", "Verifies authenticity & compliance of signed documents")
card(sl, Inches(0.45), Inches(1.35), Inches(6.15), Inches(2.45), "Document Review", [
    "Input: signed documents + signature logs",
    "Output: review reports (approved / rejected)",
    "Viewer: signed docs + per-doc sig & audit logs",
    "Action: Review Selected → dialog",
], PURPLE, sz=10)
card(sl, Inches(6.85), Inches(1.35), Inches(6.00), Inches(2.45), "Compliance Check", [
    "Input: compliance criteria",
    "Output: compliance status (COMPLIANT / NON / PENDING)",
    "Dashboard: counts + review reports table",
    "Updates document compliance_status & status → REVIEWED / REJECTED",
], PRIMARY_DARK, sz=10)
rect(sl, Inches(0.45), Inches(4.10), Inches(12.45), Inches(2.00), WHITE, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.10))
txt(sl, Inches(0.70), Inches(4.25), Inches(12), Inches(0.30), "Reviewer Dashboard — 2 tabs (purple header #4C1D95)", sz=12, bold=True, color=TEXT)
bullets(sl, Inches(0.70), Inches(4.60), Inches(11.90), Inches(1.30), [
    "Document Review tab: top = signed docs, bottom = reviews  |  Buttons: View Sig Logs, View Audit, Review Selected, Refresh",
    "Compliance tab: summary (COMPLIANT / NON / PENDING counts) + full review reports table — from ReviewService.complianceCounts()",
    "Review dialog: comments + compliance dropdown + approved checkbox → async REVIEW audit + document update",
], sz=10, color=MUTED)

# ── 7 Architecture ──
sl = prs.slides.add_slide(prs.slide_layouts[6])
bg(sl, BG)
header(sl, "Architecture & Project Structure", "Layered, UI-agnostic — Swing today, Servlets tomorrow")
rect(sl, Inches(0.45), Inches(1.30), Inches(12.45), Inches(2.45), WHITE, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.10))
layers = [
    ("UI  —  LoginFrame + Admin / Signer / Reviewer Dashboards  +  UIStyle (theme, colors, tables)", PRIMARY_DARK),
    ("Service  —  Auth · User · Document · Signature · Review  (validation + async audit + collections & streams)", PRIMARY),
    ("DAO  —  GenericDAO<T,K>  ·  6 DAOs (User, Document, Signature, Template, Audit, Review)", GREEN),
    ("DB  —  DBUtil  →  H2 file  ./data/paperless  (JDBC: DriverManager + PreparedStatement + ResultSet)", MUTED),
]
for i, (txt_, col) in enumerate(layers):
    y = Inches(1.50 + i*0.52)
    rect(sl, Inches(0.75), y, Inches(0.16), Inches(0.36), col, r=Inches(0.04))
    txt(sl, Inches(1.05), y+Inches(0.04), Inches(11.6), Inches(0.28), txt_, sz=10, color=TEXT)
txt(sl, Inches(0.75), Inches(3.85), Inches(11.9), Inches(0.24), "Build:  javac --release 17 -cp lib/* -d bin  •  Run:  java -cp bin;lib/* App  •  VS Code: .vscode/settings.json → src + test + lib/**/*.jar", sz=8, color=MUTED, align=PP_ALIGN.CENTER)
card(sl, Inches(0.45), Inches(4.35), Inches(6.20), Inches(2.70), "src/ layout — 43 classes", [
    "App.java — init DB (DDL + seed) → LoginFrame",
    "model/ — Auditable, BaseEntity, 6 entities, 3 enums",
    "exception/ — PaperlessException hierarchy",
    "util/ — DBUtil, PasswordUtil, Validator, AsyncExecutor",
    "dao/ — 6 DAOs  |  service/ — 5 services  |  ui/ — 4 frames",
    "test/ — PaperlessTest (15)  |  lib/ — H2 + JUnit",
], PRIMARY, sz=9.5)
card(sl, Inches(6.95), Inches(4.35), Inches(6.00), Inches(2.70), "Why Swing + H2 for Review 1", [
    "No Tomcat / MySQL install — any evaluator runs run.bat",
    "Pure JDK 17 — satisfies JDBC rubric directly",
    "Swap DB: one constant in DBUtil.java",
    "Swap UI: keep service/dao/model → add Servlets/JSP",
    "All DAOs: PreparedStatement + try-with-resources",
    "AsyncExecutor pool — audit never blocks UI",
], GREEN, sz=9.5)

# ── 8 Database ──
sl = prs.slides.add_slide(prs.slide_layouts[6])
bg(sl, BG)
header(sl, "Database Design — H2 + JDBC", "6 tables, FKs, idempotent DDL, seeded demo accounts")
rect(sl, Inches(0.45), Inches(1.30), Inches(7.60), Inches(5.05), WHITE, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.10))
txt(sl, Inches(0.70), Inches(1.42), Inches(7), Inches(0.30), "Tables — DBUtil.initDatabase()", sz=12, bold=True, color=TEXT)
rows = [
    ("users", "id PK, name, email UNIQUE, password_hash, role, active, timestamps"),
    ("documents", "id PK, title, content CLOB, file_path, status, owner_id FK, compliance"),
    ("signature_templates", "id PK, name, signature_data CLOB, user_id FK, is_default"),
    ("signatures", "id PK, document_id FK, signer_id FK, template_id, hash, signed_at, ip"),
    ("audit_logs", "id PK, action, user_id, document_id, details, timestamp"),
    ("reviews", "id PK, document_id FK, reviewer_id FK, comments, compliance, approved"),
]
for i, (tbl, cols_) in enumerate(rows):
    y = Inches(1.78 + i*0.72)
    rect(sl, Inches(0.70), y, Inches(7.10), Inches(0.60), BG, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.06))
    txt(sl, Inches(0.82), y+Inches(0.04), Inches(1.70), Inches(0.22), tbl, sz=9, bold=True, color=PRIMARY_DARK)
    txt(sl, Inches(0.82), y+Inches(0.24), Inches(6.85), Inches(0.32), cols_, sz=8, color=MUTED)
card(sl, Inches(8.30), Inches(1.30), Inches(4.60), Inches(1.80), "ER — simplified", [
    "users 1 — N documents (owner_id)",
    "users 1 — N signature_templates",
    "documents 1 — N signatures / audits / reviews",
    "users 1 — N signatures + reviews",
], PRIMARY, sz=10)
card(sl, Inches(8.30), Inches(3.30), Inches(4.60), Inches(1.80), "Seed (if empty)", [
    "admin@paperless.com / admin123 → ADMINISTRATOR",
    "alice@paperless.com / signer123 → SIGNER",
    "bob@paperless.com / reviewer123 → REVIEWER",
    "URL: jdbc:h2:file:./data/paperless",
], GREEN, sz=10)
card(sl, Inches(8.30), Inches(5.30), Inches(4.60), Inches(1.45), "JDBC touchpoints", [
    "DriverManager.getConnection(URL, sa, \"\")",
    "PreparedStatement + ResultSet mapping",
    "try-with-resources everywhere",
], PURPLE, sz=10)

# ── 9 UI/UX ──
sl = prs.slides.add_slide(prs.slide_layouts[6])
bg(sl, BG)
header(sl, "UI / UX — Login + 3 Role Dashboards", "Aesthetic, responsive — role-color headers & consistent cards")
rect(sl, Inches(0.45), Inches(1.30), Inches(12.45), Inches(0.65), WHITE, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.08))
txt(sl, Inches(0.65), Inches(1.38), Inches(12), Inches(0.20), "Theme — UIStyle.java", sz=9, bold=True, color=MUTED)
for i, (col, label) in enumerate([(PRIMARY_DARK,"Admin #0D47A1"),(GREEN,"Signer #0F6B3A"),(PURPLE,"Reviewer #4C1D95"),(PRIMARY,"Primary #1A73E8"),(BG,"BG #F5F7FB")]):
    x = Inches(0.65 + i*2.45)
    rect(sl, x, Inches(1.62), Inches(0.42), Inches(0.18), col, r=Inches(0.04))
    txt(sl, x+Inches(0.50), Inches(1.64), Inches(1.85), Inches(0.16), label, sz=7, color=MUTED)
txt(sl, Inches(8.2), Inches(1.62), Inches(4.5), Inches(0.18), "Fonts: Segoe UI  •  Cards: rounded  •  Tables: header #EEF2FF, row 26", sz=7, color=MUTED)
card(sl, Inches(0.45), Inches(2.15), Inches(3.05), Inches(4.55), "LoginFrame", [
    "Email + Password (demo hints visible)",
    "AuthService.login() → role redirect",
    "Enter key = login, errors inline",
    "Footer: © GUVI",
], PRIMARY, sz=10)
card(sl, Inches(3.65), Inches(2.15), Inches(3.05), Inches(4.55), "Administrator (blue)", [
    "Header: name + email + Logout",
    "User Mgmt: table + Add/Edit/Delete",
    "Access Control: cards + matrix",
    "System: metrics + audit viewer",
], PRIMARY_DARK, sz=10)
card(sl, Inches(6.85), Inches(2.15), Inches(3.05), Inches(4.55), "Signer (green)", [
    "Document Signing: New + Sign → SIGNED",
    "Signature Mgmt: templates CRUD",
    "Signed Docs: signatures table",
    "Hash + IP traceability",
], GREEN, sz=10)
card(sl, Inches(10.05), Inches(2.15), Inches(2.85), Inches(4.55), "Reviewer (purple)", [
    "Doc Review: signed + reviews",
    "View Sig Logs / Audit per doc",
    "Review dialog: comments + status",
    "Compliance: counts + reports",
], PURPLE, sz=10)

# ── 10 Rubric + Verify + Thanks ──
sl = prs.slides.add_slide(prs.slide_layouts[6])
bg(sl, BG)
header(sl, "Rubric Mapping & Live Verification", "What Review 1 asks → where it is → how to check in 30 seconds")
rect(sl, Inches(0.45), Inches(1.30), Inches(7.90), Inches(4.55), WHITE, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.10))
txt(sl, Inches(0.70), Inches(1.42), Inches(7.4), Inches(0.30), "Desktop track — 33 marks", sz=13, bold=True, color=TEXT)
rubric = [
    ("1  OOP (Poly / Inherit / Exceptions / Interfaces)", "10", "Auditable + BaseEntity + 6 models"),
    ("2  Collections & Generics", "6", "GenericDAO<T,K> + groupingBy"),
    ("3  Multithreading & Sync", "4", "AsyncExecutor + synchronized + 5-thread test"),
    ("4  Classes for DB ops", "7", "6 DAOs + 5 Services"),
    ("5  DB Connectivity (JDBC)", "3", "DBUtil + DriverManager"),
    ("6  JDBC schema + CRUD", "3", "DDL 6 tables + FK + ResultSet"),
]
txt(sl, Inches(0.70), Inches(1.78), Inches(3.40), Inches(0.22), "Criterion", sz=8, bold=True, color=MUTED)
txt(sl, Inches(4.60), Inches(1.78), Inches(0.60), Inches(0.22), "Marks", sz=8, bold=True, color=MUTED, align=PP_ALIGN.CENTER)
txt(sl, Inches(5.35), Inches(1.78), Inches(2.70), Inches(0.22), "Evidence", sz=8, bold=True, color=MUTED)
for i, (crit, marks, ev) in enumerate(rubric):
    y = Inches(2.02 + i*0.56)
    c = WHITE if i % 2 == 0 else BG
    rect(sl, Inches(0.60), y, Inches(7.60), Inches(0.46), c, RGBColor(0xE5,0xE7,0xEB), r=Inches(0.05))
    txt(sl, Inches(0.70), y+Inches(0.09), Inches(3.85), Inches(0.28), crit, sz=9, color=TEXT)
    txt(sl, Inches(4.60), y+Inches(0.09), Inches(0.60), Inches(0.28), marks, sz=10, bold=True, color=PRIMARY, align=PP_ALIGN.CENTER)
    txt(sl, Inches(5.35), y+Inches(0.09), Inches(2.60), Inches(0.28), ev, sz=8, color=MUTED)
card(sl, Inches(8.55), Inches(1.30), Inches(4.35), Inches(4.55), "Verify live (no installs)", [
    "1) run.bat — compiles, opens Login",
    "2) admin@... / admin123 → check tabs",
    "3) alice@... → New Doc → Template → Sign",
    "4) bob@... → Logs → Review → counts",
    "5) test.bat → 15/15 OK (real H2 DB)",
    "Docs: docs/0*.md  •  ER in 03",
    "Switch DB: one constant in DBUtil",
], PURPLE, sz=10)
rect(sl, Inches(0.45), Inches(6.15), Inches(12.45), Inches(0.85), PRIMARY_DARK, r=Inches(0.08))
txt(sl, Inches(0.65), Inches(6.28), Inches(8.0), Inches(0.60), "Thank you  —  ready for Review 1  •  Code quality: layered, commented, modular, tested  •  All deliverables included", sz=10, color=WHITE)
txt(sl, Inches(8.6), Inches(6.32), Inches(4.2), Inches(0.55), "Submit: this PPTX + GitHub link (public)\nhttps://github.com/ankurrbpandey2006-cloud/Paperless-World-Project", sz=8, bold=True, color=RGBColor(0xBF,0xD3,0xFF), align=PP_ALIGN.RIGHT)

out = r"C:\Users\Ankur Panday\Desktop\java\Paperless world\Paper\docs\Paperless-World-Review1.pptx"
prs.save(out)
print(f"Saved 10 slides to {out}")
