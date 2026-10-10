#!/usr/bin/env python3
"""Generate HCL GUVI Review 1 presentation — Paperless World (PS 51)."""
from pptx import Presentation
from pptx.util import Inches, Emu, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.enum.shapes import MSO_SHAPE

PRIMARY = RGBColor(0x1A, 0x73, 0xE8)
PRIMARY_DARK = RGBColor(0x0D, 0x47, 0xA1)
BG = RGBColor(0xF5, 0xF7, 0xFB)
TEXT = RGBColor(0x1F, 0x29, 0x37)
MUTED = RGBColor(0x6B, 0x72, 0x80)
WHITE = RGBColor(0xFF, 0xFF, 0xFF)
GREEN = RGBColor(0x0F, 0x6B, 0x3A)
PURPLE = RGBColor(0x4C, 0x1D, 0x95)

prs = Presentation()
prs.slide_width = Inches(13.333)
prs.slide_height = Inches(7.5)

def set_bg(slide, color):
    bg = slide.background
    fill = bg.fill
    fill.solid()
    fill.fore_color.rgb = color

def add_shape(slide, left, top, width, height, fill_color, line_color=None, radius=None):
    shape = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE if radius is None else MSO_SHAPE.ROUNDED_RECTANGLE,
                                   left, top, width, height)
    shape.fill.solid()
    shape.fill.fore_color.rgb = fill_color
    if line_color is None:
        shape.line.fill.background()
    else:
        shape.line.color.rgb = line_color
        shape.line.width = Pt(1)
    return shape

def add_text_box(slide, left, top, width, height, text, font_size=12, bold=False, color=TEXT, alignment=PP_ALIGN.LEFT, font_name="Calibri"):
    txBox = slide.shapes.add_textbox(left, top, width, height)
    tf = txBox.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = text
    p.font.size = Pt(font_size)
    p.font.bold = bold
    p.font.color.rgb = color
    p.font.name = font_name
    p.alignment = alignment
    return txBox

def add_bullet_slide(slide, left, top, width, height, items, font_size=10, color=TEXT, bullet_char="• "):
    txBox = slide.shapes.add_textbox(left, top, width, height)
    tf = txBox.text_frame
    tf.word_wrap = True
    for i, item in enumerate(items):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.text = bullet_char + item
        p.font.size = Pt(font_size)
        p.font.color.rgb = color
        p.font.name = "Calibri"
        p.space_after = Pt(3)
        p.line_spacing = Pt(14)
        p.alignment = PP_ALIGN.LEFT
    return txBox

def header_bar(slide, title, subtitle, color=PRIMARY_DARK):
    add_shape(slide, Inches(0), Inches(0), prs.slide_width, Inches(0.85), color)
    add_text_box(slide, Inches(0.45), Inches(0.18), Inches(8), Inches(0.32), title, font_size=15, bold=True, color=WHITE)
    add_text_box(slide, Inches(0.45), Inches(0.45), Inches(8), Inches(0.24), subtitle, font_size=8.5, color=RGBColor(0xBF, 0xD3, 0xFF))
    # badge
    add_shape(slide, Inches(10.9), Inches(0.22), Inches(1.95), Inches(0.42), WHITE, radius=Inches(0.06))
    add_text_box(slide, Inches(10.9), Inches(0.26), Inches(1.95), Inches(0.32), "PS 51  •  Review 1", font_size=8, bold=True, color=color, alignment=PP_ALIGN.CENTER)

def card(slide, left, top, width, height, title, items, accent=PRIMARY, title_size=9):
    add_shape(slide, left, top, width, height, WHITE, RGBColor(0xE5, 0xE7, 0xEB), radius=Inches(0.08))
    add_shape(slide, left, top, width, Inches(0.32), accent, radius=Inches(0.08))
    # clip fix: overlay title
    add_text_box(slide, left + Inches(0.18), top + Inches(0.06), width - Inches(0.36), Inches(0.22), title, font_size=title_size, bold=True, color=WHITE)
    add_bullet_slide(slide, left + Inches(0.18), top + Inches(0.42), width - Inches(0.36), height - Inches(0.52), items, font_size=7.5)

# ---------- SLIDE 1: Title ----------
sl = prs.slides.add_slide(prs.slide_layouts[6])
set_bg(sl, WHITE)
add_shape(sl, Inches(0), Inches(0), prs.slide_width, Inches(1.85), PRIMARY_DARK)
add_text_box(sl, Inches(0.6), Inches(0.45), Inches(12), Inches(0.45), "HCL GUVI Hackathon — Review 1", font_size=11, color=RGBColor(0xBF, 0xD3, 0xFF), alignment=PP_ALIGN.LEFT)
add_text_box(sl, Inches(0.6), Inches(0.80), Inches(12), Inches(0.70), "Paperless World", font_size=32, bold=True, color=WHITE, alignment=PP_ALIGN.LEFT)
add_text_box(sl, Inches(0.6), Inches(1.42), Inches(8), Inches(0.28), "51 — E-Signature Platform  •  Secure & efficient digital signing", font_size=10.5, color=WHITE, alignment=PP_ALIGN.LEFT)
# meta row
add_text_box(sl, Inches(0.6), Inches(2.08), Inches(12), Inches(0.22), "Java 17  •  Swing  •  JDBC (H2)  •  JUnit 4  •  Desktop — no server required", font_size=7.5, color=MUTED, alignment=PP_ALIGN.LEFT)
# right card
add_shape(sl, Inches(9.2), Inches(1.0), Inches(3.45), Inches(1.55), WHITE, RGBColor(0xE5, 0xE7, 0xEB), radius=Inches(0.12))
add_text_box(sl, Inches(9.45), Inches(1.12), Inches(3.0), Inches(0.22), "Team", font_size=7, bold=True, color=MUTED, alignment=PP_ALIGN.LEFT)
add_text_box(sl, Inches(9.45), Inches(1.28), Inches(3.0), Inches(0.22), "abhishek.25scse1010473@galgotiasuniversity.ac.in", font_size=7, color=TEXT)
add_text_box(sl, Inches(9.45), Inches(1.56), Inches(3.0), Inches(0.22), "Stack: Java / H2 / Swing / JDBC", font_size=7, color=MUTED)
add_text_box(sl, Inches(9.45), Inches(1.74), Inches(3.0), Inches(0.22), "Run:  run.bat  (JDK 17+ only)", font_size=7, color=MUTED)
add_text_box(sl, Inches(9.45), Inches(1.92), Inches(3.0), Inches(0.52), "Demo logins on login screen →", font_size=7, bold=True, color=PRIMARY)
# agenda
add_text_box(sl, Inches(0.6), Inches(2.62), Inches(12), Inches(0.22), "Agenda:  Problem  →  Architecture  →  Database  →  UI Demos  →  Rubric  →  Live Steps", font_size=7.5, color=MUTED)
# overview bullets
card(sl, Inches(0.45), Inches(3.05), Inches(4.15), Inches(2.55), "What we built", [
    "E-signature platform — eliminates physical signatures",
    "3 roles: Administrator / Signer / Reviewer",
    "6 functionalities + 3 dashboards (as per brief)",
    "Every sign & review is audited for traceability",
    "Runs offline: H2 file DB, no MySQL/Tomcat",
], PRIMARY)
card(sl, Inches(4.75), Inches(3.05), Inches(4.15), Inches(2.55), "Review 1 focus (asked)", [
    "Project structure  ✓  layered, .vscode ready",
    "Database design  ✓  6 tables + ER",
    "Database connectivity  ✓  JDBC working",
    "UI/UX — aesthetics & responsiveness  ✓",
    "Code quality — modular, commented, tests",
], GREEN)
card(sl, Inches(9.05), Inches(3.05), Inches(3.85), Inches(2.55), "Quick verify (30 sec)", [
    'run.bat  →  Login (admin@paperless.com / admin123)',
    "Admin: users / permissions / metrics",
    "Swap to alice@... → New Doc → Sign",
    "Swap to bob@... → View Logs → Review",
    "test.bat → 15/15 OK",
], PURPLE)
add_text_box(sl, Inches(0.6), Inches(6.95), Inches(12), Inches(0.22), "© 2024 GUVI Geek Network — PS 51 • Submission for Review 1", font_size=6.5, color=MUTED, alignment=PP_ALIGN.CENTER)

# ---------- SLIDE 2: Problem & Roles ----------
sl = prs.slides.add_slide(prs.slide_layouts[6])
set_bg(sl, BG)
header_bar(sl, "Problem & User Roles", "Why paperless + who uses it")
card(sl, Inches(0.45), Inches(1.10), Inches(6.25), Inches(1.55), "Summary / Problem", [
    "Manual signatures → delays, physical storage, weak traceability",
    "Goal: secure, efficient electronic signing for digital docs",
    "Outcomes: faster turnaround, reduced paper, verifiable audit trail",
], PRIMARY)
card(sl, Inches(6.95), Inches(1.10), Inches(6.0), Inches(1.55), "User Types (from brief)", [
    "Administrator — manages platform, roles & access",
    "Signer — signs digital documents electronically",
    "Reviewer — verifies authenticity & compliance",
], GREEN)
# functional grid
add_text_box(sl, Inches(0.55), Inches(2.90), Inches(12), Inches(0.28), "Functionalities → Implementation", font_size=9, bold=True, color=TEXT)
cols = [
    ("For Administrator", PRIMARY_DARK, [
        "User Mgmt — CRUD users/roles → confirm dialog",
        "Access Control — role-based permission matrix",
        "System Maintenance — status & metrics + logs",
    ]),
    ("For Signer", GREEN, [
        "Document Signing — pick doc + template → hash",
        "Signature Mgmt — create / delete templates",
        "Signed Docs — table of all signatures",
    ]),
    ("For Reviewer", PURPLE, [
        "Document Review — signed docs + sig/audit logs",
        "Compliance Check — COMPLIANT / NON / PENDING",
        "Review Reports — table + counts",
    ]),
]
for idx, (title, col, items) in enumerate(cols):
    card(sl, Inches(0.45 + idx*4.45), Inches(3.28), Inches(4.15), Inches(2.10), title, items, col)
# dashboards line
card(sl, Inches(0.45), Inches(5.62), Inches(12.45), Inches(1.25), "Dashboards (expected in brief) → delivered", [
    "Admin: user table + graphical permissions + metrics  |  Signer: signing interface + template tools + signed table  |  Reviewer: signed docs + logs table + compliance reports",
], MUTED)

# ---------- SLIDE 3: Architecture ----------
sl = prs.slides.add_slide(prs.slide_layouts[6])
set_bg(sl, BG)
header_bar(sl, "Architecture & Project Structure", "Layered, UI-agnostic — Swing today, Servlet/JSP tomorrow without touching services")
# diagram
add_shape(sl, Inches(0.45), Inches(1.10), Inches(12.45), Inches(2.35), WHITE, RGBColor(0xE5, 0xE7, 0xEB), radius=Inches(0.10))
# layers as horizontal bars
layers = [
    ("UI  —  LoginFrame + Admin / Signer / Reviewer Dashboards  +  UIStyle", PRIMARY_DARK),
    ("Service  —  Auth · User · Document · Signature · Review  (validation + async audit + collections)", PRIMARY),
    ("DAO  —  GenericDAO<T,K>  ·  UserDAO  ·  DocumentDAO  ·  SignatureDAO  ·  TemplateDAO  ·  AuditDAO  ·  ReviewDAO", GREEN),
    ("DB  —  DBUtil  →  H2 file  ./data/paperless  (JDBC: DriverManager + PreparedStatement + ResultSet)", MUTED),
]
for i, (txt, col) in enumerate(layers):
    top = Inches(1.28 + i*0.52)
    add_shape(sl, Inches(0.75), top, Inches(0.14), Inches(0.36), col, radius=Inches(0.04))
    add_text_box(sl, Inches(1.02), top + Inches(0.04), Inches(11.6), Inches(0.28), txt, font_size=7.5, color=TEXT)
add_text_box(sl, Inches(0.75), Inches(3.55), Inches(11.9), Inches(0.22), "Build:  javac --release 17 -cp lib/* -d bin  •  Run:  java -cp bin;lib/* App  •  VS Code: .vscode/settings.json includes src + test", font_size=6.5, color=MUTED, alignment=PP_ALIGN.CENTER)
# structure two cols
add_text_box(sl, Inches(0.55), Inches(3.95), Inches(6), Inches(0.28), "Package map", font_size=9, bold=True, color=TEXT)
add_text_box(sl, Inches(6.95), Inches(3.95), Inches(5.8), Inches(0.28), "Key design choices", font_size=9, bold=True, color=TEXT)
card(sl, Inches(0.45), Inches(4.32), Inches(6.2), Inches(2.85), "src/ layout (43 classes)", [
    "App.java — init DB (DDL + seed) → Login",
    "model/ — Auditable, BaseEntity, 6 entities, 3 enums",
    "exception/ — PaperlessException hierarchy",
    "util/ — DBUtil, PasswordUtil, Validator, AsyncExecutor",
    "dao/ — 6 DAOs (Generic),  service/ — 5 services",
    "ui/ — UIStyle + 3 dashboards + login",
    "test/ — PaperlessTest (15)  |  lib/ — H2 + JUnit",
], PRIMARY)
card(sl, Inches(6.95), Inches(4.32), Inches(6.0), Inches(2.85), "Why Swing + H2 for Review 1", [
    "No Tomcat/MySQL install → any evaluator runs run.bat",
    "Pure JDK 17 — satisfies JDBC rubric directly",
    "Swap DB: change one constant in DBUtil",
    "Swap UI: keep service/dao/model → add Servlets/JSP",
    "All DAOs use PreparedStatement + try-with-resources",
    "Async audit never blocks UI thread",
], GREEN)

# ---------- SLIDE 4: Database ----------
sl = prs.slides.add_slide(prs.slide_layouts[6])
set_bg(sl, BG)
header_bar(sl, "Database Design — H2 + JDBC", "6 tables, FKs, idempotent DDL, seeded demo accounts")
# left: tables
add_shape(sl, Inches(0.45), Inches(1.08), Inches(7.55), Inches(5.25), WHITE, RGBColor(0xE5, 0xE7, 0xEB), radius=Inches(0.10))
add_text_box(sl, Inches(0.70), Inches(1.18), Inches(7), Inches(0.28), "Tables — DBUtil.initDatabase()", font_size=9, bold=True, color=TEXT, alignment=PP_ALIGN.LEFT)
rows = [
    ("users", "id PK, name, email UNIQUE, password_hash, role, active, created_at, updated_at"),
    ("documents", "id PK, title, content CLOB, file_path, status, owner_id FK→users, compliance_status"),
    ("signature_templates", "id PK, name, signature_data CLOB, user_id FK→users, is_default"),
    ("signatures", "id PK, document_id FK→docs, signer_id FK→users, template_id, hash, signed_at, ip"),
    ("audit_logs", "id PK, action, user_id, document_id, details, timestamp"),
    ("reviews", "id PK, document_id FK→docs, reviewer_id FK→users, comments, compliance_status, approved"),
]
for i, (tbl, cols_) in enumerate(rows):
    y = Inches(1.55 + i*0.76)
    add_shape(sl, Inches(0.70), y, Inches(7.05), Inches(0.62), BG, RGBColor(0xE5, 0xE7, 0xEB), radius=Inches(0.06))
    add_text_box(sl, Inches(0.82), y + Inches(0.05), Inches(1.55), Inches(0.20), tbl, font_size=7, bold=True, color=PRIMARY_DARK)
    add_text_box(sl, Inches(0.82), y + Inches(0.22), Inches(6.85), Inches(0.34), cols_, font_size=6.2, color=MUTED)
# right cards
card(sl, Inches(8.25), Inches(1.08), Inches(4.65), Inches(1.85), "ER (simplified)", [
    "users 1 — N documents (owner_id)",
    "users 1 — N signature_templates",
    "documents 1 — N signatures / audit_logs / reviews",
    "users 1 — N signatures (signer) + reviews (reviewer)",
], PRIMARY)
card(sl, Inches(8.25), Inches(3.10), Inches(4.65), Inches(1.85), "Seed (if empty)", [
    "admin@paperless.com / admin123 → ADMINISTRATOR",
    "alice@paperless.com / signer123 → SIGNER",
    "bob@paperless.com / reviewer123 → REVIEWER",
    "JDBC URL: jdbc:h2:file:./data/paperless",
], GREEN)
card(sl, Inches(8.25), Inches(5.08), Inches(4.65), Inches(1.55), "JDBC touchpoints", [
    "DriverManager.getConnection(URL, sa, \"\")",
    "Class.forName(\"org.h2.Driver\")",
    "PreparedStatement + ResultSet mapping (every DAO)",
    "try-with-resources everywhere",
], PURPLE)
add_text_box(sl, Inches(0.55), Inches(6.55), Inches(12), Inches(0.22), "Full DDL + Mermaid ER → docs/03-database-design.md", font_size=6.5, color=MUTED, alignment=PP_ALIGN.CENTER)

# ---------- SLIDE 5: UI / UX ----------
sl = prs.slides.add_slide(prs.slide_layouts[6])
set_bg(sl, BG)
header_bar(sl, "UI / UX — Login + 3 Role Dashboards", "Aesthetic, responsive — role-color headers, consistent cards & tables")
# theme strip
add_shape(sl, Inches(0.45), Inches(1.08), Inches(12.45), Inches(0.58), WHITE, RGBColor(0xE5, 0xE7, 0xEB), radius=Inches(0.08))
add_text_box(sl, Inches(0.65), Inches(1.16), Inches(12), Inches(0.18), "Theme — UIStyle.java", font_size=7, bold=True, color=MUTED)
swatches = [(PRIMARY_DARK, "Admin #0D47A1"), (GREEN, "Signer #0F6B3A"), (PURPLE, "Reviewer #4C1D95"), (PRIMARY, "Primary #1A73E8"), (BG, "BG #F5F7FB")]
for i, (col, label) in enumerate(swatches):
    x = Inches(0.65 + i*2.45)
    add_shape(sl, x, Inches(1.34), Inches(0.42), Inches(0.18), col, radius=Inches(0.04))
    add_text_box(sl, x + Inches(0.50), Inches(1.36), Inches(1.85), Inches(0.16), label, font_size=6, color=MUTED)
add_text_box(sl, Inches(8.2), Inches(1.33), Inches(4.5), Inches(0.18), "Fonts: Segoe UI  •  Cards: rounded + border  •  Tables: header #EEF2FF, row 26", font_size=6, color=MUTED)
# flow cards
card(sl, Inches(0.45), Inches(1.84), Inches(3.05), Inches(4.85), "LoginFrame", [
    "Email + Password (demo hints visible)",
    "AuthService.login() → role redirect",
    "Enter key = login, errors inline",
    "Footer: © GUVI",
], PRIMARY)
card(sl, Inches(3.65), Inches(1.84), Inches(3.05), Inches(4.85), "Administrator (blue)", [
    "Header: name + email + Logout",
    "User Mgmt: sortable table + Add/Edit/Delete",
    "Access Control: counts by role + matrix",
    "System: users/audits/JVM/DB + log viewer",
], PRIMARY_DARK)
card(sl, Inches(6.85), Inches(1.84), Inches(3.05), Inches(4.85), "Signer (green)", [
    "Document Signing: all docs + New + Sign",
    "  → pick template → hash → SIGNED",
    "Signature Mgmt: templates CRUD",
    "Signed Docs: signatures table",
], GREEN)
card(sl, Inches(10.05), Inches(1.84), Inches(2.85), Inches(4.85), "Reviewer (purple)", [
    "Doc Review: signed docs + reviews",
    "View Sig Logs / Audit per doc",
    "Review dialog: comments + status",
    "Compliance: counts + reports",
], PURPLE)
add_text_box(sl, Inches(0.55), Inches(6.85), Inches(12), Inches(0.22), "10 screenshots to capture → docs/05-ui-flows.md  •  All flows validated; data created via: New Doc → Template → Sign → Review", font_size=6.5, color=MUTED, alignment=PP_ALIGN.CENTER)

# ---------- SLIDE 6: Rubric & Verify ----------
sl = prs.slides.add_slide(prs.slide_layouts[6])
set_bg(sl, BG)
header_bar(sl, "Rubric Mapping & Live Verification", "What Review 1 asks → where it is → how to check in 30 seconds")
# rubric table as card
add_shape(sl, Inches(0.45), Inches(1.08), Inches(7.95), Inches(5.05), WHITE, RGBColor(0xE5, 0xE7, 0xEB), radius=Inches(0.10))
add_text_box(sl, Inches(0.70), Inches(1.18), Inches(7.4), Inches(0.28), "Desktop track — 33 marks (also maps to Web track)", font_size=9, bold=True, color=TEXT)
rubric = [
    ("1  OOP (Poly/Inher/Ex/Interfaces)", "10", "Auditable, BaseEntity, 6 models, exceptions"),
    ("2  Collections & Generics", "6", "GenericDAO<T,K>, groupingBy, Comparator"),
    ("3  Multithreading & Sync", "4", "AsyncExecutor + synchronized + 5-thread test"),
    ("4  Classes for DB ops", "7", "6 DAOs + 5 Services"),
    ("5  DB Connectivity (JDBC)", "3", "DBUtil + DriverManager + PreparedStatement"),
    ("6  JDBC schema + CRUD", "3", "DDL 6 tables + FK + seed + ResultSet"),
]
add_text_box(sl, Inches(0.70), Inches(1.52), Inches(1.15), Inches(0.20), "#  Criterion", font_size=6.5, bold=True, color=MUTED, alignment=PP_ALIGN.LEFT)
add_text_box(sl, Inches(3.85), Inches(1.52), Inches(0.65), Inches(0.20), "Marks", font_size=6.5, bold=True, color=MUTED, alignment=PP_ALIGN.CENTER)
add_text_box(sl, Inches(4.70), Inches(1.52), Inches(3.05), Inches(0.20), "Evidence", font_size=6.5, bold=True, color=MUTED)
for i, (crit, marks, ev) in enumerate(rubric):
    y = Inches(1.74 + i*0.58)
    bg = WHITE if i % 2 == 0 else BG
    add_shape(sl, Inches(0.60), y, Inches(7.65), Inches(0.48), bg, RGBColor(0xE5, 0xE7, 0xEB), radius=Inches(0.05))
    add_text_box(sl, Inches(0.70), y + Inches(0.10), Inches(3.20), Inches(0.28), crit, font_size=6.8, color=TEXT)
    add_text_box(sl, Inches(3.92), y + Inches(0.10), Inches(0.55), Inches(0.28), marks, font_size=7.5, bold=True, color=PRIMARY, alignment=PP_ALIGN.CENTER)
    add_text_box(sl, Inches(4.70), y + Inches(0.10), Inches(2.90), Inches(0.28), ev, font_size=6.5, color=MUTED)
add_text_box(sl, Inches(0.65), Inches(5.42), Inches(7.6), Inches(0.42), "Gold: matches brief's dashboards & traceability — not only rubric items.", font_size=6.5, color=GREEN, alignment=PP_ALIGN.LEFT)
add_text_box(sl, Inches(0.65), Inches(5.75), Inches(7.6), Inches(0.22), "Details → docs/04-rubric-mapping.md", font_size=6, color=MUTED)
# verify steps
card(sl, Inches(8.60), Inches(1.08), Inches(4.35), Inches(5.05), "Verify live (no installs)", [
    "1) run.bat  — compiles 43 classes, opens Login",
    "2) admin@paperless.com / admin123 → check tabs",
    "3) alice@paperless.com / signer123 → New Doc → Create Template → Sign",
    "4) bob@paperless.com / reviewer123 → View Logs → Review → compliance counts",
    "5) test.bat → 15/15 OK (real H2 file DB)",
    "—",
    "Docs: docs/0*.md  •  ER in 03",
    "Switch DB: one constant in DBUtil",
    "Switch to Servlets: keep service/dao/model",
], PURPLE)
add_shape(sl, Inches(0.45), Inches(6.35), Inches(12.45), Inches(0.62), PRIMARY_DARK, radius=Inches(0.08))
add_text_box(sl, Inches(0.65), Inches(6.42), Inches(7.5), Inches(0.48), "Thank you  —  ready for Review 1  •  Code quality: layered, commented, modular, tested  •  All deliverables included", font_size=7.5, color=WHITE)
add_text_box(sl, Inches(8.6), Inches(6.46), Inches(4.2), Inches(0.42), "Submit: this PPTX + GitHub link (public) + README", font_size=7.5, bold=True, color=RGBColor(0xBF, 0xD3, 0xFF), alignment=PP_ALIGN.RIGHT)

# save
out = r"C:\Users\Ankur Panday\Desktop\java\Paperless world\Paper\docs\Paperless-World-Review1.pptx"
prs.save(out)
print(f"Saved to {out}")
