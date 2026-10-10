package com.paperless.ui;

import com.paperless.model.Document;
import com.paperless.model.SignatureTemplate;
import com.paperless.model.User;
import com.paperless.service.DocumentService;
import com.paperless.service.SignatureService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SignerDashboard extends JFrame {
    private final User currentUser;
    private final DocumentService docService = new DocumentService();
    private final SignatureService sigService = new SignatureService();

    private final DefaultTableModel docModel = new DefaultTableModel(new Object[]{"ID","Title","Status","Compliance","Created"}, 0) {
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable docTable = new JTable(docModel);

    private final DefaultTableModel tmplModel = new DefaultTableModel(new Object[]{"ID","Name","Default"},0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable tmplTable = new JTable(tmplModel);

    private final DefaultTableModel signedModel = new DefaultTableModel(new Object[]{"ID","Doc","Signer","Hash","IP","Time"},0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable signedTable = new JTable(signedModel);

    public SignerDashboard(User user) {
        super("Signer — Paperless World  |  " + user.getName());
        this.currentUser = user;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIStyle.BG);
        setLayout(new BorderLayout());
        add(header(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIStyle.BODY);
        tabs.addTab("Document Signing", docSigningPanel());
        tabs.addTab("Signature Management", templatePanel());
        tabs.addTab("Signed Documents", signedPanel());
        add(tabs, BorderLayout.CENTER);
        loadDocuments(); loadTemplates(); loadSigned();
    }

    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0x0F_6B_3A));
        p.setBorder(new EmptyBorder(12,18,12,18));
        JLabel t = new JLabel("Signer Dashboard");
        t.setFont(new Font("Segoe UI", Font.BOLD, 16)); t.setForeground(Color.WHITE);
        JLabel u = new JLabel(currentUser.getEmail() + "  •  " + currentUser.getRole().getDisplayName());
        u.setForeground(new Color(0xD1_FA_E5));
        JButton lo = UIStyle.outlineButton("Logout");
        lo.setBackground(Color.WHITE);
        lo.addActionListener(e->{dispose(); new LoginFrame().setVisible(true);});
        p.add(t, BorderLayout.WEST); p.add(u, BorderLayout.CENTER); p.add(lo, BorderLayout.EAST);
        return p;
    }

    private JPanel docSigningPanel() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBackground(UIStyle.BG); p.setBorder(new EmptyBorder(12,12,12,12));
        UIStyle.styleTable(docTable);
        p.add(new JScrollPane(docTable), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(UIStyle.BG);
        JButton newDoc = UIStyle.primaryButton("New Document");
        JButton signBtn = UIStyle.primaryButton("Sign Selected");
        JButton refresh = UIStyle.outlineButton("Refresh");
        actions.add(newDoc); actions.add(signBtn); actions.add(refresh);
        p.add(actions, BorderLayout.SOUTH);
        newDoc.addActionListener(e -> showNewDocDialog());
        signBtn.addActionListener(e -> signSelected());
        refresh.addActionListener(e -> { loadDocuments(); loadSigned(); });
        return p;
    }

    private JPanel templatePanel() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBackground(UIStyle.BG); p.setBorder(new EmptyBorder(12,12,12,12));
        UIStyle.styleTable(tmplTable);
        p.add(new JScrollPane(tmplTable), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(UIStyle.BG);
        JButton create = UIStyle.primaryButton("Create Template");
        JButton del = UIStyle.outlineButton("Delete");
        JButton refresh = UIStyle.outlineButton("Refresh");
        actions.add(create); actions.add(del); actions.add(refresh);
        p.add(actions, BorderLayout.SOUTH);
        create.addActionListener(e -> showTemplateDialog());
        del.addActionListener(e -> {
            int r = tmplTable.getSelectedRow();
            if (r<0){JOptionPane.showMessageDialog(this,"Select a template"); return;}
            int id=(int)tmplModel.getValueAt(r,0);
            try{sigService.deleteTemplate(id); loadTemplates();}catch(Exception ex){err(ex);}
        });
        refresh.addActionListener(e->loadTemplates());
        return p;
    }

    private JPanel signedPanel() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBackground(UIStyle.BG); p.setBorder(new EmptyBorder(12,12,12,12));
        UIStyle.styleTable(signedTable);
        p.add(new JScrollPane(signedTable), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(UIStyle.BG);
        JButton refresh = UIStyle.primaryButton("Refresh");
        actions.add(refresh);
        p.add(actions, BorderLayout.SOUTH);
        refresh.addActionListener(e->loadSigned());
        return p;
    }

    private void loadDocuments() {
        try {
            List<Document> docs = docService.getAll();
            docModel.setRowCount(0);
            for (Document d: docs) docModel.addRow(new Object[]{d.getId(), d.getTitle(), d.getStatus(), d.getComplianceStatus(), d.getCreatedAt()});
        } catch(Exception ex){err(ex);}
    }

    private void loadTemplates() {
        try {
            List<SignatureTemplate> list = sigService.getTemplatesByUser(currentUser.getId());
            tmplModel.setRowCount(0);
            for (var t: list) tmplModel.addRow(new Object[]{t.getId(), t.getName(), t.isDefault()});
        } catch(Exception ex){err(ex);}
    }

    private void loadSigned() {
        try {
            var list = sigService.getAllSignatures();
            signedModel.setRowCount(0);
            for (var s: list) signedModel.addRow(new Object[]{s.getId(), s.getDocumentId(), s.getSignerId(), s.getSignatureHash()!=null?s.getSignatureHash().substring(0,12)+"…":"", s.getIpAddress(), s.getSignedAt()});
        } catch(Exception ex){err(ex);}
    }

    private void showNewDocDialog() {
        JTextField titleF = new JTextField();
        JTextArea contentA = new JTextArea(6,30);
        contentA.setLineWrap(true); contentA.setWrapStyleWord(true);
        JPanel form = new JPanel(new BorderLayout(6,6));
        JPanel top = new JPanel(new GridLayout(1,2,8,8));
        top.add(new JLabel("Title")); top.add(titleF);
        form.add(top, BorderLayout.NORTH);
        form.add(new JLabel("Content"), BorderLayout.CENTER);
        form.add(new JScrollPane(contentA), BorderLayout.SOUTH);
        int res = JOptionPane.showConfirmDialog(this, form, "New Document", JOptionPane.OK_CANCEL_OPTION);
        if (res!=JOptionPane.OK_OPTION) return;
        try {
            docService.createDocument(titleF.getText().trim(), contentA.getText(), currentUser.getId());
            loadDocuments();
            JOptionPane.showMessageDialog(this, "Document created — confirmation: PENDING_SIGNATURE");
        } catch(Exception ex){err(ex);}
    }

    private void showTemplateDialog() {
        JTextField nameF = new JTextField();
        JTextArea dataA = new JTextArea(4,30);
        dataA.setText(currentUser.getName() + " — ✍️");
        JPanel form = new JPanel(new GridLayout(0,1,6,6));
        form.add(new JLabel("Template Name")); form.add(nameF);
        form.add(new JLabel("Signature Data (text / style)")); form.add(new JScrollPane(dataA));
        int res = JOptionPane.showConfirmDialog(this, form, "Create Signature Template", JOptionPane.OK_CANCEL_OPTION);
        if (res!=JOptionPane.OK_OPTION) return;
        try {
            sigService.createTemplate(nameF.getText().trim(), dataA.getText(), currentUser.getId());
            loadTemplates();
            JOptionPane.showMessageDialog(this, "Signature status: template created");
        } catch(Exception ex){err(ex);}
    }

    private void signSelected() {
        int r = docTable.getSelectedRow();
        if (r<0){JOptionPane.showMessageDialog(this,"Select a document to sign"); return;}
        int docId=(int)docModel.getValueAt(r,0);
        try {
            var templates = sigService.getTemplatesByUser(currentUser.getId());
            if (templates.isEmpty()){
                JOptionPane.showMessageDialog(this,"Create a signature template first (Signature Management tab)");
                return;
            }
            JComboBox<String> combo = new JComboBox<>();
            for (var t: templates) combo.addItem(t.getId()+" — "+t.getName());
            JPanel p = new JPanel(new GridLayout(0,1,6,6));
            p.add(new JLabel("Choose template")); p.add(combo);
            int res = JOptionPane.showConfirmDialog(this, p, "Sign Document #"+docId, JOptionPane.OK_CANCEL_OPTION);
            if (res!=JOptionPane.OK_OPTION) return;
            int templateId = templates.get(combo.getSelectedIndex()).getId();
            sigService.signDocument(docId, currentUser.getId(), templateId, "127.0.0.1");
            JOptionPane.showMessageDialog(this, "Confirmation of successful document signing ✓");
            loadDocuments(); loadSigned();
        } catch(Exception ex){err(ex);}
    }

    private void err(Exception ex){ JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
}
